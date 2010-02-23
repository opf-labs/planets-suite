package eu.planets_project.ifr.core.storage.impl.file.temp;

import java.io.File;
import java.util.NoSuchElementException;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class TempFilesystemDigitalObjectManagerImpl extends
		FilesystemDigitalObjectManagerImpl {

	/**
	 * Constructor
	 * @param config 
	 */
	public TempFilesystemDigitalObjectManagerImpl(Configuration config) {
		super(config);
		this.finalize();
    	try {
        	String path = config.getString(PATH_KEY);
        	this.checkConstructorArguments(new File(path));
        	this._root = new File(path);
    	} catch (NoSuchElementException e) {
    		throw new IllegalArgumentException("Path property with key " + PATH_KEY + " not found in config");
    	}
	}

	/**
	 * Finalize will clear up the directory, this is called at construction and destruction
	 */
	@Override
	protected void finalize() {
		deleteDir(this._root);
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int iLoop = 0; iLoop < children.length; iLoop++) {
				if (!deleteDir(new File(dir, children[iLoop]))) {
					return false;
				}
			}
		}
		return dir.delete();
	}
}
