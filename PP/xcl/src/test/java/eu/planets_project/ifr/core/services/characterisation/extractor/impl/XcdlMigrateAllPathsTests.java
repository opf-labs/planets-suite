package eu.planets_project.ifr.core.services.characterisation.extractor.impl;

import java.net.URISyntaxException;

import org.junit.Test;

import eu.planets_project.services.datatypes.MigrationPath;

/**
 * Test of the extractor (local and remote) using binaries.
 * local and in the data registry after the tests
 * @author Peter Melms
 * @author Fabian Steeg
 */
public class XcdlMigrateAllPathsTests extends XcdlMigrateTests{

    @Test
    public void testAllPossibleMigrationPathways() throws URISyntaxException {
        System.out.println("Testing all possible pathways...START");
        for (int i = 0; i < migrationPaths.length; i++) {
            MigrationPath path = migrationPaths[i];
            testPath(path);
        }
        System.out.println("Testing all possible pathways...END");
    }
}
