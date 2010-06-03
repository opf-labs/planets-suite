package eu.planets_project.tb.impl.data.util;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.tb.api.data.util.DigitalObjectRefBean;

/**
 * This servlet returns the content stored in the JCR repo for the provided
 * digital object id (permanent uri).
 * 
 * @author <a href="mailto:christian.sadilek@ait.ac.at">Christian Sadilek</a>
 */
public class TBDigitalObjectContentResolver extends HttpServlet {	
	private static final long serialVersionUID = -8269793550349486731L;
	
	private static Log log = LogFactory.getLog(TBDigitalObjectContentResolver.class);
	private DataHandlerImpl dh = new DataHandlerImpl();
	
	private static final int BUFFER_SIZE = 4096;
	private static final String ID_PARAMETER_NAME = "id";
	private static final String MIME_TYPE_METADATA_NAME = "mimeType";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		InputStream is = null;
		String id = request.getParameter(ID_PARAMETER_NAME);
		
		try {
			if(id==null) throw new DigitalObjectNotFoundException("id is null");

			// Fix up any encoding issues:
			id = PDURI.encodePlanetsUriStringAsUri(id).toASCIIString();
			
			DigitalObjectRefBean digoRef = dh.get(id);
			if(digoRef==null) throw new DigitalObjectNotFoundException("digital object "+id+" not found");
			
			// set the metadata if it's contained within the digital object
			DigitalObject object = digoRef.getDigitalObject();
			for(Metadata md : object.getMetadata()) {
				if(md.getName().equals(MIME_TYPE_METADATA_NAME))
					response.setContentType(md.getContent());
			}
			
			is = digoRef.getContentAsStream();
			
			// read from input stream and write to client
			int bytesRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			while((bytesRead=is.read(buffer))!=-1){
			    response.getOutputStream().write(buffer, 0, bytesRead);
			}			
		} catch (DigitalObjectNotFoundException e) {
			log.info(e.getMessage(),e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "object not found with id="+id);
		} finally {
			if(is!=null) is.close();
		}
	}
}
