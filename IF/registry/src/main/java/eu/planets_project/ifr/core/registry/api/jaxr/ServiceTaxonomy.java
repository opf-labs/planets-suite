package eu.planets_project.ifr.core.registry.api.jaxr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.Classification;
import javax.xml.registry.infomodel.ClassificationScheme;
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.scout.registry.infomodel.ClassificationImpl;

import eu.planets_project.ifr.core.registry.api.jaxr.jaxb.concepts.JAXRClassificationScheme;
import eu.planets_project.ifr.core.registry.api.jaxr.jaxb.concepts.JAXRConcept;
import eu.planets_project.ifr.core.registry.api.jaxr.jaxb.concepts.PredefinedConcepts;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsCategory;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsSchema;
import eu.planets_project.ifr.core.registry.api.jaxr.model.PsService;

/**
 * This class provides access to the service taxonomy, which is defined in a
 * JAXR-conforming XML file. It is used for associating services with
 * classifications and provides access to a tree structure, which is used in the
 * UI. The taxonomy can be visualized as an indented string using the toString()
 * method.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ServiceTaxonomy {

    /**
     * The key (name) used in classification objects with free classification
     * (in the value field).
     */
    public static final String FREE = "free";
    /***/
    private PredefinedConcepts pc = null;
    /***/
    private Map<String, List<JAXRConcept>> structureMapping = null;
    /***/
    private BusinessLifeCycleManager blcm = null;
    /***/
    private Map<String, Classification> classificationsMapping = null;
    /***/
    private ClassificationScheme cs = null;
    /***/
    private static String configFolder = null;
    /***/
    private static Log log = LogFactory.getLog(ServiceTaxonomy.class.getName());
    /***/
    private Map<String, JAXRConcept> conceptsMapping = null;
    private BusinessQueryManager bqm;

    /**
     * Private config strings for the service taxonomy.
     */
    public enum ConfigStrings {
        /***/
        REGISTRYCONCEPTS_XML("registryconcepts.xml"),
        /***/
        CONF("/server/default/conf/"),
        /***/
        JBOSS_HOME_DIR_KEY("jboss.home.dir"),
        /***/
        LOCAL(
                "IF/registry/src/main/resources/eu/planets_project/ifr/core/registry/"),
        /***/
        DESTINATION_PACKAGE(
                "eu.planets_project.ifr.core.registry.api.jaxr.jaxb.concepts"),
        /***/
        FACES_CATEGORY("category"),
        /***/
        FACES_GROUP("group"),
        /***/
        AND("AND");
        /***/
        private String val;

        /**
         * @param val The value for the config element
         */
        private ConfigStrings(final String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    /**
     * Private enum for the different properties file locations.
     */
    private enum Categories {
        /**
         * Mapping of service IDs to category IDs (as defined in the
         * JAXR-conforming registryconcepts.xml).
         */
        PREDEFINED("service-registry.properties"),
        /**
         * Mapping of service IDs to a single free text string, e.g. for
         * migration pathways
         */
        FREE("service-registry-free.properties");
        /***/
        private String file;

        /**
         * @param file The name of the properties file
         */
        private Categories(final String file) {
            this.file = file;
        }
    }

    // public API:

    /**
     * Creates an instance and loads the contents of the XML file in which the
     * taxonomy is specified.
     * @param blcm The life cycle manager
     * @throws JAXRException When creating the classification scheme fails
     */
    public ServiceTaxonomy(final BusinessLifeCycleManager blcm,
            final BusinessQueryManager bqm) throws JAXRException {
        this.blcm = blcm;
        this.bqm = bqm;
        cs = this.blcm
                .createClassificationScheme("planets-eu:2008",
                        "Digital Preservation Services Taxonomy: PLANETS (2008 Release)");
        if (cs == null) {
            throw new IllegalStateException(
                    "Could not create the Planets classification scheme.");
        }
        init();
    }

    /**
     * Creates a service taxonomy without actual classifications or scheme, only
     * reads the classifications file and provides a visualization of the
     * structure, (via toString or getTree), e.g. for unit-tests
     */
    public ServiceTaxonomy() {
        init();
    }

    /***/
    private void init() {
        /*
         * If running in JBoss we use the deployment directory, else (like when
         * running a unit test) we use the project directory to retrieve the
         * concepts file:
         */
        String deployed = System
                .getProperty(ConfigStrings.JBOSS_HOME_DIR_KEY.val);
        configFolder = (deployed != null ? deployed + ConfigStrings.CONF.val
                : ConfigStrings.LOCAL.val);
        try {
            pc = loadConcepts();
            /* We assume we have one scheme only: */
            JAXRClassificationScheme s = getPC().getJAXRClassificationScheme()
                    .get(0);
            structureMapping = buildMap(s);
        } catch (JAXRException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Returns the flat predefined concepts loaded from the file.
     */
    public PredefinedConcepts getPC() {
        return pc;
    }

    /**
     * @return Returns the planets classification scheme created from the
     *         registryconcepts.xml file
     */
    public ClassificationScheme getClassificationScheme() {
        return cs;
    }

    /**
     * @param id The UUID of the classification object to return, as specified
     *        in the concepts XML-file
     * @return Returns the classification object from the planets classification
     *         scheme for the given ID
     */
    public Classification getClassification(final String id) {
        Classification classification = classificationsMapping.get(id);
        return classification;
    }

    /**
     * @param s The service to search in
     * @param classification The classification to search for in the service
     * @return Returns true if the service contains a classification with the
     *         same name and the same value as the classification
     */
    public boolean contains(final PsService s,
            final Classification classification) {
        try {
            for (PsCategory c : s.getCategories()) {
                String name1 = c.name;
                String name2 = classification.getName().getValue();
                String value1 = c.code;
                String value2 = classification.getValue();
                if (name1.equals(name2) && value1.equals(value2)) {
                    return true;
                }
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param s The service to search in
     * @param classificationID The classificationID to search for in the service
     * @return Returns true if the service contains a classification with the
     *         same name and the same value as the classification
     */
    public boolean contains(final PsService s, final String classificationID) {
        return contains(s, getClassification(classificationID));
    }

    // synchronized API with access to the properties files:

    /**
     * @param service
     * @param s The service
     * @param id The ID of the classification to add to the service
     */
    public synchronized void addClassification(final PsService s,
            final String id) {
        if (!contains(s, getClassification(id))) {
            log.info("Adding classification " + id + " for service: " + s);
            Classification classification = getClassification(id);
            s.getCategories().add(PsCategory.of(classification));
            try {
                Service service = JaxrServiceRegistryHelper.findServiceByKey(s.getKey(),
                        bqm);
                service.addClassification(classification);
                blcm.saveObjects(Arrays.asList(service));
            } catch (JAXRException e) {
                e.printStackTrace();
            }
            Properties props = loadProps(Categories.PREDEFINED.file);
            String classes = (String) props.get(s.getKey());
            if (classes == null) {
                classes = "";
            } else {
                classes += ",";
            }
            classes += id;
            props.put(s.getKey(), classes);
            log.info("Added new classification: " + id);
            saveProps(props, Categories.PREDEFINED.file);
        }
    }

    /**
     * @param service
     * @param s The service
     * @param free The free text classification to set for the service
     */
    public synchronized void setFreeCategory(final PsService s,
            final String free) {
        /* If a free classification is already there, change that: */
        PsCategory classification = null;
        try {
            for (PsCategory c : s.getCategories()) {
                if (c.name.equals(FREE)) {
                    c.code = (free);
                    classification = c;
                }
            }
            /* Else, create a new one: */
            if (classification == null) {
                Classification c = blcm.createClassification(cs, FREE, free);
                c.setKey(blcm.createKey(s.getKey() + ":" + free));
                /*
                 * Only external classifications return their value on
                 * getValue()
                 */
                ((ClassificationImpl) c).setExternal(true);
                classification = new PsCategory(c.getName().getValue(), c
                        .getValue());
                classification.id = (c.getKey().getId());
                s.getCategories().add(classification);
                Service service = JaxrServiceRegistryHelper.findServiceByKey(s.getKey(),
                        bqm);
                service.addClassification(c);
                blcm.saveObjects(Arrays.asList(service));
            }
            /* But in any case, store the change in the properties file: */
            log.info("Setting free classification '" + free + "' for service: "
                    + s);
            Properties props = loadProps(Categories.FREE.file);
            props.put(s.getKey(), free);
            saveProps(props, Categories.FREE.file);
        } catch (JAXRException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param services The services whose classification objects should be
     *        fixed. If a service contains classifications with an empty string
     *        as the classification name or value attribute, the classification
     *        values are replaced based on the data stored separately in a
     *        property file. If this happens a message is logged. If neither
     *        name nor value is an empty string (i.e. the classification ain't
     *        broken) or there are no classification objects at all, the objects
     *        are not changed. This is a work-around for a problem with the
     *        scout library.
     */
    synchronized void fixClassifications(final Collection<Service> services) {
        Properties categoryProps = loadProps(Categories.PREDEFINED.file);
        Properties freeProps = loadProps(Categories.FREE.file);
        try {
            for (Service service : services) {
                List<Classification> classifications = new ArrayList<Classification>(
                        service.getClassifications());
                /*
                 * If a free classification is defined for this service, fix
                 * that one first:
                 */
                int start = 0;
                if (freeProps.containsKey(service.getKey().getId())
                        && classifications.size() > 0) {
                    Classification freeClassification = classifications.get(0);
                    ((ClassificationImpl) freeClassification).setExternal(true);
                    freeClassification.setName(blcm
                            .createInternationalString(FREE));
                    String value = freeProps.getProperty(service.getKey()
                            .getId());
                    freeClassification.setValue(value);
                    /* Set a dummy ID: */
                    freeClassification.setKey(blcm.createKey(service.getKey()
                            .getId()
                            + ":" + value));
                    /* Remember we fixed one classification already: */
                    start = 1;
                }
                String ids = categoryProps
                        .getProperty(service.getKey().getId());
                if (classifications != null && classifications.size() > start) {
                    Classification next = (Classification) classifications
                            .get(start);
                    /* If it ain't broke, don't fix it: */
                    if (((next).getName().getValue().trim().equals("")
                            || next.getValue().trim().equals("") || next
                            .getKey() == null)
                            && ids != null) {
                        log
                                .warn("Fixing broken service classification for service: "
                                        + service.getKey().getId());
                        String[] classes = ids.split(",");
                        if (classes.length != classifications.size() - start) {
                            throw new IllegalStateException(
                                    "Number of classifications don't correspond: "
                                            + classes.length
                                            + " in properties file, "
                                            + classifications.size()
                                            + " in service!");
                        }
                        for (int j = start, i = 0; j < classifications.size(); j++, i++) {
                            Object object = classifications.get(j);
                            Classification c = (Classification) object;
                            String s = classes[i];
                            /*
                             * TODO Something wrong in the properties file
                             * handling?
                             */
                            if (s.trim().equals("")) {
                                continue;
                            }
                            Classification ok = classificationsMapping.get(s);
                            c.setName(ok.getName());
                            c.setValue(ok.getValue());
                            c.setDescription(ok.getDescription());
                            c.setConcept(ok.getConcept());
                            c.setKey(ok.getKey());
                            ((ClassificationImpl) c).setExternal(ok
                                    .isExternal());
                        }
                    }
                }
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }

    }

    /**
     * Removes all classification objects from the service that have the same
     * name as the given classification object.
     * @param s The service
     * @param classificationID The classification ID to remove from the service
     */
    public synchronized void removeClassification(final PsService s,
            final String classificationID) {
        if (contains(s, getClassification(classificationID))) {
            log.info("Removing classification " + classificationID
                    + " for service: " + s);
            try {
                removeFromService(s, classificationID);
                removeFromProps(s, classificationID);
            } catch (JAXRException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param service The service that should be removed from the taxonomy.
     *        Should be called when a service is deleted from the registry.
     */
    public synchronized void removeService(final Service service) {
        remove(service, Categories.PREDEFINED.file);
        remove(service, Categories.FREE.file);
    }

    /**
     * @param service The service to check against the filter
     * @param filter The filter containing classifications
     * @return Returns true if the key for one of the classifications of the
     *         specified service is contained in the list of classification keys
     *         to filter, else false.
     */
    public boolean included(final Service service, final List<String> filter) {
        try {
            Collection<Classification> classifications = service
                    .getClassifications();
            for (Classification c : classifications) {
                if (c == null || c.getKey() == null
                        || c.getKey().getId() == null) {
                    log
                            .warn("Service contains incomplete classification: "
                                    + c);
                    return false;
                }
                log.info("Checking classification: " + c.getKey().getId());
                if (filter.contains(c.getKey().getId())) {
                    return true;
                }
                log.info("Checking free classification: " + c.getKey().getId());
                if (c.getName().getValue().equals(FREE)) {
                    /*
                     * If it is a free classification, check if the filter
                     * contains something relevant:
                     */
                    for (String f : filter) {
                        String value = c.getValue();
                        /*
                         * Return true if all query elements connected by AND
                         * are in the free classification:
                         */
                        String[] queries = f.split(ConfigStrings.AND.val);
                        for (String q : queries) {
                            if (value.toLowerCase().contains(
                                    q.toLowerCase().trim())) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param service The service
     * @return Returns the predefined (non-free) classifications for the service
     */
    public String classifications(final PsService service) {
        StringBuilder builder = new StringBuilder();
        for (PsCategory o : service.getCategories()) {
            String name = o.name;
            /* Append the non-free classification, i.e. the predefined ones: */
            builder.append(name.equals(ServiceTaxonomy.FREE) ? ""
                    : (name + ", "));
        }
        String trim = builder.toString().trim();
        /*
         * Return the comma-separated string without a trailing comma, or two
         * dashes if we have no classifications for this service:
         */
        return trim.equals("") ? "--" : trim.replaceAll(",$", "");
    }

    /**
     * @param service The service
     * @return Returns the free text classification for the service or an empty
     *         string if no free classification is given.
     */
    public String freeClassification(final PsService service) {
        Collection<PsCategory> classifications = service.getCategories();
        for (PsCategory c : classifications) {
            if (c.name.equals(ServiceTaxonomy.FREE)) {
                String value = c.code;
                return value;
            }
        }
        return "";
    }

    // private API:

    /**
     * @param service The service to remove from the properties file
     * @param file The properties file name
     */
    private void remove(final Service service, final String file) {
        Properties predefinedProps = loadProps(file);
        try {
            predefinedProps.keySet().remove(service.getKey().getId());
        } catch (JAXRException e) {
            e.printStackTrace();
        }
        saveProps(predefinedProps, file);
    }

    /**
     * @param s The service
     * @param classificationID The classification to remove from the service
     * @throws JAXRException When accessing the service properties goes wrong
     */
    private void removeFromProps(final PsService s,
            final String classificationID) throws JAXRException {
        Properties p = loadProps(Categories.PREDEFINED.file);
        String property = p.getProperty(s.getKey());
        String[] split = property.split(",");
        /* We build a new string without the classification to be removed: */
        StringBuilder builder = new StringBuilder();
        for (String string : split) {
            if (!string.equals(classificationID)) {
                builder.append(string).append(",");
            }
        }
        String string = builder.toString();
        string = (string.endsWith(",") ? string.substring(0,
                string.length() - 1) : string);
        p.put(s.getKey(), string);
        saveProps(p, Categories.PREDEFINED.file);
    }

    /**
     * @param s The service
     * @param classificationID The classification to remove from the service
     * @throws JAXRException When accessing the service properties goes wrong
     */
    private void removeFromService(final PsService s,
            final String classificationID) throws JAXRException {
        Classification classification = getClassification(classificationID);
        List<PsCategory> newClassifications = new ArrayList<PsCategory>();
        for (PsCategory c : s.getCategories()) {
            /*
             * We should probably use the keys here, but the name is the only
             * attribute that reliably is preserved when using scout without
             * work-arounds, so this seems safer.
             */
            if (!c.name.equals(classification.getName().getValue())) {
                newClassifications.add(c);
            }
        }
        /*
         * When having more than one classification, scouts removeClassification
         * method won't work, so we do the reverse: remember what to keep and
         * replace the classification collection.
         */
        s.setCategories((newClassifications));
    }

    /**
     * @return Returns the PredefinedConcepts unmarshalled from XML
     * @throws JAXRException When accessing the service properties goes wrong
     */
    private PredefinedConcepts loadConcepts() throws JAXRException {
        classificationsMapping = new HashMap<String, Classification>();
        conceptsMapping = new HashMap<String, JAXRConcept>();
        PredefinedConcepts c = null;
        try {
            JAXBContext jc = JAXBContext
                    .newInstance(ConfigStrings.DESTINATION_PACKAGE.val);
            Unmarshaller u = jc.createUnmarshaller();
            String pathToConceptsFile = configFolder
                    + ConfigStrings.REGISTRYCONCEPTS_XML.val;
            c = (PredefinedConcepts) u.unmarshal(new File(pathToConceptsFile));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * @param s The classification scheme containing the concepts
     * @return Returns a mapping of parent IDs to a list of their children
     *         concepts.
     * @throws JAXRException When creating a classification fails
     */
    private Map<String, List<JAXRConcept>> buildMap(
            final JAXRClassificationScheme s) throws JAXRException {
        /* We build a mapping of parent concepts to their child concepts */
        Map<String, List<JAXRConcept>> map = new HashMap<String, List<JAXRConcept>>();
        List<JAXRConcept> concepts = s.getJAXRConcept();
        for (JAXRConcept concept : concepts) {
            conceptsMapping.put(concept.getId(), concept);
            if (blcm != null) {
                Classification classification = createClassification(concept);
                classificationsMapping.put(concept.getId(), classification);
            }
            String parent = concept.getParent();
            List<JAXRConcept> list = map.get(parent);
            if (list == null) {
                list = new Vector<JAXRConcept>();
                /* Don't add the scheme itself as it would appear twice: */
                if (!parent.equals(s.getId())) {
                    map.put(parent, list);
                }
            }
            if (!parent.equals(concept.getId())) {
                list.add(concept);
            }
        }
        return map;
    }

    /**
     * @param jaxbGeneratedJaxrConcept The generated JAXR concept for the
     *        classification object to create
     * @return Returns a javax.xml.registry.infomodel.Classification object for
     *         the given UUID
     * @throws JAXRException when creating registry objects fail
     */
    private Classification createClassification(
            final JAXRConcept jaxbGeneratedJaxrConcept) throws JAXRException {
        if (jaxbGeneratedJaxrConcept.getName().trim().equals("")
                || jaxbGeneratedJaxrConcept.getCode().trim().equals("")) {
            throw new IllegalStateException("Empty name and code values!");
        }
        Classification c = blcm.createClassification(cs,
                jaxbGeneratedJaxrConcept.getName(), jaxbGeneratedJaxrConcept
                        .getCode());
        ((ClassificationImpl) c).setExternal(true);
        cs.addClassification(c);
        Concept concept = blcm.createConcept(cs, jaxbGeneratedJaxrConcept
                .getName(), jaxbGeneratedJaxrConcept.getCode());
        c.setConcept(concept);
        c.setKey(blcm.createKey(jaxbGeneratedJaxrConcept.getId()));
        c.setDescription(blcm
                .createInternationalString(jaxbGeneratedJaxrConcept.getName()
                        + " " + jaxbGeneratedJaxrConcept.getCode()));
        return c;
    }

    /**
     * Saves the properties to the properties file.
     * @param p The properties object to save
     * @param fileName The properties file to use (one of the Categories enum
     *        elements)
     */
    private static void saveProps(final Properties p, final String fileName) {
        try {
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(configFolder + fileName));
            p.store(out, "");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the properties from the properties file.
     * @param fileName The properties file to use (one of the Props enum
     *        elements)
     * @return Returns a properties object loaded from the given location
     */
    private static Properties loadProps(final String fileName) {
        Properties p = new Properties();
        try {
            String location = configFolder + fileName;
            File f = new File(location);
            if (!f.exists()) {
                boolean created = f.createNewFile();
                if (!created) {
                    throw new IllegalStateException(
                            "Could not create file at: " + location);
                }
            }
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(location));
            p.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * @return The PsSchema for this taxonomy
     */
    public PsSchema getPsSchema() {
        PsSchema planetsServiceScheme = new PsSchema();
        PredefinedConcepts pdc = getPC();
        List<JAXRClassificationScheme> pc = pdc.getJAXRClassificationScheme();
        try {
            planetsServiceScheme = new PsSchema(pc.get(0));
        } catch (JAXRException e) {
            e.printStackTrace();
            planetsServiceScheme.errorMessage = ("Getting schema failed: " + e
                    .getMessage());
        }
        return planetsServiceScheme;
    }

    public Map<String, List<JAXRConcept>> getStructureMapping() {
        return structureMapping;
    }

    public Map<String, JAXRConcept> getConceptsMapping() {
        return conceptsMapping;
    }
}
