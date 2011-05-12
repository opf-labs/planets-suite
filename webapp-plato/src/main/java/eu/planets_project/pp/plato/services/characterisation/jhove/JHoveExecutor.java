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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;

import org.apache.commons.logging.Log;

import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.Module;
import edu.harvard.hul.ois.jhove.handler.XmlHandler;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;

public class JHoveExecutor {
    private Log log = PlatoLogger.getLogger(JHoveExecutor.class);
    
    private static final int[] myDate = { 2008, 2, 21 };

    public void execute(String outputFile, String filePathName) {
        try {
        
            App app = new App("", "", myDate, "", "");
            String configFile = loadConfigFile();
            String saxClass = JhoveBase.getSaxClassFromProperties();
            
            JhoveBase jhove = new JhoveBase();
            jhove.setLogLevel("SEVERE");

            jhove.init(configFile, saxClass);
            jhove.setEncoding("utf-8");
            jhove.setTempDirectory(OS.getTmpPath());
            

            // load all module
            Module module = null;

            XmlHandler aboutHandler = new XmlHandler();
            OutputStream out = newOutputStream(new StringBuffer());
            aboutHandler.setWriter(new PrintWriter(out));
            String s = new File(filePathName).toURI().toString();
            String[] dirFileOrUri = new String[] { s };

            // execute jhove
            jhove.dispatch(app, module, aboutHandler, aboutHandler, outputFile,
                    dirFileOrUri);
        } catch (Exception e) {
            log.error("could not execute jhove: "+e.getMessage(),e);
        }
    }

    /**
     * Save the dynamic configuration in a file accessible from jhove if the 
     * configuration is not present: else returns the configuration file path 
     * 
     * @return
     * @throws IOException
     */
    private String loadConfigFile() throws IOException {
        String confFile = "jhove.conf";
        File f = new File(OS.getTmpPath() + confFile);

            if (!f.exists()) {
                InputStream in = new BufferedInputStream(Thread.currentThread()
                        .getContextClassLoader().getResourceAsStream("jhove.conf"));

                OutputStream out = new FileOutputStream(f);
                
                FileUtils.writeToFile(in,out);
            }
        return f.getAbsolutePath();
    }

    // Returns an output stream for a ByteBuffer.
    // The write() methods use the relative ByteBuffer put() methods.
    public static OutputStream newOutputStream(final StringBuffer buf) {
        return new OutputStream() {
            public synchronized void write(int b) throws IOException {
                buf.append((byte) b);
            }

        };
    }

}