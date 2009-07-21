/*== Migrate_Client.java ===============================================
A dynamic client for the Migrate_0 interface.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : MigrateClient_0 implements a dynamic Migrate_0 client.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 08, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.migrate;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import eu.planets_project.services.migration.generic.GenericMigrate_0_0;

/*===================================================================*/
/** This class has an executable main method which calls the 
 * migrate service at some concrete TCP/IP port.
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
public class MigrateClient_0
{
  private static final int iRETURN_OK = 0;
  private static final int iRETURN_WARNING = 4;
  private static final int iRETURN_ERROR = 8;
  private static final String sDEFAULT_TEXT = "Hello JAX-WS!";
  private static final String sDEFAULT_HOST = "localhost";
  private static final String sDEFAULT_PORT = "8080";
  private static final String sDEFAULT_SERVICE = "GenericMigrate-0-0";
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
      "eu.planets_project.services.migrate.MigrateClient_0 [<text> [<host> [<service>]]");
    System.out.println("with");
    System.out.println("<classpath> must point to the class to be executed");
    System.out.println("            (e.g. build/classes");
    System.out.println("<text>      text to be migrated");
    System.out.println("            default: \""+sDEFAULT_TEXT+"\"");
    System.out.println("<host>      host where Web Service is published");
    System.out.println("            default: "+sDEFAULT_HOST+":"+sDEFAULT_PORT);
    System.out.println("<service>   service name");
    System.out.println("            default: "+sDEFAULT_SERVICE);
  } /* displayHelp */

  /*--------------------------------------------------------------------*/
  /** getMigrateProxy returns a proxy class implementing the Migrate_0
   * interface and representing the Web Service.
   * @param sWsdlUrl URL of WSDL.
   * @param sService service name.
   * @return output text.
   */
  private Migrate_0 getMigrateProxy(String sWsdlUrl, String sService)
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
  } /* getMigrateProxy */
  
  /*--------------------------------------------------------------------*/
  /** migrate using a Migrate_0 service 
   * @param sInput input text.
   * @param sWsdlUrl URL of WSDL.
   * @param sService service name.
   * @return output text.
   */
  private String migrate(String sInput, String sWsdlUrl, String sService)
    throws MalformedURLException
  {
    Migrate_0 mig = getMigrateProxy(sWsdlUrl, sService);
    String sOutput = mig.migrate(sInput);
    return sOutput;
  } /* migrate */
  
  /*--------------------------------------------------------------------*/
  /** constructor 
   * @param args command-line arguments.
   */
  private MigrateClient_0(String[] args)
  {
    if ((args.length > 0) && (args[0] == "-h"))
    {
      displayHelp();
      m_iReturn = iRETURN_WARNING;
    }
    else
    {
      try
      {
        String sService = sDEFAULT_SERVICE;
        if (args.length > 2)
          sService = args[2];
        String sWsdlUrl = sDEFAULT_HOST+":"+sDEFAULT_PORT;
        if (args.length > 1)
          sWsdlUrl = args[1];
        if (!sWsdlUrl.startsWith("http://"))
          sWsdlUrl = "http://" + sWsdlUrl;
        if (sWsdlUrl.endsWith("/"))
          sWsdlUrl = sWsdlUrl.substring(0,sWsdlUrl.length()-1);
        if (sWsdlUrl.indexOf(':') < 0)
          sWsdlUrl = sWsdlUrl+":"+sDEFAULT_PORT;
        sWsdlUrl = sWsdlUrl + "/" + sService+sWSDL_QUERY;
        String sText = sDEFAULT_TEXT;
        if (args.length > 0)
          sText = args[0];
        System.out.println("Input text  : "+sText);
        System.out.println("WSDL address: "+sWsdlUrl);
        System.out.println("Service name: "+sService);
        sText = migrate(sText,sWsdlUrl,sService);
        System.out.println("Output text : "+sText);
      }
      catch (Exception e)
      {
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        m_iReturn = iRETURN_ERROR;
      }
    }
  } /* constructor MigrateClient_0 */
  
  /*--------------------------------------------------------------------*/
  /** main class expects arguments echotext and WSDL URL. 
   * @param args none for default ("Hello JAX-WS" and 
   * localhost:8080/GenericMigrate-0-0/service?wsdl), -h for help,
   * or echotext and WSDL URL.
   */
  public static void main(String[] args)
  {
    MigrateClient_0 mc = new MigrateClient_0(args);
    System.exit(mc.m_iReturn);
  } /* main */

} /* class MigrateClient_0 */
