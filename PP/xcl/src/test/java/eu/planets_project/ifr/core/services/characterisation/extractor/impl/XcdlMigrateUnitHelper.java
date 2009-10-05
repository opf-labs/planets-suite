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
public final class XcdlMigrateUnitHelper {
    private static final String RESOURCES = "PP/xcl/src/test/resources/";
    /** Local host address of the JBoss instance. */
    //static final String LOCALHOST = "http://localhost:8080";
    /** Test server address of the JBoss instance. */
    //static final String PLANETARIUM = "http://planetarium.hki.uni-koeln.de:8080";
    /** System Temp folder */
    
    //private static String EXTRACTOR_HOME = System.getenv("EXTRACTOR_HOME") + File.separator;
    
    /***/
    static final String XCDL_EXTRACTOR_LOCAL_TEST_OUT = "XCDL_EXTRACTOR_LOCAL_TEST_OUT";
    static final String XCDL_EXTRACTOR_SERVER_TEST_OUT = "XCDL_EXTRACTOR_SERVER_TEST_OUT";
    static final String XCDL_EXTRACTOR_STANDALONE_TEST_OUT = "XCDL_EXTRACTOR_STANDALONE_TEST_OUT";
    static final File TIFF_INPUT = new File(RESOURCES + "test_samples/tiff/jello.tif");
    static final File BMP_INPUT = new File(RESOURCES + "test_samples/bmp/TRU256.BMP");
    static final File GIF_INPUT = new File(RESOURCES + "test_samples/gif/spaldingbasketball.gif");
    static final File PDF_INPUT = new File(RESOURCES + "test_samples/pdf/SizeTest.pdf");
    static final File JPEG_INPUT = new File(RESOURCES + "test_samples/jpeg/sanyo-vpcg250.jpg");
    static final File PNG_INPUT = new File(RESOURCES + "test_samples/png/basi4a16.png");
    static final File STRANGE_TIFF_FILE = new File("PA/imagemagick/test/resources/test_images/test_tiff/strange_tiff.tif");
    static final File TIFF_XCEL = new File(RESOURCES + "test_samples/xcel/xcel_tiff.xml"); 
    static final File BMP_XCEL = new File(RESOURCES
            + "test_samples/xcel/xcel_imageMagick.xml");
    static final File GIF_XCEL = new File(RESOURCES
            + "test_samples/xcel/xcel_imageMagick.xml");
    static final File PDF_XCEL = new File(RESOURCES + "test_samples/xcel/xcel_pdf.xml");
    static final File JPEG_XCEL = new File(RESOURCES
            + "test_samples/xcel/xcel_imageMagick.xml");
    static final File PNG_XCEL = new File(RESOURCES + "test_samples/xcel/xcel_png.xml");
    
//    static final File DOC_XCEL = new File();
    

    /**
     * We enforce non-instantiability with a private constructor.
     */
    private XcdlMigrateUnitHelper() {}

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

