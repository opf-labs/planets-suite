/**
 * 
 */
package eu.planets_project.ifr.core.storage.api.query;

import java.net.URI;

import javax.xml.xpath.XPath;

/**
 * @author CFWilson
 *
 */
public class QueryXmlMetadata extends Query{
	private URI _metadataId;
	private XPath _xpathQuery;

	public QueryXmlMetadata(URI metadataId, XPath xpathQuery) {
		this._metadataId = metadataId;
		this._xpathQuery = xpathQuery;
	}
	public void setMetadataId(URI metadataId) {
		this._metadataId = metadataId;
	}
	public URI getMetadataId() {
		return _metadataId;
	}
	public void setXpathQuery(XPath xpathQuery) {
		this._xpathQuery = xpathQuery;
	}
	public XPath getXpathQuery() {
		return _xpathQuery;
	}

}
