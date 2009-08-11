package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.CatType;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.DataRef;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.DataRefType;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.InformType;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.LabValType;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.LabValue;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Name;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.NormData;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.PropertySet;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.SourceType;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.ValueSet;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Xcdl;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.LabValue.Type;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.LabValue.Val;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.PropertySet.ValueSetRelations;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.PropertySet.ValueSetRelations.Ref;
import eu.planets_project.services.datatypes.Property;

/**
 * Creates an XCDL XML string from a list of properties.
 * <p/>
 * Note: This is work in progress.
 * @see XcdlCreatorTests
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class XcdlCreator {

    private String xcdlXml;

    /**
     * @param xcdlProps Properties to be converted into an XCDL. It is assumed
     *        these are properties of a single object and will be represented as
     *        a single object in the resulting XCDL. The props need to contain
     */

    public XcdlCreator(List<Property> xcdlProps) {
        try {
            JAXBContext jc = JAXBContext
                    .newInstance("eu.planets_project.ifr.core.services."
                            + "characterisation.extractor.xcdl.generated");
            Marshaller marshaller = jc.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            Xcdl xcdl = createXcdlObject(xcdlProps);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller
                    .setProperty("jaxb.schemaLocation",
                            "http://www.planets-project.eu/xcl/schemas/xcl res/xcl/xcdl/XCDLCore.xsd");
            marshaller.marshal(xcdl, stringWriter);
            this.xcdlXml = stringWriter.toString();
            System.out.println(String.format("Marshalled %s to:\n%s", xcdl,
                    xcdlXml));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The XCDL XML string created from the properties.
     */
    public String getXcdlXml() {
        return xcdlXml;
    }

    // ------------
    // private API:
    // ------------

    /**
     * Names for XCDL properties.
     */
    enum PropertyName {
        PROPERTY("property"), PROPERTYSET("propertyset"), NORMDATA("normdata");
        String s;

        private PropertyName(String s) {
            this.s = s;
        }

        /**
         * {@inheritDoc}
         * @see java.lang.Enum#toString()
         */
        public String toString() {
            return s;
        }
    }

    private Xcdl createXcdlObject(List<Property> xcdlProps) {
        /*
         * The given properties are assumed to be properties of a single object
         * and are thus wrapped in a single XCDL-Object.
         */
        eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object object = new eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object();
        object.setId("generated_" + String.valueOf(System.currentTimeMillis()));
        for (Property prop : xcdlProps) {
            if (prop.getType() == null) {
                throw new IllegalArgumentException("Property has no name: "
                        + prop);
            }
            if (prop.getType().toLowerCase().equals(PropertyName.NORMDATA.s)) {
                addNormData(object, prop);
            } else if (prop.getType().toLowerCase().equals(
                    PropertyName.PROPERTYSET.s)) {
                addPropertySet(object, prop);
            } else if (prop.getType().toLowerCase().equals(
                    PropertyName.PROPERTY.s)) {
                addProperty(object, prop);
            } else {
                throw new IllegalArgumentException(
                        String
                                .format(
                                        "Cannot convert property with type '%s', only know about '%s'",
                                        prop.getType(), Arrays
                                                .asList(PropertyName.values())));
            }
        }
        Xcdl xcdl = new Xcdl();
        xcdl.setId("generated_" + String.valueOf(System.currentTimeMillis()));
        xcdl.getObjects().add(object);
        return xcdl;
    }

    private void addProperty(
            eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object object,
            Property prop) {
        eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Property p = new eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Property();
       
        /*
         * This is where it gets ugly: we map our property values onto the XCDL
         * XML types:
         */
        String[] levelOneTokens = clean(prop.getDescription().split(","));
        String[] valueTokens = clean(levelOneTokens[0].split(" "));
        String[] nameTokens = clean(levelOneTokens[1].split(" "));
        String[] valueSetTokens = clean(levelOneTokens[2].split(" "));
        String[] labValTokens = clean(levelOneTokens[3].split(" "));
        String[] dataRefTokens = clean(levelOneTokens[4].split(" "));
        
        p.setId("p" + nameTokens[1].replaceAll("id", ""));

        p.setSource(SourceType.fromValue(valueTokens[0].toLowerCase()));
        p.setCat(CatType.fromValue(valueTokens[1].toLowerCase()));

        /* The name element: */
        Name name = new Name();
        name.getValues().add(nameTokens[2]);
        name.setId(nameTokens[1]);
        p.setName(name);

        // TODO no multiple value sets, no multiple lab vals or data refs per
        // value set, is this OK?

        /* The value set element: */
        ValueSet set = new ValueSet();
        set.setId(valueSetTokens[1]);

        /* The lab val: */
        LabValue labValue = new LabValue();
        Type type = new Type();
        type.setValue(determineLabValType(labValTokens[2]));
        labValue.getTypes().add(type);
        Val val = new Val();
        val.getValues().add(labValTokens[1]);
        labValue.getVals().add(val);
        set.setLabValue(labValue);

        /* The data ref: */
        DataRef dataRef = new DataRef();
        dataRef.setPropertySetId(dataRefTokens[1]);
        dataRef.setInd(determineDataRef(dataRefTokens[2]));
        set.getDataReves().add(dataRef);

        p.getValueSets().add(set);
        object.getProperties().add(p);
    }

    private String[] clean(String[] split) {
        List<String> result = new ArrayList<String>();
        for (String string : split) {
            String clean = string.trim();
            if (clean.length() > 0) {
                result.add(clean);
            }
        }
        return result.toArray(new String[] {});
    }

    private void addPropertySet(
            eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object object,
            Property prop) {
        PropertySet set = new PropertySet();
        set.setId("id_0");
        ValueSetRelations rel = new ValueSetRelations();
        String desc = prop.getDescription();
        if (desc == null || !desc.contains(" ")) {
            throw new IllegalArgumentException(String.format(
                    "Cannot use description '%s' here", desc));
        }
        String[] levelOneTokens = desc.split(",");
        for (String s : levelOneTokens) {
            String[] tokens = s.trim().split(" ");
            Ref ref = new Ref();
            ref.setValueSetId(tokens[1]);
            ref.setName(tokens[2]);
            rel.getReves().add(ref);
        }
        set.setValueSetRelations(rel);
        object.getPropertySets().add(set);
    }

    private void addNormData(
            eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object object,
            Property prop) {
        NormData normData = new NormData();
        String description = prop.getDescription();
        if (description == null || description.trim().equals("")) {
            throw new IllegalArgumentException(
                    "Normdata property must have a description");
        }
        normData.setType(InformType.fromValue(description.toLowerCase()));
        normData.setId("nd1");
        if (prop.getValue() == null) {
            throw new IllegalArgumentException("Normdata must have a value");
        }
        normData.setValue(prop.getValue());
        object.getNormDatas().add(normData);
    }

    private DataRefType determineDataRef(String description) {
        String d = description;
        DataRefType type = DataRefType.NONE;
        try {
            type = DataRefType.fromValue(d);
        } catch (IllegalArgumentException e) {
            System.err
                    .println(String
                            .format(
                                    "Warning: could not create a DataRefType for '%s', defaulting to '%s'",
                                    d, type));
        }
        return type;
    }

    private LabValType determineLabValType(String description) {
        LabValType labValType = LabValType.STRING;
        String labValProp = description;
        try {
            labValType = LabValType.fromValue(labValProp);
        } catch (IllegalArgumentException e) {
            System.err
                    .println(String
                            .format(
                                    "Warning: could not create a LabValType for '%s', defaulting to '%s'",
                                    labValProp, labValType));
        }
        return labValType;
    }

}
