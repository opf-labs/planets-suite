package eu.planets_project.ifr.core.storage.impl.util;

/**
 * Helper class that contains JCR type and name constants
 * 
 * @author CFwilson
 *
 */
public final class JCRConstants {
	/**
	 * Name of the nt:file child node that holds content 
	 */
	public static final String JCR_NODE_NAME_CONTENT = "jcr:content";
	/**
	 * Type of the JCR file node nt:file
	 */
	public static final String JCR_NODE_TYPE_FILE = "nt:file";
	/**
	 * Type of the JCR file node nt:folder
	 */
	public static final String JCR_NODE_TYPE_FOLDER = "nt:folder";
	/**
	 * Type of the JCR file node nt:resource
	 */
	public static final String JCR_NODE_TYPE_RESOURCE = "nt:resource";
	/**
	 * Default value for the jcr:encoding property
	 */
	public static final String JCR_PROPERTY_DEFAULT_ENCODING = "UTF-8";
	/**
	 * Default value for the jcr:mimetype property
	 */
	public static final String JCR_PROPERTY_DEFAULT_MIMETYPE = "application/octet-stream";
	/**
	 * Name of the jcr:name property
	 */
	public static final String JCR_PROPERTY_NAME_DATA = "jcr:data";
	/**
	 * Name of the jcr:encoding property
	 */
	public static final String JCR_PROPERTY_NAME_ENCODING = "jcr:encoding";
	/**
	 * Name of the jcr:lastModified property
	 */
	public static final String JCR_PROPERTY_NAME_LASTMODIFIED = "jcr:lastModified";
	/**
	 * Name of the jcr:mimeType property
	 */
	public static final String JCR_PROPERTY_NAME_MIMETYPE = "jcr:mimeType";
	/**
	 * Name of the jcr:mimeType property
	 */
	public static final String JCR_PATH_SEPARATOR = "/";
}
