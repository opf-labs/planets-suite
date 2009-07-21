/*== Content_1.java ====================================================
Content of a PLANETS DigitalObject_1.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : Content_1 represents the "content" of a DigitalObject_1.
              It can either be given by value (byte stream) or by 
              reference (URL).
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 09, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.datatypes;

import java.lang.IllegalArgumentException;
import java.net.URL;
import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

import eu.planets_project.services.migrate.Migrate_1;

/*===================================================================*/
/** Content for digital objects is given either by reference (URL) 
 * or by value (byte stream).
 * This object is a simple JAVA bean which helps keeping its 
 * marshalling (and thus the WSDL) simple.
 * 
 * Create content by reference or value:
 * Instantiate the class and either set reference or value.
 * If neither is set, then an "empty" value is assumed.
 *  
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(namespace=Migrate_1.sNS)
public final class Content_1
{
  /** value */ 
  @XmlElement(name="dhValue")
  private @XmlMimeType(value="application/octet-stream") DataHandler m_dhValue;
  public DataHandler getValue() { return m_dhValue; }
  private void setValue(DataHandler dhValue) { m_dhValue = dhValue; }
  /** reference */
  @XmlElement(name="urlReference")
  private URL m_urlReference;
  public URL getReference() { return m_urlReference; }
  private void setReference(URL urlReference) { m_urlReference = urlReference; }
  
  /* make constructor inaccessible */
  private Content_1() {}
  
  /** factory */
  public static Content_1 getInstance(DataHandler dhValue, URL urlReference)
  {
    if ((urlReference != null) && (dhValue != null))
      throw new IllegalArgumentException("Content cannot be both by reference and by value!");
    Content_1 c = new Content_1();
    c.setValue(dhValue);
    c.setReference(urlReference);
    return c;
  } /* getInstance */
  
} /* class Content_1 */
