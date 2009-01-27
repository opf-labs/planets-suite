package eu.planets_project.services.migration.pdf2html;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebParam;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.RequestWrapper;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.*;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.ifr.core.techreg.api.formats.Format;


@Local(Migrate.class)
@Remote(Migrate.class)
@Stateless

@WebService(/*name = Pdf2HtmlMigration.NAME,*/
        serviceName = Migrate.NAME,
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.migrate.Migrate" )
public class Pdf2HtmlMigration implements Migrate, Serializable {

    /** The service name */
    static final String NAME = "Pdf2HtmlMigration";



    /**
     * {@inheritDoc}
     *
     * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameters)
     */
    @WebMethod(operationName = Migrate.NAME, action = PlanetsServices.NS
            + "/" + Migrate.NAME)
    @WebResult(name = Migrate.NAME + "Result", targetNamespace = PlanetsServices.NS
            + "/" + Migrate.NAME, partName = Migrate.NAME
            + "Result")
    @RequestWrapper(className="eu.planets_project.services.migrate."+Migrate.NAME+"Migrate")
    @ResponseWrapper(className="eu.planets_project.services.migrate."+Migrate.NAME+"MigrateResponse")
    public MigrateResult migrate(
            @WebParam(name = "digitalObject", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "digitalObject")
            final DigitalObject digitalObject,
            @WebParam(name = "inputFormat", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "inputFormat")
            URI inputFormat,
            @WebParam(name = "outoutFormat", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "outputFormat")
            URI outputFormat,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "parameters")
            Parameters parameters ){

        ServiceReport report = new ServiceReport();


        if (inputFormat!= null && !digitalObject.getFormat().equals(inputFormat)){
            report.setError("Object was not of the specified input format");
            return new MigrateResult(null,report);
        }
        try {
            String htmlBlob = TextExtractor.getText(digitalObject.getContent().read());
            DigitalObject.Builder factory = new DigitalObject.Builder(Content.byValue(htmlBlob.getBytes()));
            factory.title(digitalObject.getTitle()+".html");
            factory.permanentUrl(new URL("http://example.com/test.html"));
            factory.format(Format.extensionToURI("html"));

            DigitalObject htmlObject = factory.build();
            return new MigrateResult(htmlObject,report);

        } catch (Exception e) {
            e.printStackTrace();
            report.setErrorState(1);
            report.setError(e.toString());
            return new MigrateResult(null,report);
        }
    }

    /**
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    @WebMethod(operationName = Migrate.NAME + "_" + "describe", action = PlanetsServices.NS + "/" + Migrate.NAME + "/" + "describe")
    @WebResult(name = Migrate.NAME + "Description", targetNamespace = PlanetsServices.NS + "/" + Migrate.NAME, partName = Migrate.NAME + "Description")
    @ResponseWrapper(className = "eu.planets_project.services.migrate." + Migrate.NAME + "DescribeResponse")
    public ServiceDescription describe() {

        ServiceDescription.Builder builder = new ServiceDescription.Builder(NAME, Migrate.class.getName());

        builder.author("Claus Jensen <cjen@kb.dk>");
        builder.classname(this.getClass().getCanonicalName());
        builder.description("Extracts the textual contents of pdf files to html");

        /* TODO There are many uris for pdf. How is this handled?*/
        MigrationPath[] mPaths = new MigrationPath []{
                new MigrationPath(Format.extensionToURI("pdf"), Format.extensionToURI("html"),null)
        };


        builder.paths(mPaths);
        builder.version("0.1");

        return builder.build();


    }


}
