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

package eu.planets_project.pp.plato.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.Length;

/**
 * Class containing all properties for workflow step 'Define Alternatives'.
 *
 * @author Hannes Kulovits
 */
@Entity
public class AlternativesDefinition implements Serializable, ITouchable {

    private static final long serialVersionUID = 5305133244443843393L;

    @Id @GeneratedValue
    private int id;
    
    @Length(max = 32672)
    @Column(length = 32672)
    private String description;

    public Alternative alternativeByName(String name) {
        for (Alternative a : alternatives) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }
    
    /**
     * Hibernate does NOT support IndexColumn properly
     * see: http://opensource.atlassian.com/projects/hibernate/browse/HHH-3160
     * therefore we use a sorted list instead,
     * 
     * and sort by Alternative.alternativeIndex which we have to MAINTAIN ourselves
     * to add an alternative use addAlternatives() and not getAlternatives().add()
     *
     *  update: the issue is still listed as open/unresolved; however, an update to hibernate > 3.2.3 
     *  may still be able to resolve it... in the future.
     *
     * One reason we had to use @IndexColumn was because of fetch type EAGER. This problem
     * can be resolved by using @Fetch(FetchMode.SUBSELECT)
     */
    @OneToMany(cascade=CascadeType.ALL, mappedBy="alternativesDefinition",fetch=FetchType.EAGER)
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @org.hibernate.annotations.OrderBy(clause="alternativeIndex asc")
    @Fetch(value=FetchMode.SELECT)
    private List<Alternative> alternatives = new ArrayList<Alternative>();

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    /**
     * List of alternative preservation solutions that shall not be considered for evaluation because
     * they are definitely inappropriate for instance. Consequently they need not to be deleted
     * to finish the workflow.
     */
    @Transient
    private List<Alternative> consideredAlternatives;

    public List<Alternative> getAlternatives() {
        return Collections.unmodifiableList(alternatives);
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }   

    /**
     * List of alternative preservation solutions that shall not be considered for evaluation because
     * they are definitely inappropriate for instance. Consequently they need not to be deleted
     * to finish the workflow.
     *
     * @return alternatives that shall be considered for evaluation
     */
    public List<Alternative> getConsideredAlternatives() {
        ArrayList<Alternative> consideredAlternatives = new ArrayList<Alternative>();

        for(Alternative alt : alternatives) {
            if ( ! alt.isDiscarded()) {
                consideredAlternatives.add(alt);
            }
        }

        return consideredAlternatives;
    }

    public void setConsideredAlternatives(List<Alternative> consideredAlternatives) {

    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }
    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }

    public boolean isChanged(){
        return changeLog.isAltered();
    }

    public void touch() {
        changeLog.touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        h.visit(this);
        // call handleChanges of all child elementss
        for (Alternative alt : alternatives) {
            alt.handleChanges(h);
        }
    }

    public void removeAlternative(Alternative alternative) {
        alternatives.remove(alternative);
    }
    
    /**
     * returns a list containing the lower case names of all alternatives (including discarded)
     */
    private List<String> getUsedNames(){
        List<String> usedNames = new ArrayList<String>();
        for (Alternative a: alternatives) {
            usedNames.add(a.getName().toLowerCase());
        }
        return usedNames;
    }
    
    /**
     * Creates a unique alternative name based on <code>name</code>
     * and adds the new name to the list of <code>usedNames</code>
     *
     * @param usedNames a list of all names of the alternatives in the current project
     * @param name
     * @return a unique alternative name with a maximum length of 20 characters.
     */
    public String createUniqueName(String name){
        List<String> usedNames = getUsedNames();
        
        String shortname = name.substring(0, Math.min(30, name.length()));
        if (!usedNames.contains(shortname.toLowerCase())) {
            return shortname;
        } else {
            // start with 1-digit numbers
            int i = 1;
            int exp = 0;
            String base;
            if (shortname.length() <= 28)
                base = shortname;
            else
                base = shortname.substring(0, 28);
            String newName = base+ "-" + i;
            while (usedNames.contains(newName.toLowerCase())) {
                i++;
                if ((int)Math.log10(i) > exp) {
                    // i-digits are not enough - extend the postfix
                    exp = (int)Math.log10(i);
                    // and reduce the length of the base if necessary
                    base = shortname.substring(0, Math.min(shortname.length(), 28-exp));
                }
                newName = base + "-" + i;
            }
            return newName;
        }
    }
    

    /**
     * adds the given alternative to the list of alternatives.
     * used for importing by the digester.
     *
     * we have to ensure referential integrity!
     *
     * @param alternative
     */
    public void addAlternative(Alternative alternative) {

        // to ensure referential integrity
        alternative.setAlternativesDefinition(this);

        long index = 1;
        if (alternatives.size() > 0) {
            index = (alternatives.get(alternatives.size()-1)).getAlternativeIndex()+1;
        }

        alternative.setAlternativeIndex(index);
        alternatives.add(alternative);
    }

}
