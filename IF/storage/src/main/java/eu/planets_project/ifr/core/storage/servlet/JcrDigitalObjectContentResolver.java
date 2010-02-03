package eu.planets_project.ifr.core.storage.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerImpl;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Metadata;

/**
 * This servlet returns the content stored in the JCR repo for the provided
 * digital object id (permanent uri).
 * 
 * @author <a href="mailto:christian.sadilek@ait.ac.at">Christian Sadilek</a>
 */
public class JcrDigitalObjectContentResolver extends HttpServlet {	
	private static final long serialVersionUID = -8269793550314460731L;
	
	private static Logger logger = Logger.getLogger(JcrDigitalObjectContentResolver.class.getName());
	
	private static final int BUFFER_SIZE = 4096;
	private static final String ID_PARAMETER_NAME = "id";
	private static final String MIME_TYPE_METADATA_NAME = "mimeType";
	
	/**
	 * Constructor, to watch what is happening.
	 */
	public JcrDigitalObjectContentResolver() {
	    logger.info("Constructing JcrDigitalObjectContentResolver...");
	}

	/* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("Initialising JcrDigitalObjectContentResolver...");
    }


    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		InputStream is = null;
		
		JcrDigitalObjectManagerImpl jcrDo = 
			(JcrDigitalObjectManagerImpl) JcrDigitalObjectManagerImpl.getInstance();
		
		String id = request.getParameter(ID_PARAMETER_NAME);
		try {
			if(id==null) throw new DigitalObjectNotFoundException("id is null");
			
			// unfortunately, we have to search for the right metadata field
			DigitalObject object = jcrDo.retrieve(new URI(id), false);			
			for(Metadata md : object.getMetadata()) {
				if(md.getName().equals(MIME_TYPE_METADATA_NAME))
					response.setContentType(md.getContent());
			}			
			is = jcrDo.retrieveContentAsStream(object.getPermanentUri());
			
			// read from input stream and write to client
			int bytesRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			while((bytesRead=is.read(buffer))!=-1){
			    response.getOutputStream().write(buffer, 0, bytesRead);
			}			
		} catch (DigitalObjectNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "object not found with id="+id);
		} catch (URISyntaxException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "invalid URI");
		} catch (RepositoryException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "check repository");
		} finally {
			if(is!=null) is.close();
		}
	}
    
}
