/**
 * 
 */
package eu.planets_project.tb.impl.exceptions;

/**
 * @author Andrew Lindley, ARC
 *
 */
public class ServiceInvocationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3594319747643806766L;

	public ServiceInvocationException(){
			
	}
		
	public ServiceInvocationException(String msg){
		super(msg);
	}

}
