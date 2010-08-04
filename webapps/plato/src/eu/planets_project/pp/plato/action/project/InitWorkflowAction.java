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
package eu.planets_project.pp.plato.action.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 *
 * @author Christoph Becker
 * This action performs necessary initialisation work before the workflow can be started.
 * It is mostly about refreshing and outjecting session variables, lists, the tree model etc.,
 * when reloading or changing a project.
 */

@Name("initworkflow")
@Scope(ScopeType.SESSION)
public class InitWorkflowAction implements Serializable {

    private static Log log = PlatoLogger.getLogger(InitWorkflowAction.class);

    /*
     * We need that for setting the leaves variable to an empty list
     * in the Seam Context, because without it we get an Exception
     * if we select a Node in Eval/Transform, so that the leaves are
     * set and then select new project and go to Eval/Transform back.
     * Changed it now to just an empty leaves variable. Seems to work.
     * And is better, because no other call has to be performed. Just
     * outchecking null seems to be enough.
     */
    @In(required=false)
    @Out(required=false)
    List<Leaf> leaves; //= new ArrayList<Leaf>();

    /**
     * List of alternatives. The session variable contains all alternatives that shall be considered for
     * further evaluation. The list doesn't contain those alternatives that shall be discarded.
     */
    @DataModel
    private List<Alternative> alternativeList;

    /**
     * the list of sample records comes from the selectedPlan and contains all its sample records
     */
    @DataModel
    private List<SampleObject> records;

    /**
     * this is the currently active planning project - the central member variable of Plato :)
     */
    @In
    private Plan selectedPlan;

    /**
     * alternativeList and records are loaded everytime a new project is created
     * or a project is loaded. Important for various tables!
     * This observer function gets active whenever the <code>reload</code> event is raised.
     * It clears all the member variables and fills them again.
     * Also calls @see {@link #initTree()}
     */
    @Observer("reload")
    public void init() {
        log.debug("Init Method Called");
        log.debug("selectedPlan: " + selectedPlan);

        if (leaves == null) {
            leaves = new ArrayList<Leaf>();
        } else {
            leaves.clear();
        }

        this.alternativeList = selectedPlan.getAlternativesDefinition().getConsideredAlternatives();

        
        //
        // Perform insanity check
        //
        
        List<String> allAlternativesNames = new ArrayList<String>();
        List<String> discardedAlternativesNames = new ArrayList<String>();
        
        for (Alternative a: selectedPlan.getAlternativesDefinition().getAlternatives()) {
            allAlternativesNames.add(a.getName());
            if (a.isDiscarded()) {
                discardedAlternativesNames.add(a.getName());
            }
        }

        this.records = selectedPlan.getSampleRecordsDefinition().getRecords();

        log.debug("performing SANITY CHECK on evaluation values...");
        log.debug("-- number of records: "+records.size());
        log.debug("-- number of alternatives: "+alternativeList.size());
        
        StringBuffer alternatives=new StringBuffer();
        alternatives.append("Alternatives: ");
        for (Alternative a: alternativeList) {
            alternatives.append(a.getName()).append(":::");
        }
        log.debug(alternatives.toString());

        for (Leaf l : selectedPlan.getTree().getRoot().getAllLeaves()) {
            
            int size = l.getValueMap().values().size();
            
            log.debug("-- "+l.getName()+" has values for "+size+" alternatives ");
            log.debug("-- "+l.getName()+ " is "+(l.isSingle() ? "" : " NOT ") + "single");
            
            for (String s : l.getValueMap().keySet()) {
                // if alternative is not in list of all alternatives -> insanity
                if (! allAlternativesNames.contains(s)) {
                    log.error("-- INSANITY FOUND! Leaf "+l.getName()+" has values for the following alternative names: "+s);
                }
                // if the alternative is a dicarded one we issue a warning
                if (discardedAlternativesNames.contains(s)) {
                    log.warn("-- Leaf " + l.getName() + " has values for a discarded alternative named: " + s);
                }
            }
            
            for (Entry<String, Values> e : l.getValueMap().entrySet()) {
                
                Values v = e.getValue();
                if ((!l.isSingle() && v.size() != records.size()) ||
                       (l.isSingle() && v.size() > 1) ) {
                    log.error("---- INSANITY FOUND! ---- a values object with "+v.size()+" entries");
                }
                for (int i = 0; i < v.getList().size(); i++) {
                    Value value = v.getList().get(i);
                    if (value.getScale() == null)  {
                        if (discardedAlternativesNames.contains(e.getKey())) {
                            log.warn("Potential insantity found: record " + i + " for alternative "+e.getKey()+": Scale == null" );
                        } else {
                            log.error("------ INSANITY FOUND! record " + i + ": Scale == null" );
                        }
                    }
                }
            }
        }


    }

}
