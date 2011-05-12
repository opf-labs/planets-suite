/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.pp.plato.test.controller;

import java.util.List;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.Decision;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.Decision.GoDecision;
import eu.planets_project.pp.plato.model.scales.IntRangeScale;
import eu.planets_project.pp.plato.model.scales.OrdinalScale;
import eu.planets_project.pp.plato.model.scales.PositiveFloatScale;
import eu.planets_project.pp.plato.model.scales.PositiveIntegerScale;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.values.IntRangeValue;
import eu.planets_project.pp.plato.model.values.OrdinalValue;
import eu.planets_project.pp.plato.model.values.PositiveFloatValue;
import eu.planets_project.pp.plato.model.values.PositiveIntegerValue;

public class TestProjectFactory {
    static public Plan createMinimalistTestProject() {
        Plan q = new Plan();

        String byteStream = "just a byte stream";

        q.getPlanProperties().setName(
                "Minimalist test project in state x");
        q.getPlanProperties().setDescription(
                "This is Kevin's minimalist project");
        q.getPlanProperties().setAuthor("Kevin Stadler");
        q.getPlanProperties().setOrganization(
                "Vienna University of Technology");

        q.getProjectBasis().setDocumentTypes(
                "textdateien MIT ƒ÷‹‰ˆ¸ﬂ, die der kevin alle selbst geschrieben hat :(");

        SampleObject rec1 = new SampleObject();
        rec1.setFullname("sample 1");
        rec1.setShortName("eins");
        rec1.getData().setData(byteStream.getBytes());

        q.getSampleRecordsDefinition().setSamplesDescription(
                "some test samples");

        q.getSampleRecordsDefinition().addRecord(rec1);

        SampleObject rec2 = new SampleObject();
        rec2.setFullname("sample number two");
        rec2.setShortName("zwo");
        rec2.getData().setData(byteStream.getBytes());

        q.getSampleRecordsDefinition().addRecord(rec2);

        Alternative alt1 = Alternative.createAlternative();
        alt1.setName("PDF/A ToolA");
        alt1.setDescription("Convert to PDF/A using this new tool named 'A'");
        q.getAlternativesDefinition().addAlternative(alt1);

        Alternative alt2 = Alternative.createAlternative();
        alt2.setName("PDF/A ToolB");
        alt2.setDescription("Convert to PDF/A using the well-tested tool 'B'");
        q.getAlternativesDefinition().addAlternative(alt2);

        Decision d2 = new Decision();
        d2.setDecision(GoDecision.GO);
        d2.setActionNeeded("also no actions needed");
        d2.setReason("Reason, why no actions are needed? Hmm...");

        q.setDecision(d2);

        Node rootN = new Node();
        rootN.setName("Minimalist root node");

        Node childNode = new Node();
        childNode.setName("Image properties");
        rootN.addChild(childNode);

        Leaf leafWithUnit = new Leaf();
        leafWithUnit.setName("Amount of Pixel");
        leafWithUnit.changeScale(new PositiveIntegerScale());
        leafWithUnit.getScale().setUnit("px");
        childNode.addChild(leafWithUnit);

        Leaf ordinalLeaf = new Leaf();
        ordinalLeaf.setName("Karma");
        OrdinalScale ordinalScale = new OrdinalScale();
        ordinalScale.setRestriction("Good/Bad/Evil");
        ordinalLeaf.changeScale(ordinalScale);
        rootN.addChild(ordinalLeaf);

        Leaf numericLeaf = new Leaf();
        numericLeaf.setName("Filesize (in Relation to Original)");
        numericLeaf.changeScale(new PositiveFloatScale());
        numericLeaf.getScale().setUnit("MB");
        rootN.addChild(numericLeaf);

        Leaf singleLeaf = new Leaf();
        singleLeaf.setName("A Single-Leaf");
        singleLeaf.changeScale(new PositiveFloatScale());
        singleLeaf.getScale().setUnit("tbd");
        singleLeaf.setSingle(true);
        rootN.addChild(singleLeaf);

        Leaf restrictedLeaf = new Leaf();
        restrictedLeaf.setName("IntRange 0-10");
        IntRangeScale range = new IntRangeScale();
        range.setLowerBound(0);
        range.setUpperBound(10);
        restrictedLeaf.changeScale(range);
        rootN.addChild(restrictedLeaf);

        q.getTree().setRoot(rootN);

        q.getTree().initValues(q.getAlternativesDefinition().getAlternatives(),
                q.getSampleRecordsDefinition().getRecords().size());


        int j = 3;
        int alternativeIndex = 0;
        for (Alternative alt : q.getAlternativesDefinition().getAlternatives()) {

            double[] singleLeafValues = { 3.2, 5.2 };
            int[][] leafWithUnitValues = { { 1024, 2048 }, { 2048, 2048 } };
            int[][] ordinalValues = { { 0, 1 }, { 2, 1 } };
            double[][] numericValues = { { 6500.32, 7312, 28 },
                    { 8212.65, 7921.235 } };
            int[][] restrictedValues = { { 8, 5 }, { 3, 7 } };
            ((PositiveFloatValue) singleLeaf.getValueMap().get(alt.getName())
                    .getValue(0)).setValue(singleLeafValues[alternativeIndex]);

            List<String> ordinalOptions = ((OrdinalScale) ordinalLeaf
                    .getScale()).getList();

            for (int i = 0; i < q.getSampleRecordsDefinition().getRecords()
                    .size(); i++) {
                ((PositiveIntegerValue) leafWithUnit.getValueMap().get(
                        alt.getName()).getValue(i))
                        .setValue(leafWithUnitValues[alternativeIndex][i]);
                ((OrdinalValue) ordinalLeaf.getValueMap().get(alt.getName())
                        .getValue(i)).setValue(ordinalOptions
                        .get(ordinalValues[alternativeIndex][i]));
                ((PositiveFloatValue) numericLeaf.getValueMap().get(
                        alt.getName()).getValue(i))
                        .setValue(numericValues[alternativeIndex][i]);
                ((IntRangeValue) restrictedLeaf.getValueMap()
                        .get(alt.getName()).getValue(i))
                        .setValue(restrictedValues[alternativeIndex][i]);
            }
/*
            NumericTransformer nt = (NumericTransformer) singleLeaf
                    .getTransformer();
            for (int i = 0; i < nt.getThresholds().size(); i++) {
                nt.getThresholds().put(i, new Double(2 + i));
            }
            nt = (NumericTransformer) leafWithUnit.getTransformer();
            for (int i = 0; i < nt.getThresholds().size(); i++) {
                nt.getThresholds().set(i, new Double(Math.pow(2, i + 8)));
            }
            nt = (NumericTransformer) numericLeaf.getTransformer();
            for (int i = 0; i < nt.getThresholds().size(); i++) {
                nt.getThresholds().set(i, new Double(4000 + i * 1000));
            }
            nt.setMode(TransformationMode.LINEAR); // !!!

            nt = (NumericTransformer) restrictedLeaf.getTransformer();
            for (int i = 0; i < nt.getThresholds().size(); i++) {
                nt.getThresholds().set(i, new Double(1 + 2 * i));
            }
            OrdinalTransformer ot = (OrdinalTransformer) ordinalLeaf
                    .getTransformer();
            Map<String, TargetValueObject> map = ot.getMapping();

            int i = 4;
            for (String s : ordinalScale.getList()) {
                TargetValueObject o = new TargetValueObject();
                // 3 ordinal values, first -> 4, second -> 2, third -> 0
                o.setValue(i);
                map.put(s, o);
                i -= 2;
            }
*/
            j += 7;
            alternativeIndex++;
        }
        q.getTree().initWeights();
        q.getState().setValue(5);
        return q;
    }

}
