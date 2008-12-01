package eu.planets_project.ifr.core.registry.impl;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.registry.api.Registry;
import eu.planets_project.ifr.core.registry.api.Response;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * ServiceDecriptionRegistry implementation persisting the descriptions as XML
 * files.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class PersistentRegistry implements Registry {
    private static final String SUFFIX = ".xml";
    private static Log log = LogFactory.getLog(PersistentRegistry.class
            .getName());
    private static final String DESCRIPTION_REGISTRY = "service-description-registry";
    private static final String LOCAL = "IF/registry/src/main/resources/";
    private static final String SERVER_DEFAULT_CONF = "/server/default/conf/";
    private static final String JBOSS_HOME_DIR = "jboss.home.dir";
    private static String rootLocation = null;
    private static File root = null;
    /*
     * If running in JBoss we use the deployment directory, else (like when
     * running a unit test) we use the project directory to persist the service
     * descriptions:
     */
    static {
        String deployed = System.getProperty(JBOSS_HOME_DIR);
        rootLocation = (deployed != null ? deployed + SERVER_DEFAULT_CONF
                : LOCAL)
                + DESCRIPTION_REGISTRY;
    }
    private Registry registry;

    /**
     * @param registry The backing registry, e.g. a CoreRegistry
     * @return A persistent service description registry
     */
    public static Registry getInstance(final Registry registry) {
        return new PersistentRegistry(registry);
    }

    /**
     * @param registry The backing registry, e.g. a CoreRegistry
     * @param location The full path to the folder that should be used to store
     *        the service descriptions in
     * @return A persistent service description registry
     */
    public static Registry getInstance(final Registry registry,
            final String location) {
        rootLocation = location;
        return new PersistentRegistry(registry);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#query(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public List<ServiceDescription> query(final ServiceDescription example) {
        return registry.query(example);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#register(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public Response register(final ServiceDescription serviceDescription) {
        String xml = serviceDescription.toXml();
        File f = new File(root, filename(serviceDescription));
        writeTo(xml, f);
        return registry.register(serviceDescription);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.Registry#clear()
     */
    public Response clear() {
        String[] list = root.list();
        for (String name : list) {
            File file = new File(root, name);
            if (!file.isHidden() && file.getName().endsWith(SUFFIX)) {
                boolean delete = file.delete();
                if (file.exists() && !delete) {
                    throw new IllegalStateException(
                            "Could not delete registry root: " + file);
                }
            }
        }
        return registry.clear();
    }

    /**
     * @param serviceDescription A service description to save to a file
     * @return The name that will be used for the file stored
     */
    private String filename(final ServiceDescription serviceDescription) {
        String prefix = serviceDescription.getName() == null ? "unnamed"
                : serviceDescription.getName();
        return prefix + serviceDescription.hashCode() + SUFFIX;
    }

    /**
     * @param registry The backing registry instance
     */
    private PersistentRegistry(final Registry registry) {
        log.debug("Using registry root: " + rootLocation);
        root = new File(rootLocation);
        this.registry = registry;
        boolean mkdir = root.mkdir();
        if (!mkdir && !root.exists()) {
            throw new IllegalStateException("Could not create registry root: "
                    + root);
        }
        /*
         * When instantiating the registry, we read all available descriptions
         * from disk:
         */
        String[] list = root.list();
        for (String string : list) {
            File f = new File(root, string);
            if (!f.isHidden() && f.getName().endsWith(SUFFIX)) {
                String xml = readFrom(f);
                this.registry.register(ServiceDescription.of(xml));
            }
        }
    }

    /**
     * @param file The file to read from
     * @return The contents of the file
     */
    private String readFrom(final File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format(
                    "File %s does not exist!", file.getAbsolutePath()));
        }
        StringBuilder builder = new StringBuilder();
        Scanner s = null;
        try {
            s = new Scanner(new BufferedInputStream(new FileInputStream(file)));
            while (s.hasNextLine()) {
                builder.append(s.nextLine()).append(" ");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (s != null) {
                s.close();
            }
        }
        return builder.toString();
    }

    /**
     * @param content The string to write
     * @param file The file to store the string in
     */
    private void writeTo(final String content, final File file) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
