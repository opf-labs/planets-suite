package eu.planets_project.ifr.core.sample.impl;

import java.io.File;

/**
 * Test content handling via web service using a small file.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ContentViaServerTestsSmall extends ContentViaServerTestsTemplate {

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.sample.impl.ContentViaServerTestsTemplate#file()
     */
    @Override
    protected File file() {
        return new File("tests/test-files/images/bitmap/test_tiff/2274192346_4a0a03c5d6.tif");
    }

}
