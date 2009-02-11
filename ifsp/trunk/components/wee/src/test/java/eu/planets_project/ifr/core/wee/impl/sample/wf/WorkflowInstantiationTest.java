package eu.planets_project.ifr.core.wee.impl.sample.wf;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.planets_project.ifr.core.wee.api.workflow.WorkflowInstance;
import eu.planets_project.ifr.core.wee.api.workflow.generated.WorkflowConf;
import eu.planets_project.ifr.core.wee.impl.workflow.WorkflowFactory;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.utils.ByteArrayHelper;

public class WorkflowInstantiationTest {
	
	File fSchema = null;
	File fSample = null;
	String path = null;
	//test data
	File fpng1 = null;
	File fpng2 = null;
	
	@Before
	public void setup(){
		path = "D:/Implementation/non-svn/wee/";
		fSchema = new File(path+"planets_wdt.xsd");
		fSample = new File(path+"ws-sample_testing.xml");
		//test data
		fpng1 = new File ("D:/Implementation/forum_europe_png.png");
		fpng2 = new File ("D:/Implementation/usa_bundesstaaten_png.png");
		
	}
	
	@Test
	public void readFiles(){
		Assert.assertTrue(fSchema.canRead());
		Assert.assertTrue(fSample.canRead());
		Assert.assertTrue(fpng1.canRead());
		Assert.assertTrue(fpng2.canRead());
	}
	
	@Test
	public void unmarshalWorkflowConfig(){
		try {
			JAXBContext context = JAXBContext.newInstance(WorkflowConf.class); 
			Unmarshaller um = context.createUnmarshaller(); 
			WorkflowConf wfc = (WorkflowConf) um.unmarshal( new FileReader(this.fSample)); 
			Assert.assertEquals(wfc.getTemplate().getClazz(),"eu.planets_project.ifr.core.wee.impl.sample.wf.DroidWorkflowTemplate");
			Assert.assertEquals(wfc.getServices().getService().size(),1);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Assert.assertEquals(true, false);
		}
	}
	
	@Test
	public void buildWorkflowWithFactory(){
		try {
			JAXBContext context = JAXBContext.newInstance(WorkflowConf.class); 
			Unmarshaller um = context.createUnmarshaller(); 
			WorkflowConf wfc = (WorkflowConf) um.unmarshal( new FileReader(this.fSample)); 
			
			
			//temp: build the digital objects = data
			DigitalObject dob1 = new DigitalObject.Builder( Content.byValue(ByteArrayHelper.read(fpng1)) ).build();
			DigitalObject dob2 = new DigitalObject.Builder( Content.byValue(ByteArrayHelper.read(fpng2)) ).build();
			List<DigitalObject> dobs = new ArrayList<DigitalObject>();
			dobs.add(dob1);
			dobs.add(dob2);
			
			WorkflowInstance wfinstance = WorkflowFactory.create(wfc,dobs);
			
			Assert.assertTrue(false);
			/*Identify droid = droidWF.getDroid();
			Assert.assertNull(droid);
			droidWF.init(wfc,dobs);
			droidWF.execute();
			Assert.assertNotNull(droid);*/
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Assert.assertEquals(true, false);
		}
	}
}
