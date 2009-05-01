package eu.planets_project.tb.unittest.data.demo.queryable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException;
import eu.planets_project.ifr.core.storage.api.query.QueryDateRange;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.impl.data.demo.queryable.GenericOAIDigitalObjectManagerImpl;

/**
 *
 * @author onb
 */
public class GenericOAIDigitalObjectManagerImplTest {

    public GenericOAIDigitalObjectManagerImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test(expected=DigitalObjectNotStoredException.class)
    public void testStore() throws Exception {
        System.out.println("store");
        URI pdURI = null;
        DigitalObject digitalObject = null;
        DigitalObjectManager instance = new GenericOAIDigitalObjectManagerImpl("http://www.diva-portal.org/oai/OAI");
        instance.store(pdURI, digitalObject);
    }

    @Test
    public void testListAndRetrieve() throws QueryValidationException {
        System.out.println("list with no query");
        
        DigitalObjectManager instance = new GenericOAIDigitalObjectManagerImpl("http://www.diva-portal.org/oai/OAI");
        List<URI> result = instance.list(null);
        System.out.println("number of results (no query): " + result.size());
        
		Calendar start = Calendar.getInstance();
		start.add(Calendar.YEAR, -1);
		Calendar now = Calendar.getInstance();
		result = instance.list(null, new QueryDateRange(start, now));
        System.out.println("number of results (with 1 yr query): " + result.size());
        
        for (URI uri : result) {
            String id = uri.toString();
            System.out.println("id: " + id);
            try {
	            DigitalObject dob = instance.retrieve(new URI(id));
	            System.out.println("successfully retrieved: " + dob.getTitle());
            } catch (DigitalObjectNotFoundException e) {
            	System.out.println("Could not retrieve " + id + " (" + e.getMessage() + ")");
            } catch (URISyntaxException e) {
            	System.out.println("Could not retrieve " + id + " (" + e.getMessage() + ")");
            }
        }
    }
    
    
}