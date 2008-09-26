/**
 * 
 */
package eu.planets_project.ifr.core.wee.gui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

//import org.jboss.ws.core.server.HttpContext;
//import org.jboss.ws.core.server.HttpServer;

import eu.planets_project.ifr.core.wee.api.WorkflowExecutionEngine;
import eu.planets_project.ifr.core.wee.impl.WeeManagerImpl;

/**
 * 
 * This servlet provides some basic status information.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class WeeManagerServlet extends HttpServlet {
    private static final long serialVersionUID = 8584229539252629375L;
    
    private Endpoint endpoint;
    
    @Override
    public void init(ServletConfig config) throws ServletException
    {
       super.init(config);
       
       // http://www.javadocexamples.com/java_source/org/jboss/test/ws/jaxws/endpoint/EndpointServlet.java.html
       /*
       
       // Create the endpoint
       SimpleCharacterisationService epImpl = new SimpleCharacterisationService();
       endpoint = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, epImpl);

       // Create and start the HTTP server
       SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
       HttpServer httpServer = spiProvider.getSPI(HttpServerFactory.class).getHttpServer();
       httpServer.start();
       
       // Create the context and publish the endpoint
       HttpContext context = httpServer.createContext("/jaxws-endpoint");
       endpoint.publish(context);
       
       */

    }
    
    @Override
    public void destroy()
    {
       // Stop the endpoint
//       endpoint.stop();
       
       super.destroy();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        /*
       // Create the port
       URL wsdlURL = getServletContext().getResource("/WEB-INF/wsdl/TestService.wsdl");
       QName qname = new QName("http://org.jboss.ws/jaxws/endpoint", "TestService");
       Service service = Service.create(wsdlURL, qname);
       SimpleCharacterisationService port = (SimpleCharacterisationService)service.getPort(SimpleCharacterisationService.class);
       // Invoke the endpoint
       String param = req.getParameter("param");
       String retStr = port.characteriseFile(param);

       // Return the result
       PrintWriter pw = new PrintWriter(res.getWriter());
       pw.print(retStr);
       */
        
        WorkflowExecutionEngine wee = WeeManagerImpl.getPlanetsWeeManager().getWee();
      res.getWriter().println( wee );
//      res.getWriter().println( wee.getSecs() );
        res.getWriter().println( " ACK");
    }

}
