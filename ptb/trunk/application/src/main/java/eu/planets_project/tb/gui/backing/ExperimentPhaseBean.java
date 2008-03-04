/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.model.Comment;
import eu.planets_project.tb.api.CommentManager;
import eu.planets_project.tb.impl.CommentManagerImpl;

/**
 * @author AnJackson
 *
 */
public class ExperimentPhaseBean {
    // The logger:
    private Log log = LogFactory.getLog(ExperimentPhaseBean.class);
    // The Experiment concerned:
    private ExperimentBean eb = null;
    // The Experimental Phase:
    private String experimentPhaseID = null;
    // The CommentManager:
    private CommentManager cm = CommentManagerImpl.getInstance();

    // Bean constructor
    public ExperimentPhaseBean() {
    }

    /**
     * Constructs a new ExperimentPhaseBean.
     * @param eb The parent ExperimentBean.
     * @param experimentPhaseID The experiment phase, described as a String.
     */
    public ExperimentPhaseBean(ExperimentBean eb, String experimentPhaseID) {
        this.eb = eb;
        this.experimentPhaseID = experimentPhaseID;
        //log.debug("Initialised an EPB with: " + experimentPhaseID );
    }
    
    /**
     * Having set up the Experiment ID and Stage in the bean, this method will get the comments.
     * @return A List of Comment objects about this stage of the experiment.
     */
    public List<Comment> getComments() {
        log.debug("getting comments on #" + eb.getID() + " at phase " + experimentPhaseID );
        return cm.getComments(eb.getID(), experimentPhaseID);
    }

    /**
     * @return The number of comments.
     */
    public int getNumberOfComments() {
        return getComments().size();
    }

    /**
     * @return True if there are no comments, false otherwise.
     */
    public boolean getHasNoComments() {
        return getComments().isEmpty();
    }

    /**
     * @return True if there are no comments, false otherwise.
     */
    public boolean getHasComments() {
        return !getHasNoComments();
    }

    /**
     * @return the experimentPhaseID
     */
    public String getExperimentPhaseID() {
        return experimentPhaseID;
    }

    /**
     * @param experimentPhaseID the experimentPhaseID to set
     */
    public void setExperimentPhaseID(String experimentPhaseID) {
        this.experimentPhaseID = experimentPhaseID;
    }
}
