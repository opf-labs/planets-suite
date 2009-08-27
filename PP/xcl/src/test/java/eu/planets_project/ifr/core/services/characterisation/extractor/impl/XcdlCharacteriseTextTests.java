package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

/**
 * Tests XCL Extractor characterisation of a text file (PDF).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 *
 */
public class XcdlCharacteriseTextTests extends AbstractXcdlCharacteriseTests{

    @Override
    String getInputFile() {
        return XcdlCharacteriseUnitHelper.SAMPLE_FILE_PDF;
    }

    @Override
    String getXcelFile() {
        return XcdlCharacteriseUnitHelper.SAMPLE_XCEL_PDF;
    }

}
