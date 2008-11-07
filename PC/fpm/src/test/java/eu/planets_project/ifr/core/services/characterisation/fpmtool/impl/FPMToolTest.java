package eu.planets_project.ifr.core.services.characterisation.fpmtool.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.compare.BasicCompareFormatProperties;
import eu.planets_project.services.utils.test.ServiceCreator;

public class FPMToolTest {

    @Test
    public void localTests() {
        test(new FPMTool());
    }

    @Test
    public void clientTests() {
        BasicCompareFormatProperties bcfp = ServiceCreator.createTestService(
                BasicCompareFormatProperties.QNAME, FPMTool.class,
                "/pserv-pc-fpmtool/FPMTool?wsdl");
        test(bcfp);
    }

    public void test(BasicCompareFormatProperties fpmt) {

        try {
            String parameters = "fmt_10:fmt_13:";
            String result = null;
            result = fpmt.basicCompareFormatProperties(parameters);
            assertNotNull("response was null", result);
            assertTrue("Result contains an error: " + result, !result
                    .contains("<fpmError>"));
            assertTrue("Wrong result: " + result, result
                    .startsWith("<fpmResponse><format puid="));
            System.out.println("XCDL: " + result);
        } catch (PlanetsException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testBasicCompareFormatProperties() throws IOException,
            PlanetsException {
        String FPMTOOL_HOME = System.getenv("FPMTOOL_HOME");
        System.out.println(FPMTOOL_HOME);
        String parameters = "fmt_10:fmt_13:";
        FPMTool fpmtool = new FPMTool();
        String result = null;
        result = fpmtool.basicCompareFormatProperties(parameters);
        assertNotNull("response was null", result);
        assertTrue(result.startsWith("<fpmResponse><format puid="));
        System.out.println("XCDL: " + result);
    }

}
