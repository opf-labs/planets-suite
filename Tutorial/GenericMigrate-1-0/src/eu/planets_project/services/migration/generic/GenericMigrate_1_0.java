/*== GenericMigrate_1_0.java ===========================================
Generic PLANETS migration service implementation.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : GenericMigrate_1_0 implements Migrate_1 and executes 
              a command-line tool turning the request object
              into a result object.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 07, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.migration.generic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;
import com.sun.xml.ws.developer.StreamingDataHandler;

import eu.planets_project.services.datatypes.Content_1;
import eu.planets_project.services.datatypes.DigitalObject_1;
import eu.planets_project.services.datatypes.Parameter_1;
import eu.planets_project.services.datatypes.ServiceReport_1;
import eu.planets_project.services.utils.TempFileDataSource;
import eu.planets_project.services.migrate.Migrate_1;
import eu.planets_project.services.migrate.MigrateResult_1;
import eu.planets_project.services.utils.CommandExec;

/*===================================================================*/
/** This class is the bean (SIB) that implements the Migrate 
 * interface (SEI).
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
@WebService(
  endpointInterface = "eu.planets_project.services.migrate.Migrate_1",
  portName=Migrate_1.sNAME + "-" + Migrate_1.sVERSION,
  targetNamespace=Migrate_1.sNS,
  serviceName=GenericMigrate_1_0.sNAME + "-" + GenericMigrate_1_0.sVERSION)
@MTOM(enabled=true, threshold=Migrate_1.iSTREAM_BUFFER_SIZE)
public class GenericMigrate_1_0 
  implements Migrate_1
{
  public static final String sNS = "http://generic.migration.services.planets-project.eu/";
  public static final String sNAME = "GenericMigrate";
  public static final String sVERSION = Migrate_1.sVERSION + "-0";
  
  private Logger m_log = Logger.getLogger(GenericMigrate_1_0.class.getName());
  private static final String sCOMMAND_TEMPLATE = "cmd /C copy /b \"{0}\" \"{1}\"";
  protected String getCommandTemplate() { return sCOMMAND_TEMPLATE; }
  private static final long lMS_TIMEOUT = 3600000; /* 1 hour in ms */
  protected long getMsTimeout() { return lMS_TIMEOUT; }
  private static final String sINPUT_EXTENSION = ".dat";
  protected String getInputExtension() { return sINPUT_EXTENSION; }
  private static final String sOUTPUT_EXTENSION = ".dat";
  protected String getOutputExtension() { return sOUTPUT_EXTENSION; }
  
  @Resource
  WebServiceContext wsc;

  /*--------------------------------------------------------------------*/
  /** creates a generic migrate command from resources, input and output file.
   * @param fileInput input file.
   * @param fileOutput output file.
   * @return command line to be executed.
   */
  protected String createCommand(File fileInput, File fileOutput)
    throws IOException
  {
    String sCommand = MessageFormat.format(sCOMMAND_TEMPLATE, new Object[] {
      fileInput.getAbsolutePath(),
      fileOutput.getAbsolutePath()});
    return sCommand;
  } /* createCommand */
  
  /*--------------------------------------------------------------------*/
  /** executes the given command, possibly terminating with a timeout.
   * @param sCommand command.
   * @param lMsTimeout timeout in milliseconds.
   * @return service report with exit code, stdout and stderr.
   * @throws IOException if an I/O error occurred,
   *         InterruptedException if process was interrupted
   *         or a timeout occurred.
   */
  private ServiceReport_1 executeCommand(String sCommand, long lMsTimeout)
    throws IOException, InterruptedException
  {
    ServiceReport_1 sr = null;
    CommandExec ce = new CommandExec();
    try
    {
      int iResult = ce.execute(sCommand, lMsTimeout);
      /* we make the assumption, that external commands return 0 on success */
      if (iResult == ServiceReport_1.STATE_SUCCESS)
        sr = ServiceReport_1.getInstance(ce.getOutput(), ce.getError());
      else
        sr = ServiceReport_1.getInstance(ce.getOutput(), ce.getError(), iResult);
    }
    catch(IOException ie) { m_log.severe(ie.getClass().getName()+": "+ie.getMessage()); }
    catch(InterruptedException ie) { m_log.severe(ie.getClass().getName()+": "+ie.getMessage()); }
    return sr;
  } /* executeCommand */
  
  /*--------------------------------------------------------------------*/
  /* (non-Javadoc)
   * @see eu.planets_project.services.migrate.Migrate_1#migrate(java.lang.String)
   * migrate implements a command-line from the resources.
   */
  public MigrateResult_1 migrate(
    DigitalObject_1 doRequest,
    URI uriFormatOutput, 
    List<Parameter_1>listParameters)
  {
    m_log.info("Classpath: "+System.getProperty("java.class.path"));
    MessageContext mc = wsc.getMessageContext();
    @SuppressWarnings("unchecked")
    Map<String,List> mapRequestHeaders = (Map<String,List>)mc.get(MessageContext.HTTP_REQUEST_HEADERS);
    for (Iterator<String> iterRequestHeader = mapRequestHeaders.keySet().iterator();
         iterRequestHeader.hasNext(); )
    {
      String sKey = (String)iterRequestHeader.next();
      @SuppressWarnings("unchecked")
      List<String> listValue = (List<String>)mapRequestHeaders.get(sKey);
      m_log.info(sKey+": "+listValue);
    }
    ServiceReport_1 sr = null;
    DigitalObject_1 doResponse = null;
    try
    {
      /* save content of doRequest as a temporary input file */
      File fileInput = File.createTempFile("mig", getInputExtension());
      /* make sure, input at least gets removed when the service is stopped */
      fileInput.deleteOnExit();
      /* redirect streaming data handler to this file */
      DataHandler dhInput = doRequest.getContent().getValue();
      if (dhInput instanceof StreamingDataHandler)
      {
        m_log.info("Streaming to "+fileInput.getAbsolutePath());
        StreamingDataHandler sdhInput = (StreamingDataHandler)dhInput;
        sdhInput.moveTo(fileInput);
        sdhInput.close();
      }
      else // appears to be superfluous
      {
        m_log.info("Copying to "+fileInput.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(fileInput);
        InputStream is = dhInput.getInputStream();
        byte[] buf = new byte[Migrate_1.iSTREAM_BUFFER_SIZE];
        for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
          fos.write(buf,0,iRead);
        is.close();
        fos.close();
      }
      m_log.info("Received "+String.valueOf(fileInput.length())+" bytes.");
      /* we ignore all parameters for now: Timeout might become a parameter later */
      File fileOutput = File.createTempFile("mig", getOutputExtension());
      /* create the command-line from the file names and the parameters */
      String sCommand = createCommand(fileInput, fileOutput);
      m_log.info("Command: "+sCommand);
      sr = executeCommand(sCommand,getMsTimeout());
      m_log.info("Migrated content to : "+fileOutput.getAbsolutePath());
      fileInput.delete();
      m_log.info("File "+fileInput.getAbsolutePath()+" deleted.");
      /* make sure, output at least gets removed when the service is stopped */
      fileOutput.deleteOnExit();
      m_log.info("Sending "+String.valueOf(fileOutput.length())+" bytes.");
      /* load content of doResponse from the temporary output file */
      DataHandler dhResponse = new DataHandler(new TempFileDataSource(fileOutput));
      Content_1 content = Content_1.getInstance(dhResponse, null);
      doResponse = DigitalObject_1.getInstance(null, uriFormatOutput, content, null);
    }
    catch(Exception e)
    {
      sr = ServiceReport_1.getInstance(
        e.getClass().getName()+": "+e.getMessage(),
        ServiceReport_1.STATE_ERROR);
    }
    m_log.info("returning result");
    return MigrateResult_1.getInstance(doResponse, sr);
  } /* migrate */

} /* GenericMigrate_1_0 */
