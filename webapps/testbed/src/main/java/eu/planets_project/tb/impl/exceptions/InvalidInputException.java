/**
 * 
 */
package eu.planets_project.tb.impl.exceptions;

/**
 * @author alindley
 *
 */
public class InvalidInputException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8600842830927004915L;

	public InvalidInputException(){
		
	}
	
	public InvalidInputException(String msg){
		super(msg);
	}

}
