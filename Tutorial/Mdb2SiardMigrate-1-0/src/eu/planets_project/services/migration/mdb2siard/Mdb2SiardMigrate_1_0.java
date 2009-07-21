/*== Mdb2SiardMigrate_1_0.java =========================================
PLANETS migration service implementation migrating MDB files to SIARD files.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : Mdb2SiardMigrate_1_0 extends GenericMigrate_1_0 and executes 
              the command-line tool convmdb turning the request object
              into a result object.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 07, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.migration.mdb2siard;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Logger;

import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import eu.planets_project.services.migrate.Migrate_1;
import eu.planets_project.services.migration.generic.GenericMigrate_1_0;

/*===================================================================*/
/** this class extends GenericMigrate_1_0 and executes the command-line
 * tool convmdb turning the request object into a result object.
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
@WebService(
    endpointInterface = "eu.planets_project.services.migrate.Migrate_1",
    portName=Migrate_1.sNAME+"-"+Migrate_1.sVERSION,
    targetNamespace=Migrate_1.sNS,
    serviceName=Mdb2SiardMigrate_1_0.sNAME + "-" + Mdb2SiardMigrate_1_0.sVERSION)
  @MTOM(enabled=true, threshold=Migrate_1.iSTREAM_BUFFER_SIZE)
public class Mdb2SiardMigrate_1_0
  extends GenericMigrate_1_0 /* and therefore implements Migrate_1 */
{
  private Logger m_log = Logger.getLogger(Mdb2SiardMigrate_1_0.class.getName());
  public static final String sNS = "http://mdb2siard.migration.services.planets_project.eu/";
  public static final String sNAME = "Mdb2SiardMigrate";
  public static final String sVERSION = Migrate_1.sVERSION + "-0";

  private static final String sCOMMAND_TEMPLATE = "convmdb.cmd /dsn:{0} \"{1}\" \"{2}\"";
  @Override
  protected String getCommandTemplate() { return sCOMMAND_TEMPLATE; }
  private static final long lMS_TIMEOUT = 3600000; /* 1 hour in ms */
  @Override
  protected long getMsTimeout() { return lMS_TIMEOUT; }
  private static final String sINPUT_EXTENSION = ".mdb";
  @Override
  protected String getInputExtension() { return sINPUT_EXTENSION; }
  private static final String sOUTPUT_EXTENSION = ".siard";
  @Override
  protected String getOutputExtension() { return sOUTPUT_EXTENSION; }

  /*--------------------------------------------------------------------*/
  /** creates a generic migrate command from resources, input and output file.
   * @param fileInput input file.
   * @param fileOutput output file.
   * @return command line to be executed.
   */
  @Override
  protected String createCommand(File fileInput, File fileOutput)
    throws IOException
  {
    /* compute DSN from input file name */
    String sDsn = fileInput.getName();
    sDsn = sDsn.substring(0,sDsn.length()-getInputExtension().length());
    m_log.info("DSN: "+sDsn);
    String sCommand = MessageFormat.format(sCOMMAND_TEMPLATE, 
      new Object[] {
        sDsn,
        fileInput.getAbsolutePath(),
        fileOutput.getAbsolutePath()}
      );
    return sCommand;
  } /* createCommand */
  
} /* class Mdb2SiardMigrate_1_0 */
