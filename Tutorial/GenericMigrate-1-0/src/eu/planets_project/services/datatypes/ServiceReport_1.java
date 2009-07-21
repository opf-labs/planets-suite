/*== ServiceReport_1.java ==============================================
Report about a PLANETS service's execution.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : ServiceReport_1 represents the report type of a PLANETS 
              service.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 09, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.datatypes;

import java.lang.IllegalArgumentException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import eu.planets_project.services.migrate.Migrate_1;

/*===================================================================*/
/** the service report contains information about a service's     
 * success and execution. 
 *  
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace=Migrate_1.sNS)
public class ServiceReport_1
{
  /** success state */
  public static final int STATE_SUCCESS = 0;
  /** error state */
  public static final int STATE_ERROR = 1;
  
  /** state of service invocation */
  @XmlElement(name="iState")
  private int m_iState = STATE_SUCCESS;
  public int getState() { return m_iState; }
  private void setState(int iState) { m_iState = iState; }
 
  /** info */
  @XmlElement(name="sInfo")
  private String m_sInfo = null;
  public String getInfo() { return m_sInfo; }
  private void setInfo(String sInfo) { m_sInfo = sInfo; }
  
  /** error */
  @XmlElement(name="sError")
  private String m_sError = null;
  public String getError() { return m_sError; }
  private void setError(String sError) { m_sError = sError; }
  
  /* make constructor inaccessible */
  private ServiceReport_1() {}
  
  /** factory successful with stdout */
  public static ServiceReport_1 getInstance(String sInfo)
  {
    ServiceReport_1 sr = new ServiceReport_1();
    sr.setInfo(sInfo);
    sr.setState(STATE_SUCCESS);
    return sr;
  } /* getInstance */

  /** factory successful with stdout and stderr */
  public static ServiceReport_1 getInstance(String sInfo, String sError)
  {
    ServiceReport_1 sr = new ServiceReport_1();
    sr.setInfo(sInfo);
    sr.setError(sError);
    sr.setState(STATE_SUCCESS);
    return sr;
  } /* getInstance */

  /** factory error with stderr */
  public static ServiceReport_1 getInstance(String sError, int iState)
  {
    if (iState == STATE_SUCCESS)
      throw new IllegalArgumentException("Error state cannot be set to zero!");
    ServiceReport_1 sr = new ServiceReport_1();
    sr.setError(sError);
    sr.setState(iState);
    return sr;
  } /* getInstance */

  /** factory error with stdout and stderr */
  public static ServiceReport_1 getInstance(String sInfo, String sError, int iState)
  {
    if (iState == STATE_SUCCESS)
      throw new IllegalArgumentException("Error state cannot be set to zero!");
    ServiceReport_1 sr = new ServiceReport_1();
    sr.setError(sInfo);
    sr.setError(sError);
    sr.setState(iState);
    return sr;
  } /* getInstance */

} /* class ServiceReport */
