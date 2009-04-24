package eu.planets_project.ifr.core.storage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.storage.impl.file.DigitalObjectManagerTests;
import eu.planets_project.ifr.core.storage.impl.oai.OaiOnbDigitalObjectManagerImplTest;

/**
 * @author CFWilson
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { DigitalObjectManagerTests.class,
        OaiOnbDigitalObjectManagerImplTest.class })
public class AllStorageSuite {}
