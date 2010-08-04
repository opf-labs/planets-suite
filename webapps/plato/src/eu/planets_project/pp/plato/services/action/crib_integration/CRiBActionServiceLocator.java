/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.services.action.crib_integration;

import java.rmi.RemoteException;
import java.util.LinkedList;

import javax.ejb.EJBException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;

import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.model.Parameter;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.action.IMigrationAction;
import eu.planets_project.pp.plato.services.action.MigrationResult;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.Criterion;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.FileObject;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.MetaconverterServiceLocator;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.Metaconverter_PortType;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.MigrationPath;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.RepresentationObject;
import eu.planets_project.pp.plato.util.PlatoLogger;
/**
 * A common action interface for all CRiB migration services located at http://crib.dsi.uminho.pt
 *  
 * @author Michael Kraxner
 *
 */
public class CRiBActionServiceLocator implements IMigrationAction {
    private MetaconverterServiceLocator metaconverterServiceLocator;
    private Metaconverter_PortType metaconverterService;
    
    private MigrationResult lastResult;
    
    private static final Log log = PlatoLogger.getLogger(CRiBActionServiceLocator.class);
    
    
    public CRiBActionServiceLocator(){
        metaconverterServiceLocator = new MetaconverterServiceLocator();
    }

    public MigrationResult getLastResult() {
        return lastResult;
    }

    public MigrationResult migrate(PreservationActionDefinition action, SampleObject sampleObject) throws PlatoServiceException {
        if (perform(action, sampleObject)) {
            return lastResult;             
        }
        return null;
    }

    private String getCriterion(Criterion [] criteria, String name){
        if (name == null || criteria == null)
            return null;
        int idx = 0;
        while (idx < criteria.length) {
            if (name.equals(criteria[idx].getName()))
                return criteria[idx].getValue();
            idx++;
        }
        return null;
    }
    /**
     * Migrates sample record <code>sampleObject</code>  with the migration action  defined in <code>action</code>.
     *  
     */
    public boolean perform(PreservationActionDefinition action, SampleObject sampleObject) throws PlatoServiceException{
        try {
            // all migration actions are done via the metaconverter
            metaconverterService = metaconverterServiceLocator.getMetaconverter();

            // prepare the data of the sample record for migration
            FileObject sampleFile = new FileObject(sampleObject.getData().getData(), sampleObject.getFullname());
            RepresentationObject representationObject = new RepresentationObject(new FileObject[]{sampleFile});

            // the action parameters specify which migration service is called
            MigrationPath migrationPath = new MigrationPath();
            LinkedList<String> urls = new LinkedList<String>();
            for (Parameter param : action.getParams()) {
                urls.add(param.getValue());
            }
            String[] urlArr = urls.toArray(new String[]{});
            migrationPath.setAccessPoints(urlArr);
            /*
             * convert source object
             */
            eu.planets_project.pp.plato.services.crib_integration.remoteclient.MigrationResult migrationResult = metaconverterService.convert(representationObject, migrationPath);
            /*
             * collect migration results
             */
            MigrationResult result = new MigrationResult();
            lastResult = result;

            /*
             * if the migration was successful is indicated by two flags:
             * "process::availability" and "process::stability"
             * - which are float values (!)
             */ 
            Criterion[] criteria = migrationResult.getReport().getCriteria();
            double availability = Double.parseDouble(getCriterion(criteria, "process::availability"));
            double stability = Double.parseDouble(getCriterion(criteria, "process::stability"));
            result.setSuccessful((availability > 0.0) && (stability > 0.0));
            
            if (!result.isSuccessful()) {
                result.setReport(String.format("Service '%s' failed to migrate sample '%s'.", action.getShortname() ,  sampleObject.getFullname()));//+ getCriterion(criteria, "message::reason"));
                log.debug(String.format("Service '%s' failed to migrate sample '%s': %s", action.getShortname(), sampleObject.getFullname(), getCriterion(criteria, "message::reason")));
                return true;
            } else {
                result.setReport(String.format("Migrated object '%s' to format '%s'. Completed at %s.",
                        sampleObject.getFullname(), action.getTargetFormat(), migrationResult.getReport().getDatetime()));
            }
            result.getMigratedObject().getData().setData(migrationResult.getRepresentation().getFiles()[0].getBitstream().clone());
            
            /*
             * message::filename contains the name of the source-file, NOT the migrated
             */
            String filename = migrationResult.getRepresentation().getFiles()[0].getFilename();
            
            /*
             * if filename is missing, use name from the source object (without extension)
             */
            if ((filename == null) || "".equals(filename)) {
                filename = sampleObject.getFullname();
                int bodyEnd = filename.lastIndexOf(".");
                if (bodyEnd >= 0)
                    filename = filename.substring(0, bodyEnd);
            }
            result.getMigratedObject().setFullname(filename);
            int bodyEnd;

            /*
             * CRiB does not provide forther information about the format of the migrated object,
             * therfore the file extension of the migrated object is derived from the action's target formats 
             */
            bodyEnd = filename.lastIndexOf(".");
            if ((bodyEnd < 0) &&
                ((result.getTargetFormat().getDefaultExtension() == null) ||
                  "".equals(result.getTargetFormat().getDefaultExtension()))) {
                FormatInfo targetFormat = new FormatInfo();
                setFormatFromCRiBID(action.getTargetFormat(), targetFormat);
                result.setTargetFormat(targetFormat);
                filename = filename + "." + result.getTargetFormat().getDefaultExtension();
                result.getMigratedObject().setFullname(filename);
            }
            lastResult = result;
            return true;
        } catch (NumberFormatException e) {
            throw new PlatoServiceException("Migration failed, CRiB returned an invalid result.", e);
        } catch (RemoteException e) {
            throw new PlatoServiceException("Migration failed, could not access CRiB.", e);
        } catch (ServiceException e) {
            throw new PlatoServiceException("Migration failed, could not access CRiB.", e);
        } catch (EJBException e) {
            throw new PlatoServiceException("Migration failed, could not access CRiB.", e);
        }
    }

