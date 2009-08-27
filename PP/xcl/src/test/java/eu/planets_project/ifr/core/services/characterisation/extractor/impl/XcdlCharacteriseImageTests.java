package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

/**
 * Tests XCL Extractor characterisation of an image file (PNG).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 *
 */
public class XcdlCharacteriseImageTests extends AbstractXcdlCharacteriseTests{

    @Override
    String getInputFile() {
        return XcdlCharacteriseUnitHelper.SAMPLE_FILE_PNG;
    }

    @Override
    String getXcelFile() {
        return XcdlCharacteriseUnitHelper.SAMPLE_XCEL_PNG;
    }

}
