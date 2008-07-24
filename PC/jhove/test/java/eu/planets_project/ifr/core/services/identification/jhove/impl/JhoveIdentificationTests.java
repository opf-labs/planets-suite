package eu.planets_project.ifr.core.services.identification.jhove.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.common.services.ByteArrayHelper;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.datatypes.Types;
import eu.planets_project.ifr.core.common.services.identify.IdentifyOneBinary;
import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification.FileType;

/**
 * Local and client tests of the JHOVE identification functionality.
 * 
 * @author Fabian Steeg
 */
public final class JhoveIdentificationTests {

    /**
     * Tests JHOVE identification using a local JhoveIdentification instance.
     */
    @Test
    public void localTests() {
        System.out.println("Local:");
        test(new JhoveIdentification());
    }

    /**
     * Tests JHOVE identification using a JhoveIdentification instance retrieved
     * via the web service running on localhost.
     */
    @Test
    public void clientTests() {
        URL url = null;
        try {
            url = new URL(
                    "http://localhost:8080/pserv-pc-jhove/JhoveIdentification?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service = Service.create(url, new QName(PlanetsServices.NS,
                IdentifyOneBinary.NAME));
        IdentifyOneBinary jHove = service.getPort(IdentifyOneBinary.class);
        System.out.println("Remote:");
        test(jHove);
    }

    /**
     * Tests a JhoveIdentification instance against the enumerated file types in
     * FileTypes (testing sample files against their expected PRONOM IDs).
     * 
     * @param identification The JhoveIdentification instance to test
     */
    private void test(final IdentifyOneBinary identification) {
        /* We check all the enumerated file types: */
        for (FileType type : FileType.values()) {
            System.out.println("Testing identification of: " + type);
            /* For each we get the sample file: */
            String location = type.getSample();
            /* And try identifying it: */
            Types result = identification.identifyOneBinary(ByteArrayHelper
                    .read(new File(location)));
            assertEquals("Wrong pronom ID;", type.getPronom(), result.types[0]
                    .toString());
        }
    }

}
