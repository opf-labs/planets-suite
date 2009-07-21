/*== MigrateResult_1.java ==============================================
PLANETS Migrate Result.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : MigrateResult consists of a DigitalObject resulting
              from the migration and a ServiceReport reporting
              about the success of the migration.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 07, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.migrate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import eu.planets_project.services.datatypes.DigitalObject_1;
import eu.planets_project.services.datatypes.ServiceReport_1;

/*===================================================================*/
/** the migrate result consists of a service report (stdout, stderr, 
 * return code) and a returned DigitalObject.     
 *  
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace=Migrate_1.sNS)
public class MigrateResult_1 
{
  /** response object */
  @XmlElement(name="doResponse")
  private DigitalObject_1 m_doResponse = null;
  public DigitalObject_1 getDigitalObject() { return m_doResponse; }
  private void setDigitalObject(DigitalObject_1 doResponse) { m_doResponse = doResponse; }
  
  /** service report */
  @XmlElement(name="sr")
  private ServiceReport_1 m_sr = null;
  public ServiceReport_1 getServiceReport() { return m_sr; }
  private void setServiceReport(ServiceReport_1 sr) { m_sr = sr; }
  
  /* make constructor inaccessible */
  private MigrateResult_1() {}

  /** factory */
  public static MigrateResult_1 getInstance(DigitalObject_1 doResponse, ServiceReport_1 sr)
  {
    MigrateResult_1 mr = new MigrateResult_1();
    mr.setDigitalObject(doResponse);
    mr.setServiceReport(sr);
    return mr;
  } /* getInstance */
  
} /* class MigrateResult_1 */
