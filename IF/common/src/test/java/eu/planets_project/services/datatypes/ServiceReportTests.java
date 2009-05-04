package eu.planets_project.services.datatypes;

import junit.framework.Assert;

import org.junit.Test;

import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;

/**
 * Tests and sample usage for the ServiceReport class.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ServiceReportTests {
    @Test
    public void info() {
        /* We create a report: */
        String message = "Success!";
        ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS,
                message);
        /* We inspect a report: */
        Assert.assertEquals(Type.INFO, report.getType());
        Assert.assertEquals(Status.SUCCESS, report.getStatus());
        Assert.assertEquals(message, report.getMessage());
    }

    @Test
    public void warn() {
        /* We create a report: */
        String message = "Warning!";
        ServiceReport report = new ServiceReport(Type.WARN, Status.SUCCESS,
                message);
        /* We inspect a report: */
        Assert.assertEquals(Type.WARN, report.getType());
        Assert.assertEquals(Status.SUCCESS, report.getStatus());
        Assert.assertEquals(message, report.getMessage());
    }

    @Test
    public void errorTool() {
        /* We create a report: */
        String message = "Tool error!";
        ServiceReport report = new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
                message);
        /* We inspect a report: */
        Assert.assertEquals(Type.ERROR, report.getType());
        Assert.assertEquals(Status.TOOL_ERROR, report.getStatus());
        Assert.assertEquals(message, report.getMessage());
    }

    @Test
    public void errorInstall() {
        /* We create a report: */
        String message = "Install error!";
        ServiceReport report = new ServiceReport(Type.ERROR,
                Status.INSTALLATION_ERROR, message);
        /* We inspect a report: */
        Assert.assertEquals(Type.ERROR, report.getType());
        Assert.assertEquals(Status.INSTALLATION_ERROR, report.getStatus());
        Assert.assertEquals(message, report.getMessage());
    }

}
