package eu.planets_project.ifr.core.services.identification.jhove.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification.FileType;
import eu.planets_project.services.datatypes.Types;
import eu.planets_project.services.identify.IdentifyOneBinary;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.test.ServiceCreator;

/**
 * Local and client tests of the JHOVE identification functionality.
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
        IdentifyOneBinary jhove = ServiceCreator.createTestService(
                IdentifyOneBinary.QNAME, JhoveIdentification.class,
                "/pserv-pc-jhove/JhoveIdentification?wsdl");
        System.out.println("Remote:");
        test(jhove);
    }

    /**
     * Tests a JhoveIdentification instance against the enumerated file types in
     * FileTypes (testing sample files against their expected PRONOM IDs).
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
