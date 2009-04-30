package eu.planets_project.tb.impl.data.demo.queryable;

@Deprecated
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
        pmhSources.addChild(new GenericOAIQuerySource("http://eprints.ucm.es/cgi/oai2", "E-Prints Complutense"));
        pmhSources.addChild(new GenericOAIQuerySource("http://www.diva-portal.org/oai/OAI", "Academic Archive On-line"));
        pmhSources.addChild(new GenericOAIQuerySource("http://academiccommons.columbia.edu:8080/ac-oai/request", "Academic Commons"));
        pmhSources.addChild(new GenericOAIQuerySource("http://www.alexandria.unisg.ch/EXPORT/OAI/server.oai", "University of St.Gallen"));
        pmhSources.addChild(new GenericOAIQuerySource("http://www.asdlib.org/oai/oai.php", "Analytical Sciences Digital Library")); 
        pmhSources.addChild(new GenericOAIQuerySource("http://www.socpvs.org/journals/index.php/wbp/oai", "Wildlife Biology Practice"));
        pmhSources.addChild(new GenericOAIQuerySource("http://www.jiia.it/Library/oai/", "Journal of Intercultural and Interdisciplinary Archaeology")); 
        root.addChild(pmhSources);
    }
    
    public QuerySource getRoot() {
    	return root;
    }

}
