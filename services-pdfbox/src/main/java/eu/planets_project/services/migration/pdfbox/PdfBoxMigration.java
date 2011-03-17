package eu.planets_project.services.migration.pdfbox;

import java.io.Serializable;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * The PdfBoxMigration migrates PDF files to HTML or text files.
 *
 * @author <a href="mailto:cjen@kb.dk">Claus Jensen</a>
 *
 */
@Stateless
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
@WebService(
        name = PdfBoxMigration.NAME,
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate")
public class PdfBoxMigration implements Migrate, Serializable {

    /**
     * Initialize logger.
     */
    private static Logger log = Logger.getLogger(PdfBoxMigration.class.getName());

    /**
     *  Used for serialization.
     */
    private static final long serialVersionUID = -1639737711901176613L;

    /**
     * The service name.
     */
    public static final String NAME = "PdfBoxMigration";

    /**
     * URIs for HTML.
     */
    private static URI outputformatHtml = URI.create("info:pronom/fmt/99");

    /**
     * URIs for Unicode.
     */
    private static URI outputformatUnicode = URI.create("info:pronom/x-fmt/16");

    /**
     * {@inheritDoc}
     *
     * @see eu.planets_project.services.migrate.Migrate#migrate(
     * eu.planets_project.services.datatypes.DigitalObject, java.net.URI,
     * java.net.URI, eu.planets_project.services.datatypes.Parameter)
     */
    public final MigrateResult migrate(final DigitalObject digitalObject,
                                       final URI inputFormat, final URI outputFormat,
                                       final List<Parameter> parameters) {
        final ServiceReport report = new ServiceReport(Type.INFO, Status.SUCCESS, "OK");

        ServiceReport sr = checkMigrateArgs(digitalObject, inputFormat, outputFormat, report);
       
        if (sr.getStatus() != report.getStatus()){
            return this.fail(sr);
        }
        
        if (outputFormat.equals(outputformatUnicode)) {
            return textMigration(digitalObject, report);
        }

        if (outputFormat.equals(outputformatHtml)) {
            return htmlMigration(digitalObject, report);
        } else {
            return this.fail(new ServiceReport(Type.ERROR,
                    Status.INSTALLATION_ERROR,
                    "The chosen output format is not supported"));
        }
    }
    
    /**
     * Migrates PDF content to text.
     * @param digitalObject A DigitalObject with a associated PDF file.
     * @param report A ServiceReport containing a status of the migration.
     * @return MigrateResult
     */
    private MigrateResult textMigration(
            final DigitalObject digitalObject, final ServiceReport report) {
        try {
            final TextExtractor te = new TextExtractor();
            final String textBlob = te.getText(
                    digitalObject.getContent().getInputStream());
            final DigitalObject.Builder factory =
                    new DigitalObject.Builder(
                            Content.byValue(textBlob.getBytes()));
            factory.title(digitalObject.getTitle() + ".txt");
            // Unicode File format (UTF-16)
            factory.format(outputformatUnicode);
            final DigitalObject textObject = factory.build();

            return new MigrateResult(textObject, report);

        } catch (Exception e) {
            e.printStackTrace();
            return this.fail(new ServiceReport(Type.ERROR, Status.TOOL_ERROR, e
                    .toString()));
        }
    }

    /**
     * Migrates PDF content to HTML.
     * @param digitalObject A DigitalObject with a associated PDF file.
     * @param report A ServiceReport containing a status of the migration.
     * @return MigrateResult
     */
    private MigrateResult htmlMigration(
            final DigitalObject digitalObject, final ServiceReport report) {
        try {
            final HtmlExtractor he = new HtmlExtractor();
            final String htmlBlob = he.getText(
                    digitalObject.getContent().getInputStream());
            final DigitalObject.Builder factory =
                    new DigitalObject.Builder(
                            Content.byValue(htmlBlob.getBytes()));
            factory.title(digitalObject.getTitle() + ".html");
            // HTML 4.0 File format
            factory.format(outputformatHtml);
            DigitalObject htmlObject = factory.build();

            return new MigrateResult(htmlObject, report);

        } catch (Exception e) {
            log.severe(e.getClass().getName()+": "+e.getMessage());
            return this.fail(new ServiceReport(Type.ERROR, Status.TOOL_ERROR, e
                    .toString()));
        }
    }

    /** Check the arguments of migrate method.
     * @param digitalObject From the migrate method.
     * @param inputFormat From the migrate method.
     * @param outputFormat From the migrate method.
     * @param report Planets ServiceReport.
     */
    private ServiceReport checkMigrateArgs(final DigitalObject digitalObject,
                                  final URI inputFormat, final URI outputFormat,
                                  final ServiceReport report) {

        if (digitalObject == null) {
            return new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
                    "An empty (null) digital object was given");
        } else if (digitalObject.getContent() == null) {
            System.out.println("Content NULL");
            return new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
                    "The content of the digital object " + "is empty (null)");
        }

        if (inputFormat == null) {
            return new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
                    "An empty (null) input object was given");
        }

        if (outputFormat == null) {
            return new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
            "An empty (null) output object was given");
        }
        return report;
    }

    /**
     * @see eu.planets_project.services.migrate.Migrate#describe()
     * @return ServiceDescription
     */
    public final ServiceDescription describe() {
        ServiceDescription.Builder builder =
                new ServiceDescription.Builder(NAME, Migrate.class.getName());
        builder.author("Claus Jensen <cjen@kb.dk>");
        builder.classname(this.getClass().getCanonicalName());
        builder.description("Extracts the textual "
                + "contents of pdf files and migrate it "
                + "to HTML 4.0 or UTF-16 text");

        Set<URI> outputformats = new HashSet<URI>();
        outputformats.add(outputformatUnicode);
        outputformats.add(outputformatHtml);

        FormatRegistry fm = FormatRegistryFactory.getFormatRegistry();
        List<MigrationPath> paths =
                MigrationPath.constructPaths(fm.getUrisForExtension("pdf"),
                        outputformats);

        builder.paths(paths.toArray(new MigrationPath[]{}));
        builder.version("0.1");

        return builder.build();
    }

    /**
     * Handles the failure of a migration.
     * @param report Planets ServiceReport containing a status of the migration.
     * @return MigrateResult.
     */
    private MigrateResult fail(final ServiceReport report) {
        return new MigrateResult(null, report);
    }
}
