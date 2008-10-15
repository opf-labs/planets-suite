/**
 * 
 */
package eu.planets_project.services.migrate;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

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
     * For JAXB.
     */
    public MigrateServiceDescription() {
        super();
    }

    /**
     * @param name
     * @param type
     */
    public MigrateServiceDescription(String name, String type) {
        super(name, type);
    }
    
    
    /***/
    private static java.io.File baseDir = new java.io.File(
            "IF/common/src/resources");
    /***/
    private static String schemaFileName = "migrate_service_description.xsd";

    /** Resolver for schema generation. */
    static class Resolver extends SchemaOutputResolver {
        /**
         * {@inheritDoc}
         * 
         * @see javax.xml.bind.SchemaOutputResolver#createOutput(java.lang.String,
         *      java.lang.String)
         */
        public Result createOutput(final String namespaceUri,
                final String suggestedFileName) throws IOException {
            return new StreamResult(new java.io.File(baseDir, schemaFileName+"_"+suggestedFileName));
        }
    }

    /**
     * Generates the XML schema for this class.
     * 
     * @param args Ignored
     */
    public static void main(final String[] args) {
        try {
            Class<MigrateServiceDescription> clazz = MigrateServiceDescription.class;
            JAXBContext context = JAXBContext.newInstance(clazz);
            context.generateSchema(new Resolver());
            System.out.println("Generated XML schema for "
                    + clazz.getSimpleName()
                    + " at "
                    + new java.io.File(baseDir, schemaFileName)
                            .getAbsolutePath());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}

