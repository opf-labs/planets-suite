package eu.planets_project.tb.gui.backing.admin;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import eu.planets_project.ifr.core.wee.api.wsinterface.WeeService;
import eu.planets_project.ifr.core.wee.api.wsinterface.WftRegistryService;
import eu.planets_project.services.PlanetsException;
import eu.planets_project.tb.impl.services.util.wee.WeeRemoteUtil;

public class ManageWEE {
	
	private UploadedFile myUploadedFile;
	private WeeRemoteUtil weeUtil;
	private WftRegistryService weeRegistry;
	private WeeService weeService;
	private ArrayList<String> errorMessageString;
	
	public ManageWEE(){
		weeUtil = WeeRemoteUtil.getInstance();
		weeRegistry = weeUtil.getWeeRegistryService();
		weeService = weeUtil.getWeeService();	
		errorMessageString = new ArrayList<String>();
	}
	
	
	public String getNrOfTemplates(){
		return weeRegistry.getAllSupportedQNames().size()+"";
	}
	
	public void setNrOfTemplates(String s){
		//
	}
	
	public ArrayList<String> getTemplates() {
		ArrayList<String> templates = weeRegistry.getAllSupportedQNames();
		if(templates==null){
			return new ArrayList<String>();
		}
		return templates;
	}
	
	public void setMyUploadedFile(UploadedFile myUploadedFile) {
		this.myUploadedFile = myUploadedFile;
	}
	
	public UploadedFile getMyUploadedFile() {
		return myUploadedFile;
	}
	
	public ArrayList<String> getErrorMessageString() {
		return errorMessageString;
	}
	
	public String processTemplate() {
		errorMessageString.clear();
		InputStream in;
		byte[] classBytes;
		String templateName = myUploadedFile.getName();
		String className = "";
		int i = templateName.indexOf(".java");
		if (i > -1) {
			className = templateName.substring(0, i);
		} else {
			errorMessageString.add("Templates must be Java source files!");
			return null;
		}
		try {
			in = new BufferedInputStream(myUploadedFile.getInputStream());
		} catch (Exception x) {
			errorMessageString
					.add("Error retrieving template file input stream!");
			return null;
		}
		try {
			classBytes = inputStreamToBytes(in);
		} catch (IOException e1) {
			errorMessageString
					.add("Unable to extract template byte array from input stream!");
			return null;
		}
		String packageName = extractPackage(classBytes);
		String qName = packageName + "." + className;
		if (this.weeRegistry.getAllSupportedQNames().contains(qName)) {
			errorMessageString.add("Template " + qName
					+ " is already registered!");
			return null;
		}
		try {
			this.weeRegistry.registerWorkflowTemplate(qName, classBytes);
		} catch (PlanetsException e) {
			e.printStackTrace();
			errorMessageString.add("Unable to register template!");
			return null;
		}
		return "OK";
	}
	
	private byte[] inputStreamToBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		in.close();
		out.close();
		return out.toByteArray();
	}
	
	private String extractPackage(byte[] buffer) {
		String packageName = "";
		String classString = "";
		try {
			classString = new String(buffer, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			errorMessageString
					.add("Unable to extract UTF-8 String from byte array!");
			e.printStackTrace();
		}
		int i = classString.indexOf("package ");
		if (i > -1) {
			int j = classString.indexOf(";", i);
			if (j > i + 7) {
				packageName = classString.substring(i + 8, j);
			}
		}
		i = classString.indexOf("implements WorkflowTemplate");
		if (i < 0) {
			errorMessageString
					.add("WARNING: Template does not appear to implement the WorkflowTemplate interface! Invalid?");
		}
		return packageName;
	}

}
