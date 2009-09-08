/**
 * 
 */
package eu.planets_project.services.migration.dia.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Utility class for loading arbitrary resource files at runtime.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class ResourceLocator {

    private final String resourcePath;

    /**
     * Initialise this resource locator with a path/name of the resource to be
     * located. <code>resourcePath</code> must be either an absolute file path
     * on a mounted file system or a relative file path / file name on the
     * classpath.
     * 
     * @param resourcePath
     *            file name, relative or absolute path to the resource to
     *            locate.
     */
    public ResourceLocator(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * Get an inputstream containing the contents of the resource. This method
     * will first attempt finding the resource by using the class loader and if
     * that fails then it will attempt to access the resource as a regular file.
     * In the latter case, the <code>resourcePath</code> given at construction
     * time must either be an absolute file path or a relative file path / file
     * name on the classpath for the operation to succeed.
     * 
     * @return <code>InputStream</code> containing the resource data.
     * @throws FileNotFoundException
     *             if the resource could not be located.
     * @throws IOException
     *             if a input stream could not be initialised with the resource
     *             even though it was found by the class loader.
     */
    public InputStream getResourceStream() throws FileNotFoundException,
            IOException {

        InputStream resourceInputStream;

        final ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        final URL resourceURL = classLoader.getResource(resourcePath);
        if (resourceURL != null) {
            resourceInputStream = resourceURL.openStream();
        } else if (new File(resourcePath).isFile()) {
            resourceInputStream = new FileInputStream(resourcePath);
        } else {
            throw new FileNotFoundException("Could not locate resource: "
                    + resourcePath);
        }

        return resourceInputStream;
    }
}
