package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.datatypes.Property;

/**
 * Tests and sample usage for the XcdlCreator.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class XcdlCreatorTests {
    private static XcdlCreator creator;
    private static String xcdlXml;

    @BeforeClass
    public static void setup() {
        /*
         * TODO: Does this make any sense? Probably not so much from an API
         * perspective, but maybe when such properties are generated... Can we
         * unclutter this? Should we use enums for the string values? Should we
         * get them from the annotations of the generated classes, e.g.
         * PropertySet.class.getAnnotation(XmlRootElement.class).name();?
         */
        List<Property> norm = Arrays
                .asList(
                /* Norm data: */
                new Property.Builder(XcdlProperties.makePropertyURI("nd1",
                        "normData"))
                        .name("normData")
                        .type("nd1")
                        .description("image")
                        .value(
                                "00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 10 11 12 13 14 15")
                        .build());
        /* Property sets: */
        List<Property> sets = Arrays
                .asList(new Property.Builder(XcdlProperties.makePropertyURI(
                        "nd1", "propertySet"))
                        .name("propertySet")
                        .type("id_1")
                        .value(
                                "ref i_i1_i217_s4 suggestedPaletteAlpha, ref i_i1_i217_s5 suggestedPaletteFrequency")
                        .build());
        /*
         * The ref references a property's value set (see below) via the type:
         */
        // Prop.name("ref").type("i_i1_i217_s4").description(
        // "suggestedPaletteAlpha").build(),
        // Prop.name("ref").type("i_i1_i217_s5").description(
        // "suggestedPaletteFrequency").build()).build());
        /* Properties: */

        List<Property> properties = Arrays
                .asList(
                        new Property.Builder(XcdlProperties.makePropertyURI(
                                "p74", "property"))
                                .name("property")
                                .type("p74")
                                .value(
                                        "raw descr, name id57 suggestedPaletteFrequency, "
                                                + "valueSet i_i1_i217_s5, labValue 0 int inch, dataRef id_1 global")
                                .build(),
                        // new Property.Builder(new
                        // URI()).name("value").value("raw, descr").build(),
                        // Prop.name("name").type("id57").description(
                        // "suggestedPaletteFrequency").build(),
                        // Prop.name("valueSet").type("i_i1_i217_s5").values(
                        // Prop.name("labValue").values("0").description(
                        // "int").unit("inch").build(),
                        // /*
                        // * The data ref references a property set (see
                        // * above) via the type:
                        // */
                        // Prop.name("dataRef").type("id_1").description(
                        // "global").build()).build()).build(),
                        new Property.Builder(XcdlProperties.makePropertyURI(
                                "p73", "property"))
                                .name("property")
                                .type("p73")
                                .value(
                                        "raw descr, name id56 suggestedPaletteAlpha, "
                                                + "valueSet i_i1_i217_s4, labValue 255 int inch, dataRef id_1 global")
                                .build());
        // Prop.name("value").values("raw", "descr").build(),
        // Prop.name("name").type("id56").description(
        // "suggestedPaletteAlpha").build(),
        // Prop.name("valueSet").type("i_i1_i217_s4").values(
        // Prop.name("labValue").values("255")
        // .description("int").unit("inch")
        // .build(),
        // Prop.name("dataRef").type("id_1").description(
        // "global").build()).build()).build());
        List<Property> all = new ArrayList<Property>();
        all.addAll(norm);
        all.addAll(sets);
        all.addAll(properties);
        creator = new XcdlCreator(all);
        xcdlXml = creator.getXcdlXml();
    }

    @Test
    public void testSerialiaztion() {
        Assert.assertNotNull("Could not serialize the XCDL object tree",
                xcdlXml);
        System.out.println(xcdlXml);

    }

    @Test
    public void testNormData() {
        Assert.assertTrue("Norm data element not present in serialized form",
                xcdlXml.contains("<normData"));
    }

    @Test
    public void testProperties() {
        Assert.assertTrue("Property element not present in serialized form",
                xcdlXml.contains("<property"));
    }

    @Test
    public void testPropertySets() {
        Assert.assertTrue(
                "Property set element not present in serialized form", xcdlXml
                        .contains("<propertySet"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFaultyProp() {
        new XcdlCreator(Arrays.asList(new Property.Builder(XcdlProperties
                .makePropertyURI("some", "totally")).name("random").value(
                "property").build()));
    }
}
