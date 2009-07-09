package eu.planets_project.tb.impl.model.ontology;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import eu.planets_project.tb.api.model.ontology.OntologyProperty;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;

/**
 * This is the Testbed's representation of an ontology property modeled in the specificationPropertyNames XCLOntology class.
 * A) It contains predefined (RDF) queries of the ontology for basic attributes
 * as e.g. the OWLIndividuals URI within the ontology, comment, unit, name etc. for
 * direct rendering in a bean.
 * B) contains all other information provided by the OWLIndividual and accessible 
 * by the getOWLObjectProperty method, also applying the PelletJena reasoner.
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
    //the ProtegeReasoner that's connected to the given owl model
    private ProtegeReasoner reasoner = null;
    
    
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
    

    /**
     * Returns the ProtegePelletJenaReasoner that's connected to the given owl model
     * @return
     */
    private ProtegeReasoner getReasoner(){
    	if(reasoner==null){
    		ReasonerManager reasonerManager = ReasonerManager.getInstance();
    		reasoner = reasonerManager.getProtegeReasoner(this.individual.getOWLModel());
    	}
    	return reasoner;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getOWLIndividual()
     */
    public OWLIndividual getOWLIndividual(){
    	return this.individual;
    }

    private String uri = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getURI()
     */
    public String getURI() {
    	if(uri==null){
    		uri = individual.getURI();
    	}
        return uri;
    }

    private String name = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getName()
     */
    public String getName() {
    	if(name==null){
    		name = individual.getLocalName();
    	}
        return name;
    }
    
    
    private String hrName = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getHumanReadableName()
     */
    public String getHumanReadableName(){
    	if(hrName==null){
    		//logic to get hrName: if exactly one is_same_as relation - use this as
    		//human readable name - else use the individual's name
    		if(this.getIsSameAsNames().size()==1){
    			//there's a human readable name
    			hrName = this.getIsSameAsNames().iterator().next();
    		}
    		else{
    			//there may be non or 2..n - in this case use the individual's own name
    			hrName = this.getName();
    		}
    	}
    	return hrName;
    }

    private String parentType = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getParentType()
     */
    public String getParentType() {
    	if(parentType==null){
	    	//get a certain DatatypeProperty
	    	RDFProperty propertyType = individual.getOWLModel()
	    	.getRDFProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	    	Object oType = individual.getPropertyValue(propertyType);
	    	if(oType instanceof DefaultOWLNamedClass){
	        	DefaultOWLNamedClass type = (DefaultOWLNamedClass)oType;
	        	//return a name without the namespace prefixes
	        	parentType = type.getLocalName();
	        }else{
	        	parentType="";
	        }
    	}
        return parentType;
    }
    
    
    private String comment = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getComment()
     */
    public String getComment() {
    	if(comment==null){
	    	//get a certain DatatypeProperty
	        RDFProperty propertyComment = individual.getOWLModel()
	        .getRDFProperty("http://www.w3.org/2000/01/rdf-schema#comment");
	        Object oComment = individual.getPropertyValue(propertyComment);
	        if(oComment instanceof String){
	        	comment = (String)oComment;
	        } else{
	        	comment = "";
	        }
    	}
        //System.out.println("<Prop name: "+property.getBrowserText() +" value: "+individual.getPropertyValue(property));
        return comment;
    }
    

    private List<String> lis_same_asNames = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getIsSameAs()
     */
    public List<String> getIsSameAsNames(){
    	if(lis_same_asNames==null){
    		lis_same_asNames = new ArrayList<String>();
	    	Iterator<OWLIndividual> it = this.getIsSameAs().iterator();
	    	while(it.hasNext()){
				OWLIndividual sameAsIndividual = it.next();
				lis_same_asNames.add(sameAsIndividual.getLocalName());
			}
    	}
        return lis_same_asNames;
    }
    
    
    private List<OWLIndividual> lis_same_asIndividuals= null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getIsSameAs()
     */
    public List<OWLIndividual> getIsSameAs(){
    	if(lis_same_asIndividuals==null){
    		lis_same_asIndividuals = new ArrayList<OWLIndividual>();
	    	
			OWLObjectProperty propertyIs_same_as = individual.getOWLModel().getOWLObjectProperty("http://planetarium.hki.uni-koeln.de/public/XCL/ontology/XCLOntology.owl#is_same_as");
			try {
				//using the reasoner for resolving the symmetric individual relations
				Iterator<OWLIndividual> it = this.getReasoner().getRelatedIndividuals(individual, propertyIs_same_as).iterator();
				while(it.hasNext()){
					OWLIndividual sameAsIndividual = it.next();
					lis_same_asIndividuals.add(sameAsIndividual);
				}
			} catch (ProtegeReasonerException e) {
				// TODO Auto-generated catch block
				log.debug("Problems resolving is_same_as relationship with the pellet reasoner",e);
			}
    	}
    	return lis_same_asIndividuals;
    }

    
    private String unit = null;
    /* (non-Javadoc)
     * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getUnit()
     */
    public String getUnit(){
        if(unit==null){
	    	RDFProperty propertyUnit = individual.getOWLModel()
	        .getRDFProperty("http://planetarium.hki.uni-koeln.de/public/XCL/ontology/XCLOntology.owl#Unit");
	        Object oUnit = individual.getPropertyValue(propertyUnit);
	        if(oUnit instanceof DefaultOWLIndividual){
	        	DefaultOWLIndividual dUnit = (DefaultOWLIndividual)individual.getPropertyValue(propertyUnit);
	        	unit= dUnit.getLocalName();
	        }
	        else{
	        	unit="";
	        }
        }
        return unit;
    }
    
    private String dataType = null;
    /* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ontology.OntologyProperty#getDataType()
	 */
	public String getDataType() {
		if(dataType==null){
			RDFProperty propertyDatatype = individual.getOWLModel()
			.getRDFProperty("http://planetarium.hki.uni-koeln.de/public/XCL/ontology/XCLOntology.owl#Datatype");
	        //System.out.println("<Prop name: "+datatype.getBrowserText() +" value: "+individual.getPropertyValue(datatype)); 
	        Object oDatatype = individual.getPropertyValue(propertyDatatype);
	        if(oDatatype instanceof DefaultOWLIndividual){
	        	DefaultOWLIndividual ddatatype = (DefaultOWLIndividual)individual.getPropertyValue(propertyDatatype);
	        	dataType = ddatatype.getLocalName();
	        }else{
	        	dataType="";
	        }
		}
        return dataType;
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
        return "[uri:"+this.getURI()+", name:"+this.getName()+", human readable name:"+this.getHumanReadableName()+", unit:"+this.getUnit()+", comment:"+this.getComment()+", type:"+this.getDataType()/*+", autoextractable:"+this.autoextractable*/+"]";
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
