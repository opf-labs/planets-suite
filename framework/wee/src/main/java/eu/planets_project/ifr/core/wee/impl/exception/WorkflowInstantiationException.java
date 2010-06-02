/**
 * 
 */
package eu.planets_project.ifr.core.wee.impl.exception;

/**
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 30.10.2008
 *
 */
public class WorkflowInstantiationException extends Exception{

	private errCodes err; // a unique id
	enum errCodes{
		invalidXMLTemplate(1,"The provided XML Template is invalid against the schema"),
		missingElements(2,"The provided XML Template does not contain all data for initializing the workflow");
		
		private int errID;
		private String errMsg;
	
		errCodes(int id, String errMessage){
			this.errID = id;
			this.errMsg = errMessage;
		}
	
		public String errMessage(){
			return this.errMsg;
		}
		
		public int errID(){
			return this.errID;
		}
	}

	public WorkflowInstantiationException(errCodes err){
		this.err = err;
	}  
	
	public errCodes getError(){
		return this.err;
	}

}
