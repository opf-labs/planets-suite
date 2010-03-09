package eu.planets_project.ifr.core.simple.impl;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;

import org.jboss.annotation.ejb.TransactionTimeout;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.servreg.utils.client.wrappers.MigrateWrapper;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * PassThruMigrationService testing service. This service does nothing except to implement the Migrate interface to
 * allow real-world testing of digital objects.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(
        name = PassThruMigrationService.NAME, serviceName = Migrate.NAME, 
        targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.migrate.Migrate")
@MTOM
@StreamingAttachment(parseEagerly=true, memoryThreshold=(long)ServiceUtils.JAXWS_SIZE_THRESHOLD)
//@TransactionTimeout(100000)
public final class PassThruMigrationService implements Migrate, Serializable {
    /** The service name. */
    static final String NAME = "PassThruMigrationService";

    /** The unique class id. */
    private static final long serialVersionUID = 2127494848765937613L;

    /** */
    private static final Logger log = Logger.getLogger(PassThruMigrationService.class.getName());

    /** */
    @Resource WebServiceContext wsc;

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project.services.datatypes.DigitalObject,
     *      java.net.URI, java.net.URI, eu.planets_project.services.datatypes.Parameter)
     */
    public MigrateResult migrate(final DigitalObject digitalObject, URI inputFormat, final URI outputFormat,
            final List<Parameter> parameters) {
        MessageContext mc = wsc.getMessageContext();
        // Resource wsc has been initialized by JAX-WS, thus 
        // fixing the bug with the streaming data (!!!)
        // log the message context
        log.info("Service message context:");
        for( String k : mc.keySet() ) {
            log.info("  " + k + " = " + mc.get(k) );
        }
        
        /*
         * We just return a new digital object with the same required arguments as the given:
         */
        
        // Clone the content:
        File tmpFile = FileUtils.getTempFile("digital-object-content", "bin");
        FileUtils.writeInputStreamToFile(
                digitalObject.getContent().getInputStream(), tmpFile );
        
        // Create a new Digital Object, based on the old one:
        DigitalObject newDO = new DigitalObject.Builder(digitalObject).content(Content.byReference(tmpFile)).build();
        
        boolean success = newDO != null;
        ServiceReport report;
        if (success) {
            report = new ServiceReport(ServiceReport.Type.INFO, ServiceReport.Status.SUCCESS,
                    "Passed through");
            System.out.println("Passing back: " + newDO.getContent().length() + " bytes");
        } else {
            report = new ServiceReport(Type.ERROR, Status.TOOL_ERROR, "Null result");
        }
        MigrateResult migrateResult = new MigrateResult(newDO, report);
        System.out.println("Pass-through migration: " + migrateResult);
        return migrateResult;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.PlanetsService#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder mds = new ServiceDescription.Builder(NAME, Migrate.class.getCanonicalName());
        mds.description("A pass-thru test service, that simply clones and passes data through unchanged.");
        mds.author("Fabian Steeg <fabian.steeg@uni-koeln.de>, Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.classname(this.getClass().getCanonicalName());
        return mds.build();
    }

}
