package eu.planets_project.ifr.core.registry.api.jaxr;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.registry.api.jaxr.ServiceTaxonomy;
import eu.planets_project.ifr.core.registry.api.jaxr.jaxb.concepts.JAXRClassificationScheme;
import eu.planets_project.ifr.core.registry.api.jaxr.jaxb.concepts.JAXRConcept;

/**
 * Tests for the ServiceTaxonomy. Tests if the taxonomy has been unmarshalled
 * successfully and if the structure makes sense (e.g. if specified parent nodes
 * exist).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class ServiceTaxonomyTests {

    ServiceTaxonomy s;
    private List<JAXRClassificationScheme> schemes;

    /**
     * Creates the service taxonomy.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        s = new ServiceTaxonomy();
        schemes = s.getPC().getJAXRClassificationScheme();
    }

    /**
     * Tests if all the attributes from the XML file made it into the
     * unmarshalled objects.
     */
    @Test
    public void testAttributes() {
        for (JAXRClassificationScheme cs : schemes) {
            /* Test the attributes for all schemes: */
            assertNotNull("Scheme description is null!", cs.getDescription());
            assertNotNull("Scheme ID is null!", cs.getId());
            assertNotNull("Scheme name is null!", cs.getName());
            List<JAXRConcept> concepts = cs.getJAXRConcept();
            for (JAXRConcept concept : concepts) {
                /* Test the attributes for all concepts: */
                assertNotNull("Concept code is null!", concept.getCode());
                assertNotNull("Concept ID is null!", concept.getId());
                assertNotNull("Concept name is null!", concept.getName());
                assertNotNull("Concept parent is null!", concept.getParent());
            }
        }
    }

    /**
     * Tests if the linear XML structure represent a valid tree structure, by
     * testing if referenced parent nodes actually exist.
     */
    @Test
    public void testStructure() {
        for (JAXRClassificationScheme cs : schemes) {
            List<JAXRConcept> concepts = cs.getJAXRConcept();
            /* Retrieve all IDs: */
            List<String> ids = new Vector<String>();
            ids.add(cs.getId());
            for (JAXRConcept concept : concepts) {
                String id = concept.getId();
                ids.add(id);
            }
            /* Check if all parents exist: */
            for (JAXRConcept concept : concepts) {
                String parent = concept.getParent();
                assertTrue("Parent '" + parent + "' of concept '"
                        + concept.getId() + "' not found!", ids
                        .contains(parent));
            }
        }
    }

    /**
     * Tests if the tree representation has been successfully created and is not
     * empty and prints a human-readable string representation of the taxonomy.
     */
//    @Test
//    public void testTreeCreation() {
//        TreeNode root = s.getTree();
//        assertNotNull("Tree is null!", root);
//        assertTrue("No children in root!", root.getChildCount() > 0);
//        // check if every entry is in the tree only once
//        checkDuplicates(new HashMap<String, Integer>(), root);
//        // print the taxonomy in a human-readable format:
//        System.out.println(s.toString());
//
//    }

    /**
     * Recursively checks the tree starting with root for duplicate entries.
     * @param map The map to remember if a node has been visited
     * @param root The starting node to check for duplicates
     */
//    private void checkDuplicates(Map<String, Integer> map, TreeNode root) {
//        String ID = root.getIdentifier();
//        Integer i = map.get(ID);
//        assertTrue("Node already present: " + ID, i == null);
//        map.put(ID, 1);
//        List children = root.getChildren();
//        for (Object object : children) {
//            checkDuplicates(map, (TreeNode) object);
//        }
//    }

}
