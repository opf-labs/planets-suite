package eu.planets_project.ifr.core.services.characterisation.fits.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.junit.Assert;
import org.junit.Test;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.utils.test.ServiceCreator;
import eu.planets_project.services.utils.test.TestFile;

/**
 * Tests for {@link FitsCharacterise}.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class FitsCharacteriseTests {
    private static final File INPUT = new File("tests/test-files/images/bitmap/test_gif/test10.gif");
    private static final Characterise FITS = ServiceCreator.createTestService(Characterise.QNAME,
            FitsCharacterise.class, "/pserv-pc-fits/FitsCharacterise?wsdl");

    @Test
    public void image() {
        DigitalObject input = new DigitalObject.Builder(Content.byReference(INPUT)).build();
        CharacteriseResult result = FITS.characterise(input, null);
        Assert.assertNotNull("Result object should not be null", result);
        Assert.assertEquals(Status.SUCCESS, result.getReport().getStatus());
        System.err.println(result.getProperties());
    }

    @Test
    public void all() throws FitsException {
        TestFile[] files = TestFile.values();
        for (TestFile file : files) {
            String location = file.getLocation();
            DigitalObject digitalObject = new DigitalObject.Builder(Content.byReference(new File(
                    location))).build();
            System.out.println("Examined: " + file + " as: "
                    + FITS.characterise(digitalObject, null).getProperties());
        }
    }

    @Test
    public void api() throws FitsException, IOException {
        Fits fits = new Fits(FitsCharacterise.FITS);
        FitsOutput result = fits.examine(INPUT);
        Document fullResult = result.getFitsXml();
        System.out.println("Full result: " + fullResult);
        if (result.hasMetadataElement("name")) {
            List<FitsMetadataElement> elements = result.getMetadataElements("name");
            System.out.println("Name elements: " + elements);
        }
        System.out.println("File info: " + readable(result.getFileInfoElements()));
        System.out.println("File status: " + readable(result.getFileStatusElements()));
        System.out.println("Technical type: " + result.getTechMetadataType());
        System.out.println("Technical data: " + readable(result.getTechMetadataElements()));
        System.out.println("Conflict in name: " + result.hasConflictingMetadataElements("name"));
    }

    private String readable(final List<FitsMetadataElement> techMetadataElements) {
        StringBuilder builder = new StringBuilder();
        for (FitsMetadataElement element : techMetadataElements) {
            builder.append(
                    String.format("[ %s=%s by %s %s (status %s) ]", element.getName(),
                            element.getValue(), element.getReportingToolName(),
                            element.getReportingToolVersion(), element.getStatus())).append(" ");
        }
        return builder.toString().trim();
    }
}
