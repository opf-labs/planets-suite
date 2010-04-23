package eu.planets_project.tb.impl.properties;

import java.io.Serializable;

import eu.planets_project.tb.api.properties.ManuallyMeasuredProperty;

public class ManuallyMeasuredPropertyImpl implements ManuallyMeasuredProperty, Serializable{

	private String name, description;
	private String pURI;
	private boolean bUserCreated = false;
	
	public ManuallyMeasuredPropertyImpl(String name, String description, String pURI){
		this(name,description,pURI,false);
	}
	
	public ManuallyMeasuredPropertyImpl(String name, String description, String pURI, boolean userCreated){
		setName(name);
		setDescription(description);
		setURI(pURI);
		this.bUserCreated = userCreated;
	}
	
	/** {@inheritDoc} */
	public String getName() {
		if(name!=null)
			return name;
		return "";
	}
	
	/** {@inheritDoc} */
	public String getDescription() {
		if(description!=null)
			return description;
		return "";
	}
	
	/** {@inheritDoc} */
	public String getURI() {
		if(pURI!=null)
			return pURI;
		return "";
	}

	private void setName(String name){
		this.name = name;
	}
	
	private void setDescription(String description){
		this.description = description;
	}
	
	private void setURI(String pURI){
		this.pURI = pURI;
	}

	/** {@inheritDoc} */
	public boolean isUserCreated() {
		return this.bUserCreated;
	}
	
	@Override
	public boolean equals(Object o){
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ManuallyMeasuredPropertyImpl om = (ManuallyMeasuredPropertyImpl) o;
		if((this.getURI().equals(om.getURI()))&&(this.getName().equals(om.getName()))){
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public String toString(){
		return "uri: "+this.getURI()+" name: "+this.getName()+" userCreated: "+this.isUserCreated();
	}

}
