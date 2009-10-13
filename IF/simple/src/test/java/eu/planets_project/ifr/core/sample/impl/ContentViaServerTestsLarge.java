package eu.planets_project.ifr.core.sample.impl;

import java.io.File;

/**
 * Test content handling via web service using a larger file (~50 MB).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ContentViaServerTestsLarge extends ContentViaServerTestsTemplate {

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.sample.impl.ContentViaServerTestsTemplate#file()
     */
    @Override
    protected File file() {
        return new File("tests/test-files/video/dx1_03.m2ts.mpg");
    }

}
