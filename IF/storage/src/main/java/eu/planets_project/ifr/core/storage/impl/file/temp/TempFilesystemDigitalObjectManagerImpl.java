package eu.planets_project.ifr.core.storage.impl.file.temp;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.UUID;

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
		// Call the super constructor
		super(config);
		
		// But now we need to subvert the root path to make this thread safe
    	try {
        	String path = config.getString(PATH_KEY);
        	path += "/" + UUID.randomUUID().toString();
        	this._root = new File(path);
        	this.checkConstructorArguments(this._root);
    	} catch (NoSuchElementException e) {
    		throw new IllegalArgumentException("Path property with key " + PATH_KEY + " not found in config");
    	}
	}

	/**
	 * Finalize will clear up the directory, this is called at construction and destruction
	 */
	@Override
	public void finalize() {
		System.out.println("FINALIZE called, root:" + this._root.getAbsolutePath());
		if (!deleteDirectory(this._root)) System.out.println("deletion of directory failed");
	}

	private static boolean deleteDirectory(File path) {
		try {
			if (path.exists()) {
				File[] files = path.listFiles();
				for (File file : files) {
					if (file.isDirectory()) {
						if (!deleteDirectory(file))
							System.out.println("Failed to delete directory " + file.getAbsolutePath());
					} else {
						if (!file.delete())
							System.out.println("Failed to delete file " + file.getAbsolutePath());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return path.delete();
	}
}
