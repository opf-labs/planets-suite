package eu.planets_project.services.utils.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import eu.planets_project.services.utils.test.FileAccess;

/**
 * Tests for the {@link FileAccess} class.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class FileAccessTests {
    @Test
    public void test() {
        /* Get the singleton instance: */
        FileAccess access = FileAccess.INSTANCE;
        /* Get a random BMP file for testing: */
        File file = access.get("bmp");
        /* That file exists and is a BMP: */
        Assert.assertTrue(file.exists());
        Assert.assertTrue(file.getAbsolutePath().endsWith("bmp"));
    }
}
