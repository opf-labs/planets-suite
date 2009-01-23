package eu.planets_project.ifr.core.services.comparison.comparator.config;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.planets_project.services.datatypes.Prop;

/**
 * Tests for the {@link ComparatorConfigCreator}.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 *
 */
public class ComparatorConfigCreatorTests {
    @Test
    public void test() {
        List<String> l = Arrays.asList(
        /* The two XCDL files to compare: */
        "XCDL1.xml", "XCDL2.xml",
        /* Comparator config values for the comparison of image height: */
        "imageHeight", "equal", "intDiff", "percDev", "pixel",
        /* Comparator config values for the comparison of norm data: */
        "normData", "hammingDistance", "RMSE", "levenstheinDistance");
        Iterator<String> i = l.iterator();
        /* We create 4 props for these, using names and specifying the IDs: */
        Prop s = Prop.name("source").values(i.next()).build();
        Prop t = Prop.name("target").values(i.next()).build();
        Prop p1 = Prop.name(i.next()).type("55").props(
                Prop.name("metric").type("200").description(i.next()).build(),
                Prop.name("metric").type("201").description(i.next()).build(),
                Prop.name("metric").type("210").description(i.next()).build())
                .unit(i.next()).build();
        Prop p2 = Prop.name(i.next()).type("35").props(
                Prop.name("metric").type("10").description(i.next()).build(),
                Prop.name("metric").type("50").description(i.next()).build(),
                Prop.name("metric").type("15").description(i.next()).build())
                .build();
        /*
         * From this object representation, we can create the PCR XML file
         * required by the XCDL Comparator:
         */
        ComparatorConfigCreator creator = new ComparatorConfigCreator(Arrays
                .asList(s, t, p1, p2));
        String pcr = creator.getComparatorConfigXml();
        for (String string : l) {
            Assert.assertTrue(String.format("String '%s' not in result: %s",
                    string, pcr), pcr.contains(string));
        }
    }
}
