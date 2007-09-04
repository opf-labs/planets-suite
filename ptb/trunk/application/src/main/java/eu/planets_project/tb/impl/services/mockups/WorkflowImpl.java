/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.api.services.mockups.Service;

/**
 * @author alindley
 *
 */
@Entity
public class WorkflowImpl implements Workflow, java.io.Serializable{
	
	@Id
	@GeneratedValue
	private long id;
	private Vector<Service> vServiceWorkflow;  
	
	public WorkflowImpl() {
		vServiceWorkflow = new Vector<Service>();
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addInputData(java.io.File)
	 */
	public void addInputData(File file) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addInputData(java.util.List)
	 */
	public void addInputData(List<File> files) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addOutputData(java.io.File)
	 */
	public void addOutputData(File file) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addOutputData(java.util.List)
	 */
	public void addOutputData(List<File> files) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addRequiredInputFileType(java.lang.String)
	 */
	public void addRequiredInputFileType(String mimeType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addRequiredOutputFileType(java.lang.String)
	 */
	public void addRequiredOutputFileType(String mimeType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#addService(int, eu.planets_project.tb.api.services.mockups.Service)
	 */
	public void addService(int position, Service service) {
		System.out.println("addService0");
		try{
			this.vServiceWorkflow.get(position);
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("addService1 ");
			this.vServiceWorkflow.add(position,service);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getInputData()
	 */
	public List<File> getInputData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getOutputData()
	 */
	public List<File> getOutputData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getRequiredInputFileTypes()
	 */
	public List<String> getRequiredInputFileTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getRequiredOutputFileTypes()
	 */
	public List<String> getRequiredOutputFileTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getService(int)
	 */
	public Service getService(int position) {
		return this.vServiceWorkflow.get(position);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#getServices()
	 */
	public List<Service> getServices() {
		return this.vServiceWorkflow;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeInputData(java.io.File)
	 */
	public void removeInputData(File file) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeInputData(java.util.List)
	 */
	public void removeInputData(List<File> files) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeRequiredInputFileType(java.lang.String)
	 */
	public void removeRequiredInputFileType(String mimeType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeRequiredOutputFileType(java.lang.String)
	 */
	public void removeRequiredOutputFileType(String mimeType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#removeService(int)
	 */
	public void removeService(int position) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setInputData(java.util.List)
	 */
	public void setInputData(List<File> files) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.mockups.Workflow#setOutputData(java.util.List)
	 */
	public void setOutputData(List<File> files) {
		// TODO Auto-generated method stub

	}

}
