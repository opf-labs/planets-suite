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
package eu.planets_project.pp.plato.services.characterisation.xcl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.model.ObjectProperty;
import eu.planets_project.pp.plato.model.characterisation.xcl.XCLObjectProperty;
import eu.planets_project.pp.plato.model.measurement.Metric;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.services.PlatoServiceException;
import eu.planets_project.pp.plato.services.characterisation.ICharacterisationService;
import eu.planets_project.pp.plato.util.CommandExecutor;
import eu.planets_project.pp.plato.util.OS;
import eu.planets_project.pp.plato.util.PlatoLogger;
import eu.planets_project.pp.plato.xml.StrictErrorHandler;

/**
 * This class deals with the current implementation of the PP5 characterisation tool.
 * Based on a list of pronom unique identifiers (puid) it returns all measurable properties.
 *
 * @author Hannes Kulovits
 */
public class XclPropertiesExplorer implements ICharacterisationService, Serializable {

    private static final long serialVersionUID = 3499698474788470264L;

    private static final Log log = PlatoLogger.getLogger(XclPropertiesExplorer.class);
    
    private Set<ObjectProperty> propertiesSet;
    private List<String> warnings;
    private String format;
    private String status;
    private String error;
    
    private String xclExplorerPath;
    
    private String makeTempDir() {
        String tempDir = OS.getTmpPath() + "xclexplorer" + System.nanoTime() + "/";
        new File(tempDir).mkdir();
        return tempDir;
    }
    
    public XclPropertiesExplorer() {
        
        xclExplorerPath = System.getenv("XCLEXPLORER_HOME");
        if (xclExplorerPath != null) {
            xclExplorerPath = xclExplorerPath + (xclExplorerPath.endsWith(File.separator) ? "": File.separator);
        } else {
            log.error("Environment variable XCLEXPLORER_HOME is not defined!");
        }
    }

    /**
     * @param PUIDs colon seperated list of "planets puids", i.e. pronom unique identifieres using an
     *        underscore character instead of a forward slash. The list must end with a colon.
     *        Example: fmt_10:fmt_13:
     */
    public List<ObjectProperty> characterise(String PUIDs, List<String> warnings) throws PlatoServiceException{

        if (("".equals(PUIDs)) || (xclExplorerPath == null)) {
            return new ArrayList<ObjectProperty>();
        }

        String xcelString;
        String tempDir = makeTempDir();
        String command = xclExplorerPath+"XCLExplorer "+PUIDs+ " -o "+tempDir;
        
        try {
            CommandExecutor cmdExecutor = new CommandExecutor();
            cmdExecutor.setWorkingDirectory(xclExplorerPath);
            try {
                int exitStatus = cmdExecutor.runCommand(command);
                // r.setSuccess(exitStatus == 0);
                //r.setReport(cmdExecutor.getCommandError());
            } catch (Exception e) {
              log.error(e.getMessage(),e);
            }
       
            String outputFile = tempDir+"fpm.fpm";
            
            // we use a set as we don't want dublicate properties
            propertiesSet = new HashSet<ObjectProperty>();
            this.warnings = warnings;
            status = null;
            format = null;
            error = null;


            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setErrorHandler(new StrictErrorHandler());

            digester.push(this);
            
            digester.addCallMethod("*/XCLExplorer/fpmError", "setError",0);

            // maybe there is a warning in the property "status"
            digester.addSetProperties("*/XCLExplorer/format");
            digester.addCallMethod("*/XCLExplorer/format", "addWarning", 0 );
            
            digester.addObjectCreate("*/XCLExplorer/format/property", XCLObjectProperty.class);
            digester.addBeanPropertySetter("*/XCLExplorer/format/property/id", "propertyId");
            digester.addBeanPropertySetter("*/XCLExplorer/format/property/name", "name");
            digester.addBeanPropertySetter("*/XCLExplorer/format/property/description", "description");
            digester.addBeanPropertySetter("*/XCLExplorer/format/property/unit", "unit");
            digester.addBeanPropertySetter("*/XCLExplorer/format/property/type", "type");

            digester.addObjectCreate("*/XCLExplorer/format/property/metrics/m", Metric.class);
            digester.addBeanPropertySetter("*/XCLExplorer/format/property/metrics/m/mId", "metricId");
            digester.addBeanPropertySetter("*/XCLExplorer/format/property/metrics/m/mName", "name");
            digester.addBeanPropertySetter("*/XCLExplorer/format/property/metrics/m/mDescription", "description");
            digester.addBeanPropertySetter("*/XCLExplorer/format/property/metrics/m/mType", "type");
            digester.addSetNext("*/XCLExplorer/format/property/metrics/m", "addMetric");

            digester.addSetNext("*/XCLExplorer/format/property", "addProperty");

            try {
                digester.setUseContextClassLoader(true);

                digester.parse( new FileInputStream(outputFile));
                
                if (error != null) {
                    throw new PlatoServiceException("XCLExplorer failed: " + error);
                }
                
                return new ArrayList<ObjectProperty>(propertiesSet);
                
            } catch (IOException e) {
                throw new PlatoServiceException("The response of XCLExplorer is invalid.", e);
            } catch (SAXException e) {
                throw new PlatoServiceException("The response of XCLExplorer is invalid.", e);
            } finally {
                new File(tempDir+"fpm.fpm").delete();
                new File(tempDir).delete();
            }
        }  catch (Exception e) {
            log.error(e.getMessage(),e);
            return new ArrayList<ObjectProperty>();
        } 
    }
    
    /**
     * Adds the ObjectProperty to the propertiesSet.
     * Used to parse the fpmResponse.
     *  
     * @param p
     */
    public void addProperty(ObjectProperty p) {
        propertiesSet.add(p);
    }
    
    /**
     * Adds the status info to the {@link #warnings}.
     * Used to parse the fpmResponse.
     * 
     * @param s
     */
    public void setStatus(String s) {
        status = s;
    }
    
    public void setPuid(String f) {
        format = f;
    }
    public void addWarning(String s) {
        if ((status != null) && !("".equals(status)))
            if ("unavailable".equals(status))
               warnings.add("At the moment the characterisation service at hki.uni-koeln.de does not support the format " + format + ".");                
            else
               warnings.add(format + ": " + status);
        
        format = null;
        status = null;
    }
    
    public static Scale adjustCriterionToMetric(Metric metric) {
        if (metric == null)
            return null;
        /*
         * Plato's Scales are more precise than the result types of XCL metrics: 
         * There are not only int, float and boolean values, but a distinction is drawn between
         * positive and negative numbers.
         * Therefore we have to choose the scale according to the metric itself.  
         */
        Scale scale = MetricToScaleMapping.getScale(metric.getMetricId().trim());
        return scale;
        
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }    
}
