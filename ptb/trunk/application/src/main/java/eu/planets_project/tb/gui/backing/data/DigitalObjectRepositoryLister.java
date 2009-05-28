/**
 * 
 */
package eu.planets_project.tb.gui.backing.data;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException;
import eu.planets_project.tb.impl.data.DigitalObjectMultiManager;

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
    private static PlanetsLogger log = PlanetsLogger.getLogger(DigitalObjectRepositoryLister.class);
    
    // The data sources are managed here:
    DigitalObjectMultiManager dsm = new DigitalObjectMultiManager();
    
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
        children = dsm.list(location);
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
        if( ! this.dsm.hasDataManager(puri) ) return false;
        // Default to accessible:
        return true;
    }
    
    /**
     * Utility to get hold of the DataManagerLocal for a URI;
     * @param puri
     * @return
     */
    public DigitalObjectMultiManager getDataManager( URI puri ) {
        return dsm;
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
                    URI parentUri = this.getLocation().resolve("..");
                    if( ! dsm.hasDataManager(parentUri )) parentUri = null;
                    DigitalObjectTreeNode treeNode = new DigitalObjectTreeNode( parentUri );
                    treeNode.setLeafname("..");
                    return (E) treeNode;
                } else {
                    return (E) this.createDobFromUri(children.get(index-1));
                }
                
            } else {
                return (E) this.createDobFromUri(children.get(index));
            }
        } else {
            return null;
        }
    }
    
    /** */
    private DigitalObjectTreeNode createDobFromUri( URI item ) {
        // Object or folder? If null, or empty folder, 
        if( dsm.list(item) == null ) {
            // This is a DO:
            try {
                DigitalObjectTreeNode itemNode = new DigitalObjectTreeNode(item, dsm.retrieve(item));
                return itemNode;
                
            } catch (DigitalObjectNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            // This is a location:
            return new DigitalObjectTreeNode(item);
        }
        
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
        log.warn("Called unimplemented method: .iterator().");
        return null;
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
