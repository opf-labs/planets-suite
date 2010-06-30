/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.tb.impl.serialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.impl.model.ExperimentImpl;

/**
 * This manages the temporary files needed to import and export Experiments.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExperimentFileCache {
    private static Log log = LogFactory.getLog(ExperimentFileCache.class);
    
    private static File cachedir = null;
    private static String cacheExt = ".cache";
    private static String expExt = ".exp.xml";
    
    /**
     * 
     */
    public ExperimentFileCache() {
        if( cachedir != null ) return;
        // Generate a temporary file and turn it into a directory:
        try {
            UUID dirname = UUID.randomUUID();
            File tmp = File.createTempFile(dirname.toString(), cacheExt);
            tmp.delete();
            tmp.mkdir();
            tmp.deleteOnExit();
            cachedir = tmp;
            log.info("Set up export cache: "+cachedir.getPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param exp
     * @return
     */
    public String createExperimentExport( ExperimentImpl ... exp ) {
        try {
            File tmp = createTempFile();
            //ExperimentViaJAXB.writeToFile(exp, tmp);
            ExperimentRecords.writeExperimentsToOutputStream( new FileOutputStream( tmp ), exp );
            log.info("Written to file: "+tmp.getName());
            return tmp.getName();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    public String createExperimentsExport( Collection<Experiment> allExps ) {
        try {
            File tmp = createTempFile();
            ExperimentRecords.writeExperimentsToOutputStream(new FileOutputStream( tmp ), allExps );
            log.info("Written to file: "+tmp.getName());
            return tmp.getName();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 
     * @return
     * @throws IOException
     */
    public File createTempFile() throws IOException {
        // Create a temporary file, and ensure it will get cleaned up on exit:
        String exname = UUID.randomUUID().toString();
        File tmp = File.createTempFile(exname.toString(), expExt, cachedir);
        tmp.deleteOnExit();
        log.info("Created temp file: "+tmp);
        return tmp;
    }
    
    /**
     * 
     * @param exportID
     * @return
     */
    public File getExportedFile( String exportID ) {
        File exported = new File( cachedir.getPath() + File.separator + exportID );
        log.info("Getting file: "+exported);
        return exported;
    }

}
