package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlCreator.PropertyName;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.LabValue;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.NormData;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Property;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.ValueSet;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Xcdl;
import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.LabValue.Val;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.FileUtils;

/**
 * Access to a complete XCDL, via JAXB-generated classes.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class XcdlParser implements XcdlAccess {

    static final String NONE = "none";
    private Xcdl xcdl;

    /**
     * @param xcdl The reader containing the XCDL to parse.
     */
    public XcdlParser(final Reader xcdl) {
        FileReader fileReader = null;
        try {
            this.xcdl = (Xcdl) createUnmarshaller().unmarshal(xcdl);
        } catch (JAXBException e) {
            e.printStackTrace();
            FileUtils.close(fileReader);
            throw new IllegalArgumentException("Could not load XCDL from " + xcdl);
        }
    }

    /**
     * @param xcdl The input stream containing the XCDL to parse.
     */
    public XcdlParser(final InputStream xcdl) {
        try {
            this.xcdl = (Xcdl) createUnmarshaller().unmarshal(xcdl);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Could not load XCDL from " + xcdl);
        }
    }

    private Unmarshaller createUnmarshaller() {
        Unmarshaller unmarshaller = null;
        try {
            JAXBContext jc = JAXBContext
                    .newInstance("eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated");
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return unmarshaller;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlAccess#getCharacteriseResult()
     */
    public CharacteriseResult getCharacteriseResult() {
        List<eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object> list = xcdl
                .getObjects();
        List<List<eu.planets_project.services.datatypes.Property>> all = new ArrayList<List<eu.planets_project.services.datatypes.Property>>();
        for (eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object o : list) {
            all.add(objectProperties(o));
        }
        if (all.size() == 1) {
            return new CharacteriseResult(all.get(0), new ServiceReport(Type.INFO, Status.SUCCESS,
                    "Flat properties"));
        }
        List<CharacteriseResult> embeddedResults = new ArrayList<CharacteriseResult>();
        for (List<eu.planets_project.services.datatypes.Property> props : all) {
            embeddedResults.add(new CharacteriseResult(props, new ServiceReport(Type.INFO,
                    Status.SUCCESS, "Embedded properties")));
        }
        return new CharacteriseResult(
                new ArrayList<eu.planets_project.services.datatypes.Property>(), new ServiceReport(
                        Type.INFO, Status.SUCCESS, "Empty top-level properties"), embeddedResults);
    }

    private List<eu.planets_project.services.datatypes.Property> objectProperties(
            eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object o) {
        List<eu.planets_project.services.datatypes.Property> result = new ArrayList<eu.planets_project.services.datatypes.Property>();
        if (o.getNormDatas().size() > 0) {
            result.add(normDataProperty(o));
        }
        List<Property> properties = o.getProperties();
        Set<String> added = new HashSet<String>();
        for (Property property : properties) {
            String name = property.getName().getValues().get(0);
            List<ValueSet> valueSets = property.getValueSets();
            for (ValueSet valueSet : valueSets) {
                // Avoid duplicate properties, which cause validation errors in the comparator
                if (!added.contains(name)) {
                    result.add(objectProperty(property, name, valueSet));
                    added.add(name);
                }
            }
        }
        return result;
    }

    private eu.planets_project.services.datatypes.Property objectProperty(Property property,
            String name, ValueSet valueSet) {
        LabValue labValue = valueSet.getLabValue();
        URI propUri = XcdlProperties.makePropertyURI(name);
        String value = "";
        String unit = NONE;
        if (labValue != null) {
            List<Val> val = labValue.getVals();
            List<String> values = val.get(0).getValues();
            value = values.size() > 0 ? values.get(0) : "";
            unit = labValue.getTypes().get(0).getValue().value();
        }
        String description = createDescription(property, valueSet);
        eu.planets_project.services.datatypes.Property p = new eu.planets_project.services.datatypes.Property.Builder(
                propUri).name(name).value(value).type(unit).unit(unit).description(description)
                .build();
        return p;
    }

    private eu.planets_project.services.datatypes.Property normDataProperty(
            eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated.Object o) {
        NormData normData = o.getNormDatas().get(0);
        String normDataType = normData.getType().value();
        normDataType = Character.toUpperCase(normDataType.charAt(0)) + normDataType.substring(1);
        eu.planets_project.services.datatypes.Property build = new eu.planets_project.services.datatypes.Property.Builder(
                XcdlProperties.makePropertyURI("normData" + normDataType)).name(
                "normData" + normDataType).type(normDataType.toLowerCase()).description(
                "raw descr, normData " + normData.getId()).value(normData.getValue()).build();
        return build;
    }

    private String createDescription(final Property property, final ValueSet valueSet) {
        String objectRef = valueSet.getObjectRef() != null ? valueSet.getObjectRef() : NONE;
        /* e.g. "raw desc, property id118, valueSet i_i1_i1_s32, objectRef none" */
        String result = String.format("%s %s, %s %s, valueSet %s, objectRef %s", property
                .getSource().value(), property.getCat().value(), PropertyName.PROPERTY.s, property
                .getName().getId(), valueSet.getId(), objectRef);
        System.out.println("Generated description: " + result);
        return result;
    }

    /**
     * @return the XCDL object
     */
    public Xcdl getXcdl() {
        return xcdl;
    }
}
