package eu.planets_project.services.shotgun;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.modify.ModifyResult;
import eu.planets_project.services.shotgun.FileShotgun.Action;
import eu.planets_project.services.shotgun.FileShotgun.Key;
import eu.planets_project.services.utils.DigitalObjectUtils;

/**
 * Tests and sample usage for the ShotgunModify service.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ShotgunModifyTests {
    private static final File INPUT_FILE = new File(
            "tests/test-files/images/bitmap/test_tiff/2274192346_4a0a03c5d6.tif");
    private static final DigitalObject INPUT_DIGITAL_OBJECT = new DigitalObject.Builder(Content.byReference(INPUT_FILE))
            .build();
    private static final byte[] INPUT_BYTES = read(INPUT_FILE);
    private static final byte[] WRITE_RESULT = shotgun(Action.CORRUPT);
    private static final byte[] DELETE_RESULT = shotgun(Action.DROP);

    /**
     * Sample usage for the ShotgunModify service.
     * @param shotgunAction The action to apply to the input format
     * @return The ModifyResult object (see below on how to use it)
     */
    private static ModifyResult sampleUsage(final Action shotgunAction) {
        /* Configure the shotgun: */
        Parameter count = new Parameter(Key.SEQ_COUNT.toString(), "5");
        Parameter length = new Parameter(Key.SEQ_LENGTH.toString(), "15");
        Parameter action = new Parameter(Key.ACTION.toString(), shotgunAction.toString());
        /* Instantiate the shotgun and modify the file without parameters: */
        ModifyResult modify = new ShotgunModify().modify(INPUT_DIGITAL_OBJECT, null, null);
        /* Instantiate the shotgun and modify the file with parameters: */
        modify = new ShotgunModify().modify(INPUT_DIGITAL_OBJECT, null, Arrays.asList(count, length, action));
        return modify;
    }

    /**
     * @param shotgunAction The action to apply to the input format
     * @return The bytes of the resulting file; used below in the tests
     */
    private static byte[] shotgun(final Action shotgunAction) {
        Assert.assertTrue(INPUT_FILE != null && INPUT_FILE.exists());
        ModifyResult modify = sampleUsage(shotgunAction);
        /* Check that we have a result: */
        Assert.assertNotNull("Result object is null", modify);
        DigitalObject outputDigitalObject = modify.getDigitalObject();
        Assert.assertNotNull("Result digital object is null", modify);
        File resultFile = DigitalObjectUtils.toFile(outputDigitalObject);
        Assert.assertNotNull("Result file is null", resultFile);
        Assert.assertTrue("Result file does not exist", resultFile.exists());
        /* Return the bytes of the resulting file (used in the tests below) */
        try {
            return FileUtils.readFileToByteArray(resultFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testDescription() {
        ServiceDescription serviceDescription = new ShotgunModify().describe();
        Assert.assertNotNull("Service description must not be null", serviceDescription);
        Assert.assertEquals("Shotgun should accept any input format", FormatRegistryFactory.getFormatRegistry()
                .createAnyFormatUri(), serviceDescription.getInputFormats().get(0));
        Assert.assertEquals("Shotgun should have a classname set", ShotgunModify.class.getName(), serviceDescription
                .getClassname());
    }

    @Test
    public void testWriteActionChangedBytes() {
        assertArraysNotEqual(INPUT_BYTES, WRITE_RESULT);
    }

    @Test
    public void testWriteActionDidNotChangeLength() {
        Assert.assertEquals(INPUT_BYTES.length, WRITE_RESULT.length);
    }

    @Test
    public void testDeleteActionChangedBytes() {
        assertArraysNotEqual(INPUT_BYTES, DELETE_RESULT);
    }

    @Test
    public void testDeleteActionChangedLength() {
        Assert.assertNotSame(INPUT_BYTES.length, DELETE_RESULT.length);
    }

    private void assertArraysNotEqual(byte[] one, byte[] two) {
        if (one.length != two.length) {
            return;
        }
        for (int i = 0; i < one.length; i++) {
            if (one[i] != two[i]) {
                return;
            }
        }
        Assert.fail("Byte arrays are equal");
    }
    
    private static byte[] read(final File inputFile) {
        try {
            return FileUtils.readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
