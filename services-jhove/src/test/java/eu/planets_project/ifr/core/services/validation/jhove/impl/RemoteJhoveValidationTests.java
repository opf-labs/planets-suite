package eu.planets_project.ifr.core.services.validation.jhove.impl;

import org.junit.BeforeClass;

import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.validate.Validate;

/**
 * Client tests of the JHOVE validation functionality.
 * @author Fabian Steeg
 */
public final class RemoteJhoveValidationTests extends JhoveValidationTests {

    /**
     * Tests JHOVE validation using a JhoveValidation instance retrieved via the
     * web service.
     */
    @BeforeClass
    public static void setup() {
        System.out.println("Remote:");
        jhove = ServiceCreator.createTestService(Validate.QNAME,
                JhoveValidation.class, "/pserv-pc-jhove/JhoveValidation?wsdl");
    }

}
