package eu.planets_project.services.migration.pdf2text;

import java.io.Serializable;
import java.net.URI;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;



/**
 * The Pdf2TextMigration migrates PDF files to text files.
 *
 * @author Claus Jensen <cjen@kb.dk>
 *
 */
@Local(Migrate.class)
@Remote(Migrate.class)
@Stateless
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(
        name = Pdf2TextMigration.NAME,
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate")
public class Pdf2TextMigration implements Migrate, Serializable {

    /**
     *  Used for serialization.
     */
    private static final long serialVersionUID = -1639737711901176613L;

    /**
     * The service name.
     */
    static final String NAME = "Pdf2TextMigration";

    /**
     * {@inheritDoc}
     *
     * @see eu.planets_project.services.migrate.Migrate#migrate(
     * eu.planets_project.services.datatypes.DigitalObject, java.net.URI,
     * java.net.URI, eu.planets_project.services.datatypes.Parameters)
     */

    public MigrateResult migrate(final DigitalObject digitalObject,
            URI inputFormat, URI outputFormat, Parameters parameters) {
        ServiceReport report = new ServiceReport();

        if (inputFormat != null
                && !digitalObject.getFormat().equals(inputFormat)) {
            report.setError("Object was not of the specified input format");
            return new MigrateResult(null, report);
        }

        try {
            String textBlob = PlainTextExtractor.getText(
                    digitalObject.getContent().read());
            DigitalObject.Builder factory =
                    new DigitalObject.Builder(
                            Content.byValue(textBlob.getBytes()));
            factory.title(digitalObject.getTitle() + ".txt");
            // Plain Text File format
            factory.format(new URI("info:pronom/x-fmt/111"));
            DigitalObject textObject = factory.build();

            return new MigrateResult(textObject, report);

            } catch (Exception e) {
             e.printStackTrace();
             report.setErrorState(1);
             report.setError(e.toString());
             return new MigrateResult(null, report);
         }
     }

     /**
      * @see eu.planets_project.services.migrate.Migrate#describe()
      * @return ServiceDescription
      */
    public ServiceDescription describe() {
        ServiceDescription.Builder builder =
            new ServiceDescription.Builder(NAME, Migrate.class.getName());
        builder.author("Claus Jensen <cjen@kb.dk>");
        builder.classname(this.getClass().getCanonicalName());
        builder.description("Extracts the textual "
                + "contents of pdf files to text");

        FormatRegistry fm = FormatRegistryFactory.getFormatRegistry();
        MigrationPath[] paths =
            MigrationPath.constructPaths(fm.getURIsForExtension("pdf"),
                    fm.getURIsForExtension("txt"));

        builder.paths(paths);
        builder.version("0.1");

        return builder.build();
    }
}
