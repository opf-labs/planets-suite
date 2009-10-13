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
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Template to test handling of content via the server. The file to use is determined by subclasses.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public abstract class ContentViaServerTestsTemplate {
    private static final Migrate MIGRATE = ServiceCreator.createTestService(Migrate.QNAME,
            PassThruMigrationService.class, "/pserv-if-simple/PassThruMigrationService?wsdl");
    private URL url;
    private InputStream stream;
    private byte[] byteArray;

    /** @return The file to use for testing content handling via web service. */
    protected abstract File file();

    @Before
    public void init() throws IOException {
        url = file().toURI().toURL();
        stream = file().toURL().openStream();
        byteArray = FileUtils.readFileIntoByteArray(file());
    }

    @Test public void byReferenceToUrl() { test(Content.byReference(url)); }
    @Test public void byReferenceToFile() { test(Content.byReference(file())); }
    @Test public void byReferenceToInputStream() { test(Content.byReference(stream)); }

    @Test public void byValueOfFile() { test(Content.byValue(file())); }
    @Test public void byValueOfInputStream() { test(Content.byValue(stream)); }
    @Test public void byValueOfByteArray() { test(Content.byValue(byteArray)); }

    private void test(DigitalObjectContent content) {
        DigitalObject in = new DigitalObject.Builder(content).build();
        MigrateResult res = MIGRATE.migrate(in, null, null, null);
        DigitalObject out = res.getDigitalObject();
        Assert.assertNotNull("Resulting digital object must not be null", out);
        Assert.assertEquals("Input and output objects must be equal", in, out);
    }

}
