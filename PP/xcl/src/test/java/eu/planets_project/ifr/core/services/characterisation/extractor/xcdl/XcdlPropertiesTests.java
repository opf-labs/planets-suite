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
    private static final String ID = "5";
    final static URI URI = XcdlProperties.makePropertyURI(ID, NAME);

    @Test
    public void makeUri() {
        Assert.assertEquals("planets:pc/xcdl/property/id5/testProp", URI
                .toString());
    }

    @Test
    public void getIdFromUri() {
        Assert.assertEquals(ID, XcdlProperties.getIdFromUri(URI));
    }

    @Test
    public void getNameFromUri() {
        Assert.assertEquals(NAME, XcdlProperties.getNameFromUri(URI));
    }
}
