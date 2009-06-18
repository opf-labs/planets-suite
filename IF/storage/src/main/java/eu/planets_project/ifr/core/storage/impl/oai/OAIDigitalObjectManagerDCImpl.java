package eu.planets_project.ifr.core.storage.impl.oai;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
// import java.util.Calendar;
// import java.util.List;

import org.dom4j.Element;

import se.kb.oai.pmh.Record;
import se.kb.oai.OAIException;
import se.kb.oai.pmh.OaiPmhServer;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Metadata;
import eu.planets_project.services.datatypes.DigitalObject.Builder;

/**
 * DC implementation of the OAI digital object manager.
 */
public class OAIDigitalObjectManagerDCImpl extends AbstractOAIDigitalObjectManagerImpl {
	
	/**
	 * @param baseURL The base URL
	 */
	public OAIDigitalObjectManagerDCImpl(String baseURL) {
		super(baseURL, "oai_dc");
	}
	
	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#retrieve(java.net.URI)
	 */
	public DigitalObject retrieve(URI pdURI) throws DigitalObjectNotFoundException {
		try {
			OaiPmhServer server = new OaiPmhServer(baseURL);
			Record record = server.getRecord(pdURI.toString(), metaDataPrefix);
			Element metadata = record.getMetadata();
			
			/*
            OutputFormat screenOutFormat = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(System.out, screenOutFormat);
			writer.setIndentLevel(2);
			writer.write(record.getResponse());
			*/
			
			if (metadata != null) {
				// Namespace URI
				URI namespaceURI = null;
				try {
					namespaceURI = new URI(metadata.getNamespaceURI());
				} catch (URISyntaxException e) {
					log.warn("Error parsing namespace URI: " + metadata.getNamespaceURI() + " (should never happen...)");
				}
				
				// DigitalObject title
				String title = null;
				
				// DigitalObject URL (required)
				String url = null;
				
				for (Object child : metadata.content()) {
					if (child instanceof Element) {
						Element c = (Element) child;
						
						// Title
						if (c.getName().equalsIgnoreCase("title"))
							title = c.getData().toString();
						
						// URL (if provided)
						if (c.getName().equalsIgnoreCase("identifier")) {
							String id = c.getData().toString();
							if (id.startsWith("http://"))
								url = id;
						}
					}
				}
				
				if (url != null) {
					Builder builder = new DigitalObject.Builder(Content.byReference(new URL(url)));
					builder.title(title);
					builder.metadata(new Metadata(namespaceURI, record.getMetadataAsString()));
					return builder.build();
				}
			}
			
			throw new DigitalObjectNotFoundException("No HTTP URL available for this record");
		} catch (OAIException e) {
			throw new DigitalObjectNotFoundException(e.getMessage());
		} catch (IOException e) {
			throw new DigitalObjectNotFoundException(e.getMessage());
		}
	}

    /* (non-Javadoc)
     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManager#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
     */
    public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
        // TODO Auto-generated method stub
        return null;
    }

	/*
	public static void main(String[] args) {
		OAIDigitalObjectManagerDCImpl oaiImpl = new OAIDigitalObjectManagerDCImpl("http://www.bibliovault.org/perl/oai2");		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MONTH, -24);
		Calendar now = Calendar.getInstance();
		
		// ListIdentifiers
		System.out.println("starting query.");
		try {
			List<URI> identifiers = oaiImpl.list(null, new QueryDateRange(start, now));
			System.out.println(identifiers.size() + " found.");
			
			// GetRecord for each identifier
			for (URI id : identifiers) {
				try {
					DigitalObject dob = oaiImpl.retrieve(id);
					System.out.println("retrieved file: " + dob.getTitle());
				} catch (DigitalObjectNotFoundException e) {
					System.out.println("couldn't retrieve file: " + e.getMessage());
				}
			}
		} catch (QueryValidationException e) {
			System.out.println("QueryValidationException: " + e.getMessage());
		}
		
		System.out.println("done.");
	}
	*/
	
}
