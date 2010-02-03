/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.bl.dls.uitls;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class DlsItemList {
	private HashMap<Integer, Integer> itemMap = null;
	
	/**
	 * @param itemList 
	 */
	public DlsItemList(File itemList) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			ItemListHandler handler = new ItemListHandler();
		    SAXParser saxParser = factory.newSAXParser();
		    saxParser.parse(itemList, handler);
		    this.itemMap = handler.getItemMap();
		    System.out.println("We have " + this.itemMap.size() + " items.");
		} catch (Throwable err) {
		    err.printStackTrace ();
		}
	}
	
	/**
	 * @return the Map of content metadata pairs
	 */
	public Map<Integer, Integer> getItemMap() {
		return this.itemMap;
	}
	
	/**
	 * @return the number of items found
	 */
	public int itemCount() {
		return this.itemMap.size();
	}
	
	/**
	 * Just a SAX handler to parse the list of DLS items from it's XML form
	 * 
	 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
	 */
	public class ItemListHandler extends DefaultHandler
	{
		private static final String ITEM_ELE_QNAME = "item";
		private static final String CONTENT_ATT_QNAME = "content";
		private static final String METADATA_ATT_QNAME = "metadata";
		@SuppressWarnings("hiding")
		private HashMap<Integer, Integer> itemMap = new HashMap<Integer, Integer>();
		/**
		 * Constructor for the Handler, just calls super 
		 */
		public ItemListHandler() {
			super();
		}
		
		@Override
		public void startDocument() {
			// Clear out the item list
			this.itemMap.clear();
		}

		@SuppressWarnings("boxing")
		@Override
		public void startElement(String uri, String name, String qName, Attributes atts) {
			// Check that it's an item element
			if (qName.equals(ITEM_ELE_QNAME)) {
				// If it is add the content and metadata pair to the list
				System.out.println("looking for meta " + atts.getValue(METADATA_ATT_QNAME) + " and content " + atts.getValue(CONTENT_ATT_QNAME));
				Integer metaId = Integer.parseInt(atts.getValue(METADATA_ATT_QNAME));
				Integer contId = Integer.parseInt(atts.getValue(CONTENT_ATT_QNAME));
				System.out.println("Adding meta " + metaId + " and content " + contId);
				this.itemMap.put(metaId, contId);
			}
		}
		
		/**
		 * @return a clone of the HashMap of content metadata pairs
		 */
		@SuppressWarnings("unchecked")
		public HashMap<Integer, Integer> getItemMap() {
			return (HashMap<Integer, Integer>) this.itemMap.clone();
		}
	}

}
