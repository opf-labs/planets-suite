/*== Metadata_1.java ==================================================
Metadata of a PLANETS object.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : Metadata_1 represents the metadata type of a PLANETS 
              object.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 09, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.datatypes;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import eu.planets_project.services.migrate.Migrate_1;

/*===================================================================*/
/** metadata is essentially a type/content pair.   
 * This object is a simple JAVA bean which helps keeping its 
 * marshalling (and thus the WSDL) simple.
 *  
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(namespace=Migrate_1.sNS)
public final class Metadata_1 
{
  /** type */
  @XmlElement(name="uriType")
  private URI m_uriType;
  public URI getType() { return m_uriType; }
  private void setType(URI uriType) { m_uriType = uriType; }
  
  /** content */
  @XmlElement(name="sContent")
  private String m_sContent;
  public String getContent() { return m_sContent; }
  private void setContent(String sContent) { m_sContent = sContent; }

  /* make constructor inaccessible */
  private Metadata_1() {}
  
  /** factory */
  public static Metadata_1 getInstance(URI uriType, String sContent)
  {
    Metadata_1 m = new Metadata_1();
    m.setType(uriType);
    m.setContent(sContent);
    return m;
  } /* getInstance */

} /* class Metadata_1 */
