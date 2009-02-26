package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.io.StringWriter;
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
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.MeasureType;
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
import eu.planets_project.services.datatypes.Prop;

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
    public XcdlCreator(List<Prop<Object>> xcdlProps) {
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
                            "http://www.planets-project.eu/xcl/schemas/xcl ../res/xcl/xcdl/XCDLCore.xsd");
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
    private enum PropertyName {
        PROPERTY("property"), PROPERTYSET("propertyset"), NORMDATA("normdata");
        private String s;

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

    private Xcdl createXcdlObject(List<Prop<Object>> xcdlProps) {
        /*
         * The given properties are assumed to be properties of a single object
         * and are thus wrapped in a single XCDL-Object.
         */
        eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object object = new eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object();
        object.setId("generated_" + String.valueOf(System.currentTimeMillis()));
        for (Prop<Object> prop : xcdlProps) {
            if (prop.getName().toLowerCase().equals(PropertyName.NORMDATA.s)) {
                addNormData(object, prop);
            } else if (prop.getName().toLowerCase().equals(
                    PropertyName.PROPERTYSET.s)) {
                addPropertySet(object, prop);
            } else if (prop.getName().toLowerCase().equals(
                    PropertyName.PROPERTY.s)) {
                addProperty(object, prop);
            } else {
                throw new IllegalArgumentException(
                        String
                                .format(
                                        "Cannot convert property with name '%s', only know about '%s'",
                                        prop.getName(), Arrays
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
            Prop<Object> prop) {
        eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Property p = new eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Property();
        p.setId(prop.getType());
        p.setSource(SourceType.fromValue(((Prop)prop.getValues().get(0)).getValues().get(0).toString()
                .toLowerCase()));
        p.setCat(CatType.fromValue(((Prop)prop.getValues().get(0)).getValues().get(1).toString()
                .toLowerCase()));
        /* The name element: */
        Name name = new Name();
        for (Object o : prop.getValues("name")) {
            Prop<String> nameProp = (Prop) o;
            name.getValues().add(nameProp.getDescription());
            name.setId(nameProp.getType());
            p.setName(name);
        }
        /* The value set element: */
        for (Object valSet : prop.getValues("valueset")) {
            Prop<Prop> valSetProp = (Prop) valSet;
            ValueSet set = new ValueSet();
            set.setId(valSetProp.getType());
            /* The lab vals: */
            for (Prop labProp : valSetProp.getValues("labvalue")) {
                LabValue labValue = new LabValue();
                Type type = new Type();
                type.setValue(determineLabValType(labProp));
                labValue.getTypes().add(type);
                Val val = new Val();
                // val.setUnit(determineMeasureType(labProp));
                val.getValues().addAll(labProp.getValues());
                labValue.getVals().add(val);
                set.setLabValue(labValue);
            }
            /* The data refs: */
            List<Prop> dataRefs = valSetProp.getValues("dataref");
            for (Prop dataRefProp : dataRefs) {
                DataRef dataRef = new DataRef();
                dataRef.setPropertySetId(dataRefProp.getType());
                dataRef.setInd(determineDataRef(dataRefProp));
                set.getDataReves().add(dataRef);
            }
            p.getValueSets().add(set);
        }
        object.getProperties().add(p);
    }

    private void addPropertySet(
            eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object object,
            Prop prop) {
        PropertySet set = new PropertySet();
        set.setId(prop.getType());
        ValueSetRelations rel = new ValueSetRelations();
        List<Prop> props = prop.getValues("ref");
        for (Prop p : props) {
            Ref ref = new Ref();
            ref.setValueSetId(p.getType());
            ref.setName(p.getDescription());
            rel.getReves().add(ref);
        }
        set.setValueSetRelations(rel);
        object.getPropertySets().add(set);
    }

    private void addNormData(
            eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object object,
            Prop prop) {
        NormData normData = new NormData();
        String description = prop.getDescription();
        if (description == null || description.trim().equals("")) {
            throw new IllegalArgumentException(
                    "Normdata property must have a description");
        }
        normData.setType(InformType.fromValue(description.toLowerCase()));
        normData.setId(prop.getType());
        List<String> values = prop.getValues();
        if (values.size() == 0) {
            throw new IllegalArgumentException("Normdata must have a value");
        }
        normData.setValue(values.get(0));
        object.getNormDatas().add(normData);
    }

    private DataRefType determineDataRef(Prop dataRefProp) {
        String d = dataRefProp.getDescription();
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

    private MeasureType determineMeasureType(Prop labProp) {
        String d = labProp.getUnit();
        MeasureType type = MeasureType.BIT;
        try {
            type = MeasureType.fromValue(d);
        } catch (IllegalArgumentException e) {
            System.err
                    .println(String
                            .format(
                                    "Warning: could not create a MeasureType for '%s', defaulting to '%s'",
                                    d, type));
        }
        return type;
    }

    private LabValType determineLabValType(Prop labProp) {
        LabValType labValType = LabValType.STRING;
        String labValProp = labProp.getDescription();
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
