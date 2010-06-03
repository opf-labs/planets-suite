/**
 * 
 */
package eu.planets_project.tb.impl.exceptions;

/**
 * @author alindley
 *
 */
public class ExperimentNotFoundException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1425887614667024503L;

	public ExperimentNotFoundException(){
		
	}
	
	public ExperimentNotFoundException(String msg){
		super(msg);
	}

}
