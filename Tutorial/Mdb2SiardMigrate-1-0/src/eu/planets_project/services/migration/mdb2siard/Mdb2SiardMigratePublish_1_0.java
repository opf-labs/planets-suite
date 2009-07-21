/*== Mdb2SiardMigratePublish_1_0.java ======================================
Publisher for the PLANETS migration service implementation,
migrating MDB files to SIARD files.
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
package eu.planets_project.services.migration.mdb2siard;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.ws.Endpoint;
import javax.xml.ws.soap.SOAPBinding;

import eu.planets_project.services.migration.mdb2siard.Mdb2SiardMigrate_1_0;

/*===================================================================*/
/** This class has an executable main method to publish the service 
 * to a concrete TCP/IP port.
 * @author Hartwig Thomas <hartwig.thomas@enterag.ch>
 */
public class Mdb2SiardMigratePublish_1_0
{
  private static final String sDEFAULT_HOST = "localhost";
  private static final String sDEFAULT_PORT = "8080";
  private static final String sSERVICE_PATH = "Mdb2SiardMigrate-1-0";
  
  /*--------------------------------------------------------------------*/
  /** display usage information
   */
  private static void displayHelp()
  {
    System.out.println("Usage:");
    System.out.println("java " +
      "-Djava.endorsed.dirs=../lib/endorsed -cp <classpath>\n" +
      "-Djava.util.logging.config.file=\"../etc/logging.properties\"\n" +
      "eu.planets_project.services.migrate.GenericMigratePublish [<host>]");
    System.out.println("with");
    System.out.println("<classpath> must point to the class to be executed");
    System.out.println("            (e.g. build/classes");
    System.out.println("<host>      host where Web Service is to be published");
    System.out.println("            default: localhost:8080");
  } /* displayHelp */

  /*--------------------------------------------------------------------*/
  /** main expects host paramter where service is to be published.
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
        Endpoint e = Endpoint.create(new Mdb2SiardMigrate_1_0());
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

} /* class Mdb2SiardMigratePublish_1_0 */
