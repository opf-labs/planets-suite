package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.utils.FileUtils;

/**
 * XCDL Comparator service.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService(name = Comparator.NAME, serviceName = Compare.NAME, targetNamespace = PlanetsServices.NS, endpointInterface = "eu.planets_project.services.compare.Compare")
@Stateless
public final class Comparator implements Compare {
    /***/
    static final String NAME = "Comparator";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.Compare#compare(eu.planets_project.services.datatypes.DigitalObject[],
     *      eu.planets_project.services.datatypes.DigitalObject)
     */
    public CompareResult compare(final DigitalObject[] objects,
            final DigitalObject config) {
        String xcdl = read(Arrays.asList(objects[0])).get(0);
        List<String> xcdls = read(Arrays.asList(objects).subList(1,
                objects.length));
        String pcr = read(Arrays.asList(config)).get(0);
        String result = ComparatorWrapper.compare(xcdl, xcdls, pcr);
        return new CompareResult(new DigitalObject.Builder(Content
                .byValue(result.getBytes())).build(), new ServiceReport());
    }

    /**
     * @param list The list of digital objects
     * @return A list of strings representing the content of the digital objects
     */
    private List<String> read(final List<DigitalObject> list) {
        List<String> result = new ArrayList<String>();
        for (DigitalObject digitalObject : list) {
            InputStream stream = digitalObject.getContent().read();
            String content = new String(FileUtils
                    .writeInputStreamToBinary(stream));
            result.add(content);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.compare.Compare#describe()
     */
    public ServiceDescription describe() {
        /* TODO: Set input formats to XCDL PUID when there is one. */
        return new ServiceDescription.Builder("XCL Comparator", Compare.class
                .getName()).classname(Compare.class.getName()).author(
                "Fabian Steeg").description("XCDL Comparison Service")
                .serviceProvider("The Planets Consortium").build();
    }
}
