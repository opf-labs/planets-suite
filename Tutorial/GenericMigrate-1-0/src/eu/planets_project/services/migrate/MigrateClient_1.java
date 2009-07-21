/*== Migrate_Client.java ===============================================
A dynamic client for the Migrate_1 interface.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : MigrateClient_1 implements a dynamic Migrate_0 client.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 08, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.migrate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPBinding;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.StreamingDataHandler;

import ch.enterag.utils.console.CommandLine;
import eu.planets_project.services.datatypes.Content_1;
import eu.planets_project.services.datatypes.DigitalObject_1;
import eu.planets_project.services.datatypes.ServiceReport_1;
import eu.planets_project.services.migration.generic.GenericMigrate_0_0;
import eu.planets_project.services.migration.generic.GenericMigrate_1_0;

/*===================================================================*/
/** This class has an executable main method which calls the 
 * migrate service at some concrete TCP/IP port.
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
public class MigrateClient_1
{
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_WARNING = 4;
  private static final int iRETURN_ERROR = 8;
  private static final String sDEFAULT_INPUT = "../testfiles/testin.dat";
  private static final String sDEFAULT_OUTPUT = "../testfiles/testout.dat";
  private static final String sDEFAULT_HOST = "localhost";
  private static final String sDEFAULT_PORT = "8080";
  private static final String sDEFAULT_SERVICE = GenericMigrate_1_0.sNAME + "-" + GenericMigrate_1_0.sVERSION;
  private static final String sWSDL_QUERY = "?wsdl";

  private int m_iReturn = iRETURN_OK;
  
  /*--------------------------------------------------------------------*/
  /** display usage information
   */
  private void displayHelp()
  {
    System.out.println("Usage:");
    System.out.println("java " +
      "-Djava.endorsed.dirs=../lib/endorsed -cp <classpath>\n" +
      "-Djava.util.logging.config.file=../etc/logging.properties\"\n" +
      "eu.planets_project.services.migrate.MigrateClient_1 [-h] [-n <host>] [-s <service>] [<input> [<output>]]");
    System.out.println("with");
    System.out.println("<classpath> must point to the class to be executed");
    System.out.println("            (e.g. build/classes");
    System.out.println("<service>   service name");
    System.out.println("            default: "+sDEFAULT_SERVICE);
    System.out.println("<server>    host where Web Service is published");
    System.out.println("            default: "+sDEFAULT_HOST+":"+sDEFAULT_PORT);
    System.out.println("<input>     input file to be migrated");
    System.out.println("            default: \""+sDEFAULT_INPUT+"\"");
    System.out.println("<output>    output file to be migrated");
    System.out.println("            default: \""+sDEFAULT_OUTPUT+"\"");
  } /* displayHelp */

  /*--------------------------------------------------------------------*/
  /** getMigrate_1Proxy returns a proxy class implementing the Migrate_1
   * interface and representing the Web Service.
   * @param sWsdlUrl URL of WSDL.
   * @param sService service name.
   * @return output text.
   */
  private Migrate_1 getMigrate_1Proxy(String sWsdlUrl, String sService)
    throws MalformedURLException
  {
    URL urlWsdl = new URL(sWsdlUrl);
    Migrate_1 migProxy = null;
    /* service */
    Service service = Service.create(urlWsdl, new QName(Migrate_1.sNS,sService));
    /* port corresponds to the interface */
    migProxy = (Migrate_1)service.getPort(Migrate_1.class);
    System.out.printf("Created proxy class for service %s\n",service.getServiceName());
    BindingProvider bp = (BindingProvider)migProxy;
    SOAPBinding sb = (SOAPBinding)bp.getBinding();
    /* enable MTOM for the interface */
    sb.setMTOMEnabled(true);
    if (sb.isMTOMEnabled())
      System.out.printf("MTOM enabled\n\n");
    /* enable streaming for the interface */
    Map<String,Object> mapContext = bp.getRequestContext();
    mapContext.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, new Integer(Migrate_1.iSTREAM_BUFFER_SIZE));
    return migProxy;
  } /* getMigrate_1Proxy */
  
  /*--------------------------------------------------------------------*/
  /** getMigrate_0Proxy returns a proxy class implementing the Migrate_0
   * interface and representing the Web Service.
   * @param sWsdlUrl URL of WSDL.
   * @param sService service name.
   * @return output text.
   */
  private Migrate_0 getMigrate_0Proxy(String sWsdlUrl, String sService)
    throws MalformedURLException
  {
    URL urlWsdl = new URL(sWsdlUrl);
    Migrate_0 migProxy = null;
    /* service */
    Service service = Service.create(urlWsdl, new QName(GenericMigrate_0_0.sNS,sService));
    /* port corresponds to the interface */
    migProxy = (Migrate_0)service.getPort(Migrate_0.class);
    System.out.printf("Created proxy class for service %s\n",service.getServiceName());
    return migProxy;
  } /* getMigrate_0Proxy */
  
  /*--------------------------------------------------------------------*/
  /** migrate using a Migrate_1 service with fallback to Migrate_0 
   * @param fileInput input file.
   * @param fileOutput output file.
   * @param sWsdlUrl URL of WSDL.
   * @param sService service name.
   * @return output text.
   */
  private ServiceReport_1 migrate(
    File fileInput, 
    File fileOutput, 
    String sWsdlUrl, 
    String sService)
    throws MalformedURLException, FileNotFoundException, IOException
  {
    ServiceReport_1 sr = null;
    /* determine version of service */
    int iEnd = sService.lastIndexOf("-");
    int iStart = sService.substring(0, iEnd).lastIndexOf("-")+1;
    String sInterfaceVersion = sService.substring(iStart,iEnd);
    if (sInterfaceVersion.equals("1"))
    {
      Migrate_1 mig = getMigrate_1Proxy(sWsdlUrl, sService);
      /* connect input file to data handler */
      System.out.println("Sending "+String.valueOf(fileInput.length())+" bytes.");
      DataHandler dhRequest = new DataHandler(new FileDataSource(fileInput));
      Content_1 content = Content_1.getInstance(dhRequest, null);
      DigitalObject_1 doRequest = DigitalObject_1.getInstance(
        null, null, content, null);
      /* run service */
      System.out.println("Calling service ...");
      MigrateResult_1 mr = null;
      try { mr = mig.migrate(doRequest, null, null); }
      catch(WebServiceException we)
      {
        /* try again without chunking */
        BindingProvider bp = (BindingProvider)mig;
        Map<String,Object> mapContext = bp.getRequestContext();
        mapContext.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, null);
        try 
        {
          System.out.println("... without support for streaming large files ...");
          mr = mig.migrate(doRequest, null, null); 
        }
        catch(Exception e)
        {
          System.out.println(e.getClass().getName()+": "+e.getMessage()+" ("+e.getCause()+")");
          e.printStackTrace();
        }
      }
      if (mr != null)
      {
        System.out.println("... result received.");
        /* write output file from data handler */
        DataHandler dhOutput = mr.getDigitalObject().getContent().getValue();
        if (dhOutput instanceof StreamingDataHandler)
        {
          System.out.println("Streaming to "+fileOutput.getAbsolutePath());
          StreamingDataHandler sdhResponse = (StreamingDataHandler)mr.getDigitalObject().getContent().getValue();
          sdhResponse.moveTo(fileOutput);
          sdhResponse.close();
        }
        else
        {
          System.out.println("Copying to "+fileOutput.getAbsolutePath());
          FileOutputStream fos = new FileOutputStream(fileOutput);
          InputStream is = dhOutput.getInputStream();
          byte[] buf = new byte[Migrate_1.iSTREAM_BUFFER_SIZE];
          for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
            fos.write(buf,0,iRead);
          is.close();
          fos.close();
        }
        System.out.println("Received "+String.valueOf(fileOutput.length())+" bytes.");
        sr = mr.getServiceReport();
      }
    }
    else if (sInterfaceVersion.equals("0"))
    {
      Migrate_0 mig = getMigrate_0Proxy(sWsdlUrl, sService);
      /* turn input file into a string */
      Reader rdr = new InputStreamReader(new FileInputStream(fileInput));
      StringBuilder sbInput = new StringBuilder();
      for (int iRead = rdr.read(); iRead != -1; iRead = rdr.read())
        sbInput.append((char)iRead);
      rdr.close();
      /* run service */
      String sOutput = mig.migrate(sbInput.toString());
      /* write resulting string to output file */
      Writer wtr = new OutputStreamWriter(new FileOutputStream(fileOutput));
      wtr.write(sOutput);
      wtr.close();
      /* generate empty service report */
      sr = ServiceReport_1.getInstance("Migrate_0 interface used.");
    }
    else
      throw new IllegalArgumentException(
        "Service interface must be Migrate_0 or Migrate_1!");
    return sr;
  } /* migrate */
  
  /*--------------------------------------------------------------------*/
  /** constructor 
   * @param args command-line arguments.
   */
  private MigrateClient_1(CommandLine cl)
  {
    if (cl.getOption("h") != null)
    {
      displayHelp();
      m_iReturn = iRETURN_WARNING;
    }
    else
    {
      try
      {
        String sService = cl.getOption("s");
        if (sService == null)
          sService = sDEFAULT_SERVICE;
        String sWsdlUrl = cl.getOption("n");
        if (sWsdlUrl == null)
          sWsdlUrl = sDEFAULT_HOST+":"+sDEFAULT_PORT;
        if (!sWsdlUrl.startsWith("http://"))
          sWsdlUrl = "http://" + sWsdlUrl;
        if (sWsdlUrl.endsWith("/"))
          sWsdlUrl = sWsdlUrl.substring(0,sWsdlUrl.length()-1);
        if (sWsdlUrl.indexOf(':') < 0)
          sWsdlUrl = sWsdlUrl+":"+sDEFAULT_PORT;
        sWsdlUrl = sWsdlUrl + "/" + sService+sWSDL_QUERY;
        String sInput = sDEFAULT_INPUT;
        if (cl.getArguments() > 0)
          sInput = cl.getArgument(0);
        String sOutput = sDEFAULT_OUTPUT;
        if (cl.getArguments() > 1)
          sOutput = cl.getArgument(1);
        File fileInput = new File(sInput);
        File fileOutput = new File(sOutput);
        if (!fileInput.exists())
        {
          System.err.println("File "+fileInput.getAbsolutePath()+" does not exist!");
          displayHelp();
        }
        else
        {
          System.out.println("Input file  : "+fileInput.getAbsolutePath());
          System.out.println("Output file : "+fileOutput.getAbsolutePath());
          System.out.println("WSDL address: "+sWsdlUrl);
          System.out.println("Service name: "+sService);
          ServiceReport_1 sr = migrate(fileInput,fileOutput,sWsdlUrl,sService);
          if (sr != null)
          {
            if (sr.getInfo() != null)
              System.out.println("stdout:\n"+sr.getInfo());
            if (sr.getError() != null)
              System.out.println("stderr:\n"+sr.getError());
            System.out.println("Return code : "+sr.getState());
          }
        }
      }
      catch (Exception e)
      {
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        m_iReturn = iRETURN_ERROR;
      }
    }
  } /* constructor MigrateClient_1 */
  
  /*--------------------------------------------------------------------*/
  /** main class expects arguments echotext and WSDL URL. 
   * @param args none for default ("Hello JAX-WS" and 
   * localhost:8080/GenericMigrate-0-0/service?wsdl), -h for help,
   * or echotext and WSDL URL.
   */
  public static void main(String[] args)
  {
    MigrateClient_1 mc = new MigrateClient_1(new CommandLine(args));
    System.exit(mc.m_iReturn);
  } /* main */

} /* class MigrateClient_1 */
