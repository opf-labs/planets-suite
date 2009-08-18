/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.tb.impl.serialization;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.tb.api.CommentManager;
import eu.planets_project.tb.api.model.Comment;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.CommentPersistencyRemote;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.impl.CommentManagerImpl;
import eu.planets_project.tb.impl.model.CommentImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.persistency.CommentPersistencyImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * 
 * @author AnJackson
 *
 */
@XmlRootElement(name = "ExperimentRecord", namespace = "http://www.planets-project.eu/testbed/experiment")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExperimentRecord {

    ExperimentImpl experiment;
    List<CommentImpl> comments;

    private static ExperimentPersistencyRemote edao = ExperimentPersistencyImpl.getInstance();
    private static CommentPersistencyRemote cmp = CommentPersistencyImpl.getInstance();

    /*
     * Constructor used by JAXB when importing.
     */
    public ExperimentRecord() {
    }
    
    /*
     * Constructor used when exporting:
     */
    public ExperimentRecord( long id ) {
        // Load the experiment:
        experiment = (ExperimentImpl) edao.findExperiment(id);
        // Now add the comments:
        List<Comment> coms = cmp.getComments(experiment.getEntityID(), "this argument is ignored!");
        comments = new ArrayList<CommentImpl>();
        for( Comment c : coms ) {
            comments.add( (CommentImpl) c );
        }
    }

    /**
     * Factory that does everything required when exporting an experiment as an Experiment Record
     * 
     * FIXME IMPORTANT The Comments use the DB ID key to locate the parent comment.
     * The ID should be changed when you import/export the comments.
     * i.e. we use different fields in the persisted version, and patch up on import.
     * Similarly, the experiment ID must be added to each comment when re-loading.
     * 
     * FIXME Finish this!
     * 
     * @param experimentId
     * @return
     */
    static public ExperimentRecord exportExperimentRecord( long experimentId ) {
        ExperimentRecord er = new ExperimentRecord(experimentId);
        // When creating an experiment record for export, we must ensure the comment IDs are stored okay.
        // The parentID's refer to these, and these must be re-written on import.
        for( CommentImpl c : er.comments ) {
            c.setXmlCommentID( c.getCommentID() );
        }
        // TODO Optionally add files? Or perhaps they should be stored at the ExperimentRecords level?
        return er;
    }

    /**
     * Factory that does everything required when importing an experiment from an ExperimentRecord.
     * 
     * FIXME Finish this!
     * 
     * @param er
     * @return
     */
    static public long importExperimentRecord( ExperimentRecord er ) {
        // Persist the experiment:
        long eid = edao.persistExperiment(er.experiment);
        
        // Update the comments to the new experiment id:
        
        // Persist the comments:
        
        // Go through the comments and correct the parent ids:
        
        // Don't forget to persist the id changes to the DB:
        
        // return the experiment id:
        return eid;
    }
}
