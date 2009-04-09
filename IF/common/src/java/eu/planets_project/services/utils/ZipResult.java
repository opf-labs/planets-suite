/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.File;

import eu.planets_project.services.datatypes.Checksum;

/**
 * @author melmsp
 *
 */
public class ZipResult {
	
	private File zipFile = null;
	
	private long checksum = 0;
	
	private Checksum check = null;
	
	public ZipResult(File zipFile, long checksum) {
		this.zipFile = zipFile;
		this.checksum = checksum;
		check = new Checksum("Adler32", Long.toString(checksum));
	}
	
	public ZipResult(File zipFile, Checksum checksum) {
		this.zipFile = zipFile;
		this.checksum = Long.parseLong(checksum.getValue());
		check = checksum;
	}
	
	public ZipResult() {
		this.zipFile = null;
		this.checksum = 0;
	}

	public File getZipFile() {
		return zipFile;
	}

	public void setZipFile(File zipFile) {
		this.zipFile = zipFile;
	}

	public Checksum getChecksum() {
		return check;
	}
	
	public long getChecksumAsLong() {
		return checksum;
	}

	public void setChecksum(long checksum) {
		this.checksum = checksum; 
		check = new Checksum("Adler32", Long.toString(checksum));
	}
}
