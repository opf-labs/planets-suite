package eu.planets_project.ifr.core.services.comparison.comparator.config;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.planets_project.services.datatypes.Parameter;

/**
 * Tests for the {@link ComparatorConfigCreator}.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ComparatorConfigCreatorTests {
    @Test
    public void test() {
        List<String> l = Arrays.asList(
        /* Comparator config values for the comparison of image height: */
        "imageHeight", "equal", "intDiff", "percDev", "pixel",
        /* Comparator config values for the comparison of norm data: */
        "normData", "hammingDistance", "RMSE", "levenstheinDistance");
        Iterator<String> i = l.iterator();
        /* We create 2 props for these, using names and specifying the IDs: */
        Parameter p1 = new Parameter.Builder(i.next(), String.format(
                "metric %s 200, metric %s 201, metric %s 210, metric %s 999", i
                        .next(), i.next(), i.next(), i.next())).type("55").build();
        Parameter p2 = new Parameter.Builder(i.next(), String.format(
                "metric %s 10, metric %s 50, metric %s 15", i.next(), i.next(),
                i.next())).type("35").build();
        /*
         * From this object representation, we can create the COCO XML file
         * required by the XCDL Comparator:
         */
        List<Parameter> asList = Arrays.asList(p1, p2);
        ComparatorConfigCreator creator = new ComparatorConfigCreator(asList);
        String pcr = creator.getComparatorConfigXml();
        for (String string : l) {
            Assert.assertTrue(String.format("String '%s' not in result: %s",
                    string, pcr), pcr.contains(string));
        }
    }
}
