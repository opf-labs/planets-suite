package eu.planets_project.tb.impl.data.demo.queryable;

import java.util.ArrayList;
import java.util.Date;

import eu.planets_project.tb.gui.backing.QueryResultListEntry;

/**
 * A QuerySourceCategory is a grouping element for query-able data sources,
 * rather than a real QuerySource.
 * This is used to create an organized hierarchy of QuerySources rather than
 * simply a long, flat list.
 * 
 * @author SimonR
 *
 */
public class QuerySourceCategory extends QuerySource {
	
	/**
	 * Query sources in this category
	 */
	private ArrayList<QuerySource> children = new ArrayList<QuerySource>();
	
	/**
	 * Creates a new query source category with the specified name
	 * @param categoryName the name
	 */
	public QuerySourceCategory(String categoryName) {
		super(categoryName);
	}

	/**
	 * Adds a query source to this category
	 * @param querySource the query source
	 */
	public void addChild(QuerySource querySource) {
		children.add(querySource);
	}
	
	/**
	 * Returns the query sources in this category
	 * @return the query sources
	 */
	public ArrayList<QuerySource> getChildren() {
		return children;
	}
	
    public QueryResultListEntry[] query(String query, int limit, int offset) {
		// Need to find a better way of building the tree model...
    	return new QueryResultListEntry[0];
	}
    
    public QueryResultListEntry[] query(Date from, Date until) {
		// Need to find a better way of building the tree model...
    	return new QueryResultListEntry[0];    	
    }
	
}
