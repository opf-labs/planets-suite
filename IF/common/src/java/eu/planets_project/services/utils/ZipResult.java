/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.File;
import java.util.Arrays;

import eu.planets_project.services.datatypes.Checksum;

/**
 * @author Peter Melms
 */
public class ZipResult {

    private File zipFile = null;

    private Checksum check = null;
    

    /**
     * @param zipFile The ZIP file
     * @param checksum The checksum
     */
    public ZipResult(File zipFile, Checksum checksum) {
        this.zipFile = zipFile;
        check = checksum;
    }

    /**
     * Create an empty result.
     */
    public ZipResult() {
        this.zipFile = null;
    }

    /**
     * @return The actual file
     */
    public File getZipFile() {
        return zipFile;
    }

    /**
     * @param zipFile The file to set
     */
    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    /**
     * @return The Checksum
     */
    public Checksum getChecksum() {
        return check;
    }

    
    /**
     * @param checksum The Planets Checksum to set
     */
    public void setChecksum(Checksum checksum) {
        this.check = checksum;
    }
    
    public void setChecksum(String algorithm, byte[] digest) {
    	check = new Checksum(algorithm, Arrays.toString(digest));
    }
}
