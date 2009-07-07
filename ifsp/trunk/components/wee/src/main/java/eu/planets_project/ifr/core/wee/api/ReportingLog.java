/***/
package eu.planets_project.ifr.core.wee.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;

import eu.planets_project.services.utils.FileUtils;

/**
 * A reporting logger that builds up a HTML report from the log messages and
 * forwards the messages to a backing normal logger.
 * @see ReportingLogTests
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ReportingLog implements Log {

    private static final String TEMPLATE = "ReportTemplate.html";
    private static final String CONTENT_MARKER = "###CONTENT###";
    static final String LOCAL = "components/wee/src/main/resources/";
    private static final String WEE_DATA = "/server/default/data/wee/";
    private static final String JBOSS_HOME_DIR_KEY = "jboss.home.dir";
    private static final String deployedJBossHome = System
            .getProperty(JBOSS_HOME_DIR_KEY);
    private static final String REPORT_OUTPUT_FOLDER = (deployedJBossHome != null ? deployedJBossHome
            + WEE_DATA
            : LOCAL);

    /**
     * Enum for the different possible levels of log messages.
     * @author Fabian Steeg (fsteeg)
     */
    enum Level {
        INFO("#CCFF99"), DEBUG("#F0F0F0"), TRACE("#FFFFFF"), FATAL("#CC3333"), ERROR("#FFCC66"), WARN("#99CCFF");
        String color;

        private Level(final String color) {
            this.color = color;
        }
    }

    private Log backingLog;
    private StringBuilder builder = new StringBuilder();
    // private static final String END = "</html>";
    private static final String ENTRY =
    // A template for a workflow report message, used with String.format:
    "<fieldset><legend><b>Workflow Message</legend>"
            + "<table bgcolor=%s width=100%%><tr><td>" // first insert: color
            + "<b>Type: </b>%s<br/> " // second insert: type
            + "<b>Message: </b>%s<br/>" // third insert: message
            + "<b>Details: </b>%s<br/>" // fourth insert: details
            + "</td></tr></table></fieldset>";

    /**
     * @param backingLog The backing log
     */
    public ReportingLog(final Log backingLog) {
        this.backingLog = backingLog;
    }

    /**
     * @return The report assembled during logging
     */
    public String reportAsString() {
        String content = builder.toString();
        InputStream stream = this.getClass().getResourceAsStream(TEMPLATE);
        String template = new String(FileUtils.writeInputStreamToBinary(stream));
        String result = template.replace(CONTENT_MARKER, content);
        return result;
    }

    /**
     * @return The file the HTML report has been written to
     */
    public File reportAsFile() {
        File file = new File(REPORT_OUTPUT_FOLDER, "wf-report"
                + System.currentTimeMillis() + ".html");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(reportAsString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(writer);
        }
        return file;
    }

    private String message(Level level, Object message) {
        return message(level, message, null);
    }

    private String message(Level level, Object message, Throwable t) {
        return String.format(ENTRY, level.color, level, message,
                t == null ? "--" : t.getLocalizedMessage());
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#debug(java.lang.Object)
     */
    public void debug(Object message) {
        backingLog.debug(message);
        builder.append(message(Level.DEBUG, message));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#debug(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void debug(Object message, Throwable t) {
        backingLog.debug(message, t);
        builder.append(message(Level.DEBUG, message, t));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#error(java.lang.Object)
     */
    public void error(Object message) {
        backingLog.error(message);
        builder.append(message(Level.ERROR, message));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#error(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void error(Object message, Throwable t) {
        backingLog.error(message, t);
        builder.append(message(Level.ERROR, message, t));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object)
     */
    public void fatal(Object message) {
        backingLog.fatal(message);
        builder.append(message(Level.FATAL, message));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void fatal(Object message, Throwable t) {
        backingLog.fatal(message, t);
        builder.append(message(Level.FATAL, message, t));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#info(java.lang.Object)
     */
    public void info(Object message) {
        backingLog.info(message);
        builder.append(message(Level.INFO, message));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#info(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void info(Object message, Throwable t) {
        backingLog.info(message, t);
        builder.append(message(Level.INFO, message, t));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#trace(java.lang.Object)
     */
    public void trace(Object message) {
        backingLog.trace(message);
        builder.append(message(Level.TRACE, message));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#trace(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void trace(Object message, Throwable t) {
        backingLog.trace(message, t);
        builder.append(message(Level.TRACE, message, t));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#warn(java.lang.Object)
     */
    public void warn(Object message) {
        backingLog.warn(message);
        builder.append(message(Level.WARN, message));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#warn(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void warn(Object message, Throwable t) {
        backingLog.warn(message, t);
        builder.append(message(Level.WARN, message, t));
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#isDebugEnabled()
     */
    public boolean isDebugEnabled() {
        return backingLog.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#isErrorEnabled()
     */
    public boolean isErrorEnabled() {
        return backingLog.isErrorEnabled();
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#isFatalEnabled()
     */
    public boolean isFatalEnabled() {
        return backingLog.isFatalEnabled();
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#isInfoEnabled()
     */
    public boolean isInfoEnabled() {
        return backingLog.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#isTraceEnabled()
     */
    public boolean isTraceEnabled() {
        return backingLog.isTraceEnabled();
    }

    /**
     * {@inheritDoc}
     * @see org.apache.commons.logging.Log#isWarnEnabled()
     */
    public boolean isWarnEnabled() {
        return backingLog.isWarnEnabled();
    }
}
