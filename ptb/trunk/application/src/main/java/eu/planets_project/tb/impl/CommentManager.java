/**
 * 
 */
package eu.planets_project.tb.impl;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.Comment;

/**
 * @author alindley
 *
 */
//@Entity
public class CommentManager implements eu.planets_project.tb.api.CommentManager, java.io.Serializable {
	
	/*
	 * The CommentManager is used for managing existing comments and for creating/registering new ones.
	 * It does not contain any information on the structure (Post-Replies) of the comments, as this is 
	 * located in the Comment Class itself.
	 * 
	 * The structure looks like:
	 * HashMap hmExperimentComments<ExperimentID, HashMap<sPhaseID,Vector<Comments>>>
	 * and at the same time (mapped synchronously)
	 * HashMap hmCommentsMapping<CommentID,Comment>
	 */
	
	//@Id
	//@GeneratedValue
	private long CommentManagerID;
	private HashMap<Long,HashMap<String,Vector<Comment>>> hmExperimentComments;
	private HashMap<Long,Comment> hmCommentsMapping;
	
	private static CommentManager instance;
	
	private CommentManager(){
		hmExperimentComments = new HashMap<Long,HashMap<String,Vector<Comment>>>();
		hmCommentsMapping = new HashMap<Long,Comment>();
	}
	
