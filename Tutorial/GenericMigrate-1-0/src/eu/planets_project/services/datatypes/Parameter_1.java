/*== Parameter_1.java ==================================================
Parameter of a PLANETS service.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : Parameter_1 represents the parameter type of a PLANETS 
              service.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 09, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.datatypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import eu.planets_project.services.migrate.Migrate_1;

/*===================================================================*/
/** A parameter of a service is essentially a name/value pair.   
 * This object is a simple JAVA bean which helps keeping its 
 * marshalling (and thus the WSDL) simple.
 *  
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(namespace=Migrate_1.sNS)
public final class Parameter_1
{
  /**
   * A name for the parameter.  Must be uniquely meaningful to the service, 
   * but is not expected to carry any meaning outside the service.
   */
  @XmlElement(name="sName")
  private String m_sName = null;
  public String getName() { return m_sName; }
  private void setName(String sName) { m_sName = sName; }

  /**
   * The value for this parameter.  Should be set to the default by the 
   * service when parameter discovery is happening.
   */
  @XmlElement(name="sValue")
  private String m_sValue = null;
  public String getValue() { return m_sValue; }
  private void setValue(String sValue) { m_sValue = sValue; }
    
  /* make constructor inaccessible */
  private Parameter_1() {}
  
  /** factory 
   * @param sName parameter name must not be null.
   * @param sValue parameter value.
   */
  public static Parameter_1 getInstance(String sName, String sValue)
  {
    Parameter_1 p = new Parameter_1();
    p.setName(sName);
    p.setValue(sValue);
    return p;
  } /* getInstance */
  
} /* class Parameter_1 */
