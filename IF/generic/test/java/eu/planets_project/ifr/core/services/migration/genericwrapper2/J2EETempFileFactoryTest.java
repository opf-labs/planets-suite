package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the J2EE/FileUtils dependent implementation of the
 * <code>{@link TemporaryFileFactory}</code> interface.
 * 
 * @author Thomas Skou Hansen <tsh@statsbiblioteket.dk>
 */
public class J2EETempFileFactoryTest {

    private J2EETempFileFactory tempFileFactory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	tempFileFactory = new J2EETempFileFactory(this.getClass()
		.getCanonicalName());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
	tempFileFactory.getTempFileDir().delete();
    }

    /**
     * Test method for
     * {@link eu.planets_project.ifr.core.services.migration.genericwrapper2.J2EETempFileFactory#prepareRandomNamedTempFile()}
     * .
     */
    @Test
    public void testPrepareRandomNamedTempFile() {

	final File tempFile = tempFileFactory.prepareRandomNamedTempFile();

	assertNotNull("The generated temporary file path should not be null",
		tempFile.getPath());
    }

    /**
     * Test method for
     * {@link eu.planets_project.ifr.core.services.migration.genericwrapper2.J2EETempFileFactory#prepareRandomNamedTempFile(String)}
     * .
     */
    @Test
    public void testPrepareRandomNamedTempFileString() {

	final String humanReadableID = "humanReadable";
	final File tempFile = tempFileFactory
		.prepareRandomNamedTempFile(humanReadableID);

	assertNotNull("The generated temporary file path should not be null",
		tempFile.getPath());

	assertTrue("Did not find the ID in the file name.", tempFile.getPath()
		.indexOf(humanReadableID) >= 0);
    }

    /**
     * Test method for
     * {@link eu.planets_project.ifr.core.services.migration.genericwrapper2.J2EETempFileFactory#prepareTempFile(String)}
     * .
     */
    @Test
    public void testPrepareTempFileString() {

	final String desiredFileName = "myOddlyNamedFile";

	final File tempFile = tempFileFactory.prepareTempFile(desiredFileName);

	assertNotNull("The generated temporary file path should not be null",
		tempFile.getPath());

	assertEquals("Un-expected filename.", desiredFileName, tempFile
		.getName());
    }
}
