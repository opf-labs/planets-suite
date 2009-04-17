package eu.planets_project.tb.impl.data.demo.queryable;

public class QuerySourceManager {
	
	/**
	 * 'Root category' of the QuerySource hierarchy
	 */
	private QuerySourceCategory root;
    
    /**
     * The constructor creates the list of known QuerySources
     */
    public QuerySourceManager() {
    	// Root element
    	root = new QuerySourceCategory("root");
    	
        // Yahoo! Image API
        root.addChild(new YahooImageAPIQuerySource());
        
        // SRU data sources
        QuerySourceCategory sruSources = new QuerySourceCategory("SRU sources");
        sruSources.addChild(new GenericSRUQuerySource());
        root.addChild(sruSources);
        
        // OAI-PMH sources
        QuerySourceCategory pmhSources = new QuerySourceCategory("OAI-PMH sources");
        root.addChild(pmhSources);
    }
    
    public QuerySource getRoot() {
    	return root;
    }

}
