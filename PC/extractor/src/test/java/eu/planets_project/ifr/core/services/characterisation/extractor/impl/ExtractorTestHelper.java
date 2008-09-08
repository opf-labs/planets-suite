package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import eu.planets_project.ifr.core.common.services.PlanetsServices;

/**
 * Utility class for the extractor tests.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 * 
 */
public final class ExtractorTestHelper {
    /** Local host address of the JBoss instance. */
    static final String LOCALHOST = "http://localhost:8080";
    /** Test server address of the JBoss instance. */
    static final String PLANETARIUM = "http://planetarium.hki.uni-koeln.de:8080";
    /***/
    static final String CLIENT_OUTPUT_DIR = "PC/extractor/src/resources/"
            .replaceAll("/", File.separator)
            + "output";
    /***/
    static final String SAMPLE_FILE = "PC/extractor/src/resources/basi0g08.png"
            .replaceAll("/", File.separator);
    /***/
    static final String SAMPLE_XCEL = "PC/extractor/src/resources/xcel_png.xml"
            .replaceAll("/", File.separator);

    /**
     * We enforce non-instantiability with a private constructor.
     */
    private ExtractorTestHelper() {}

    /**
     * @param <T> The interface type of the instance to retrieve
     * @param location The location of the service endpoint
     * @param clazz The class of the instance to create
     * @return Returns an instance of the given class created from the given
     *         endpoint
     */
    static <T> T getRemoteInstance(final String location, final Class<T> clazz) {
        URL url = null;
        try {
            url = new URL(location);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service service = Service.create(url, new QName(PlanetsServices.NS,
                clazz.getSimpleName()));
        T instance = service.getPort(clazz);
        return instance;
    }

}
