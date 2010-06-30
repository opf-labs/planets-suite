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
package eu.planets_project.tb.impl.services.util;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class DescribeService {
   
    @SuppressWarnings("unchecked")
    public static ServiceDescription getServiceDescription( String classname ) {
        // Attempt to instantiate the class:
        Class serviceClazz;
        try {
            serviceClazz = DescribeService.class.getClassLoader().loadClass(classname);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }
        
        // Attempt to invoke the service:
        PlanetsService service;
        try {
            service = (PlanetsService) serviceClazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        
        // Return the description:
        return service.describe();
    }

    /**
     * Main so that this can be invoked from the build scripts to create descriptions from the code.
     * 
     * @param args Pass the fully qualified class name of your service.
     */
    public static void main(String [] args)  {
        String classname;
        if( args.length >= 1 ) {
            classname = args[0];
        } else {
            classname = "eu.planets_project.services.sanselan.SanselanIdentify";
        }
        
        ServiceDescription sd = DescribeService.getServiceDescription(classname);
        System.out.print(sd.toXmlFormatted());
    }
}

/*
 * Original idea was to do this as an ant task, but this means having ANT on the server classpath unless we modify the build system.
 * 
 * 
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

/ **
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 * /
public class DescribeService extends Task {

       private String name=null;

       public void setName(String name) {
          this.name = name;
       }

       public String getName() {
          return name;
       }

       public void execute() {
          if (name != null && name.length() > 0) {
             log("Hello, " + name + "!", Project.MSG_INFO);
          }
          else {
             log("Hello, World!", Project.MSG_INFO);
          }
       }

}
*/