package eu.planets_project.ifr.core.wdt.gui.faces;

public class HelloWorldBacking {

    
    private String name;
    
    public HelloWorldBacking(){   
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Method that is backed to a submit button of a form.
     */
    public String send(){
        //do real logic
        return ("success");
    }
}