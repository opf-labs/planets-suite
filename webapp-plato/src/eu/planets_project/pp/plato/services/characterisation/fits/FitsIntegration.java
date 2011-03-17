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
package eu.planets_project.pp.plato.services.characterisation.fits;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.dom4j.io.OutputFormat;

import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.util.CommandExecutor;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;

public class FitsIntegration implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Log log = PlatoLogger.getLogger(FitsIntegration.class);

   // private static final long serialVersionUID = 5183122855613215086L;
    public static OutputFormat prettyFormat = new OutputFormat(" ", true,"ISO-8859-1"); //OutputFormat.createPrettyPrint();

    private static String FITS_HOME;
    
    private static String FITS_COMMAND = "%FITS_EXEC% -i %INPUT% -o %OUTPUT%"; 
    
    public FitsIntegration() throws PlatoServiceException{
        FITS_HOME = System.getenv("FITS_HOME");
        if (FITS_HOME == null) {
            FITS_HOME = "/home/kraxner/dev/fits-0.2.6/";
            if (! new File(FITS_HOME).exists()) {
                FITS_HOME = null;
            }
        }
        if (FITS_HOME == null) {
            throw new PlatoServiceException("FITS is not propertly configured - FITS_HOME is not defined.");
        }
    }
    
    public String characterise(File input) throws PlatoServiceException{
        CommandExecutor cmdExecutor = new CommandExecutor();
        cmdExecutor.setWorkingDirectory(FITS_HOME);
        String scriptExt;
        if ("Linux".equalsIgnoreCase(System.getProperty("os.name"))){
            scriptExt = "./fits.sh";
        } else {
            scriptExt = "cmd /c %FITS_HOME%/fits";
        }
        File output = new File(OS.getTmpPath() + "fits"+System.nanoTime()+".out");
        try {
            String commandLine = FITS_COMMAND.replace("%FITS_EXEC%", scriptExt)
                .replace("%INPUT%", input.getAbsolutePath())
                .replace("%OUTPUT%", output.getAbsolutePath());
            
            try {
                int exitcode = cmdExecutor.runCommand(commandLine);
                if (exitcode != 0) {
                    String cmdError = cmdExecutor.getCommandError();
                    throw new PlatoServiceException("FITS characterisation for file: " + input + " failed: " + cmdError);
                }
                if (!output.exists()) {
                    throw new PlatoServiceException("FITS characterisation for file: " + input + " failed: no output was written.");
                }
                
                return new String(FileUtils.getBytesFromFile(output));
            } catch (PlatoServiceException e) {
                throw e;
            } catch (Throwable t) {
                throw new PlatoServiceException("FITS characterisation for file: " + input + " failed: " + t.getMessage(), t);            
            }
        } finally {
            output.delete();
        }
    }
    
}
