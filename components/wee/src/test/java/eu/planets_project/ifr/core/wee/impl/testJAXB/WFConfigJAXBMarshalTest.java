/**
 * 
 */
package eu.planets_project.ifr.core.wee.impl.testJAXB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.corba.se.spi.orbutil.threadpool.Work;

import eu.planets_project.ifr.core.wee.api.workflow.generated.ObjectFactory;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;


/**
 * @author <a href="mailto:andrew.lindley@arcs.ac.at">Andrew Lindley</a>
 * @since 14.11.2008
 *
 */
public class WFConfigJAXBMarshalTest {
	
	File fSchema = null;
	File fSample = null;
	String path = null;
	
	@Before
	public void setup(){
		path = "D:/Implementation/non-svn/wee/";
		fSchema = new File(path+"planets_wdt.xsd");
		fSample = new File(path+"ws-sample_testing.xml");
	}
	
	@Test
	public void readFiles(){
		Assert.assertTrue(fSchema.canRead());
		Assert.assertTrue(fSample.canRead());
	}
	
	
	@Test
	public void marshalGeneratedClasses(){
		try {
			//rebuild the ws-sample.xml file
			ObjectFactory obf = new ObjectFactory();
			WorkflowConf wft = obf.createWorkflow();
			//Template
			WorkflowConf.Template template = obf.createWorkflowTemplate();
			template.setClazz("planets_project.ifr.core.wdt.MyWorkflowTemplateImpl");
			
			//services
			WorkflowConf.Services services = obf.createWorkflowServices();
				//service
			WorkflowConf.Services.Service service1 = obf.createWorkflowServicesService();
			service1.setId("s1");
			service1.setEndpoint("http://myhost/myservice/api");
				//parameters
			WorkflowConf.Services.Service.Parameters params = obf.createWorkflowServicesServiceParameters();
			WorkflowConf.Services.Service.Parameters.Param param1 = obf.createWorkflowServicesServiceParametersParam();
			param1.setName("planets://properties/resolution");
			param1.setValue("1024x768");
			params.getParam().add(param1);
			WorkflowConf.Services.Service.Parameters.Param param2 = obf.createWorkflowServicesServiceParametersParam();
			param2.setName("planets://properties/color");
			param2.setValue("RGB");
			params.getParam().add(param2);
			service1.setParameters(params);
			
			WorkflowConf.Services.Service service2 = obf.createWorkflowServicesService();
			service2.setId("s2");
			service2.setEndpoint("http://somehost/aservice/endpoint");
			
			services.getService().add(service1);
			services.getService().add(service2);
			
			//data
			WorkflowConf.Data data = obf.createWorkflowData();
			WorkflowConf.Data.RefURL d1 = obf.createWorkflowDataRefURL();
			d1.setId("d1");
			d1.setValue("http://myUrl.com");
			data.getBase64OrRefURL().add(d1);
			
			WorkflowConf.Data.Base64 d2 = obf.createWorkflowDataBase64();
			d2.setId("d2");
			d2.setValue((new String("cmFpbmVy")).getBytes("UTF8"));
			data.getBase64OrRefURL().add(d2);
			
			WorkflowConf.Data.Base64 d3 = obf.createWorkflowDataBase64();
			d3.setId("d3");
			d3.setValue((new String("cmFpbmVy")).getBytes("UTF8"));
			data.getBase64OrRefURL().add(d3);
			
			wft.setTemplate(template);
			wft.setServices(services);
			wft.setData(data);
			
			//now marshal 
			JAXBContext context = JAXBContext.newInstance(WorkflowConf.class); 
			Marshaller m = context.createMarshaller();
			//specify whether or not the marshalled XML data is formated with linefeeds and indentation
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE ); 
			m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "planets_wdt.xsd");
			m.marshal( wft, System.out );
			m.marshal(wft, new FileWriter(this.path+"Test1.xml"));
			
			Assert.assertTrue(new File(this.path+"Test1.xml").canRead());
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertFalse(true);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertFalse(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertFalse(true);
		}

	}
	
	@Test
	public void unmarshalGeneratedXML(){
		//generates the Test1.xml file
		this.marshalGeneratedClasses();
		
		try {
			JAXBContext context = JAXBContext.newInstance(WorkflowConf.class); 
			Unmarshaller um = context.createUnmarshaller(); 
			WorkflowConf wfc = (WorkflowConf) um.unmarshal( new FileReader(this.path+"Test1.xml")); 
			Assert.assertEquals(wfc.getTemplate().getClazz(),"planets_project.ifr.core.wdt.MyWorkflowTemplateImpl");
			Assert.assertEquals(wfc.getServices().getService().size(),2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Assert.assertEquals(true, false);
		}
	}

}
