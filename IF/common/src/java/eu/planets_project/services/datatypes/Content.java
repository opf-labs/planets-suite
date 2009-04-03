package eu.planets_project.services.datatypes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.utils.ByteArrayDataSource;
import eu.planets_project.services.utils.FileUtils;

/**
 * Content for digital objects, either by reference or by value. Create content
 * by reference or value: {@code Content c = Content.reference(url);} or {@code
 * Content c = Content.value(bytes); } However created, you can read the content
 * form the instance: {@code InputStream s = c.read();}
 * @see ContentTests
 * @author Asger Blekinge-Rasmussen (abr@statsbiblioteket.dk)
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 * @author Peter Melms (peter.melms@uni-koeln.de)
 */
public final class Content implements Serializable, DigitalObject.Content {
    private static Log log = LogFactory.getLog(Content.class);

    /***/
    private static final long serialVersionUID = 7135127983024589335L;
    
    /** */
    @XmlAttribute
    private URL reference;
    
    /*
     * FIXME: The data handler class is not serializable. If an API consumer
     * would create content by value and use Java's object serialization, this
     * would not work. Do our classes need to implement Serializable at all?
     */
    @XmlElement
    @XmlMimeType("application/octet-stream")
    /* The DataHandler is not serializable, so we define it transient: */
    private DataHandler dataHandler;

    /** */
    @XmlAttribute
    private long length = -1;
    
    /*
     * We use static factory methods to provide named constructors for the
     * different kinds of content instances:
     */

    /**
     * Create content by reference.
     * @param reference The reference to the actual content value
     * @return A content instance referencing the given location
     */
    public static Content byReference(final URL reference) {
        return new Content(reference);
    }
    
    /**
     * Create content by reference, from a File.
     * 
     * Note that the file must be left in place long enough for the web service client to complete the access.
     * 
     * @param reference The reference to the actual content value, using a File whose content will be streamed over the connection.
     * @return A content instance referencing the given location.
     */
    public static Content byReference( final File reference ) {
        return new Content(reference);
    }

    /**
     * Create content by value.
     * <p/>
     * Note that content created by value cannot be used with Java's object
     * serialization.
     * @param value The value for the content
     * @return A content instance with the specified value
     */
    public static Content byValue(final byte[] value) {
        return new Content(value);
    }

    /**
     * Create content by value.
     * <p/>
     * Note that content created by value cannot be used with Java's object
     * serialization.
     * 
     * @param file The value for the content, a File that should be read into a byte array.
     * @return A content instance with the specified value
     */
    public static Content byValue(final File value) {
        //byte[] bytes = FileUtils.readFileIntoByteArray(value);
        //return new Content( bytes );
        return new Content( value );
    }
    
    /**
     * Create content by value.
     * <p/>
     * Note that content created by value cannot be used with Java's object
     * serialization.
     * 
     * @param inputStream The InputStream containing the value for the content. The InputStream is written to a byte[]
     * @return A content instance with the specified value
     */
    public static Content byValue(final InputStream inputStream) {
    	// create a File from the InputStream and call the Content.byValue(File) 
    	// to avoid having the whole (maybe large) file in memory
    	File tmpFile = FileUtils.writeInputStreamToFile(inputStream, FileUtils.getSystemTempFolder(), "tempContent.dat");
        return new Content( tmpFile );
    }
    

    /**
     * @param value The content value
     */
    private Content(final byte[] value) {
        ByteArrayDataSource bads = new ByteArrayDataSource(value,
                "application/octet-stream");
        DataHandler dh = new DataHandler(bads);
        this.length = value.length;
        this.dataHandler = dh;
        log.info("Created Content from byte array: "+ this.length+ " bytes in length.");
    }

    /**
     * @param value The content value, from a file.
     */
    private Content(final File value) {
       FileDataSource ds = new FileDataSource(value);
       ds.setFileTypeMap(FileTypeMap.getDefaultFileTypeMap());
       DataHandler dh = new DataHandler(ds);
       this.length = value.length();
       this.dataHandler = dh;
       log.info("Created Content from file '"+value.getAbsolutePath()+"': " + this.length + " bytes in length.");
    }

    /**
     * @param reference The content, passed as an explicit reference.
     */
    private Content(final URL reference) {
        this.length = -1;
        this.reference = reference;
        log.info("Created Content from file '"+reference);
    }
    
    /** No-args constructor for JAXB. Clients should not use this. */
    private Content() {}

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject.Content#read()
     */
    public InputStream read() {
        try {
            if (isByValue()) {
                log.info("Opening dataHandler stream of type: "+dataHandler.getContentType());
                log.info("Opening dataHandler stream available: "+dataHandler.getInputStream().available() );
                return dataHandler.getDataSource().getInputStream();
            } else {
                log.info("Opening reference: "+reference);
                return reference.openStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Should be used with care. If used in web service context, large files
     * could cause errors, when sent as byte[] via SOAP. The read() method
     * should be used instead, to stream large files.
     * @return The value of this content.
     * @deprecated Use {@link #read()} instead
     */
    public byte[] getValue() {
        /* Should work for both content by reference and by value: */
        return FileUtils.writeInputStreamToBinary(read());
    }

    /**
     * @return The reference, if any (might be null). Clients should not use
     *         this method to access the actual data, but {@link #read()} or
     *         {@link #getValue()}, which will always return the actual content,
     *         no matter how it was created (by value or by reference).
     */
    public URL getReference() {
        return reference;
    }

    /**
     * @return True, if this Content contains the actual value, or false if it
     *         contains a reference
     */
    public boolean isByValue() {
        return reference == null;
    }
    
    /**
     * @return The size of the Content, in bytes.  Returns -1 if this is a 'by reference' Content object.
     */
    public long length() {
        return length;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Content)) {
            return false;
        }
        Content other = (Content) obj;
        /*
         * Two content object, even if they would be based on the same data, are
         * not equal if they are not both by reference or both by value:
         */
        if (this.isByValue() != other.isByValue()) {
            return false;
        }
        /* Else we compare either value or reference: */
        if (isByValue()) {
            return this.dataHandler.equals(other.dataHandler);
        } else {
            return this.reference.toString().equals(other.reference.toString());
        }
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return isByValue() ? dataHandler.hashCode() : reference.toString()
                .hashCode();
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Content by %s: %s",
                isByValue() ? "value (DataHandler)" : "reference",
                isByValue() ? dataHandler : reference);
    }

}
