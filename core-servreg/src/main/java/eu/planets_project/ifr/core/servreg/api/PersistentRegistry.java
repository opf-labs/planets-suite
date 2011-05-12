package eu.planets_project.ifr.core.servreg.api;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * ServiceDecriptionRegistry implementation persisting the descriptions as XML
 * files. NOTE: Clients should use the RegistryFactory to instantiate a Registry.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
final class PersistentRegistry implements ServiceRegistry {
    private static Logger LOG = Logger.getLogger(PersistentRegistry.class.getName());
    private static final String SUFFIX = ".xml";
    private static final String SERVICE_REGISTRY = "service-registry";
    private static final String LOCAL = "servreg/";
    private static final String SERVER_DEFAULT_CONF = "/server/default/data/";
    private static final String JBOSS_HOME_DIR = "jboss.home.dir";
    private static URI ROOT_LOCATION = null;
    private static File ROOT = null;
    /*
     * If running in JBoss we use the deployment directory, else (like when
     * running a unit test) we use the project directory to persist the service
     * descriptions:
     */
    static {
    	String rootLoc = "";
        String deployed = System.getProperty(JBOSS_HOME_DIR);
        if (deployed != null) {
            rootLoc = SERVER_DEFAULT_CONF + SERVICE_REGISTRY;
        } else {
        	rootLoc = PersistentRegistry.class.getResource("/").toString() + LOCAL + SERVICE_REGISTRY;
        }
        	
        ROOT_LOCATION = URI.create(rootLoc);
    }
    private ServiceRegistry registry;
    private long updated;

    /**
     * NOTE: Clients should use the RegistryFactory to instantiate a Registry.
     * @param registry The backing registry, e.g. a CoreRegistry
     * @return A persistent service description registry
     */
    static ServiceRegistry getInstance(final ServiceRegistry registry) {
        return new PersistentRegistry(registry);
    }

    /**
     * NOTE: Clients should use the RegistryFactory to instantiate a Registry.
     * @param registry The backing registry, e.g. a CoreRegistry
     * @param location The full path to the folder that should be used to store
     *        the service descriptions in
     * @return A persistent service description registry
     */
    static ServiceRegistry getInstance(final ServiceRegistry registry,
            final String location) {
        ROOT_LOCATION = new File(location).toURI();
        return new PersistentRegistry(registry);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#query(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public List<ServiceDescription> query(final ServiceDescription example) {
        updateIfChanged();
        return registry.query(example);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#delete(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public Response delete(final ServiceDescription example) {
        Response response = registry.delete(example);
        List<ServiceDescription> list = registry.query(null);
        clear();
        for (ServiceDescription serviceDescription : list) {
            // log.info("Re-registering... "+serviceDescription.getEndpoint() );
            register(serviceDescription);
        }
        return response;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#queryWithMode(eu.planets_project.services.datatypes.ServiceDescription,
     *      eu.planets_project.ifr.core.registry.impl.Query.MatchingMode)
     */
    public List<ServiceDescription> queryWithMode(
            final ServiceDescription example, final MatchingMode mode) {
        updateIfChanged();
        return registry.queryWithMode(example, mode);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#register(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public Response register(final ServiceDescription serviceDescription) {
        Response response = registry.register(serviceDescription);
        if (response.success()) {
            String xml = serviceDescription.toXml();
            File f = new File(ROOT, filename(serviceDescription));
            writeTo(xml, f);
            updateRootModified();
        }
        return response;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.servreg.api.ServiceRegistry#clear()
     */
    public Response clear() {
        String[] list = ROOT.list();
        for (String name : list) {
            File file = new File(ROOT, name);
            if (!file.isHidden() && file.getName().endsWith(SUFFIX)) {
                boolean delete = file.delete();
                if (file.exists() && !delete) {
                    throw new IllegalStateException(
                            "Could not delete registry root: " + file);
                }
            }
        }
        updateRootModified();
        return registry.clear();
    }

    /**
     * Updates the registry content from disk if it has been modified on disk.
     */
    private void updateIfChanged() {
        if (ROOT.lastModified() != updated) {
            initFromDisk();
        }
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
    private PersistentRegistry(final ServiceRegistry registry) {
        LOG.fine("Using registry root: " + ROOT_LOCATION);
    	System.out.println(ROOT_LOCATION);
        ROOT = new File(ROOT_LOCATION);
        this.registry = registry;
        boolean mkdir = ROOT.mkdirs();
        if (!mkdir && !ROOT.exists()) {
            throw new IllegalStateException("Could not create registry root: "
                    + ROOT);
        }
        initFromDisk();
    }

    /**
     * Updates the last modification time to the current time.
     */
    private void updateRootModified() {
        /*
         * We are not using root.lastModified() as is here as that gives
         * inconsistent results and its behavior varies on different platforms.
         * Instead, we explicitly set it to the current time in nanoseconds.
         */
        boolean ok = ROOT.setLastModified(System.nanoTime());
        if (!ok) {
            LOG.warning("Could not set root modified time");
        }
    }

    /**
     * Initialize the registry from disk.
     */
    private void initFromDisk() {
        this.registry.clear();
        String[] list = ROOT.list();
        for (String string : list) {
            File f = new File(ROOT, string);
            if (!f.isHidden() && f.getName().endsWith(SUFFIX)) {
                String xml = readFrom(f);
                this.registry.register(ServiceDescription.of(xml));
            }
        }
        updated = ROOT.lastModified();
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
                builder.append(s.nextLine()).append("\n");
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
