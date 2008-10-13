/**
 * 
 */
package eu.planets_project.services.migrate;

import java.util.List;

import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * A entity to hold metadata about migration services.  Again, 
 * this is defined in Java for convenience, but intended to be XML.
 * The content of this object was first defined at the IF meeting in September 2008.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class MigrateServiceDescription extends ServiceDescription {

    /**
     *  Migration Matrix: [input, output]*n
     */
    List<MigrationPath> paths;

    /**
     * @return the paths
     */
    public List<MigrationPath> getPaths() {
        return paths;
    }

    /**
     * @param paths the paths to set
     */
    public void setPaths(List<MigrationPath> paths) {
        this.paths = paths;
    }
    

    /**
     * @param name
     * @param type
     */
    public MigrateServiceDescription(String name, String type) {
        super(name, type);
    }
    
    
}

