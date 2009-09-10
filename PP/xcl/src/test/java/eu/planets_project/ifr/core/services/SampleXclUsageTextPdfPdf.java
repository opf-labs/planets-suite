package eu.planets_project.ifr.core.services;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;

/**
 * Sample XCL services usage implementation using text files.
 * @see AbstractSampleXclUsage
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class SampleXclUsageTextPdfPdf extends AbstractSampleXclUsage {

    /* Given the input file, the output file and a comparator config file: */
    private static final String RES = "PP/xcl/src/test/resources/";
    private static final String SAMPLES = RES + "sample_files/basi0g01.";
    private static final String PDF = "pdf";
    private static final String PDF_FILE = SAMPLES + PDF;
    private static final String COCO = RES + "cocoText.xml";

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#files()
     */
    @Override
    protected DigitalObject[] files() {
        return new DigitalObject[] {
                new DigitalObject.Builder(Content.byReference(new File(PDF_FILE))).format(ids()[0]).build(),
                new DigitalObject.Builder(Content.byReference(new File(PDF_FILE))).format(ids()[1]).build() };
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#ids()
     */
    @Override
    protected URI[] ids() {
        /* We get a PRONOM ID for the original and the converted file: */
        URI uri = REGISTRY.getUrisForExtension(PDF).iterator().next();
        return new URI[] { uri, uri }; // compare PDF to PDF
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.AbstractSampleXclUsage#config()
     */
    @Override
    protected DigitalObject config() {
        return new DigitalObject.Builder(Content.byReference(new File(COCO))).build();
    }

    @Override
    protected List<List<Parameter>> parameters() {
        ArrayList<List<Parameter>> list = new ArrayList<List<Parameter>>();
        list.add(null);
        list.add(null); // no parameters for PDF extraction
        return list;
    }

}
