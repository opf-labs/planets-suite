package eu.planets_project.tb.gui.backing;

import java.io.Serializable;

import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;


public class BenchmarkBean implements Serializable {

	    
    String definition;
    String name;
    String description;
    String id;
    boolean selected = false;
    String value;
    String weight;
		
    
    public BenchmarkBean() {
    	
    }
    
	public BenchmarkBean(BenchmarkGoal bm) {
		this.id = bm.getID();
		this.name = bm.getName();
		this.definition = bm.getDefinition();
		this.description = bm.getDescription();	
		this.value = bm.getValue();
		this.weight = String.valueOf(bm.getWeight());

	} 
		
    public boolean getSelected() {
      return selected;
    }
    public void setSelected(boolean selected) {
      this.selected = selected;
    }
 
    public String getValue() {
    	return value;
    }
    
    public void setValue(String value) {
    	this.value = value;
    }

	public String getWeight() {
		return this.weight;
	}

	public void setWeight(String weight) {
		this.weight=weight;
	}
    
    
	public String getDefinition() {
		return this.definition;
	}

	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}
				
	public String getID() {
		return this.id;
	}

	public void setID(String id) {
		this.id=id;
	}
	
	protected void setName(String name){
		this.name = name;
	}
	
	protected void setDefinition(String definition){
		this.definition = definition;
	}
		
	protected void setDescription(String description){
		this.description = description;
	}


}
