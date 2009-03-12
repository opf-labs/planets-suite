package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.planets_project.ifr.core.services.characterisation.extractor.impl.XcdlCharacterise;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlAccess;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlParser;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlProperties;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.FileFormatProperty;
import eu.planets_project.services.datatypes.Property;

/**
 * Tests for the XcdlAccess and implementations.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class XcdlAccessTests {
    private static final String XCDL = "PP/xcl/src/java/eu/planets_project"
            + "/ifr/core/services/characterisation/extractor/xcdl/xcdl.xml";
    private static final String PNG = "PP/xcl/src/resources/basi0g08.png";

    @Test
    public void testProperties() {
        check(new XcdlProperties(new File(XCDL)));
    }

    @Test
    public void testParser() {
        check(new XcdlParser(new File(XCDL)));
    }

    @Test
    public void testBoth() {
        /* Both implementations should return identical results: */
        List<Property> p1 = new XcdlProperties(new File(XCDL)).getProperties();
        List<Property> p2 = new XcdlParser(new File(XCDL)).getProperties();
        Assert.assertEquals(p1, p2);
    }

    @Test
    public void testListPropertiesMatchExtractedProperties() {
        XcdlCharacterise characterise = new XcdlCharacterise();
        /* Check what we can expect for PNG: */
        URI uri = FormatRegistryFactory.getFormatRegistry()
                .getURIsForExtension("png").iterator().next();
        List<FileFormatProperty> extractable = characterise.listProperties(uri);
        /* Now we actually extract a PNG: */
        CharacteriseResult result = characterise.characterise(DigitalObject
                .create(Content.byValue(new File(PNG))).build(), null);
        List<Property> extracted = result.getProperties();
        /* And check if the IDs correspond: */
        assertAllExtractedPropsAreListedAsExtractable(extractable, extracted);
    }

    private void assertAllExtractedPropsAreListedAsExtractable(
            List<FileFormatProperty> extractable, List<Property> extracted) {
        for (Property property : extracted) {
            /*
             * TODO: In the future, when the types are the same, we can just
             * check if extractable contains property, until then:
             */
            boolean found = false;
            for (FileFormatProperty fileFormatProperty : extractable) {
                if (property.getUri().equals(fileFormatProperty.getUri())) {
                    found = true;
                    break;
                }
            }
            Assert
                    .assertTrue(
                            "List of supposedly extractable properties does not contain an extracted property: "
                                    + property, found);
        }
    }

    /**
     * @param xcdlAccess The access to check
     */
    private void check(final XcdlAccess xcdlAccess) {
        List<Property> properties = xcdlAccess.getProperties();
        Assert.assertTrue("No properties extracted by "
                + xcdlAccess.getClass().getSimpleName(), properties.size() > 0);
    }
}
