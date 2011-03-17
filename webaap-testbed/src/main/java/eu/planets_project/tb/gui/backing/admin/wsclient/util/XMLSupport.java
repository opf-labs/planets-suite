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

import java.io.IOException;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.Reader;

import org.exolab.castor.xml.schema.*;
import org.exolab.castor.xml.schema.reader.SchemaReader;
import org.exolab.castor.xml.schema.writer.SchemaWriter;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


/**
 * XML support and utilities
 *
 * @author Markus Reis, ARC
 */

public class XMLSupport
{
   private XMLSupport()
   {
   }

   /**
    * Returns a string representation of the given jdom document.
    *
    * @param   doc   The jdom document to be converted into a string.
    *
    * @return  Ths string representation of the given jdom document.
    */
   public static String outputString(Document doc)
   {
      XMLOutputter xmlWriter = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());

      return xmlWriter.outputString(doc);
   }

   /**
    * Returns a string representation of the given jdom element.
    *
    * @param   elem     The jdom element to be converted into a string.
    *
    * @return  The string representation of the given jdom element.
    */
   public static String outputString(Element elem)
   {
      XMLOutputter xmlWriter = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());

      return xmlWriter.outputString(elem);
   }

   /**
    * It reads the given xml returns the jdom document.
    *
    * @param   xml   The xml to read.
    *
    * @return  The jdom document created from the xml.
    *
    * @throws  JDOMException     If the parsing failed.
    */
   public static Document readXML(String xml) throws JDOMException
   {
      return readXML(new StringReader(xml));
   }

   /**
    * It reads the given xml reader and returns the jdom document.
    *
    * @param   reader   The xml reader to read.
    *
    * @return  The jdom document created from the xml reader.
    *
    * @throws  JDOMException     If the parsing failed.
    */
   public static Document readXML(Reader reader) throws JDOMException
   {
      SAXBuilder xmlBuilder = new SAXBuilder(false);

      Document doc;
      try { 
    	  doc = xmlBuilder.build(reader);
      } catch (Exception e) { throw new JDOMException("error building XML", e);}	  

      return doc;
   }

   /**
    * It reads the given reader and returns the castor schema.
    *
    * @param   reader   The reader to read.
    *
    * @return  The castor schema created from the reader.
    *
    * @throws  IOException    If the schema could not be read from the reader.
    */
   public static Schema readSchema(Reader reader) throws IOException
   {
      // create the sax input source
      InputSource inputSource = new InputSource(reader);

      // create the schema reader
      SchemaReader schemaReader = new SchemaReader(inputSource);
      schemaReader.setValidation(false);

      // read the schema from the source
      Schema schema = schemaReader.read();

      return schema;
   }

   /**
    * Converts a castor schema into a jdom element.
    *
    * @param   schema   The castor schema to be converted.
    *
    * @return  The jdom element representing the schema.
    *
    * @throws  SAXException   If the castor schema could not be written out.
    * @throws  IOException    If the castor schema could not be written out.
    * @throws  JDOMException  If the output of the castor schema could not be converted into a jdom element.
    */
   public static Element convertSchemaToElement(Schema schema)
           throws SAXException, IOException, JDOMException
   {
      // get the string content of the schema
      String content = outputString(schema);

      // check for null content value
      if (content != null)
      {
         // create a document out of it
         Document doc = readXML(new StringReader(content));

         // return the root of the document
         return doc.getRootElement();
      }

      // return null otherwise
      return null;
   }

   /**
    * Converts the jdom element into a castor schema.
    *
    * @param   element  The jdom element to be converted into a castor schema.
    *
    * @return  The castor schema corresponding to the element.
    *
    * @throws  IOException    If the jdom element could not be written out.
    */
   public static Schema convertElementToSchema(Element element) throws IOException
   {
      // get the string content of the element
      String content = outputString(element);

      // check for null value
      if (content != null)
      {
         // create a schema from the string content
         return readSchema(new StringReader(content));
      }

      // otherwise return null
      return null;
   }

   /**
    * Returns a string representation of the given castor schema.
    *
    * @param   schema   The castor schema to be converted into a string.
    *
    * @return  The string representation of the given castor schema.
    *
    * @throws  IOException    If the schema could not be written out.
    * @throws  SAXException   If the schema could not be written out.
    */
   public static String outputString(Schema schema) throws IOException, SAXException
   {
      // create a string writer
      StringWriter writer = new StringWriter();

      // create the schema writer
      SchemaWriter schemaWriter = new SchemaWriter(writer);

      // write the schema into the source
      schemaWriter.write(schema);

      // return the content of the writer
      return writer.toString();
   }
}