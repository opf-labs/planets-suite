/**
 * 
 */
package eu.planets_project.services.utils;

import java.io.File;

/**
 * @author melmsp
 *
 */
public class ZipResult {
	
	private File zipFile = null;
	
	private long checksum = 0;
	
	public ZipResult(File zipFile, long checksum) {
		this.zipFile = zipFile;
		this.checksum = checksum;
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

	public long getChecksum() {
		return checksum;
	}

	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}
}
