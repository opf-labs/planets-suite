package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.NoSuchPathException;

/**
 * A Generic wrapper migration path map that can retrieve a
 * <code>MigrationPath</code> instance, given an input and output format ID.
 * 
 * @author Asger Blekinge-Rasmussen
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
class MigrationPaths {

    /**
     * <code>PathKey</code> is a utility class used for keys for storage and
     * look-up of migration paths.
     * 
     * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
     */
    private class PathKey {

	private final URI inputFormat;
	private final URI outputFormat;

	/**
	 * Create a <code>PathKey</code> for a migration path with the
	 * <code>inputFormat URI</code> and the <code>outputFormat URI</code>.
	 */
	PathKey(URI inputFormatURI, URI outputFormatURI) {
	    this.inputFormat = inputFormatURI;
	    this.outputFormat = outputFormatURI;
	}

	/**
	 * Get the input format <code>URI</code> of this <code>PathKey</code>
	 * instance.
	 * 
	 * @return the input format <code>URI<code>.
	 */
	URI getInputFormatURI() {
	    return this.inputFormat;
	}

	/**
	 * Get the output format <code>URI</code> of this <code>PathKey</code>
	 * instance.
	 * 
	 * @return the output format <code>URI<code>.
	 */
	URI getOutputFormatURI() {
	    return this.outputFormat;
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
	    result = prime * result
		    + ((this.outputFormat == null) ? 0 : this.outputFormat.hashCode());
	    result = prime * result
		    + ((this.inputFormat == null) ? 0 : this.inputFormat.hashCode());
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
	    if (this.outputFormat == null) {
		if (other.outputFormat != null)
		    return false;
	    } else if (!this.outputFormat.equals(other.outputFormat))
		return false;
	    if (this.inputFormat == null) {
		if (other.inputFormat != null)
		    return false;
	    } else if (!this.inputFormat.equals(other.inputFormat))
		return false;
	    return true;
	}

	private MigrationPaths getOuterType() {
	    return MigrationPaths.this;
	}
    }

    private HashMap<PathKey, MigrationPath> migrationPaths;

    MigrationPaths() {
	this.migrationPaths = new HashMap<PathKey, MigrationPath>();
    }

    /**
     * Get the <code>MigrationPath</code> instance containing the necessary
     * information for migration from the format identified by the
     * <code>inputFormat URI</code> to the format identified by the
     * <code>outputFormat URI</code>.
     * 
     * @param inputFormat
     *            <code>URI</code> identifying the input format of the path to
     *            get.
     * @param outputFormat
     *            <code>URI</code> identifying the output format of the path to
     *            get.
     * @return the migration path found.
     * @throws NoSuchPathException
     *             if no matching path was found.
     */
    MigrationPath getMigrationPath(URI inputFormat, URI outputFormat)
	    throws NoSuchPathException {

	final PathKey pathKey = new PathKey(inputFormat, outputFormat);
	final MigrationPath migrationPath = this.migrationPaths.get(pathKey);
	if (migrationPath == null) {
	    throw new NoSuchPathException(
		    "No migration path found for input format URI=\""
			    + inputFormat + "\" and output format URI=\""
			    + outputFormat + "\"");
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
    MigrationPath addMigrationPath(MigrationPath migrationPath) {

	final PathKey pathKey = new PathKey(migrationPath.getInputFormat(),
		migrationPath.getOutputFormat());

	return this.migrationPaths.put(pathKey, migrationPath);
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
    void addAll(List<MigrationPath> migrationPaths) {
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
    Collection<MigrationPath> getAllMigrationPaths() {
	return Collections.unmodifiableCollection(this.migrationPaths.values());
    }

    /**
     * Get all the unique input format URIs collected from the
     * <code>MigrationPath</code> instances held by this
     * <code>MigrationPaths</code> instance.
     * 
     * @return a <code>Set</code> containing all the unique input format URIs.
     */
    Set<URI> getInputFormatURIs() {

	final HashSet<URI> inputFormatURIs = new HashSet<URI>();
	for (PathKey pathKey : this.migrationPaths.keySet()) {
	    inputFormatURIs.add(pathKey.getInputFormatURI());
	}

	return inputFormatURIs;
    }

    /**
     * Get all the unique output format URIs collected from the
     * <code>MigrationPath</code> instances held by this
     * <code>MigrationPaths</code> instance.
     * 
     * @return a <code>Set</code> containing all the unique output format URIs.
     */
    Set<URI> getOutputFormatURIs() {

	final HashSet<URI> outputFormatURIs = new HashSet<URI>();
	for (PathKey pathKey : this.migrationPaths.keySet()) {
	    outputFormatURIs.add(pathKey.getOutputFormatURI());
	}

	return outputFormatURIs;
    }
}
