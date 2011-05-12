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
package eu.planets_project.tb.gui.backing.admin.wsclient.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A ServiceInfo is an in-memory representation of a service defined in a WSDL contract
 *
 * @author Markus Reis, ARC
 */

public class ServiceInfo
{
   /** The service name */
   String name = "";

   /** The list of operations that this service defines. */
   List operations = new ArrayList();

   /**
    * Constructor
    */
   public ServiceInfo()
   {
   }

   /**
    * Sets the name of the service
    *
    * @param value The name of the service
    */
   public void setName(String value)
   {
      name = value;
   }

   /**
    * Gets the name of the service
    *
    * @return The name of the service is returned
    */
   public String getName()
   {
      return name;
   }

   /**
    * Add an ooperation info object to this service definition
    *
    * @param operation The operation to add to this service definition
    */
   public void addOperation(OperationInfo operation)
   {
      operations.add(operation);
   }

   /**
    * Returs the operations defined by this service
    *
    * @return an Iterator that can be used to iterate the operations defined by this service
    */
   public Iterator getOperations()
   {
      return operations.iterator();
   }

   /**
    * Override toString to return the name of the service
    *
    * @return The name of the service is returned
    */
   public String toString()
   {
      return getName();
   }
}
