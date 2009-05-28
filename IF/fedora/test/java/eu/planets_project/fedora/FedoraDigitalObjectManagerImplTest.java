package eu.planets_project.fedora;

import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Metadata;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Tests for the {@link FedoraDigitalObjectManagerImpl}.
 */
public class FedoraDigitalObjectManagerImplTest extends TestCase{


    /**
     * Integration test. Attempts to get a object from a localhost fedora
     * repository.
     * @throws DigitalObjectManager.DigitalObjectNotFoundException
     * @throws URISyntaxException
     */
    public void testRetrieve() throws DigitalObjectManager.DigitalObjectNotFoundException, URISyntaxException, IOException {
        DigitalObjectManager man = new FedoraDigitalObjectManagerImpl("fedoraAdmin","fedoraAdminPass","http://localhost:7910/fedora");

        DigitalObject r = man.retrieve(new URI("demo:dc2mods.1"));
        String title = r.getTitle();
        List<Metadata> met = r.getMetadata();

        InputStream content = r.getContent().read();
        StringWriter theString = new StringWriter();
        IOUtils.copy(content,theString);
        assertNotNull(theString.toString(),"Content should not be null");
        assertNotNull(title,"The title should be set");
        assertNotNull(met.get(0).getContent(),"There should be some metadata");
    }
}
