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
package eu.planets_project.ifr.core.common.mail;

import java.io.StringWriter;
import java.util.logging.Logger;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.VelocityException;

import java.util.*;

public class PlanetsMailer 
{    
    protected static final Logger log = Logger.getLogger(PlanetsMailer.class.getName());
    private VelocityEngine velocityEngine;

    public PlanetsMailer() 
    {
        velocityEngine = new VelocityEngine();
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        props.setProperty("velocimacro.library", "");
        try {
            velocityEngine.init(props);
        } catch( Exception e ) {
            log.severe("Failed to initialise the Velocity engine. :: " + e );
        }
    }
    
    /**
     * Send a simple message based on a Velocity template.
     * @param msg
     * @param templateName
     * @param model
     */
    public void sendMessage(PlanetsMailMessage msg, String templateName,
                            Map model) {
        String result = null;

        try {
            result =
                PlanetsMailer.mergeTemplateIntoString(velocityEngine,
                                                            templateName, model);
        } catch (VelocityException e) {
            e.printStackTrace();
        }

        msg.setBody(result);
        send(msg);
    }

    /**
     * Send a simple message with pre-populated values.
     * @param msg
     */
    public void send(PlanetsMailMessage msg) {
        msg.send();
    }


    /**
     * Merges the parameters and the template using Velocity.
     * 
     * @param velocityEngine
     * @param templateLocation
     * @param model
     * @return
     * @throws VelocityException
     */
    private static String mergeTemplateIntoString(VelocityEngine velocityEngine, String templateLocation, Map model) throws VelocityException {
        StringWriter result = new StringWriter();
        try {
            VelocityContext velocityContext = new VelocityContext(model);
            velocityEngine.mergeTemplate(templateLocation, velocityContext, result);
        } catch  (VelocityException ex) {
            throw ex;
        } catch  (RuntimeException ex) {
            throw ex;
        } catch  (Exception ex) {
            log.severe("Why does VelocityEngine throw a generic checked exception, after all("+ex.getMessage()+")?");
            throw new VelocityException(ex.getMessage());
        }
        return result.toString();
    }
    
}
