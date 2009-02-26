package eu.planets_project.services.datatypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link Prop} class.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class PropTests {
    @Test
    public void usage() {
        /* A dummy example to demonstrate the principle: */
        Prop.name("color").values("red", "green").values(
                Prop.name("saturation").values("none", "half", "full").build(),
                Prop.name("brightness").values("dark", "bright").build())
                .build();
        /*
         * A sample input config property for the XCDL comparator (a typesafe
         * property, containing only one type of values, namely props):
         */

        Prop.name("imageHeight").unit("pixel").type("101").values(
                Prop.name("metric").type("200").description("equal").build(),
                Prop.name("metric").type("201").description("intDiff").build(),
                Prop.name("metric").type("210").description("percDev").build())
                .build();

        /* Or with a type: */
        Prop<Prop<?>> s1 = Prop.<Prop<?>> name("imageHeight").unit("pixel")
                .type("101").values(
                        Prop.name("metric").type("200").description("equal")
                                .build(),
                        Prop.name("metric").type("201").description("intDiff")
                                .build(),
                        Prop.name("metric").type("210").description("percDev")
                                .build()).build();

        Assert.assertEquals(3, s1.getValues().size());
        /*
         * A sample result property of the XCDL comparator (a property with
         * heterogenous values):
         */
        Prop<?> s2 = Prop.name("bitsPerSample").unit("bit").type("int").values(
                "8", "2",
                Prop.name("equal").values("false").type("metric").build(),
                Prop.name("intDiff").values("6").type("metric").build())
                .build();
        Assert.assertEquals(4, s2.getValues().size());

    }

    private final Prop p1 = Prop.name("name1").values("value1").build();
    private final Prop p2 = Prop.name("name2").values("value2").build();

    @Test
    public void testToString() {
        Assert.assertTrue(p1.toString().contains(p1.getName())
                && p1.toString().contains(p1.getValues().toString()));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(p1, Prop.name(p1.getName()).values(
                p1.getValues().toArray(new String[] {})).build());
    }

    @Test
    public void testHashCode() {
        Set<Prop> props = new HashSet<Prop>(Arrays.asList(p1, p1, p2, p2));
        Assert.assertEquals("Set contains duplicate entries", 2, props.size());
    }
}
