/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.File;

import eu.planets_project.services.datatypes.Checksum;

/**
 * @author Peter Melms
 */
public class ZipResult {

    private File zipFile = null;

    private long checksum = 0;

    private Checksum check = null;

    /**
     * @param zipFile The ZIP file
     * @param checksum The checksum
     */
    public ZipResult(File zipFile, long checksum) {
        this.zipFile = zipFile;
        this.checksum = checksum;
        check = new Checksum("Adler32", Long.toString(checksum));
    }

    /**
     * @param zipFile The ZIP file
     * @param checksum The checksum
     */
    public ZipResult(File zipFile, Checksum checksum) {
        this.zipFile = zipFile;
        this.checksum = Long.parseLong(checksum.getValue());
        check = checksum;
    }

    /**
     * Create an empty result.
     */
    public ZipResult() {
        this.zipFile = null;
        this.checksum = 0;
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
     * @return The raw checksum
     */
    public long getChecksumAsLong() {
        return checksum;
    }

    /**
     * @param checksum The checksum to set
     */
    public void setChecksum(long checksum) {
        this.checksum = checksum;
        check = new Checksum("Adler32", Long.toString(checksum));
    }
}
