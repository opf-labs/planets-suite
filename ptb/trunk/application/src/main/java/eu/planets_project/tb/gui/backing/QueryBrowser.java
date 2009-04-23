package eu.planets_project.tb.gui.backing;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.data.demo.queryable.QuerySourceManager;
import eu.planets_project.tb.impl.data.demo.queryable.QuerySource;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;

/**
 * This class is the backing bean that provides the interface to 
 * the query browser - a GUI component for accessing query-able
 * data sources from the testbed. 
 * 
 * @author Rainer Simon
 * 
 */
public class QueryBrowser {
	
    // A logger for this:
    private static PlanetsLogger log = PlanetsLogger.getLogger(QueryBrowser.class, "testbed-log4j.xml");
    
    // Currently selected query API
    private static final String NO_API_SELECTED = "[please select a query source first]";
    private QuerySource currentAPI = null;
    
    // Query string
    private String query;
    
    // Query offset
    private int queryOffset = 0;
    
    // Start date for time-based queries
    private Date from = new Date();
    
    // End date for time-based queries
    private Date until = new Date();
    
    // Maximum results displayed on a single page
    private static final int MAX_RESULTS = 20;
    
    // The currently viewed tree nodes
    private QueryResultListEntry[] currentResults = null;
    
    // The File tree model:
    TreeModel tm;

    /**
     * Constructor to set up the initial tree model.
     */
    public QueryBrowser() {
        // Build the tree.
    	QuerySourceManager qm = new QuerySourceManager();

        // Create the tree:
        tm = new TreeModelBase(new QuerySourceTreeNode(qm.getRoot()));
    }
    
    public void setDir(QuerySourceTreeNode node) {
    	if (node.getType().equals(QuerySourceTreeNode.TYPE_QUERYSOURCE)) {
    		this.currentAPI = node.getQuerySource();
    	} else {
    		this.currentAPI = null;
    	}
    	this.queryOffset = 0;
		this.currentResults = null;
    }
    
    public QueryResultListEntry[] getList() {
        return this.currentResults;
    }
    
    public TreeModel getFilerTree() {
        return tm;
    }
    
    public String getSelectedAPI() {
    	if (currentAPI == null)
    		return NO_API_SELECTED;
    	
    	return currentAPI.getName();
    }
    
    public boolean getNoAPISelected() {
    	return currentAPI == null;
    }
    
    public boolean getResultListEmpty() {
    	return currentResults == null;
    }
    
    public String startQuery() {
    	if (currentAPI != null) {
    		if (currentAPI.useOAIQueryMode()) {
    			currentResults = this.currentAPI.query(from, until);
    		} else {
	    		currentResults = this.currentAPI.query(this.query, MAX_RESULTS, queryOffset);
    		}
    		if (currentResults.length == 0)
    			currentResults = null;
    	}
    	return "success";
    }
    
    public String getQuery() {
    	return this.query;
    }
    
    public void setQuery(String query) {
    	this.query = query;
    }
    
    public String selectAll() {
    	if (currentResults != null) {
	        for (QueryResultListEntry le : currentResults) {
	        	le.setSelected(true);
	        }
    	}
        return "success";
    }

    public String selectNone() {
    	if (currentResults != null) {
	        for (QueryResultListEntry le : currentResults) {
	            le.setSelected(false);
	        }
    	}
        return "success";
    }
    
    public String addToExperiment() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        if( expBean == null ) return "failure";
        
        // Add each of the selected items to the experiment:
        for (QueryResultListEntry res : getList()) {
        	if (res.isSelected()) {
                try {
                	DataHandler dh = new DataHandlerImpl();
                	String ref = dh.addByURI(new URI(res.getUrl()));
                    // DataHandler dh = new DataHandlerImpl();
                	// String ref = dh.addFromDataRegistry(fb.dr , dob.getUri());
                	//add reference to the new experiment's backing bean
                	expBean.addExperimentInputData(ref);
                } catch (Exception e) {
                  log.error("Failed to add to experiment: " + res.getName());
                  log.error(e.getClass() + ": " + e.getMessage());
                }	
        	}
        }
        
        // Clear any selection:
        selectNone();
        
        // Return: gotoStage2 in the browse new experiment wizard
        return "goToStage2";
    }
    
    public boolean queryHasNext() {
    	return (currentAPI != null) && (currentResults != null) && (currentResults.length == MAX_RESULTS);
    }
    
    public boolean queryHasPrevious() {
    	return (currentAPI != null) && (queryOffset > 0);
    }
    
    public String next() {
    	if (queryHasNext()) {
	    	queryOffset += 20;
	    	if (queryOffset > 1000)
	    		queryOffset = 1000;
	    	return startQuery();
    	}
    	return "success";
    }
    
    public String getNextCSS() {
    	if (!queryHasNext())
    		return "color:#dddddd;";
    	
    	return "";
    }
    
    public String previous() {
    	if (queryHasPrevious()) {
	    	queryOffset -= 20;
	    	if (queryOffset < 0)
	    		queryOffset = 0;
	    	return startQuery();
    	}
    	return "success";
    }
    
    public String getPreviousCSS() {
    	if (!queryHasPrevious())
    		return "color:#dddddd;";
    	
    	return "";
    }
    
    public boolean getUseOAIQueryGUI() {
    	if (currentAPI == null)
    		return false;
    	
    	return currentAPI.useOAIQueryMode();
    }
    
    public Date getFromDate() {
    	return from;
    }
    
    public void setFromDate(Date date) {
    	this.from = date;
    }
    
    public Date getUntilDate() {
    	return until;
    }
    
    public void setUntilDate(Date date) {
    	this.until = date;
    }
    
}
