package eu.planets_project.ifr.core.techreg.api.formats;

import java.net.URI;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Format registry format mapping tests.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class FormatMappingTests {

    private FormatRegistry registry;

    @Before
    public void init() {
        registry = FormatRegistryFactory.getFormatRegistry();
    }

    @Test
    public void uriToExtension() {
        String ext = "png";
        String puid = "fmt/13";
        Set<String> extensions = registry.getFormatForURI(
                registry.puidToUri(puid)).getExtensions();
        Assert.assertTrue(String.format(
                "Found no '%s' extension for puid '%s'", ext, puid), extensions
                .contains(ext));
    }

    @Test
    public void extensionToUris() {
        String x = "png";
        Set<URI> set = registry.getURIsForExtension(x);
        Assert.assertTrue(String.format(
                "Found no Pronom URIs for extension %s", x), set.size() > 0);
    }
}
