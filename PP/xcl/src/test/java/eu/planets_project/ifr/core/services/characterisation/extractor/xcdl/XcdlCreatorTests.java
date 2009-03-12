package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlCreator;
import eu.planets_project.services.datatypes.Prop;

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
        List<Prop<Object>> norm = Arrays
                .asList(
                /* Norm data: */
                Prop
                        .name("normData")
                        .type("nd1")
                        .description("image")
                        .values(
                                "00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 10 11 12 13 14 15")
                        .build());
        /* Property sets: */
        List<Prop<Object>> sets = Arrays.asList(Prop.name("propertySet").type(
                "id_1").values(
                /*
                 * The ref references a property's value set (see below) via the
                 * type:
                 */
                Prop.name("ref").type("i_i1_i217_s4").description(
                        "suggestedPaletteAlpha").build(),
                Prop.name("ref").type("i_i1_i217_s5").description(
                        "suggestedPaletteFrequency").build()).build());

        /* Properties: */

        List<Prop<Object>> properties = Arrays.asList(Prop.name("property")
                .type("p74").values(
                        Prop.name("value").values("raw", "descr").build(),
                        Prop.name("name").type("id57").description(
                                "suggestedPaletteFrequency").build(),
                        Prop.name("valueSet").type("i_i1_i217_s5").values(
                                Prop.name("labValue").values("0").description(
                                        "int").unit("inch").build(),
                                /*
                                 * The data ref references a property set (see
                                 * above) via the type:
                                 */
                                Prop.name("dataRef").type("id_1").description(
                                        "global").build()).build()).build(),
                Prop.name("property").type("p73").values(
                        Prop.name("value").values("raw", "descr").build(),
                        Prop.name("name").type("id56").description(
                                "suggestedPaletteAlpha").build(),
                        Prop.name("valueSet").type("i_i1_i217_s4").values(
                                Prop.name("labValue").values("255")
                                        .description("int").unit("inch")
                                        .build(),
                                Prop.name("dataRef").type("id_1").description(
                                        "global").build()).build()).build());
        List<Prop<Object>> all = new ArrayList<Prop<Object>>();
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
        new XcdlCreator(Arrays.asList(Prop.<Object> name("some").values(
                Prop.name("random").values("prop").build()).build()));
    }
}
