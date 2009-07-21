/*== GenericMigrate_0_0.java ===========================================
Minimal PLANETS migration service implementation.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : GenericMigrate_0_0 implements Migrate_0 and just
              echoes the input string to the output.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 07, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.migration.generic;

import javax.jws.WebService;

import eu.planets_project.services.migrate.Migrate_0;

/*===================================================================*/
/** This class is the bean (SIB) that implements the Migrate 
 * interface (SEI).
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
@WebService(
  endpointInterface = "eu.planets_project.services.migrate.Migrate_0",
  portName=Migrate_0.sNAME+"-"+Migrate_0.sVERSION,
  targetNamespace=GenericMigrate_0_0.sNS,
	serviceName=GenericMigrate_0_0.sNAME+"-" + GenericMigrate_0_0.sVERSION)
public class GenericMigrate_0_0 implements Migrate_0
{
  public static final String sNS = "http://generic.migration.services.planets_project.eu/";
  public static final String sNAME = "GenericMigrate";
  public static final String sVERSION = Migrate_0.sVERSION + "-0";
	
	/*--------------------------------------------------------------------*/
	/* (non-Javadoc)
	 * @see eu.planets_project.services.migrate.Migrate_0#migrate(java.lang.String)
	 * migrate just implements an "echo" service.
	 */
	public String migrate(String sOriginal)
	{
		return sOriginal;
	} /* migrate */

} /* GenericMigrate_0_0 */
