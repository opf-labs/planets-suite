/**
 * 
 */
package eu.planets_project.services.migrate;

import java.net.URI;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.soap.MTOM;
import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.services.PlanetsService;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * Migration of one digital object.
 * 
 * This is intended to become the generic migration interface for complex migration services.
 * 
 * It should:
 *  - Support service description to facilitate discovery.
 *  - Allow multiple input formats and output formats to be dealt with be the same service.
 *  - Allow parameters to be discovered and submitted to control the migration.
 *  - Allow digital objects composed of more than one file/bitstream.
 *  - Allow Files/bitstreams passed by value OR by reference.
 *  
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de), Andrew Jackson <Andrew.Jackson@bl.uk>
 */
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
@WebService(
        name = Migrate.NAME, 
        targetNamespace = PlanetsServices.NS)
public interface Migrate extends PlanetsService {
    /** The interface name */
    String NAME = "Migrate";
    /** The qualified name */
    QName QNAME = new QName(PlanetsServices.NS, Migrate.NAME);

    /**
     * Migrate one digital object from inputFormat to outputFormat.
     *  
     *
     * Note: The migration should ignore the formatURI specified in the digital
     * object.
     *
     * @param digitalObject The digital object to migrate
     * @param inputFormat the initial format (migrate from)
     * @param outputFormat the required format (migrate to)
     * @param parameters a list of parameters to provide fine grained tool control
     * @return A new digital object, the result of migrating the given digital
     *         object
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
            @WebParam(name = "outputFormat", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "outputFormat") 
                URI outputFormat,
            @WebParam(name = "parameters", targetNamespace = PlanetsServices.NS
                    + "/" + Migrate.NAME, partName = "parameters") 
                List<Parameter> parameters );
}
