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

package eu.planets_project.pp.plato.services.action.planets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.action.IMigrationAction;
import eu.planets_project.pp.plato.services.action.MigrationResult;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;

/**
 * A common client-side interface for all Planets preservation services. 
 * 
 * @author Michael Kraxner
 *
 */
public class PlanetsMigrationService implements IMigrationAction {

    private static final Log log = PlatoLogger.getLogger(PlanetsMigrationService.class);
    
    private MigrationResult lastResult;

    public MigrationResult getLastResult() {
        return lastResult;
    }
    
    /**
     * Performs the preservation action.
     * 
     * For Planets migration services url in {@link PreservationActionDefinition} carries the wsdl location of the migration service. 
     */
    public boolean perform(PreservationActionDefinition action, SampleObject sampleObject) throws PlatoServiceException {
        
        URL wsdlLocation;
        try {
            wsdlLocation = new URL(action.getUrl());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return false;
        }
        
        Service service = null;
        try {
            service = Service.create(wsdlLocation, Migrate.QNAME);
        } catch(WebServiceException e) {
            throw new PlatoServiceException("Error creating web service.", e);
        }
        
        Migrate m = (Migrate) service.getPort(Migrate.class);
        
        // create digital object that contains our sample object
        DigitalObject dob = new DigitalObject.Builder( Content.byValue(sampleObject.getData().getData()) ).title("data").build();

        FormatRegistry formatRegistry = FormatRegistryFactory.getFormatRegistry();

        // create Planets URI from extension
        URI sourceFormat = formatRegistry.createExtensionUri(sampleObject.getFormatInfo().getDefaultExtension());
        
        URI targetFormat;
        try {
            targetFormat = new URI(action.getTargetFormat());
        } catch (URISyntaxException e) {
            throw new PlatoServiceException("Error in target format.", e);
        }
        
        List<Parameter> serviceParams = getParameters(action.getParamByName("settings"));
        
        if (serviceParams.size() <= 0) {
            serviceParams = null;
        }
        
        // perform migration
        MigrateResult result = m.migrate(dob, sourceFormat, targetFormat, serviceParams);

        MigrationResult migrationResult = new MigrationResult();
        migrationResult.setSuccessful((result != null) && (result.getDigitalObject() != null));

        if (migrationResult.isSuccessful()) {
            migrationResult.setReport(String.format("Service %s successfully migrated object.", wsdlLocation));
        } else {
            migrationResult.setReport(String.format("Service %s failed migrating object. " + ((result==null)? "" : result.getReport()), wsdlLocation));
            lastResult = migrationResult;
            return true;
        }

        InputStream in = result.getDigitalObject().getContent().getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] b = new byte[1024];

        try {
            while ((in.read(b)) != -1) {
                out.write(b);
            }
        } catch (IOException e) {
            throw new PlatoServiceException("Unable to read result data from service response.", e);
        }

        migrationResult.getMigratedObject().getData().setData(out.toByteArray());

        String fullName = sampleObject.getFullname();

        String ext;
        try {
            ext = formatRegistry.getFirstExtension(new URI(action.getTargetFormat()));
        } catch (URISyntaxException e) {
            ext = "";
        }
        
        // if we find an extension, cut if off ...
        if (fullName.lastIndexOf('.') > 0) {
            fullName = fullName.substring(0, fullName.lastIndexOf('.'));
        }

        // ... so we can append the new one.
        if (! "".equals(ext)) {
            fullName += ("." + ext);    
        }

        migrationResult.getMigratedObject().setFullname(fullName);

        lastResult = migrationResult;

        return true;
    }

    public MigrationResult migrate(PreservationActionDefinition action, SampleObject sampleObject) throws PlatoServiceException {
        
        if (false == perform(action, sampleObject)) {
            return null;
        }
        
        return lastResult;
    }
    
    
    private QName determineServiceQNameFromWsdl(URL wsdlLocation) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // Using factory get an instance of document builder
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        // parse using builder to get DOM representation of the XML file
        Document dom;
        try {
            dom = db.parse(wsdlLocation.openStream());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // get the root elememt
        Element root = dom.getDocumentElement();
        return new QName(root.getAttribute("targetNamespace"), root
                .getAttribute("name"));
    }    
    
    
    private List<Parameter> getParameters(String configSettings) {
        
        List<Parameter> serviceParams = new ArrayList<Parameter>();
        
        if (configSettings == null) {
            return serviceParams;
        }
        
        Scanner scanner = new Scanner(configSettings);

        int index;
        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();

            if ((index = line.indexOf('=')) > 0) {
                String name = line.substring(0, index);
                String value = line.substring(index+1);

                if (name.length()>0 && value.length()>0) {
                    serviceParams.add((new Parameter.Builder(name.trim(), value.trim()).build()));
                }
            }
        }
        
        return serviceParams;
    }

}
