package eu.planets_project.ifr.core.storage.gui;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.ifr.core.storage.impl.data.StorageDigitalObjectReference;


/**
 * This is an object used to present nodes in registries tree.
 */
@XmlRootElement(name = "Property")
@XmlAccessorType(XmlAccessType.FIELD) 
public class RegistryPropertyImpl implements RegistryProperty, Cloneable, Serializable {

    @XmlTransient
    private StorageDigitalObjectReference individual;
    private static final String TYPE_DIGITAL_OBJECT = "Digital Object";
    
    
    /**
     * This constructor must not be used. Just there to allow the PropertyDnDTreeBean to
     * extend this object for property-classes.
     */
    protected RegistryPropertyImpl(){
    	//do not use this
    }
    
    /**
     * The default constructor
     * @param individual
     */
	public RegistryPropertyImpl(StorageDigitalObjectReference individual) { 
		this.individual = individual;
	}
    

    private String uri = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.RegistryProperty#getURI()
     */
    public String getURI() {
    	if(uri==null){
        	if (individual != null) {
        		if (individual.getUri() != null) {
        			uri = individual.getUri().toString();
        		}
        	}
    	}
        return uri;
    }

    public URI getUri() {
    	URI res = null;
    	if (individual != null) {
    		if (individual.getUri() != null) {
    		   res = individual.getUri();
    		}
    	}
        return res;
    }

    private String name = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.RegistryProperty#getName()
     */
    public String getName() {
    	if(name==null){
        	if (individual != null) {
        		if (individual.getLeafname() != null) {
            		name = individual.getLeafname();
        		}
        	}
    	}
    	if (name == null) {
    		name = "";
    	}
        return name;
    }
    
    
    private String hrName = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.RegistryProperty#getHumanReadableName()
     */
    public String getHumanReadableName(){
    	if(hrName==null){
    		if (individual != null) {
    			if (individual.getLeafname() != null) {
    	    		hrName = individual.getLeafname();    				
    			}
    		}
    	}
    	if (hrName == null) {
    		hrName = "";
    	}
    	return hrName;
    }

    private String parentType = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.RegistryProperty#getParentType()
     */
    public String getParentType() {
        return parentType;
    }
    
    
    private String comment = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.RegistryProperty#getComment()
     */
    public String getComment() {
    	if(comment==null){
    		if (individual != null) {
    			if (individual.getLeafname() != null) {
    	    		comment = individual.getLeafname();
    			}
    		}
    	}
        return comment;
    }
    

    private List<String> lis_same_asNames = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.RegistryProperty#getIsSameAs()
     */
    public List<String> getIsSameAsNames(){
        return lis_same_asNames;
    }
    
    
    private String unit = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.RegistryProperty#getUnit()
     */
    public String getUnit(){
        if(unit==null){
	        unit="";
        }
        return unit;
    }
    
    private String dataType = null;
    /* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ontology.RegistryProperty#getDataType()
	 */
	public String getDataType() {
		if(dataType==null){
	        dataType="";
		}
        return dataType;
	}
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[uri:"+this.getURI()+", name:"+this.getName()+", human readable name:"+
        this.getHumanReadableName()+", unit:"+this.getUnit()+", comment:"+this.getComment()+
        ", type:"+this.getDataType()+"]";
    }
    

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.RegistryProperty#isUnitDefined()
     */
    public boolean isUnitDefined() {
        if( this.getUnit() == null ) return false;
        if( "".equals(this.getUnit())) return false;
        return true;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.RegistryProperty#isDataTypeDefined()
     */
    public boolean isDataTypeDefined() {
        if( this.getDataType() == null ) return false;
        if( "".equals(this.getDataType())) return false;
        return true;
    }

	public String getType() {
		return this.TYPE_DIGITAL_OBJECT;
	}
	
}
