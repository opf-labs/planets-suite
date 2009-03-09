package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.LabValue;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Property;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.ValueSet;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Xcdl;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.LabValue.Val;
import eu.planets_project.services.datatypes.Prop;

/**
 * Access to a complete XCDL, via JAXB-generated classes.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class XcdlParser implements XcdlAccess {

    private File xcdlFile;
    private Xcdl xcdl;

    /**
     * @param xcdl The XCDL file
     */
    public XcdlParser(final File xcdl) {
        this.xcdlFile = xcdl;
        this.xcdl = loadXcdl();
    }

    /**
     * @return The XCDL root object
     */
    private Xcdl loadXcdl() {
        try {
            JAXBContext jc = JAXBContext
                    .newInstance("eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            java.lang.Object object = unmarshaller.unmarshal(xcdlFile);
            System.out.println(object.getClass());
            return (Xcdl) object;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlAccess#getProperties()
     */
    public List<eu.planets_project.services.datatypes.Property> getProperties() {
        List<eu.planets_project.services.datatypes.Property> result = new ArrayList<eu.planets_project.services.datatypes.Property>();
        List<eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object> list = xcdl
                .getObjects();
        for (eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object o : list) {
            List<Property> properties = o.getProperties();
            for (Property property : properties) {
                String name = property.getName().getValues().get(0);
                List<ValueSet> valueSets = property.getValueSets();
                for (ValueSet valueSet : valueSets) {
                    LabValue labValue = valueSet.getLabValue();
                    List<Val> val = labValue.getVals();
                    String id = property.getName().getId().replaceAll("id", "");
                    URI propUri = XcdlProperties.makePropertyURI(id, name);
                    eu.planets_project.services.datatypes.Property p = new eu.planets_project.services.datatypes.Property(
                            propUri, name, val.get(0).getValues().get(0));
                    p.setType(labValue.getTypes().get(0).getValue().value());
                    result.add(p);
                }
            }
        }
        return result;
    }

    /**
     * @return the xcdl
     */
    public Xcdl getXcdl() {
        return xcdl;
    }

    /**
     * @param args unused
     */
    public static void main(final String[] args) {
        XcdlParser p = new XcdlParser(new File(
                "PC/extractor/src/java/eu/planets_project/xcdl/xcdl.xml"));
        List<eu.planets_project.services.datatypes.Property> properties = p
                .getProperties();
        for (eu.planets_project.services.datatypes.Property property : properties) {
            System.out.println(property);
        }
    }

    /**
     * NOTE: This is a temporary solution to avoid spreading the preliminary
     * Prop class too far. Eventually, this class will only provide on
     * getProp(ertie)s() method.
     * @return The properties parsed from the XCDL file
     */
    public List<Prop<Object>> getProps() {
        List<eu.planets_project.services.datatypes.Property> properties = this
                .getProperties();
        return propertiesToProps(properties);
    }

    private List<Prop<Object>> propertiesToProps(
            List<eu.planets_project.services.datatypes.Property> properties) {
        /*
         * This is totally work in progress... The basic idea is: We wrap all
         * this stuff here around the plain properties to make it work for the
         * XCDL comparator.
         */
        int p = 73;
        int v = 217;
        System.out.println("Attempting to convert properties: ");
        for (eu.planets_project.services.datatypes.Property property : properties) {
            System.out.println(property);
        }
        List<Prop<Object>> result = new ArrayList<Prop<Object>>();
        result.add(Prop.name("normData").type("id").description("object")
                .values("00 01 02 03 04 05 06 07 08 09 0a").build());
        result.add(Prop.name("propertySet").type("id_0").values(
                Prop.name("ref").type("id").description("id").build(),
                Prop.name("ref").type("id").description("id").build()).build());
        for (eu.planets_project.services.datatypes.Property property : properties) {
            result.add(Prop.name("property").type("p" + p).values(
                    Prop.name("value").values("raw", "descr").build(),
                    Prop.name("name").type("id")
                            .description(property.getName()).build(),
                    Prop.name("valueSet").type("i_i1_i" + v + "_s5").values(
                            Prop.name("labValue").description(
                                    property.getUnit()).values(
                                    property.getValue()).build()).build())
                    .build());
            p--;
            v--;
        }
        return result;
    }
}
