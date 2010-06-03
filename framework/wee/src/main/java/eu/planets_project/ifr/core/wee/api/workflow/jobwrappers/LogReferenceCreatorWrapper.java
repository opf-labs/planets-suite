package eu.planets_project.ifr.core.wee.api.workflow.jobwrappers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import eu.planets_project.ifr.core.wee.api.ReportingLog;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplate;
import eu.planets_project.ifr.core.wee.api.workflow.WorkflowTemplateHelper;

public class LogReferenceCreatorWrapper {
	
	
	/**
	 * Creates a wf-log.txt file for the processed template.
	 * @param processingTemplate
	 */
	public static void createLogReferences(WorkflowTemplate processingTemplate){
	
		ReportingLog repLog = processingTemplate.getWorkflowReportingLogger();
	    /* Now write the stuff to disk: */
		repLog.reportAsFile();
	    File logFile = repLog.logAsFile();
	    System.out.println("Wrote logFile to: " + logFile.getAbsolutePath());
	    /* And return a result object: */
	    try {
	    	URI outFolder = new URI("http",WorkflowTemplateHelper.getHostAuthority(),"/wee-gen/id-"+repLog.getResultsId(),null,null);
	    	URL logFileURL = new URL(outFolder+"/wf-log.txt");
	    	URL reportURL = new URL(outFolder+"/wf-report.html");
	    	processingTemplate.getWFResult().setLog(logFileURL);
	    	processingTemplate.getWFResult().setReport(reportURL);
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	    } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
