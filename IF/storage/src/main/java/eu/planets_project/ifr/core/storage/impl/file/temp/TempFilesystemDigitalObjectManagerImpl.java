package eu.planets_project.ifr.core.storage.impl.file.temp;

import java.io.File;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.impl.file.FilesystemDigitalObjectManagerImpl;

public class TempFilesystemDigitalObjectManagerImpl extends
		FilesystemDigitalObjectManagerImpl {

	/**
	 * Constructor
	 */
	public TempFilesystemDigitalObjectManagerImpl(Configuration config) {
		super(config);
		this.finalize();
	}

	/**
	 * Finalize will clear up the directory, this is called at construction and destruction
	 */
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
