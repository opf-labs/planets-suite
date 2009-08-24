package eu.planets_project.ifr.core.services;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;

/**
 * Sample XCL services usage implementation using image files.
 * @see AbstractSampleXclUsage
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class SampleXclUsageImage extends AbstractSampleXclUsage {

    /* Given the input file, the output file and a comparator config file: */
    private static final String RES = "PP/xcl/src/test/resources/";
    private static final String SAMPLES = RES + "sample_files/basketball.";
    private static final String GIF = "gif";
    private static final String JPG = "jpg";
    private static final String GIF_FILE = SAMPLES + GIF;
    private static final String JPG_FILE = SAMPLES + JPG;
    private static final String COCO = RES + "cocoImage.xml";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#files()
     */
    @Override
    protected DigitalObject[] files() {
        return new DigitalObject[] {
                new DigitalObject.Builder(Content.byReference(new File(GIF_FILE))).format(ids()[0]).build(),
                new DigitalObject.Builder(Content.byReference(new File(JPG_FILE))).format(ids()[1]).build() };
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#ids()
     */
    @Override
    protected URI[] ids() {
        /* We get a PRONOM ID for the original and the converted file: */
        return new URI[] { REGISTRY.getUrisForExtension(GIF).iterator().next(),
                REGISTRY.getUrisForExtension(JPG).iterator().next() };
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#config()
     */
    @Override
    protected DigitalObject config() {
        return new DigitalObject.Builder(Content.byReference(new File(COCO))).build();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#parameters()
     */
    @Override
    protected List<List<Parameter>> parameters() {
        ArrayList<List<Parameter>> list = new ArrayList<List<Parameter>>();
        list.add(null); // no parameters for GIF extraction
        list.add(null); // no parameters for JPG extraction
        return list;
    }

}
