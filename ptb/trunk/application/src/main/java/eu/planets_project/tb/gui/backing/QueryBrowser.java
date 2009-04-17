package eu.planets_project.tb.gui.backing;

import java.net.URI;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.faces.context.FacesContext;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;

import com.hp.hpl.jena.shared.QueryStageException;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.services.datatypes.DigitalObject;
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
    
    // The Data model
    private QuerySourceManager qsm = new QuerySourceManager();

    // The current URI/position
    private URI location = null;
    
    // Currently selected query API
    private static final String NO_API_SELECTED = "[please select a query source first]";
    private QuerySource currentAPI = null;
    
    // Query string
    private String query;
    
    // Query offset
    private int queryOffset = 0;
    
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
    		this.currentResults = null;
    	}
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
    		currentResults = this.currentAPI.query(this.query, MAX_RESULTS, queryOffset);
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
    
    public String ingest() {
    	if (currentResults != null) {
	    	ArrayList<QueryResultListEntry> rList = new ArrayList<QueryResultListEntry>();
	    	for (QueryResultListEntry r : currentResults) {
	    		if (r.isSelected())
	    			rList.add(r);
	    	}
	    	
	    	int success = currentAPI.ingest(rList.toArray(new QueryResultListEntry[rList.size()]));
	    	log.info("Successfully ingested " + success + " of " + rList.size() + " items");
    	}
    	return "success";
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
    
}