    /**
     * Sets default extension and mime type of <code>format</code> according to the provided
     * <code> cribID</code>. 
     */
    private void setFormatFromCRiBID(String cribID, FormatInfo format) {
        String id = cribID.toUpperCase();
        if (id.indexOf("JPEG 2000") >= 0) {
            format.setDefaultExtension("jp2");
            format.setMimeType("image/jpeg");
        } else if (id.indexOf("JPEG") >= 0) {
            format.setDefaultExtension("jpg");
            format.setMimeType("image/jpeg");
        } else if (id.indexOf("PORTABLE NETWORK GRAPHICS") >= 0) {
            format.setDefaultExtension("png");
            format.setMimeType("image/png");
        } else if (id.indexOf("TAGGED IMAGE FILE") >= 0) {
            format.setDefaultExtension("tiff");
            format.setMimeType("image/tiff");
        } else if (id.indexOf("BITMAP") >= 0) {
            format.setDefaultExtension("bmp");
            format.setMimeType("image/bmp");
        } else if (id.indexOf("PORTABLE DOCUMENT FORMAT") >= 0) {
            format.setDefaultExtension("pdf");
            format.setMimeType("application/pdf");
        } else if (id.indexOf("PLAIN TEXT") >= 0) {
            format.setDefaultExtension("txt");
            format.setMimeType("text/plain");
        } else if (id.indexOf("GRAPHICS INTERCHANGE FORMAT") >= 0) {
            format.setDefaultExtension("gif");
            format.setMimeType("image/gif");
        } else if (id.indexOf("MICROSOFT WORD FOR WINDOWS DOCUMENT") >= 0) {
            format.setDefaultExtension("doc");
            format.setMimeType("application/doc");
        } 

    }

}
