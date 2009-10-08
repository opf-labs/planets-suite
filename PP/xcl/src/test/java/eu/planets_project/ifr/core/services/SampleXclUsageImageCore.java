package eu.planets_project.ifr.core.services;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;

/**
 * Sample XCL services usage implementation using image files directly supported by the extractor (PNG, TIFF).
 * @see AbstractSampleXclUsage
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class SampleXclUsageImageCore extends AbstractSampleXclUsage {

    /* Given the input file, the output file and a comparator config file: */
    private static final String RES = "PP/xcl/src/test/resources/";
    private static final String SAMPLES = RES + "sample_files/basketball.";
    private static final String PNG = "png";
    private static final String TIFF = "tiff";
    private static final String PNG_FILE = SAMPLES + PNG;
    private static final String TIFF_FILE = SAMPLES + TIFF;
    private static final String COCO = RES + "cocoImage.xml";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#files()
     */
    @Override
    protected DigitalObject[] files() {
        return new DigitalObject[] {
                new DigitalObject.Builder(Content.byReference(new File(PNG_FILE))).format(ids()[0]).build(),
                new DigitalObject.Builder(Content.byReference(new File(TIFF_FILE))).format(ids()[1]).build() };
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#ids()
     */
    @Override
    protected URI[] ids() {
        /* We get a PRONOM ID for the original and the converted file: */
        return new URI[] { REGISTRY.getUrisForExtension(PNG).iterator().next(),
                REGISTRY.getUrisForExtension(TIFF).iterator().next() };
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#config()
     */
    @Override
    protected DigitalObject config() {
        return new DigitalObject.Builder(Content.byValue(new File(COCO))).build();
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#parameters()
     */
    @Override
    protected List<List<Parameter>> parameters() {
        ArrayList<List<Parameter>> list = new ArrayList<List<Parameter>>();
        list.add(null); // no parameters for PNG extraction
        list.add(null); // no parameters for TIFF extraction
        return list;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#checkCompareResult(eu.planets_project.services.compare.CompareResult)
     */
    @Override
    protected void checkCompareResult(CompareResult result) {
        Assert.assertTrue("Image comparison result should only contain top-level results", result.getProperties()
                .size() > 0
                && result.getResults().size() == 0);

    }

}
