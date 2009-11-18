package eu.planets_project.ifr.core.techreg.formats;

import java.net.URI;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.Format.UriType;
import eu.planets_project.ifr.core.techreg.formats.api.FormatRegistry;

/**
 * Format registry format URI creation and info tests.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class FormatUriTypesTests {

    private FormatRegistry registry;

    @Before
    public void init() {
        registry = FormatRegistryFactory.getFormatRegistry();
    }

    @Test
    public void mimeUri() {
        /* Create a specific URI: */
        String mime = "text/plain";
        URI uri = registry.createMimeUri(mime);
        /* Get info on that URI: */
        Assert.assertTrue(registry.isUriOfType(uri, UriType.MIME));
        Assert.assertEquals(mime, registry.getValueFromUri(uri));
    }

    @Test
    public void extensionUri() {
        /* Create a specific URI: */
        String extension = "txt";
        URI uri = registry.createExtensionUri(extension);
        /* Get info on that URI: */
        Assert.assertTrue(registry.isUriOfType(uri, UriType.EXTENSION));
        Assert.assertEquals(extension, registry.getValueFromUri(uri));
    }

    @Test
    public void pronomUri() {
        /* Create a specific URI: */
        String pronom = "fmt/13";
        URI uri = registry.createPronomUri(pronom);
        /* Get info on that URI: */
        Assert.assertTrue(registry.isUriOfType(uri, UriType.PRONOM));
        Assert.assertEquals(pronom, registry.getValueFromUri(uri));
    }

    @Test
    public void anyFormatUri() {
        /* Use format registry: */
        URI uri = registry.createAnyFormatUri();
        Assert.assertTrue(registry.isUriOfType(uri, UriType.ANY));
        /* Or use the constant: */
        uri = Format.ANY;
        Assert.assertTrue(registry.isUriOfType(uri, UriType.ANY));
        /* Any-Format URIs have no value: */
        Assert.assertEquals(null, registry.getValueFromUri(uri));
    }

    @Test
    public void unknownFormatUri() {
        /* Use format registry: */
        URI uri = registry.createUnknownFormatUri();
        Assert.assertTrue(registry.isUriOfType(uri, UriType.UNKNOWN));
        /* Or use the constant: */
        uri = Format.UNKNOWN;
        Assert.assertTrue(registry.isUriOfType(uri, UriType.UNKNOWN));
        /* Unknown-Format URIs have no value: */
        Assert.assertEquals(null, registry.getValueFromUri(uri));
    }

}
