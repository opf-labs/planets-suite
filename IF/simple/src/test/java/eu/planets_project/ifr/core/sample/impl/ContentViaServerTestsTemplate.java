package eu.planets_project.ifr.core.sample.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.simple.impl.PassThruMigrationService;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.DigitalObjectContent;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Template to test handling of content via the server. The file to use is determined by subclasses.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public abstract class ContentViaServerTestsTemplate {
    private Migrate migrate = null;
    private URL url;
    private InputStream stream;

    /** @return The file to use for testing content handling via web service. */
    protected abstract File file();

    @Before
    public void init() throws IOException {
        migrate = ServiceCreator.Mode.createFor(//ServiceCreator.createTestService(
                Migrate.QNAME,
                PassThruMigrationService.class, new URL("http://metro.planets-project.ait.ac.at/pserv-if-simple/PassThruMigrationService?wsdl"));
        url = file().toURI().toURL();
        stream = file().toURI().toURL().openStream();
    }

    @Test public void byReferenceToUrl() { test(Content.byReference(url)); }
    @Test public void byReferenceToFile() { test(Content.byReference(file())); }
    @Test public void byReferenceToInputStream() { test(Content.byReference(stream)); }

    private void test(DigitalObjectContent content) {
        DigitalObject in = new DigitalObject.Builder(content).build();
        MigrateResult res = migrate.migrate(in, null, null, null);
        DigitalObject out = res.getDigitalObject();
        // We require the input and output object to be equal:
        Assert.assertEquals("Input and output objects must be equal", in, out);
    }

}
