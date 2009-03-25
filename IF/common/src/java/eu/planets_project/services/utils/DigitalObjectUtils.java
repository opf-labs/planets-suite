/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class DigitalObjectUtils {

    /**
     * @return The total size, in bytes, of the bytestream contained or referred to by this Digital Object.
     *          Does not include the size of any associated metadata, or the Java objects etc.
     *          Recursive method for computing the total size.
     *  TODO A badly-formed DigitalObject could cause this method to recurse forever. Can that be stopped?
     */
    public static long getContentSize( DigitalObject dob ) {
        long bytes = 0;
        // Get the size at this level, if set:
        byte[] buf = new byte[1024];
        if (dob.getContent() != null) {
            InputStream inputStream = dob.getContent().read();
            int length = 0;
            try {
                while ((inputStream != null)
                        && ((length = inputStream.read(buf)) != -1)) {
                    bytes += length;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Recurse into sub-dobs:
        if( dob.getContained() != null ) {
            for( DigitalObject cdob : dob.getContained() ) {
                bytes += getContentSize( cdob );
            }
        }
        // Return the total:
        return bytes;
    }

    /*
     * These cases:
     * 
     *     - A compound DO, zip as Content, with MD outside the zip, pointing into it via Title.  
     *       This is to pass between services.
     *     - A zip file containing CDO, MD inside the zip, pointing to the binaries via the Title. 
     *       This is an pure file 'IP', in effect.
     *     - A compound DO, pulled from such a CDO zip file, with inputstreams for content.
     *     
     *     Okay, two formats, different contexts and packing/unpacking options.
     *     - (CDO[zip] or CDO) i.e. If no Content, look up to root and unpack? 
     *     - DOIP - a special ZIP file containing CDOs.
     *     
     *     Operations:
     *     - Packing one or more CDOs into a DOIP, optionally embedding referenced resources. (Value) resources always to be embedded.
     *     - Unpacking a DOIP and getting N CDOs out, optionally embedding binaries, using ZipInputStreams, or unpacking into Files?
     *     
     *     FIXME DO must know if it has a parent in order to be able to look things up?
     *     FIXME Should DO use URI internally got Content.reference, to allow relative resolution?
     */
    
    public static void main( String args[] ) {
        try {
            URI uri = new URI("FAQ.html");
            System.out.println("Got "+uri);
            System.out.println("Got "+uri.isAbsolute());
            uri = new URI("http://localhost/FAQ.html");
            System.out.println("Got "+uri);
            System.out.println("Got "+uri.isAbsolute());
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
}
