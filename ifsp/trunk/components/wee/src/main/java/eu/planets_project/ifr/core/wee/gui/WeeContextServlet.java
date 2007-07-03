package eu.planets_project.ifr.core.wee.gui;


import org.jboss.logging.Logger;
import org.jboss.ws.core.server.ServiceEndpointManagerFactory;


/**
 * The servlet that is associated with context /wee-monitor
 *
 * @author rainer.schuster@researchstudio.at
 * @since 11-Jun-2007
 */
public class WeeContextServlet extends MonitoringContextServlet
{
   // provide logging
   protected final Logger log = Logger.getLogger(WeeContextServlet.class);

   protected void initServiceEndpointManager()
   {
      ServiceEndpointManagerFactory factory = ServiceEndpointManagerFactory.getInstance();
      epManager = factory.getServiceEndpointManager();
   }
}