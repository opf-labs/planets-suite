package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

import eu.planets_project.ifr.core.storage.api.DataRegistryAccessHelper;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.BasicCompareTwoXcdlReferences;
import eu.planets_project.services.compare.BasicCompareTwoXcdlValues;
import eu.planets_project.services.compare.CompareMultipleXcdlReferences;
import eu.planets_project.services.compare.CompareMultipleXcdlValues;
import eu.planets_project.services.utils.ByteArrayHelper;

/**
 * Local and client tests of the comparator services by reference functionality.
 * 
 * @author Fabian Steeg
 */
public final class ComparatorServiceByReferenceTests extends
        ComparatorServiceTests {

    /**
     * Tests the services that use references into the IF data registry.
     * 
     * @param server The server to use
     * @param data1 The XCDL1 data
     * @param data2 The XCDL2 data
     * @param configData The config data
     */
    @Override
    protected void testServices(final String server, final byte[] data1,
            final byte[] data2, final byte[] configData) {
        /* Set up the testing data in the data registry: */
        DataRegistryAccessHelper helper = new DataRegistryAccessHelper(server);
        URI xcdl1Uri = helper.write(data1, "xcdl1.xcdl",
                "PP-Comparator-Service");
        URI xcdl2Uri = helper.write(data2, "xcdl2.xcdl",
                "PP-Comparator-Service");
        URI configUri = helper.write(configData, "config.xml",
                "PP-Comparator-Service");
        /* Test of the TWO REFERENCES service: */
        BasicCompareTwoXcdlReferences c1 = serviceFrom(server,
                BasicCompareTwoXcdlReferences.class);
        URI resultUri = c1.basicCompareTwoXcdlReferences(xcdl1Uri, xcdl2Uri);
        checkDataRegistryForResult(helper, resultUri);
        /* Test of the MULTI REFERENCES service: */
        CompareMultipleXcdlReferences c2 = serviceFrom(server,
                CompareMultipleXcdlReferences.class);
        resultUri = c2.compareMultipleXcdlReferences(new URI[] { xcdl1Uri,
                xcdl2Uri }, configUri);
        checkDataRegistryForResult(helper, resultUri);
    }

    /**
     * @param helper The data registry access helper to use for checking
     * @param resultUri The URI of the supposed result inside the IF data
     *        registry
     */
    private void checkDataRegistryForResult(
            final DataRegistryAccessHelper helper, final URI resultUri) {
        byte[] resultData = helper.read(resultUri.toASCIIString());
        checkResult(new String(resultData));
    }
}
