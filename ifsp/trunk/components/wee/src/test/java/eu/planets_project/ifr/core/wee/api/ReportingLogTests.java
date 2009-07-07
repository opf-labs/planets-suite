package eu.planets_project.ifr.core.wee.api;

import java.io.File;

import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.wee.api.ReportingLog.Level;

/**
 * Tests for the {@link ReportingLog}.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ReportingLogTests {
    private static final String WRONG = "Something went wrong!";

    static {
        /*
         * See
         * http://commons.apache.org/logging/apidocs/org/apache/commons/logging
         * /impl/SimpleLog.html
         */
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
                "false");
        System.setProperty("org.apache.commons.logging.simplelog.defaultlog",
                "trace");
    }

    private final ReportingLog LOG = new ReportingLog(LogFactory
            .getLog(ReportingLogTests.class));

    @Before
    public void logStuff() {
        /* Including a Throwable: */
        LOG.trace("trace!", new IllegalStateException(WRONG));
        LOG.debug("debug!", new IllegalStateException(WRONG));
        LOG.info("info!", new IllegalStateException(WRONG));
        LOG.warn("warn!", new IllegalStateException(WRONG));
        LOG.error("error!", new IllegalStateException(WRONG));
        LOG.fatal("fatal!", new IllegalStateException(WRONG));
        /* Plain version: */
        LOG.trace("trace!");
        LOG.debug("debug!");
        LOG.info("info!");
        LOG.warn("warn!");
        LOG.error("error!");
        LOG.fatal("fatal!");
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
            Assert.assertTrue(
                    "Result report should contain color for " + level, report
                            .contains(level.color));
            Assert.assertTrue("Result report should contain type for " + level,
                    report.contains(level.toString()));
        }
    }

    @Test
    public void writeReport() {
        File file = LOG.reportAsFile();
        Assert.assertTrue("Report file should exist", file.exists());
        System.out.println("Wrote report to: " + file.getAbsolutePath());
    }
}
