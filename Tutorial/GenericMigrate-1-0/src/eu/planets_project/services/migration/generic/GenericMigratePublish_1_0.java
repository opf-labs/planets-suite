/*== GenericMigratePublish_1_0.java ========================================
Publisher for the generic PLANETS migration service implementation,
supporting its minimal predecessor as well.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : This main method binds the service to a concrete
              TCP/IP port.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 13, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.migration.generic;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import javax.xml.ws.Endpoint;
import javax.xml.ws.soap.SOAPBinding;

import eu.planets_project.services.migration.generic.GenericMigrate_1_0;

/*===================================================================*/
/** This class has an executable main method to publish the service 
 * to a concrete TCP/IP port.
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
public class GenericMigratePublish_1_0
{
  private static final String sDEFAULT_HOST = "localhost";
  private static final String sDEFAULT_PORT = "8080";
  private static final String sPREVIOUS_SERVICE_PATH = GenericMigrate_0_0.sNAME + "_" + GenericMigrate_1_0.sVERSION;
  private static final String sSERVICE_PATH = GenericMigrate_1_0.sNAME + "-" + GenericMigrate_1_0.sVERSION;

  /*--------------------------------------------------------------------*/
  /** display usage information
   */
  private static void displayHelp()
  {
    System.out.println("Publishes service " + sSERVICE_PATH + 
      " with fallback "+sPREVIOUS_SERVICE_PATH);
    System.out.println("Usage:");
    System.out.println("java " +
      "-Djava.endorsed.dirs=../lib/endorsed -cp <classpath>\n" +
      "-Djava.util.logging.config.file=\"../etc/logging.properties\"\n" +
      "eu.planets_project.services.migrate.GenericMigratePublish [<host>]");
    System.out.println("with");
    System.out.println("<classpath> must point to the class to be executed");
    System.out.println("            (e.g. build/classes");
    System.out.println("<host>      host where Web Service is to be published");
    System.out.println("            default: "+sDEFAULT_HOST+":"+sDEFAULT_PORT);
  } /* displayHelp */

  /*--------------------------------------------------------------------*/
  /** main expects host parameter where service is to be published.
   * @param args none for default (localhost:8080), -h for help, or
   *             host or hst:port 
   */
  public static void main(String[] args)
  {
    if ((args.length > 0) && (args[0] == "-h"))
      displayHelp();
    else
    {
      try
      {
        String sUrl = sDEFAULT_HOST+":"+sDEFAULT_PORT;
        if (args.length > 0)
          sUrl = args[0];
        if (!sUrl.startsWith("http://"))
          sUrl = "http://" + sUrl;
        if (sUrl.endsWith("/"))
          sUrl = sUrl.substring(0,sUrl.length()-1);
        if (sUrl.indexOf(':') < 0)
          sUrl = sUrl+":"+sDEFAULT_PORT;
        sUrl = sUrl + "/" + sSERVICE_PATH;
        System.out.println("Service will be available under "+sUrl);
        System.out.println("User Ctrl-C to stop the service.");
        Endpoint e = Endpoint.create(new GenericMigrate_1_0());
        ExecutorService threads  = Executors.newFixedThreadPool(5);
        e.setExecutor(threads);
        SOAPBinding sb = (SOAPBinding)e.getBinding();
        sb.setMTOMEnabled(true);
        e.publish(sUrl);
        System.out.println("Published");
        if (sb.isMTOMEnabled())
          System.out.println("MTOM enabled");
        /* due to a bug in the endpoint publisher 
           we cannot support streaming */
        System.out.println("Streaming not supported\n");
      }
      catch (Exception e) { System.err.println(e.getClass().getName()+": "+e.getMessage()); }
    }
  } /* main */

} /* class GenericMigratePublish_1_0 */
