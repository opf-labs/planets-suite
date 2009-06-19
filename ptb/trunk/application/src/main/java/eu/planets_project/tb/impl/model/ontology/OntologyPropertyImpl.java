package eu.planets_project.tb.impl.model.ontology;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import eu.planets_project.tb.api.model.ontology.OntologyProperty;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;

/**
 * This is the Testbed's representation of an ontology property.
 * A) It contains predefined RDF queries of the ontology for basic attributes
 * as e.g. the OWLIndividuals URI within the ontology, comment, unit, name etc. for
 * direct rendering in a bean.
 * B) contains all other information provided by the OWLIndividual and accessible 
 * by the getRDProperty method of this class.
 * 
 * @author <a href="mailto:Andrew.Lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 02.March.2009
 *
 */
@XmlRootElement(name = "Property")
@XmlAccessorType(XmlAccessType.FIELD) 
public class OntologyPropertyImpl implements OntologyProperty, Cloneable, Serializable {

    @XmlTransient
    private long id;
    private Log log = LogFactory.getLog(OntologyPropertyImpl.class);
    //the owl model element containing the nodes property links
    private OWLIndividual individual;
    //FIXME This information is currently hardcoded and must come from the Testbed ontology extension
    private static final String TYPE_DIGITAL_OBJECT = "Digital Object";
    private static final String TYPE_SERVICE = "Service";
    
    
    /**
     * This constructor must not be used. Just there to allow the PropertyDnDTreeBean to
     * extend this object for property-classes.
     */
    protected OntologyPropertyImpl(){
    	//do not use this
    }
    
    /**
     * The default constructor
     * @param individual
     */
    public OntologyPropertyImpl(OWLIndividual individual) { 
    	this.individual = individual;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getOWLIndividual()
     */
    public OWLIndividual getOWLIndividual(){
    	return this.individual;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getURI()
     */
    public String getURI() {
        return individual.getURI();
    }


    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getName()
     */
    public String getName() {
        return individual.getLocalName();
    }


    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getParentType()
     */
    public String getParentType() {
    	//get a certain DatatypeProperty
    	RDFProperty propertyType = individual.getOWLModel()
    	.getRDFProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    	Object oType = individual.getPropertyValue(propertyType);
    	if(oType instanceof DefaultOWLNamedClass){
        	DefaultOWLNamedClass type = (DefaultOWLNamedClass)oType;
        	//return a name without the namespace prefixes
        	return type.getLocalName();
        }
        return "";
    }
    

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getComment()
     */
    public String getComment() {
    	//get a certain DatatypeProperty
        RDFProperty propertyComment = individual.getOWLModel()
        .getRDFProperty("http://www.w3.org/2000/01/rdf-schema#comment");
        Object oComment = individual.getPropertyValue(propertyComment);
        if(oComment instanceof String){
        	return (String)oComment;
        } 
        //System.out.println("<Prop name: "+property.getBrowserText() +" value: "+individual.getPropertyValue(property));
        return "";
    }
    

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getIsSameAs()
     */
    public List<String> getIsSameAsNames(){
    	List<String> ret = new ArrayList<String>();
    	RDFProperty is_same_as = individual.getOWLModel()
    	.getRDFProperty("http://planetarium.hki.uni-koeln.de/public/XCL/ontology/XCLOntology.owl#is_same_as");
        Object ois_same_as = individual.getPropertyValue(is_same_as);

        if(ois_same_as instanceof DefaultOWLIndividual){
        	DefaultOWLIndividual dIsSameAs = (DefaultOWLIndividual)individual.getPropertyValue(is_same_as);
        	//System.out.println("localname: "+dIsSameAs.getLocalName());
        	//System.out.println("directType: "+dIsSameAs.getDirectType().toString());
        	
        	for(Object x : dIsSameAs.getDirectTypes()){
        		if(x instanceof DefaultOWLNamedClass){
        			DefaultOWLNamedClass same = (DefaultOWLNamedClass)x;
        			//add its human readable name
        			ret.add(same.getLocalName());
        		}
        	}
        }
        return ret;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getIsSameAs()
     */
    public List<OWLNamedClass> getIsSameAs(){
    	List<OWLNamedClass> ret = new ArrayList<OWLNamedClass>();
    	RDFProperty is_same_as = individual.getOWLModel()
    	.getRDFProperty("http://planetarium.hki.uni-koeln.de/public/XCL/ontology/XCLOntology.owl#is_same_as");
        Object ois_same_as = individual.getPropertyValue(is_same_as);

        if(ois_same_as instanceof DefaultOWLIndividual){
        	DefaultOWLIndividual dIsSameAs = (DefaultOWLIndividual)individual.getPropertyValue(is_same_as);
        	//System.out.println("localname: "+dIsSameAs.getLocalName());
        	//System.out.println("directType: "+dIsSameAs.getDirectType().toString());
        	
        	for(Object x : dIsSameAs.getDirectTypes()){
        		if(x instanceof DefaultOWLNamedClass){
        			DefaultOWLNamedClass same = (DefaultOWLNamedClass)x;
        			//add its human readable name
        			ret.add(same);
        		}
        	}
        }
        return ret;
    }


    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getUnit()
     */
    public String getUnit(){
        RDFProperty unit = individual.getOWLModel()
        .getRDFProperty("http://planetarium.hki.uni-koeln.de/public/XCL/ontology/XCLOntology.owl#Unit");
        Object oUnit = individual.getPropertyValue(unit);
        if(oUnit instanceof DefaultOWLIndividual){
        	DefaultOWLIndividual dUnit = (DefaultOWLIndividual)individual.getPropertyValue(unit);
        	return dUnit.getLocalName();
        }
        return "";
    }
    
    /* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getDataType()
	 */
	public String getDataType() {
		RDFProperty datatype = individual.getOWLModel()
		.getRDFProperty("http://planetarium.hki.uni-koeln.de/public/XCL/ontology/XCLOntology.owl#Datatype");
        //System.out.println("<Prop name: "+datatype.getBrowserText() +" value: "+individual.getPropertyValue(datatype)); 
        Object oDatatype = individual.getPropertyValue(datatype);
        if(oDatatype instanceof DefaultOWLIndividual){
        	DefaultOWLIndividual ddatatype = (DefaultOWLIndividual)individual.getPropertyValue(datatype);
        	return ddatatype.getLocalName();
        }
        return "";
	}
    
    
    /**
     * @return the autoextractable
     */
    /*public boolean getAutoExtractable() {
        return autoextractable;
    }*/


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[uri:"+this.getURI()+", name:"+this.getName()+", unit:"+this.getUnit()+", comment:"+this.getComment()+", type:"+this.getDataType()/*+", autoextractable:"+this.autoextractable*/+"]";
    }
    

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#isUnitDefined()
     */
    public boolean isUnitDefined() {
        if( this.getUnit() == null ) return false;
        if( "".equals(this.getUnit())) return false;
        return true;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#isDataTypeDefined()
     */
    public boolean isDataTypeDefined() {
        if( this.getDataType() == null ) return false;
        if( "".equals(this.getDataType())) return false;
        return true;
    }

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getRDFProperty(java.lang.String)
	 */
	public Object getRDFProperty(String rdfString) {
		 RDFProperty rdfprop = individual.getOWLModel().getRDFProperty(rdfString);
		 return rdfprop;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getType()
	 */
	public String getType() {
		//FIXME currently all properties reflect digital object specific behavior -> TB ontology extension
		return this.TYPE_DIGITAL_OBJECT;
	}

}
