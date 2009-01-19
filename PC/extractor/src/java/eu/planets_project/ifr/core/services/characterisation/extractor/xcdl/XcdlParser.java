package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.io.File;
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
                    eu.planets_project.services.datatypes.Property p = new eu.planets_project.services.datatypes.Property(
                            name, val.get(0).getValues().get(0));
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
}
