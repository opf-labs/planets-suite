package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.NoSuchPathException;

/**
 * A Generic wrapper migration path map that can retrieve a
 * <code>MigrationPath</code> instance, given an input and output format ID.
 * 
 * @author Asger Blekinge-Rasmussen
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class MigrationPaths {

    /**
     * <code>PathKey</code> is a utility class used for keys for storage and
     * look-up of migration paths.
     * 
     * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
     */
    private class PathKey {

	private final URI sourceFormat;
	private final URI destinationFormat;

	/**
	 * Create a <code>PathKey</code> for a migration path with the
	 * <code>sourceFormat URI</code> and the
	 * <code>destinationFormat URI</code>.
	 */
	public PathKey(URI sourceFormatURI, URI destinationFormatURI) {
	    this.sourceFormat = sourceFormatURI;
	    this.destinationFormat = destinationFormatURI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + getOuterType().hashCode();
	    result = prime
		    * result
		    + ((destinationFormat == null) ? 0 : destinationFormat
			    .hashCode());
	    result = prime * result
		    + ((sourceFormat == null) ? 0 : sourceFormat.hashCode());
	    return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (!(obj instanceof PathKey))
		return false;
	    PathKey other = (PathKey) obj;
	    if (!getOuterType().equals(other.getOuterType()))
		return false;
	    if (destinationFormat == null) {
		if (other.destinationFormat != null)
		    return false;
	    } else if (!destinationFormat.equals(other.destinationFormat))
		return false;
	    if (sourceFormat == null) {
		if (other.sourceFormat != null)
		    return false;
	    } else if (!sourceFormat.equals(other.sourceFormat))
		return false;
	    return true;
	}

	private MigrationPaths getOuterType() {
	    return MigrationPaths.this;
	}
    }

    private HashMap<PathKey, MigrationPath> migrationPaths;

    public MigrationPaths() {
	migrationPaths = new HashMap<PathKey, MigrationPath>();
    }

    /**
     * Get the <code>MigrationPath</code> instance containing the necessary
     * information for migration from the format identified by the
     * <code>sourceFormat URI</code> to the format identified by the
     * <code>destinationFormat URI</code>.
     * 
     * @param sourceFormat
     *            <code>URI</code> identifying the source format of the path to
     *            get.
     * @param destinationFormat
     *            <code>URI</code> identifying the destination format of the
     *            path to get.
     * @return the migration path found.
     * @throws NoSuchPathException
     *             if no matching path was found.
     */
    public MigrationPath getMigrationPath(URI sourceFormat,
	    URI destinationFormat) throws NoSuchPathException {

	final PathKey pathKey = new PathKey(sourceFormat, destinationFormat);
	final MigrationPath migrationPath = migrationPaths.get(pathKey);
	if (migrationPath == null) {
	    throw new NoSuchPathException(
		    "No migration path found for source format URI=\""
			    + sourceFormat + "\" and destination format URI=\""
			    + destinationFormat + "\"");
	}
	return migrationPath;
    }

    /**
     * Add a <code>MigrationPath</code> instance to this collection of migration
     * paths.
     * 
     * @param migrationPath
     *            the <code>MigrationPath</code> to add.
     * @return the previously stored <code>MigrationPath</code> instance
     *         associated with the migration path specified by
     *         <code>migrationPath</code> or <code>null</code> if there no
     *         instance were associated with the path.
     */
    public MigrationPath addMigrationPath(MigrationPath migrationPath) {

	final PathKey pathKey = new PathKey(migrationPath.getSourceFormat(),
		migrationPath.getDestinationFormat());

	return migrationPaths.put(pathKey, migrationPath);
    }

    /**
     * Add all the the <code>MigrationPath</code> instances from
     * <code>migrationPaths</code> to this container. If any of the paths are
     * already mapped by this container then the previously associated
     * <code>MigrationPath</code> instances will be discarded.
     * 
     * @param migrationPaths
     *            <code>List</code> of <code>MigrationPath</code> instances to
     *            add to this container.
     */
    public void addAll(List<MigrationPath> migrationPaths) {
	for (MigrationPath path : migrationPaths) {
	    addMigrationPath(path);
	}
    }

    /**
     * Get all the migration paths held by this <code>MigrationPaths</code>
     * instance.
     * 
     * @return an unmodifiable <code>Collection</code> containing all the
     *         <code>MigrationPath<code> instances held by this <code>MigrationPaths</code>
     *         instance.
     */
    public Collection<MigrationPath> getAllMigrationPaths() {
	return Collections.unmodifiableCollection(migrationPaths.values());
    }
}
