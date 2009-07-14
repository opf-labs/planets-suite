package eu.planets_project.ifr.core.wee.api;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.wee.api.ReportingLog.Level;
import eu.planets_project.ifr.core.wee.api.ReportingLog.Message;
import eu.planets_project.services.datatypes.Parameter;

/**
 * Tests for the {@link ReportingLog}.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ReportingLogTests {
    private static final String WRONG = "Something went wrong!";

    private final ReportingLog LOG = new ReportingLog(Logger
            .getLogger(ReportingLogTests.class));

    @Before
    public void logStuff() {

        /* A Message object will be in the report, but not in the log: */

        Parameter p1 = new Parameter("Input format", "PNG");
        Parameter p2 = new Parameter("Output format", "TIFF");
        Message message = new Message("A workflow message about migration", p1,
                p2);

        /* Such a message can be logged alone: */
        LOG.trace(message);
        LOG.debug(message);
        LOG.info(message);
        LOG.warn(message);
        LOG.error(message);
        LOG.fatal(message);

        /* Or with an additional throwable: */
        LOG.trace(message, new IllegalStateException(WRONG));
        LOG.debug(message, new IllegalStateException(WRONG));
        LOG.info(message, new IllegalStateException(WRONG));
        LOG.warn(message, new IllegalStateException(WRONG));
        LOG.error(message, new IllegalStateException(WRONG));
        LOG.fatal(message, new IllegalStateException(WRONG));

        /* Logging simple string will not add to the report: */

        /* Plain version: */
        LOG.trace("trace!");
        LOG.debug("debug!");
        LOG.info("info!");
        LOG.warn("warn!");
        LOG.error("error!");
        LOG.fatal("fatal!");

        /* Including a Throwable: */
        LOG.trace("trace!", new IllegalStateException(WRONG));
        LOG.debug("debug!", new IllegalStateException(WRONG));
        LOG.info("info!", new IllegalStateException(WRONG));
        LOG.warn("warn!", new IllegalStateException(WRONG));
        LOG.error("error!", new IllegalStateException(WRONG));
        LOG.fatal("fatal!", new IllegalStateException(WRONG));

    }

    @Test
    public void getReport() {
        String report = LOG.reportAsString();
        Assert.assertNotNull("Logging should create a report", report);
        System.out.println(report);
        Assert.assertTrue("Result report should contain details message ",
                report.contains(WRONG));
        /* We check if all colors and type labels are in the report string: */
        for (Level level : Level.values()) {
            /* The levels should result in different colors in the report: */
            Assert.assertTrue(
                    "Result report should contain color for " + level, report
                            .contains(level.color));
            /* But the labels we were using for the log above, not the report: */
            Assert.assertFalse("Result report should not contain type for "
                    + level, report.contains(level.toString()));
        }
    }

    @Test
    public void writeReport() {
        File file = LOG.reportAsFile();
        Assert.assertNotNull("Report file should not be null", file);
        Assert.assertTrue("Report file should exist", file.exists());
        System.err.println("Wrote report to: " + file.getAbsolutePath());
    }
    
    @Test
    public void getLog() {
        File file = LOG.logAsFile();
        Assert.assertNotNull("Log file should not be null", file);
        Assert.assertTrue("Log file should exist", file.exists());
        System.err.println("Wrote log to: " + file.getAbsolutePath());
    }
}
