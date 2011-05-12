package eu.planets_project.ifr.core.sample.impl;

import java.io.File;

/**
 * Test content handling via web service using a medium sized file (~10 MB).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ContentViaServerTestsMedium extends ContentViaServerTestsTemplate {

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.sample.impl.ContentViaServerTestsTemplate#file()
     */
    @Override
    protected File file() {
        return new File("tests/test-files/databases/crm.mdb");
    }

}
