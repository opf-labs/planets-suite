package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationPathConfigException;

/**
 * Factory interface for migration path factory implementations which may have
 * different approaches e.g. for obtaining the migration path configurations.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
interface MigrationPathFactory {

    /**
     * Create a <code>MigrationPaths</code> object containing the migration
     * paths described by the current configuration of the factory.
     * 
     * @return A <code>MigrationPaths</code> object containing all the migration
     *         paths that can be created from the current configuration of the
     *         factory.
     * 
     * @throws MigrationPathConfigException
     *             if the configuration of the factory is invalid.
     */
    MigrationPaths getAllMigrationPaths() throws MigrationPathConfigException;
}