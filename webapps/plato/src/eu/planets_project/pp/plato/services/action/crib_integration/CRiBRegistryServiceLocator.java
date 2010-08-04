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

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.model.Parameter;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.action.IPreservationActionRegistry;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.MetaconverterServiceLocator;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.Metaconverter_PortType;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.MigrationPath;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.TransportException;
import eu.planets_project.pp.plato.services.crib_integration.remoteclient.UDDIException;
/**
 * Provides access to the CRiB service registry at http://crib.dsi.uminho.pt
 * 
 * @author Michael Kraxner
 *
 */
public class CRiBRegistryServiceLocator implements IPreservationActionRegistry {
    
    private MetaconverterServiceLocator serviceRegistryServiceLocator;
    private Metaconverter_PortType serviceRegistryService;
    private String lastInfo;
    
    public CRiBRegistryServiceLocator(){
        serviceRegistryServiceLocator = new MetaconverterServiceLocator();
    }
    
    public String getToolIdentifier(String url) {
        return "";
    }
    
    public String getToolParameters(String url) {
        return "";
    }
    

    public String getLastInfo() {
        return lastInfo;
    }
    public void connect(String url)  throws ServiceException, MalformedURLException{
        lastInfo = "";
        serviceRegistryService = serviceRegistryServiceLocator.getMetaconverter(
                new URL(url));
    }
    /**
     * Returns a list of preservation actions which can handle objects of the provided <code>sourceFormat</code>.
     */
    public List<PreservationActionDefinition> getAvailableActions(FormatInfo sourceFormat) throws PlatoServiceException{
        try {
            lastInfo = "";

            if (sourceFormat.getName() == null || "".equals(sourceFormat.getName()) 
             || sourceFormat.getVersion() == null || "".equals(sourceFormat.getVersion())) {
                throw new PlatoServiceException("The sample object's format is missing name or version information," +
                                "both of which are needed for querying this registry. " +
                                "Please provide name and version or try one of the other registries, which work on PUIDS of formats.");
            }
              
            ArrayList<String> shortnames = new ArrayList<String>();

            /*
             * CRiB uses "<name>, version <version nr>", both from PRONOM, as ID  
             */ 
            String cribID = Utils.makeCRiBId(sourceFormat);
            /*
             * First get the available target formats 
             */
            
            String [] targetFormats = serviceRegistryService.getSupportedTargetFormats(cribID);
            ArrayList<PreservationActionDefinition> preservationActions = new ArrayList<PreservationActionDefinition>(); 
            for (int i = targetFormats.length-1; i >= 0; i--) {
                String targetFormat = targetFormats[i];
                /*
                 * Then get all the possible migration paths for each target format
                 * - each migration path corresponds to one service   
                 */
                PreservationActionDefinition action;
                
                MigrationPath[] paths = serviceRegistryService.getMigrationPaths(cribID, targetFormat);
                for (int j = 0; j < paths.length; j++) {
                    MigrationPath path = paths[j];
                    action = new PreservationActionDefinition();
                    
                    action.setShortname(getShortnameFromPath(path, shortnames));
                    String urls[] = path.getAccessPoints();
                    LinkedList<Parameter> params = new LinkedList<Parameter>();
                    for (int k = 0; k < urls.length; k++) {
                        Parameter param = new Parameter();
                        param.setName(""+k);
                        param.setValue(urls[k]);
                        params.add(param);
                    }
                    action.setParams(params);
                    action.setTargetFormat(targetFormats[i]);
                    if (urls.length > 0)
                        action.setUrl(urls[0].substring(0, urls[0].lastIndexOf("/")));
                    /*
                     * extract information about the conversions from urls
                     */
                    StringBuilder b = new StringBuilder();
                    b.append(getSourceFormatFromPath(urls[0]));
                    for(int uIdx = 0; uIdx < urls.length; uIdx++) {
                        b.append(">");
                        b.append(getTargetFormatFromPath(urls[uIdx]));
                    }
                    action.setInfo(b.toString());
                    /*
                     * mark this as a CRiB action
                     */
                    action.setActionIdentifier("CRiB");
                    /*
                     * no gif, bmp, text - conversions: they are of minor interest for dp
                     */ 
/*                String info = action.getInfo().toUpperCase();
                    if (!(info.contains("GIF") ||
                        info.contains("TEXT") ||
                        info.contains("BMP") ||
                        info.contains("PDF") ||
                        info.indexOf("JPG",4) > -1 ))
*/                    
                    preservationActions.add(action);
                }
            }
            if (preservationActions.size() == 0)
                lastInfo = "This registry does not contain a service that handles objects of type " + sourceFormat.getLongName();
            return preservationActions;
        } catch (UDDIException e) {
            throw new PlatoServiceException("Access to registry failed.", e);
        } catch (TransportException e) {
            throw new PlatoServiceException("Access to registry failed.", e);
        } catch (RemoteException e) {
            throw new PlatoServiceException("Access to registry failed.", e);
        }
    }
    /**
     * Extracts the source format from the given path.
     * the path must be similar to http://yx.d/pdf2tif
     * 
     * @param path
     */
    private String getSourceFormatFromPath(String path){
        int toIdx = path.lastIndexOf("2");
        if (toIdx == path.length()-1 )
            // formats like JP2 or PDF_2 contain a "2", but only at the end
            toIdx = path.lastIndexOf("2", toIdx-1);
        return path.substring(path.lastIndexOf("/")+1, toIdx);
    }
    /**
     * Extracts the target format from the given path.
     * the path must be similar to http://yx.d/pdf2tif
     * 
     * @param path
     */
    private String getTargetFormatFromPath(String path) {
        int toIdx = path.lastIndexOf("2");
        if (toIdx == path.length()-1 )
            // formats like JP2 or PDF_2 contain a "2", but only at the end
            toIdx = path.lastIndexOf("2", toIdx-1);
        return path.substring(toIdx+1);
    }
    /**
     * Builds a short name for the given migration path
     * 
     * @param path
     * @param assigned
     */
    private String getShortnameFromPath(MigrationPath path, ArrayList<String> assigned){
        // path: http://winmigrationserver.dsi.uminho.pt:8080/axis/services/DOC2PDF
        String first = path.getAccessPoints()[0];
        String last = path.getAccessPoints()[path.getAccessPoints().length-1];
        String from = getSourceFormatFromPath(first);
        String to = getTargetFormatFromPath(last);
        
        String base = from + " > " + to;
        // maybe there are more services for this conversion
        String shortname = base;
        int i = 1;
        while (assigned.contains(shortname)) {
            i++;
            shortname = base + " #" + i;
        }
        assigned.add(shortname);
        return shortname;
    }

}
