/**
 * 
 */
package eu.planets_project.services.datatypes;

import java.net.URL;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper for the content, can be a URL or a binary, and must be built using the appropriate constructor.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * 
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Content {

    /** @See {@link #getBinary()} */
    private byte[] binary;

    /** @See {@link #getLocation()} */
    private URL location;

    /** No-arg constructor for JAXB. API clients should not use this. */
    public Content() {}

    /**
     * Create a content object from a bytestream.
     * 
     * @param contents The bytestream to store as the content.
     */
    public Content(byte[] contents) {
        binary = contents;
    }

    /**
     * Create a Content object based on a location.
     * 
     * @param location The location of the content, as a URL.
     */
    public Content(URL location) {
        this.location = location;
    }

    /**
     * Clone a Content entity, thus creating a copy and allowing new version to be build from old ones without altering data.
     * 
     * @param content The Content class to clone.
     */
    public Content(Content content) {
        this.location = content.location;
        this.binary = content.binary;
    }

    /**
     * 
     * @return
     */
    public boolean isBinary() {
        if (this.location == null) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 
     * @return
     */
    public boolean usURL() {
        return ! isBinary();
    }

    /**
     * Returns null if isPassByValue is true.
     * 
     * @return
     */
    public URL getLocation() {
        return location;
    }
    
    /**
     * @return the binary
     */
    protected byte[] getBinary() {
        return binary;
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    public InputStream openStream() throws IOException {
        if (isBinary()) {
            return new ByteArrayInputStream(binary);
        } else {
            return location.openStream();
        }
    }

    /**
     * 
     * @return
     */
    public long length() {
        if( isBinary() ) {
            return this.binary.length;
        } else {
            try {
                return this.location.openStream().available();
            } catch (IOException e) {
                return -1;
            }
        }
    }

}
