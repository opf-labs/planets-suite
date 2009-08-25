package eu.planets_project.services.datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;

/**
 * Tests and sample usage for the {@link CharacteriseResult} class.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class CharacteriseResultTests {
    @Test
    public void simple() {
        /* A simple CharacteriseResult consists of a list of properties and a service report: */
        ArrayList<Property> props = someProps();
        ServiceReport report = someReport();
        CharacteriseResult result = new CharacteriseResult(props, report);
        Assert.assertEquals(props, result.getProperties());
        Assert.assertEquals(report, result.getReport());
    }

    @Test
    public void embedded() {
        /* Top-level properties and report: */
        ArrayList<Property> props = someProps();
        ServiceReport report = someReport();
        /* Embedded property lists: */
        ArrayList<Property> embeddedProps1 = someProps();
        ArrayList<Property> embeddedProps2 = someProps();
        List<CharacteriseResult> embeddedResults = Arrays.asList(new CharacteriseResult(embeddedProps1, someReport()),
                new CharacteriseResult(embeddedProps2, someReport()));
        /* Create the top-level result with the embedded results: */
        CharacteriseResult result = new CharacteriseResult(props, report, embeddedResults);
        /* Retrieve top-level properties: */
        Assert.assertEquals(props, result.getProperties());
        Assert.assertEquals(report, result.getReport());
        /* Retrieve the embedded results: */
        List<CharacteriseResult> embedded = result.getResults();
        Assert.assertEquals(embeddedResults, embedded);
        Assert.assertEquals(embeddedProps1, embedded.get(0).getProperties());
        Assert.assertEquals(embeddedProps2, embedded.get(1).getProperties());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void immutableProperties() {
        new CharacteriseResult(someProps(), someReport()).getProperties().add(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void immutableResults() {
        new CharacteriseResult(someProps(), someReport()).getResults().add(null);
    }

    private ArrayList<Property> someProps() {
        return new ArrayList<Property>();
    }

    private ServiceReport someReport() {
        return new ServiceReport(Type.INFO, Status.SUCCESS, "Test");
    }

}
