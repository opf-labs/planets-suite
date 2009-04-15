package eu.planets_project.services.datatypes;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * {@link "http://www.iana.org/assignments/media-types/"}
 * 
 * @author AnJackson
 * 
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Types {

	/** An array of URIs denoting the format types */
	public List<URI> types;
	/** The identification status goes here */
	public String status;

	/**
	 * No-args constructor required by JAXB
	 */
	public Types() { }

    /**
     * 
     * @param uris
     * @param status
     */
    public Types(URI[] uris, String status) {
        types = new ArrayList<URI>( Arrays.asList(uris) );
        this.status = status;
    }
    
    /**
     * 
     * @param uris
     * @param status
     */
    public Types( List<URI> uris, String status) {
        types = new ArrayList<URI>(uris);
        this.status = status;
    }
}
