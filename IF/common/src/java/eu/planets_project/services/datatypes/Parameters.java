/**
 * 
 */
package eu.planets_project.services.datatypes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Parameters {

    @XmlElement(name="parameter")
    List<Parameter> parameters = null;

    /**
     * @return the parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * 
     * @param name
     * @param value
     */
    public void add(String name, String value) {
        if( this.parameters == null ) this.parameters = new ArrayList<Parameter>();
        
        Parameter p = new Parameter(name, value);
        this.parameters.add(p);
    }
    
    
}
