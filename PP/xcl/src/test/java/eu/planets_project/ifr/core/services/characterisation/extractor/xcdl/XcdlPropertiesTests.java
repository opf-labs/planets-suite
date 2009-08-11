package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.net.URI;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests for the util methods in XcdlProperties.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class XcdlPropertiesTests {
    private static final String NAME = "testProp";
    final static URI URI = XcdlProperties.makePropertyURI(NAME);

    @Test
    public void makeUri() {
        Assert.assertEquals(XcdlProperties.URI_ROOT+"testProp", URI
                .toString());
    }

    @Test
    public void getNameFromUri() {
        Assert.assertEquals(NAME, XcdlProperties.getNameFromUri(URI));
    }
}
