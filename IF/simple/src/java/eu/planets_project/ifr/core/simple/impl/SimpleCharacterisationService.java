package eu.planets_project.ifr.core.simple.impl;

import java.net.URL;

import javax.activation.DataHandler;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.ws.BindingType;
import javax.jws.soap.SOAPBinding;

import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.simple.api.SimpleCharacterisationRemoteInterface;
import eu.planets_project.ifr.core.simple.impl.util.FileTypeResolver;
import eu.planets_project.services.utils.PlanetsLogger;

@javax.jws.WebService(name="SimpleCharacterisationService", targetNamespace="http://services.planets-project.eu/ifr/characterisation", serviceName="SimpleCharacterisationService")
@Stateless
@Remote(SimpleCharacterisationRemoteInterface.class)
@RemoteBinding(jndiBinding = "planets-project.eu/SimpleCharacterisationRemoteInterface")
@BindingType(value="http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@SOAPBinding(style = SOAPBinding.Style.RPC)
/**
 * A simple characterisation service.
 *
 * @author Reis Markus, ARC
 *
 */
public class SimpleCharacterisationService implements SimpleCharacterisationRemoteInterface
{

    private final static String logConfigFile = "eu/planets_project/ifr/core/simple/scs-log4j.xml";

	@javax.jws.WebMethod()
	public String characteriseFile(String fileURL) {
		PlanetsLogger.getLogger(this.getClass(), logConfigFile).debug("Computing mime-type for: " + fileURL);
		try {
			FileTypeResolver ftr = FileTypeResolver.instantiate();
			return ftr.getMIMEType(fileURL);
		} catch (Exception e) {
			PlanetsLogger.getLogger(this.getClass(), logConfigFile).error("[WARN] Problems getting MIMEType for " + fileURL);
			return "application/octet-stream";
		}
	}

	@javax.jws.WebMethod()
	public String[] characteriseFiles(String[] fileURLs) {
		String[] result = new String[fileURLs.length];
		for (int i = 0; i < fileURLs.length; i++)
			result[i] = characteriseFile(fileURLs[i]);
        return result;
	}

	@javax.jws.WebMethod()
	public String characteriseFileURL(URL fileURL) {
		return characteriseFile(fileURL.toString());
		//DataHandler dh = new DataHandler(fileURL);
		//return dh.getContentType();
	}

	@javax.jws.WebMethod()
	public String[] characteriseFileURLs(URL[] fileURLs) {
		String[] fileURLStrings = new String[fileURLs.length];
		for (int i = 0; i < fileURLs.length; i++)
			fileURLStrings[i] = fileURLs[i].toString();
		return characteriseFiles(fileURLStrings);
	}

	@javax.jws.WebMethod()
	public String characteriseFileDH(DataHandler fileData){
		try {
			PlanetsLogger.getLogger(this.getClass(), logConfigFile).debug("content.length = " + fileData.getInputStream().available());
		} catch (Exception e) { e.printStackTrace();}
		return fileData.getContentType();
	}

	@javax.jws.WebMethod()
	public String[] characteriseFileDHs(DataHandler[] files) {
		String[] result = new String[files.length];
		for (int i = 0; i < files.length; i++)
			result[i] = files[i].getContentType();
        return result;
    }
}