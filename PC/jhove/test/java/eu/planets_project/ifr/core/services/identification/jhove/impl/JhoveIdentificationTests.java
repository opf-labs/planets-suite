package eu.planets_project.ifr.core.services.identification.jhove.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.ifr.core.services.identification.jhove.impl.JhoveIdentification.FileType;
import eu.planets_project.services.datatypes.Types;
import eu.planets_project.services.identify.IdentifyOneBinary;
import eu.planets_project.services.utils.ByteArrayHelper;

/**
 * Tests of the JHOVE identification functionality.
 * @author Fabian Steeg
 */
public class JhoveIdentificationTests {
    static IdentifyOneBinary jhove;

    /**
     * Tests JHOVE identification using a local JhoveIdentification instance.
     */
    @BeforeClass
    public static void setup() {
        System.out.println("Local:");
        jhove = new JhoveIdentification();
    }

    @Test
    public void testAiff() {
        test(FileType.AIFF);
    }

    @Test
    public void testAscii() {
        test(FileType.ASCII);
    }

    @Test
    public void testGif() {
        test(FileType.GIF);
    }

    @Test
    public void testHtml() {
        test(FileType.HTML);
    }

    @Test
    public void testJpeg() {
        test(FileType.JPEG);
    }

    @Test
    public void testPdf() {
        test(FileType.PDF);
    }

    @Test
    public void testTiff() {
        test(FileType.TIFF);
    }

    @Test
    public void testWave() {
        test(FileType.WAVE);
    }

    @Test
    public void testXml() {
        test(FileType.XML);
    }

    /**
     * The old approach: iterate over the enum types...
     */
    private void test() {
        /* We check all the enumerated file types: */
        for (FileType type : FileType.values()) {
            test(type);
        }
    }

    /**
     * @param type The enum type to test
     */
    private void test(FileType type) {
        System.out.println("Testing identification of: " + type);
        /* For each we get the sample file: */
        String location = type.getSample();
        /* And try identifying it: */
        Types result = jhove.identifyOneBinary(ByteArrayHelper.read(new File(
                location)));
        assertEquals("Wrong pronom ID;", type.getPronom(), result.types[0]
                .toString());
    }

}
