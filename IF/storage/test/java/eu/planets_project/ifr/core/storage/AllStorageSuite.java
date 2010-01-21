package eu.planets_project.ifr.core.storage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.planets_project.ifr.core.storage.impl.DataRegistryTests;
import eu.planets_project.ifr.core.storage.impl.file.DigitalObjectManagerTests;
import eu.planets_project.ifr.core.storage.impl.jcr.JcrDigitalObjectManagerTest;

/**
 * @author CFWilson
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { DigitalObjectManagerTests.class, JcrDigitalObjectManagerTest.class, DataRegistryTests.class })
public class AllStorageSuite {}
