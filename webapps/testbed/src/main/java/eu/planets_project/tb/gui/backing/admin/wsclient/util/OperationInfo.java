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



/**
 * This class defines an in-memory model to support a SOAP invocation
 *
 * @author Markus Reis, ARC
 */

public class OperationInfo
{
   /** The SOAP encoding style to use. */
   private String encodingStyle = "";

   /** The URL where the target object is located. */
   private String targetURL = "";

   /** The namespace URI used for this SOAP operation. */
   private String namespaceURI = "";

   /** The URI of the target object to invoke for this SOAP operation. */
   private String targetObjectURI = "";

   /** The name used to when making an invocation. */
   private String targetMethodName = "";

   /** The input message. */
   private String inputMessageText = "";

   /** The output message. */
   private String outputMessageText = "";

   /** The name of input message. */
   private String inputMessageName = "";

   /** The name of output message. */
   private String outputMessageName = "";

   /** The action URI value to use when making a invocation. */
   private String soapActionURI = "";

   /** The encoding type "document" vs. "rpc" */
   private String style = "document";

   /**
    * Constructor
    */
   public OperationInfo()
   {
      super();
   }

   /**
    * Constructor
    *
    * @param style Pass "document" or "rpc"
    */
   public OperationInfo(String style)
   {
      super();

      setStyle(style);
   }

   /**
    * Sets the encoding style for this operation.
    *
    * @param   value    The encoding style
    */
   public void setEncodingStyle(String value)
   {
      encodingStyle = value;
   }

   /**
    * Gets the encoding style for this operation
    *
    * @return A string value that signifies the encoding style to use.
    */
   public String getEncodingStyle()
   {
      return encodingStyle;
   }

   /**
    * Sets the MeasurementTarget URL used to make a SOAP invocation for this operation
    *
    * @param   value    The target URL
    */
   public void setTargetURL(String value)
   {
      targetURL = value;
   }

   /**
    * Gets the MeasurementTarget URL used to make a SOAP invocation for this operation
    *
    * @return The target URL is returned
    */
   public String getTargetURL()
   {
      return targetURL;
   }

   /**
    * Sets the namespace URI used for this operation
    *
    * @param   value    The namespace URI to use
    */
   public void setNamespaceURI(String value)
   {
      namespaceURI = value;
   }

   /**
    * Gets the namespace URI used for this
    *
    * @return The namespace URI of the target object
    */
   public String getNamespaceURI()
   {
      return namespaceURI;
   }

   /**
    * Sets the MeasurementTarget Object's URI used to make an invocation
    *
    * @param   value    The URI of the target object
    */
   public void setTargetObjectURI(String value)
   {
      targetObjectURI = value;
   }

   /**
    * Gets the MeasurementTarget Object's URI
    *
    * @return The URI of the target object
    */
   public String getTargetObjectURI()
   {
      return targetObjectURI;
   }

   /**
    * Sets the name of the target method to call
    *
    * @param   value    The name of the method to call
    */
   public void setTargetMethodName(String value)
   {
      targetMethodName = value;
   }

   /**
    * Gets the name of the target method to call
    *
    * @return  The name of the method to call
    */
   public String getTargetMethodName()
   {
      return targetMethodName;
   }

   /**
    * Sets the value of the target's input message name
    *
    * @param   value    The name of input message
    */
   public void setInputMessageName(String value)
   {
      inputMessageName = value;
   }

   /**
    * Gets the value of the target's input message name
    *
    * @return  The name of the input message is returned
    */
   public String getInputMessageName()
   {
      return inputMessageName;
   }

   /**
    * Sets the value of the target's output message name
    *
    * @param   value    The name of the output message
    */
   public void setOutputMessageName(String value)
   {
      outputMessageName = value;
   }

   /**
    * Gets the value of the target method's output message name
    *
    * @return  The name of the output message is returned
    */
   public String getOutputMessageName()
   {
      return outputMessageName;
   }

   /**
    * Sets the value of the target's input message
    *
    * @param   value    The SOAP input message
    */
   public void setInputMessageText(String value)
   {
      inputMessageText = value;
   }

   /**
    * Gets the value of the target's input message
    *
    * @return  The input message is returned
    */
   public String getInputMessageText()
   {
      return inputMessageText;
   }

   /**
    * Sets the value of the target method's Output message
    *
    * @param   value    The output message
    */
   public void setOutputMessageText(String value)
   {
      outputMessageText = value;
   }

   /**
    * Gets the value of the target method's Output message
    *
    * @return  The Output message is returned
    */
   public String getOutputMessageText()
   {
      return outputMessageText;
   }

   /**
    * Sets the value for the SOAP Action URI used to make a SOAP invocation
    *
    * @param   value    The SOAP Action URI value for the SOAP invocation
    */
   public void setSoapActionURI(String value)
   {
      soapActionURI = value;
   }

   /**
    * Gets the value for the SOAP Action URI used to make a SOAP invocation
    *
    * @return The SOAP Action URI value for the SOAP invocation is returned.
    */
   public String getSoapActionURI()
   {
      return soapActionURI;
   }

   /**
    * Sets the encoding document/literal vs. rpc/encoded
    *
    * @return value A string value "document" or "rpc" should be passed.
    */
   public void setStyle(String value)
   {
      style = value;
   }

   /**
    * Returns the style "document" or "rpc"
    *
    * @return The style type is returned
    */
   public String getStyle()
   {
      return style;
   }

   /**
    * Override toString to return a name for the operation
    *
    * @return The name of the operation is returned
    */
   public String toString()
   {
      return getTargetMethodName();
   }
}
