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

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jfree.util.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
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
    private static PlanetsLogger log = PlanetsLogger.getLogger(ExperimentRecord.class);

    /* The Experiment */
    ExperimentImpl experiment;
    
    /* The Comments associated with the Experiment */
    @XmlElement(name="comment", type=CommentImpl.class)
    List<CommentImpl> comments;
    

    /* The hooks to the DB back-end */
    @XmlTransient
    private static ExperimentPersistencyRemote edao = ExperimentPersistencyImpl.getInstance();
    @XmlTransient
    private static CommentPersistencyRemote cmp = CommentPersistencyImpl.getInstance();

    /**
     * Constructor used by JAXB when importing.
     */
    protected ExperimentRecord() {
    }
    
    /**
     * Constructor used when exporting:
     * 
     * IMPORTANT The Comments use the DB ID key to locate the parent comment.
     * The ID should be changed when you import the comments.
     * i.e. we use different fields in the persisted version, and patch up on import.
     * Similarly, the experiment ID must be added to each comment when re-loading.
     * 
     */
    public ExperimentRecord( long id ) {
        // Load the experiment:
        experiment = (ExperimentImpl) edao.findExperiment(id);
        // Now add the comments:
        List<Comment> coms = cmp.getAllComments(experiment.getEntityID());
        comments = new ArrayList<CommentImpl>();
        for( Comment c : coms ) {
            log.info("Adding comment: "+c.getTitle()+" by "+c.getAuthorID());
            comments.add( (CommentImpl) c );
        }
        // When creating an experiment record for export, we must ensure the comment IDs are stored okay.
        // The parentID's refer to these, and these must be re-written on import.
        for( CommentImpl c : comments ) {
            c.setXmlCommentID( c.getCommentID() );
        }
        // TODO Optionally add files? Or perhaps they should be stored at the ExperimentRecords level?
        // NOTE that this is not required for basic migration between DBs, as the data refs will remain valid.
    }

    /**
     * Factory that does everything required when exporting an experiment as an Experiment Record
     * 
     * @param experimentId
     * @return
     */
    static public ExperimentRecord exportExperimentRecord( long experimentId ) {
        return new ExperimentRecord(experimentId);
    }

    /**
     * Factory that does everything required when importing an experiment from an ExperimentRecord.
     * 
     * FIXME Test this comment loader!
     * 
     * @param er
     * @return
     */
    static public long importExperimentRecord( ExperimentRecord er ) {
        // Persist the experiment:
        long eid = edao.persistExperiment(er.experiment);
        // Also remember the comments, to make it easier to patch up the lists:
        HashMap<Long,Comment> cmts = new HashMap<Long,Comment>();
        
        // Persist the comments, using the correct experiment ID:
        for( CommentImpl c : er.comments ) {
        	// Update the comments to the new experiment id:
            c.setExperimentID(eid);
            // Persist the comments:
            long cid = cmp.persistComment(c);
            // Retrieve it again, for cross-reference resolution:
            cmts.put(new Long(c.getXmlCommentID()), cmp.findComment(cid));
        }        
        
        // Go through the comments and correct the parent IDs:
        for( Comment c1 : cmts.values() ) {
        	// For this old identifier, look for it's parent comment:
        	Comment c2 = cmts.get(c1.getParentID());
        	if( c2 != null ) {
        	    // Update the parent ID to the new comment ID:
        	    c1.setParentID( c2.getCommentID() );
        	    // Don't forget to persist the id changes to the DB:
        	    cmp.updateComment(c1);
        	}
        }
        
        // return the experiment id:
        return eid;
    }
}
