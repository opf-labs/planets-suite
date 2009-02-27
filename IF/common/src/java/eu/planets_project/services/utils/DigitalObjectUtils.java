/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.IOException;

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
        // Get the size at this level:
        if( dob.getContent() != null ) {
            bytes += getSizeOfContent(dob.getContent());
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
     * Attempts to determine the size of a Content object.
     */
    private static long getSizeOfContent( Content con ) {
        if( con == null ) return 0;
        try {
            return con.read().available();   
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
}
