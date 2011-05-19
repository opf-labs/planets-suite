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
package eu.planets_project.pp.plato.services.characterisation.jhove;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Vector;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;

import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.StrictErrorHandler;

public class JHove {

    private static final Log log = PlatoLogger.getLogger(JHove.class);

    String tmpPath = OS.getJhoveTmpPath();

    /**
     * describes a bytestream with Jhove.
     * Creates a tempfile for this and deletes it afterwards. 
     * @param fileName filename to use
     * @param byteStream the data to describe
     * @return the JHOVE XML String or empty if error
     */
    public String getJHoveInfoAsString(String fileName, byte[] byteStream) { 
        String tmpFile = tmpPath + fileName;
        try {
            log.debug("Write " + tmpFile + " to file");
            InputStream in = new ByteArrayInputStream(byteStream);
            OutputStream out=new FileOutputStream(tmpFile);
            FileUtils.writeToFile(in,out);
        
            return getJHoveInfoAsString(tmpFile);
            
        } catch (IOException iex) {
            log.error("error in jhove characterisation: "+iex.getMessage(),iex);
            log.error("Cannot read file " + tmpFile + "!");
            return "";
        } finally {
            new File(tmpFile).delete();
        }
    }

    
    /**
     * Executes jHove and returns the output as a XML String. Uses the default
     * System Temporary Directory to save the temporary extracted files.
     * 
     * @param tmpFile pointing to the file to describe. 
     * This file is left untouched, not deleted.
     * @return the JHOVE XML String or empty if error
     */
    public String getJHoveInfoAsString(String tmpFile) {
        String jhoveOutputFileName = tmpFile + ".xml";
        String stringToReturn = "";
        try {

            log.debug("Executing JHove...");
//            new JHoveExecutor().execute(jhoveOutputFileName, tmpFile);

            log.debug("Reading file " + jhoveOutputFileName + " ...");
            String tmpLine = "";
            BufferedReader br = new BufferedReader(new FileReader(
                     jhoveOutputFileName));
            while ((tmpLine = br.readLine()) != null) {
                stringToReturn += tmpLine + "\n";
            }
            //new File(tmpFile).delete();
            log.debug("Deleted file " + tmpFile);
            new File(jhoveOutputFileName).delete();
            log.debug("Deleted file " + jhoveOutputFileName);

            return stringToReturn;
        } catch (IOException iex) {
            log.error("error in jhove characterisation: "+iex.getMessage(),iex);
            log.error("Cannot read file " + tmpFile + "!");
            return "";
        }
        
    }

    /**
     * Extract the JHoveFileProperty from the String and returns a
     * {@link JHoveFileProperty} class.
     */
    public JHoveFileProperty digestString(String stringToDigest) {
        Digester digester = null;
        try {

            digester = new Digester();
            digester.setValidating(false);

            StrictErrorHandler errorHandler = new StrictErrorHandler();
            digester.setErrorHandler(errorHandler);

            digester.setUseContextClassLoader(true);

            digester.addObjectCreate("jhove", JHoveFileProperty.class);
            // GENERAL INFOS
            digester.addBeanPropertySetter("jhove/date", "extractionDate");
            digester.addSetProperties("jhove/repInfo/uri", "fileURI", "uri");
            digester.addBeanPropertySetter("jhove/repInfo/size", "fileSize");
            digester.addBeanPropertySetter("jhove/repInfo/format", "format");
            digester.addBeanPropertySetter("jhove/repInfo/version", "version");
            digester.addBeanPropertySetter("jhove/repInfo/status", "status");
            digester
                    .addBeanPropertySetter("jhove/repInfo/mimeType", "mimetype");
            digester.addBeanPropertySetter("jhove/repInfo/sigMatch/module",
                    "jhoveModuleName");
            // OBJECT: MODULE - START
            digester.addObjectCreate("jhove/repInfo/reportingModule",
                    Module.class);
            digester.addSetProperties("jhove/repInfo/reportingModule",
                    "release", "release");
            digester.addSetProperties("jhove/repInfo/reportingModule", "date",
                    "date");
            digester.addBeanPropertySetter("jhove/repInfo/reportingModule",
                    "name");
            digester.addSetNext("jhove/repInfo/reportingModule", "setModule");
            // OBJECT: MODULE - END

            // OBJECT: PROFILES - START
            digester.addObjectCreate("jhove/repInfo/profiles", Vector.class);

            // OBJECT: PROFILE - START
            digester.addCallMethod("jhove/repInfo/profiles/profile", "add", 1);
            digester.addCallParam("jhove/repInfo/profiles/profile", 0);

            digester.addSetNext("jhove/repInfo/profiles", "setProfiles");
            // OBJECT: PROFILES- END

            // OBJECT: PROPERTIES - START
            digester.addObjectCreate("jhove/repInfo/properties", Vector.class);

            // OBJECT: PROPERTY - START
            digester.addObjectCreate("*/property/", Property.class);
            digester.addBeanPropertySetter("*/property/name", "name");
            digester.addSetProperties("*/property/values", "type", "type");

            digester.addObjectCreate("*/property/values", Vector.class);
            digester.addCallMethod("*/property/values/value", "add", 1);
            digester.addCallParam("*/property/values/value", 0);

            digester.addSetNext("*/property/values", "setValues");

            // OBJECT: PROPERTY - END
            digester.addSetNext("*/property/", "add");

            digester.addSetNext("jhove/repInfo/properties", "setProperties");

            Object jhoveFileProp = digester.parse(new StringReader(
                    stringToDigest));

            if (jhoveFileProp instanceof JHoveFileProperty)
                return (JHoveFileProperty) jhoveFileProp;
            else
                return null;

        } catch (Exception exc) {
            log.error("error happened while digesting the following jhove string \n====================\n"+stringToDigest);
            log.error("could not digest jhove results. error: "+exc.getMessage(),exc);
            return null;
        } finally {
            digester.clear();
        } 
      }

}
