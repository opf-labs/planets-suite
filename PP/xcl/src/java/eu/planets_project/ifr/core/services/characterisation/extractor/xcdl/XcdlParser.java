package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlCreator.PropertyName;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.DataRef;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.LabValue;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Property;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.ValueSet;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Xcdl;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.LabValue.Val;
import eu.planets_project.services.utils.FileUtils;

/**
 * Access to a complete XCDL, via JAXB-generated classes.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class XcdlParser implements XcdlAccess {

    private Xcdl xcdl;

    /**
     * @param xcdl The XCDL file
     */
    public XcdlParser(final File xcdl) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader( xcdl );
            this.xcdl = loadXcdl(fileReader);
        } catch (FileNotFoundException e) {
            this.xcdl =  null;
            FileUtils.close(fileReader);
        }
    }
    
    /**
     * @param xcdl The XCDL, held in a String.
     */
    public XcdlParser(final String xcdl ) {
        this.xcdl = loadXcdl( new StringReader( xcdl ));
    }

    /**
     * @return The XCDL root object
     */
    private Xcdl loadXcdl( Reader source ) {
        try {
            JAXBContext jc = JAXBContext
                    .newInstance("eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            java.lang.Object object = unmarshaller.unmarshal(source);
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
                    String value = val.get(0).getValues().get(0);
                    String type = PropertyName.PROPERTY.s;
                    String unit = labValue.getTypes().get(0).getValue().value();
                    String description = createDescription(property, valueSet);
                    eu.planets_project.services.datatypes.Property p = new eu.planets_project.services.datatypes.Property.Builder(
                            propUri).name(name).value(value).type(type).unit(
                            unit).description(description).build();
                    result.add(p);
                }
            }
        }
        return fixPropertiesForXcdl(result);
    }

    private String createDescription(final Property property,
            final ValueSet valueSet) {
        List<DataRef> dataReves = valueSet.getDataReves();
        String result = String.format("%s %s, name %s %s, "
                + "valueSet %s, labValue %s %s inch, dataRef %s %s", property
                .getSource().value(), property.getCat().value(), property
                .getName().getId(), property.getName().getValues().get(0),
                valueSet.getId(), valueSet.getLabValue().getVals().get(0)
                        .getValues().get(0), valueSet.getLabValue().getTypes()
                        .get(0).getValue().value(), dataReves == null
                        || dataReves.size() == 0 ? "null" : dataReves.get(0)
                        .getPropertySetId(), dataReves == null
                        || dataReves.size() == 0 ? "null" : dataReves.get(0)
                        .getInd().value());
        System.out.println("Generated description: " + result);
        return result;
    }

    /**
     * @return the XCDL object
     */
    public Xcdl getXcdl() {
        return xcdl;
    }

    /**
     * @param args unused
     */
    public static void main(final String[] args) {
        XcdlParser p = new XcdlParser(
                new File(
                        "PP/xcl/src/java/eu/planets_project/ifr/core/services/characterisation/extractor/xcdl/xcdl.xml"));
        List<eu.planets_project.services.datatypes.Property> properties = p
                .getProperties();
        for (eu.planets_project.services.datatypes.Property property : properties) {
            System.out.println(property);
        }
    }

    /**
     * NOTE: This is a temporary solution.
     * @return The properties parsed from the XCDL file
     */
    static List<eu.planets_project.services.datatypes.Property> fixPropertiesForXcdl(
            List<eu.planets_project.services.datatypes.Property> properties) {
        /*
         * This is totally work in progress... The basic idea is: We wrap all
         * this stuff here around the plain properties to make it work for the
         * XCDL comparator.
         */
        System.out.println("Attempting to convert properties: ");
        for (eu.planets_project.services.datatypes.Property property : properties) {
            System.out.println(property);
        }
        List<eu.planets_project.services.datatypes.Property> result = new ArrayList<eu.planets_project.services.datatypes.Property>();
        result.add(new eu.planets_project.services.datatypes.Property.Builder(
                XcdlProperties.makePropertyURI("1", "normData")).name(
                "normData").type("normData").description("object").value(
                "00 01 02 03 04 05 06 07 08 09 0a").build());
        result.add(new eu.planets_project.services.datatypes.Property.Builder(
                XcdlProperties.makePropertyURI("id_0", "propertySet")).name(
                "propertySet").type("propertySet").description(
        /*
         * FIXME: this following ID is particularly nasty and setting it here
         * makes no sense whatsoever; could we get rid of property sets here
         * altogether?
         */
        "ref i_i1_i5 suggestedPaletteAlpha").build());
        for (eu.planets_project.services.datatypes.Property property : properties) {
            result
                    .add(new eu.planets_project.services.datatypes.Property.Builder(
                            property.getUri()).name(property.getName()).type(
                            property.getType()).value(property.getValue())
                            .unit(property.getUnit()).description(
                                    /* FIXME ...since this is really nasty too: */
                                    property.getDescription().replace(
                                            "dataRef null null",
                                            "dataRef id_0 global")).build());
        }
        return result;
    }
}