	/**
	 * This class is implemented following the Java Singleton Pattern.
	 * Use this method to retrieve the instance of this class.
	 * @return
	 */
	public static synchronized CommentManager getInstance(){
		if (instance == null){
			instance = new CommentManager();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#getComment(java.lang.String)
	 */
	public  eu.planets_project.tb.api.model.Comment getComment(long commentID) {
		return this.hmCommentsMapping.get(commentID);
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#getCommentIDs(long, java.lang.String)
	 */
	public Vector<Long> getCommentIDs(long experimentID, String experimentPhaseID) {
		Vector<Long> vRet = new Vector<Long>();
		//HashMap hmExperimentComments<ExperimentID, HashMap<sPhaseID,Vector<Comments>>>
		if(this.hmExperimentComments.containsKey(experimentID)){
			HashMap<String,Vector<Comment>> hmPhaseComment = this.hmExperimentComments.get(experimentID);
			if(hmPhaseComment.containsKey(experimentPhaseID)){
				Iterator<Comment> itComments = hmPhaseComment.get(experimentPhaseID).iterator();
				while(itComments.hasNext()){
					vRet.addElement(itComments.next().getCommentID());
				}
			}
		}
		return vRet;
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#getComments(long, java.lang.String)
	 */
	public Vector<eu.planets_project.tb.api.model.Comment> getComments(long experimentID, String experimentPhaseID) {
		Vector<Comment> vRet = new Vector<Comment>();
		//HashMap hmExperimentComments<ExperimentID, HashMap<PhaseID,Vector<Comments>>>
		if(this.hmExperimentComments.containsKey(experimentID)){
			HashMap<String,Vector<Comment>> hmPhaseComment = this.hmExperimentComments.get(experimentID);
			if(hmPhaseComment.containsKey(experimentPhaseID)){
				vRet = hmPhaseComment.get(experimentPhaseID);
			}
		}
		return vRet;
	}

	/* (non-Javadoc)
	 * Please note: one ExperimentID as well as one ExperimentPhase may have multiple root comments
	 * getNewComment automatically register it in hmCommentsMapping AND in 	hmExperimentComments.
	 * @see eu.planets_project.tb.api.CommentManager#getNewComment()
	 */
	public Comment getNewRootComment(long lExperimentID, String sExperimentPhaseID) {
		eu.planets_project.tb.impl.model.Comment c1 = new eu.planets_project.tb.impl.model.Comment(lExperimentID, sExperimentPhaseID);
		boolean bOK = CommentHelper(true,c1,lExperimentID, sExperimentPhaseID,true);
		if(bOK)
			return c1;
		return null;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#removeComment(java.lang.String)
	 */
	public void removeComment(long commentID) {
		Comment comment = this.hmCommentsMapping.get(commentID);
		CommentHelper(false, comment,comment.getExperimentID(),comment.getExperimentPhaseID(),false);
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#registerComment(Comment, long, java.lang.String)
	 */
	public void registerComment(
			eu.planets_project.tb.api.model.Comment comment, long experimentID,
			String experimentPhaseID) {
		
		CommentHelper(true,comment,experimentID,experimentPhaseID,false);
		
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.CommentManager#updateComment(eu.planets_project.tb.api.model.Comment)
	 */
	public void updateComment(Comment comment) {
		CommentHelper(false, comment,comment.getExperimentID(),comment.getExperimentPhaseID(),false);
		
	}
	
	/**
	 * This method may be used to register a comment for a given Experiment and Phase within the CommentManager's HashMaps
	 * @param register_remove: States if a register (true) or a remove (false) shall be performed
	 * @param cInput: The input comment
	 * @param lExperimentID
	 * @param sExperimentPhaseID
	 * @param newComment: indicates if the prcedure for a new comment (true) or an existing comment (false) shall be followed
	 */
	private boolean CommentHelper(boolean bRegister_Remove, Comment cInput, long lExperimentID, String sExperimentPhaseID, boolean newComment){
		boolean bRet = true;
		Comment c1 = (Comment)cInput;
		HashMap<String,Vector<Comment>> hmPhaseComments;
		Vector<Comment> vExistingComments;
		
		if (lExperimentID>0&&sExperimentPhaseID!=null){
			
			//TODO: Problem why it could not work: Comment needs to be persisted before an ID is assigned!
			boolean bCommentExists = hmCommentsMapping.containsKey(c1.getCommentID());
			//if flag newComment and commentExists in every case return: false
			if((bRegister_Remove&&bCommentExists&&newComment)||(!bRegister_Remove&&bCommentExists&&newComment)){
				//TODO error log-message: CommentID does already exist
				return false;
			}
			
			//ADD TO hmCommentsMapping
			//INFO: HashMap hmCommentsMapping<CommentID,Comment>
			if(bRegister_Remove){
				//add
				hmCommentsMapping.put(c1.getCommentID(), c1);
			}else{
				//remove
				hmCommentsMapping.remove(c1.getCommentID());
			}
				
			//ADD TO hmExperimentComments
			//INFO:HashMap hmExperimentComments<ExperimentID, HashMap<sPhaseID,Vector<Comments>>>
			//check if this is the first comment for the given ExperimentID;
			boolean bContainsExperiment = hmExperimentComments.containsKey(c1.getExperimentID());
			if(!bContainsExperiment){
				//create new hmPhaseComments
				hmPhaseComments = new HashMap<String,Vector<Comment>>();
			}
			else{
				//take existing hmPhaseComments
				hmPhaseComments = hmExperimentComments.get(c1.getExperimentID());
			}
			boolean bFirstCommentForStage = hmPhaseComments.containsKey(c1.getExperimentPhaseID());
			if(bFirstCommentForStage){
				//take existing comment vector
				vExistingComments = hmPhaseComments.get(c1.getExperimentPhaseID());
			}else{
				// create new vExistingComments
				vExistingComments = new Vector<Comment>();
			}
				
			boolean bContains = vExistingComments.contains(c1);
				if(bRegister_Remove){
					//register
					if(bContains)
						vExistingComments.remove(c1);
					vExistingComments.add(c1);
				}
				else{
					//remove
					vExistingComments.remove(c1);
				}
				
			//now put everything together again
			hmPhaseComments.put(c1.getExperimentPhaseID(), vExistingComments);
			this.hmExperimentComments.put(c1.getExperimentID(), hmPhaseComments);
			
		}
		else{
			bRet = false;
		}
		
		return bRet;
	}

}
