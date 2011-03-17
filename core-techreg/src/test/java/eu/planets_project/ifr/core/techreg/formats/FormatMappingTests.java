package eu.planets_project.ifr.core.techreg.formats;

import java.net.URI;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;

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
        String puid = "fmt/13";
        // Expected extensions include:
        String ext = "png";
        Set<String> extensions = registry.getExtensions(registry
                .createPronomUri(puid));
        Assert.assertTrue(String.format(
                "Found no '%s' extension for puid '%s'", ext, puid), extensions
                .contains(ext));
        System.out.print("For puid "+puid+" :: ");
        for( String pext : extensions ) {
        	System.out.print(pext+" ");
        }
    }

    @Test
    public void extensionToUris() {
        String x = "png";
        Set<URI> set = registry.getUrisForExtension(x);
        Assert.assertTrue(String.format(
                "Found no Pronom URIs for extension %s", x), set.size() > 0);
        System.out.print("For extension "+x+" :: ");
        for( URI puid : set ) {
        	System.out.print(puid+" ");
        }
    }
}
