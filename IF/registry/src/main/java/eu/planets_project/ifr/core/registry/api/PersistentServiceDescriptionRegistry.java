package eu.planets_project.ifr.core.registry.api;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import eu.planets_project.ifr.core.registry.api.model.ServiceRegistryMessage;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * ServiceDecriptionRegistry implementation persisting the descriptions as XML
 * files.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class PersistentServiceDescriptionRegistry implements
        ServiceDescriptionRegistry {

    private ServiceDescriptionRegistry registry;
    /* TODO determine the dir to use when running on the server... */
    private File root = new File(
            "IF/registry/src/main/resources/service-description-registry");

    /**
     * @param registry The backing registry, e.g. a
     *        CoreServiceDescriptionRegistry
     * @return A persistent service description registry
     */
    public static ServiceDescriptionRegistry getInstance(
            final ServiceDescriptionRegistry registry) {
        return new PersistentServiceDescriptionRegistry(registry);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceDescriptionRegistry#query(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public List<ServiceDescription> query(final ServiceDescription example) {
        return registry.query(example);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceDescriptionRegistry#register(eu.planets_project.services.datatypes.ServiceDescription)
     */
    public ServiceRegistryMessage register(
            final ServiceDescription serviceDescription) {
        String xml = serviceDescription.toXml();
        File f = new File(root, filename(serviceDescription));
        writeTo(xml, f);
        return registry.register(serviceDescription);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.registry.api.ServiceDescriptionRegistry#clear()
     */
    public ServiceRegistryMessage clear() {
        String[] list = root.list();
        for (String name : list) {
            File file = new File(root, name);
            boolean delete = file.delete();
            if (file.exists() && !delete) {
                throw new IllegalStateException(
                        "Could not delete registry root: " + file);
            }
        }
        return registry.clear();
    }

    /**
     * @param serviceDescription A service description to save to a file
     * @return The name that will be used for the file stored
     */
    private String filename(final ServiceDescription serviceDescription) {
        return serviceDescription.hashCode() + ".xml";
    }

    private PersistentServiceDescriptionRegistry(
            ServiceDescriptionRegistry registry) {
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
            File f = new File(string);
            String xml = readFrom(f);
            this.registry.register(ServiceDescription.of(xml));
        }
    }

    private String readFrom(File f) {
        StringBuilder builder = new StringBuilder();
        try {
            Scanner s = new Scanner(new BufferedInputStream(
                    new FileInputStream(f)));
            while (s.hasNextLine()) {
                builder.append(s.nextLine()).append(" ");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private void writeTo(String xml, File f) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(xml);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
