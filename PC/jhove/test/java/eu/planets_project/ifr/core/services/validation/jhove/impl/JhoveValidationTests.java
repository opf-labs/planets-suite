package eu.planets_project.ifr.core.services.validation.jhove.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification.FileType;
import eu.planets_project.services.PlanetsException;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.validate.BasicValidateOneBinary;

/**
 * Local and client tests of the JHOVE validation functionality.
 * 
 * @author Fabian Steeg
 */
public final class JhoveValidationTests {

    /**
     * Tests JHOVE validation using a local JhoveValidation instance.
     */
    @Test
    public void localTests() {
        System.out.println("Local:");
        test(new JhoveValidation());
    }

    /**
     * Tests JHOVE validation using a JhoveValidation instance retrieved via the
     * web service running on localhost.
     */
    @Test
    public void clientTests() {
        URL url = null;
        try {
            url = new URL(
                    "http://localhost:8080/pserv-pc-jhove/JhoveValidation?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service = Service.create(url, new QName(PlanetsServices.NS,
                BasicValidateOneBinary.NAME));

        BasicValidateOneBinary jHove = service
                .getPort(BasicValidateOneBinary.class);
        System.out.println("Remote:");
        test(jHove);
    }

    /**
     * Tests a JhoveValidation instance against the enumerated file types in
     * FileTypes (testing sample files against their expected PRONOM IDs).
     * 
     * @param validation The JhoveValidation instance to test
     */
    private void test(final BasicValidateOneBinary validation) {
        /* We check all the enumerated file types: */
        for (FileType type : FileType.values()) {
            System.out.println("Testing validation of: " + type);
            /* For each we get the sample file: */
            String location = type.getSample();
            /* And try validating it: */
            boolean result = false;
            try {
                result = validation.basicValidateOneBinary(ByteArrayHelper
                        .read(new File(location)), new URI(type.getPronom()));
            } catch (PlanetsException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            assertTrue("Not validated: " + type, result);
        }
    }

}
