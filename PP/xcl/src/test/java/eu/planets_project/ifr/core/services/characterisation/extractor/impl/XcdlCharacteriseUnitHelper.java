package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import eu.planets_project.services.PlanetsServices;

/**
 * Utility class for the extractor tests.
 * 
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 * 
 */
public final class XcdlCharacteriseUnitHelper {
    /** Local host address of the JBoss instance. */
    static final String LOCALHOST = "http://localhost:8080";
    /** Test server address of the JBoss instance. */
    static final String PLANETARIUM = "http://planetarium.hki.uni-koeln.de:8080";
    /** System Temp folder */
    private static final String SYSTEM_TEMP = System.getProperty("java.io.tmpdir");
    
    //private static String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME") + File.separator;
    
    /***/
    static final String BASIC_EXTRACTOR2BINARY_TEST_OUT = SYSTEM_TEMP + File.separator + "BASIC_EXTRACTOR2BINARY_TEST_OUT";
    
    static final String EXTRACTOR2URI_OUTPUT_DIR = SYSTEM_TEMP + File.separator + "EXTRACTOR2URI_TEST_OUT";
    
    static final String EXTRACTOR_LOCAL_TEST_OUT = "EXTRACTOR_LOCAL_TEST_OUT";
    
    static final String EXTRACTOR_SERVER_TEST_OUT = "EXTRACTOR_SERVER_TEST_OUT";
    
    static final String EXTRACTOR_STANDALONE_TEST_OUT = "EXTRACTOR_STANDALONE_TEST_OUT";
           
    static final String SAMPLE_FILE = "PP/xcl/src/test/resources/sample_files/bgai4a16.png";

    static final String SAMPLE_XCEL = "PP/xcl/src/test/resources/sample_files/xcel_png.xml";

    /**
     * We enforce non-instantiability with a private constructor.
     */
    private XcdlCharacteriseUnitHelper() {}

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

