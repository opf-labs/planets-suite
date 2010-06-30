/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.tb.gui.backing.data;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.ifr.core.storage.api.DataRegistry.DigitalObjectManagerNotFoundException;


/**
 * 
 * This class provides the access point for mapping between the DigitalObjectManager interface and the TB, by
 * creating the DigitalObjectReference beans that are used in the TB interface to explore the DOMs.
 * 
 * The DigitalObjectMultiManager does the actual work.
 * 
 * @author AnJackson
 *
 */
public class DigitalObjectRepositoryLister<E> implements List<E> {
    private static Log log = LogFactory.getLog(DigitalObjectRepositoryLister.class);
    
    // The data sources are managed here:
    DataRegistry dataReg = DataRegistryFactory.getDataRegistry();
    
    /**
     * @return
     */
    public DigitalObjectTreeNode getRootDigitalObject() {
        return new DigitalObjectTreeNode();
    }
    
    // The current location to list:
    private URI location;

    // The children of the current location:
    private List<URI> children;
    
    // The parent Browser, if known:
    protected DigitalObjectBrowser digitalObjectBrowser;
    
    /**
     * @param digitalObjectBrowser
     */
    public DigitalObjectRepositoryLister( DigitalObjectBrowser digitalObjectBrowser) {
        super();
        this.digitalObjectBrowser = digitalObjectBrowser;
        this.setLocation(null);
    }

    /** */
    public void setLocation( URI location ) {
        this.location = location;
        if( this.location != null ) {
            this.location = this.location.normalize();
        }
    	log.info("setLocation() Calling Data Registry List for " + location);
        this.children = this.dataReg.list(location);
    }
    
    /** */
    public URI getLocation() {
        return this.location;
    }
    
    /**
     * Can the current user access this resource?
     * @param puri
     * @return
     */
    public boolean canAccessURI( URI puri ) {
        // Do not allow paths above the root to be accessed:
        if( ! this.dataReg.hasDigitalObjectManager(puri) ) return false;
        // Default to accessible:
        return true;
    }
    
    /**
     * Utility to get hold of the DataManagerLocal for a URI;
     * @param puri
     * @return
     */
    public DataRegistry getDataRegistry( URI puri ) {
        return this.dataReg;
    }
    
    /* (non-Javadoc)
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean add(E o) {
        log.warn("Called unimplemented method: .add().");
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int, java.lang.Object)
     */
    public void add(int index, E element) {
        log.warn("Called unimplemented method: .add(,).");
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends E> c) {
        log.warn("Called unimplemented method: .add(C).");
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        log.warn("Called unimplemented method: .add(i,C).");
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#clear()
     */
    public void clear() {
        log.warn("Called implemented method: .clear().");
        if( this.children != null ) this.children.clear();
    }

    /* (non-Javadoc)
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        log.warn("Called unimplemented method: .contains().");
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        log.warn("Called unimplemented method: .containsAll().");
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if( this.children != null ) {
            // Patch in '..':
            if( this.getLocation() != null ) {
                if( index == 0 ) {
                	log.info("Location is " + this.location + ", index is zero");
                    URI parentUri = this.getLocation().resolve("..");
                    log.info("Resolved parent URI is " + parentUri);
                    if( ! this.dataReg.hasDigitalObjectManager(parentUri )) {
                    	log.info("Setting parentUri to null");
                    	parentUri = null;
                    }
                    DigitalObjectTreeNode treeNode = new DigitalObjectTreeNode( parentUri );
                    treeNode.setLeafname("..");
                    return (E) treeNode;
                }
				log.info("Location is " + this.location + ", index is " + index);
				log.info("Calling createDobFromUri for URI " + this.children.get(index - 1));
				return (E) this.createDobFromUri(this.children.get(index-1));
            }
			log.info("Location is null, index is " + index);
			log.info("Calling createDobFromUri for URI " + this.children.get(index));
			return (E) this.createDobFromUri(this.children.get(index));
        }
		return null;
    }
    
    /** */
    private DigitalObjectTreeNode createDobFromUri( URI item ) {
        // Object or folder? If null, or empty folder, 
    	log.info("CreateDobFromURI() Calling Data Registry List for " + item);
        if( dataReg.list(item) == null ) {
            // This is a DO:
            DigitalObjectTreeNode itemNode = new DigitalObjectTreeNode(item, dataReg );
            return itemNode;
        }
		// This is a location:
		DigitalObjectTreeNode itemNode = new DigitalObjectTreeNode(item);
		try {
			itemNode.setDescription( dataReg.getDescription(item));
		} catch (DigitalObjectManagerNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return itemNode;
        
    }

    /* (non-Javadoc)
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object o) {
        log.warn("Called unimplemented method: .indexOf().");
        return 0;
    }

    /* (non-Javadoc)
     * @see java.util.List#isDigitalObjectTreeNodempty()
     */
    public boolean isEmpty() {
        log.warn("Called unimplemented method: .isEmpty().");
        if( this.children != null ) return this.children.isEmpty();
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.List#iterator()
     */
    public Iterator<E> iterator() {
        return new SimpleIterator<E>( this );
    }
    
    private class SimpleIterator<F> implements Iterator<E> {
        List<E> list = null;
        int i = 0;
        
        public SimpleIterator( List<E> list ) {
            this.list = list;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            if( i < list.size() ) return true;
            return false;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public E next() {
            i++;
            return list.get(i-1);
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            log.warn("Called unimplemented method: .iterator().remove().");
        }
        
    }

    /* (non-Javadoc)
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object o) {
        log.warn("Called unimplemented method: .lastIndexOf().");
        return 0;
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator()
     */
    public ListIterator<E> listIterator() {
        log.warn("Called unimplemented method: .listIterator().");
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator(int)
     */
    public ListIterator<E> listIterator(int index) {
        log.warn("Called unimplemented method: .listIterator().");
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        log.warn("Called unimplemented method: .remove().");
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public E remove(int index) {
        log.warn("Called unimplemented method: .remove(i).");
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.List#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        log.warn("Called unimplemented method: .removeAll().");
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        log.warn("Called unimplemented method: .retainAll().");
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int, java.lang.Object)
     */
    public E set(int index, E element) {
        log.warn("Called unimplemented method: .set(i,o).");
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size() {
        if( this.children != null ) {
            if( this.getLocation() != null ) {
                return this.children.size() + 1;
            } else {
                return this.children.size();
            }
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see java.util.List#subList(int, int)
     */
    public List<E> subList(int fromIndex, int toIndex) {
        log.warn("Called unimplemented method: .subList(,).");
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray()
     */
    public Object[] toArray() {
        log.warn("Called unimplemented method: .toArray().");
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray(T[])
     */
    public <T> T[] toArray(T[] a) {
        log.warn("Called unimplemented method: .add(T).");
        return null;
    }
    
}
