/**
 * 
 */
package eu.planets_project.services.utils.wrappers;

import java.net.URI;

import javax.xml.ws.Service;

import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameters;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.migrate.BasicMigrateOneBinary;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateOneBinary;
import eu.planets_project.services.migrate.MigrateOneBinaryResult;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.PlanetsServiceExplorer;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * This is a wrapper class that upgrades all supported Migrate service
 * interfaces to the same level.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * 
 */
public class BasicMigrateWrapper implements Migrate {

    PlanetsServiceExplorer pse = null;
    Migrate m = null;
    MigrateOneBinary mob = null;
    BasicMigrateOneBinary bmob = null;

    /**
     * Construct based on a service explorer.
     * 
     * @param pse
     */
    public BasicMigrateWrapper(PlanetsServiceExplorer pse) {
        this.pse = pse;
        if (pse.getQName().equals(MigrateOneBinary.QNAME)) {
            mob = (MigrateOneBinary) Service.create(pse.getWsdlLocation(), pse
                    .getQName());
        } else if (pse.getQName().equals(BasicMigrateOneBinary.QNAME)) {
            bmob = (BasicMigrateOneBinary) Service.create(
                    pse.getWsdlLocation(), pse.getQName());
        } else {
            m = (Migrate) Service.create(pse.getWsdlLocation(), pse.getQName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {
        if (pse.getQName().equals(MigrateOneBinary.QNAME)) {
            return mob.describe();
        } else if (pse.getQName().equals(BasicMigrateOneBinary.QNAME)) {
            return new ServiceDescription(
                    "Migration Service With No Service Description", pse
                            .getServiceClass().getCanonicalName());
        } else {
            return m.describe();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project
     * .services.datatypes.DigitalObject, java.net.URI, java.net.URI,
     * eu.planets_project.services.datatypes.Parameters)
     */
    public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
            URI outputFormat, Parameters parameters) {

        // Transform the DO into a single binary, if that is sane
        byte[] binary = null;
        if (digitalObject.getContent() == null
                && digitalObject.getContained() != null) {
            ServiceUtils
                    .createErrorReport("This service cannot deal with composite digital objects.");
        }

        // Invoke the service based on the type (QName):
        if (pse.getQName().equals(MigrateOneBinary.QNAME)) {
            // Migrate One Binary:
            MigrateOneBinaryResult mobr = mob.migrate(binary, inputFormat,
                    outputFormat, parameters);

            DigitalObject ndo = new DigitalObject.Builder(digitalObject
                    .getPermanentUrl(), Content.byValue(mobr.getBinary()))
                    .build();

            return new MigrateResult(ndo, mobr.getReport());

        } else if (pse.getQName().equals(BasicMigrateOneBinary.QNAME)) {
            // Basic Migrate One Binary:
            try {
                byte[] bresult = bmob.basicMigrateOneBinary(binary);

                DigitalObject ndo = new DigitalObject.Builder(digitalObject
                        .getPermanentUrl(), Content.byValue(bresult)).build();

                return new MigrateResult(ndo, new ServiceReport());
            } catch (PlanetsException e) {
                return new MigrateResult(null, ServiceUtils
                        .createExceptionErrorReport("Binary migration failed.",
                                e));
            }

        } else {
            // Migrate:
            return m.migrate(digitalObject, inputFormat, outputFormat,
                    parameters);
        }
    }

}
