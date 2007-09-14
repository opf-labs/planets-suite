package eu.planets_project.tb.unittest.model.mockup;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import eu.planets_project.tb.api.model.mockups.ExperimentWorkflow;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.impl.model.mockup.ExperimentWorkflowImpl;
import eu.planets_project.tb.impl.model.mockup.WorkflowImpl;
import junit.framework.TestCase;

public class ExperimentWorkflowTest extends TestCase{
	
	private ExperimentWorkflow expWF;
	private Workflow wf;
	
	public void setUp(){
		/*WorkflowHandler wfhandler = new WorkflowHandlerImpl();
		Iterator<Long> itIDs = wfhandler.getAllWorkflowIDs()
		Workflow workflow = new WorkflowImpl();*/
		wf = new WorkflowImpl();
		expWF = new ExperimentWorkflowImpl(wf);
	}
	
	public void testContainsWorkflow(){
		assertEquals(wf,expWF.getWorkflow());
	}
	
	public void testInputData(){
		//Test1:
		assertEquals(0,expWF.getInputData().size());
		
		//Test2: Test addInputData
		try{
		URI uri1 = new URI("file:http://planets-project.eu/testbed/files/1");
		expWF.addInputData(uri1);
		assertTrue(expWF.getInputData().contains(uri1));
		assertEquals(1,expWF.getInputData().size());
		
		//Test3:
		URI uri2 = new URI("file:http://planets-project.eu/testbed/files/2");
		expWF.addInputData(uri2);
		assertTrue(expWF.getInputData().contains(uri1));
		assertTrue(expWF.getInputData().contains(uri2));
		assertEquals(2,expWF.getInputData().size());
		
		expWF.addInputData(uri2);
		assertEquals(2,expWF.getInputData().size());
		
		//Test4: Test removeInputData
		expWF.removeInputData(uri2);
		assertTrue(expWF.getInputData().contains(uri1));
		assertTrue(!(expWF.getInputData().contains(uri2)));
		assertEquals(1,expWF.getInputData().size());
		
		expWF.removeInputData(uri2);
		assertTrue(expWF.getInputData().contains(uri1));
		assertTrue(!(expWF.getInputData().contains(uri2)));
		assertEquals(1,expWF.getInputData().size());

		//Test5: add/remove InputData(List);
		expWF = new ExperimentWorkflowImpl(this.wf);
		Vector<URI> uris = new Vector<URI>();
		uris.add(uri1);
		uris.add(uri2);
		expWF.addInputData(uris);
		assertTrue(expWF.getInputData().contains(uri1));
		assertTrue((expWF.getInputData().contains(uri2)));
		assertEquals(2,expWF.getInputData().size());

		//Test6:
		Vector<URI> uris2 = new Vector<URI>();
		URI uri3 = new URI("file:http://planets-project.eu/testbed/files/3");
		URI uri4 = new URI("file:http://planets-project.eu/testbed/files/4");
		uris2.add(uri2);
		uris2.add(uri3);
		expWF.addInputData(uris2);
		assertTrue(expWF.getInputData().contains(uri1));
		assertTrue((expWF.getInputData().contains(uri2)));
		assertTrue((expWF.getInputData().contains(uri3)));
		assertEquals(3,expWF.getInputData().size());

		//Test7: remove InputData(List)
		Vector<URI> uris3 = new Vector<URI>();
		uris3.add(uri1);
		uris3.add(uri2);
		uris3.add(uri4);
		expWF.removeInputData(uris3);
		assertTrue(!(expWF.getInputData().contains(uri1)));
		assertTrue(!(expWF.getInputData().contains(uri2)));
		assertTrue((expWF.getInputData().contains(uri3)));
		assertEquals(1,expWF.getInputData().size());
		
		}
		catch(URISyntaxException e){
			System.out.println("Error in ExperimentWorkflowTest: "+e.toString());
			assertEquals(true,false);
		}
	}

	
	public void testOutputData(){
		//Test1:
		assertEquals(0,expWF.getOutputData().size());
			
		try{
			//Test2:  should not get added
			URI uri1in = new URI("file:http://planets-project.eu/testbed/files/1in");
			URI uri1out = new URI("file:http://planets-project.eu/testbed/files/1out");
			URI uri2in = new URI("file:http://planets-project.eu/testbed/files/2in");
			URI uri2out = new URI("file:http://planets-project.eu/testbed/files/2out");
			expWF.setOutputData(uri1in,uri1out);
			assertEquals(0,expWF.getOutputData().size());
			assertEquals(0,expWF.getDataEntries().size());
			
			//Test2: Test setOutputData(URI,URI)
			expWF.addInputData(uri1in);
			assertTrue(expWF.getInputData().contains(uri1in));
			assertEquals(1,expWF.getInputData().size());
			expWF.setOutputData(uri1in,uri1out);
			Entry<URI,URI> entry1 = expWF.getDataEntry(uri1in);
			assertEquals(uri1in,entry1.getKey());
			assertEquals(uri1out,entry1.getValue());
			
			//Test3: add no output data --> return null
			expWF = new ExperimentWorkflowImpl(this.wf);
			expWF.addInputData(uri1in);
			assertTrue(expWF.getInputData().contains(uri1in));
			
			entry1 = expWF.getDataEntry(uri1in);
			assertEquals(uri1in,entry1.getKey());
			assertEquals(null,entry1.getValue());
			
			//Test4: setOutputData(Entry<URI,URI>)
			expWF = new ExperimentWorkflowImpl(this.wf);
			expWF.addInputData(uri1in);
			expWF.addInputData(uri2in);
			//create sample Map
			HashMap<URI,URI> hm1 = new HashMap<URI,URI>();
			hm1.put(uri1in, uri1out);
			hm1.put(uri2in, uri2out);
			Iterator<Entry<URI,URI>> itEntry = hm1.entrySet().iterator();
			while(itEntry.hasNext()){
				Entry<URI,URI> entryTest = itEntry.next();
				expWF.setOutputData(entryTest.getKey(),entryTest.getValue());
			}
			assertEquals(2,expWF.getDataEntries().size());
			assertEquals(2,expWF.getOutputData().size());
			
			Entry<URI,URI> entryFound = expWF.getDataEntry(uri1in);
			assertEquals(uri1out, entryFound.getValue());
			
			
			//Test5: setOutputData(List<Entry<URI,URI>);
			expWF = new ExperimentWorkflowImpl(this.wf);
			expWF.addInputData(uri1in);
			expWF.addInputData(uri2in);
			//create sample Map
			hm1 = new HashMap<URI,URI>();
			hm1.put(uri1in, uri1out);
			hm1.put(uri2in, uri2out);
			expWF.setOutputData(hm1.entrySet());
			assertEquals(2,expWF.getDataEntries().size());
			assertEquals(2,expWF.getOutputData().size());
			
			entryFound = expWF.getDataEntry(uri1in);
			assertEquals(uri1out, entryFound.getValue());
			
		}
		catch(URISyntaxException e){
			System.out.println("Error in ExperimentWorkflowTest: "+e.toString());
			assertEquals(true,false);
		}
	}


	public void testDataEntries(){
		//Test1:
		assertEquals(0,expWF.getDataEntries().size());
		
		//Test2: Test getDataEntry(URI)
		try{
			URI uri1in = new URI("file:http://planets-project.eu/testbed/files/1in");
			URI uri1out = new URI("file:http://planets-project.eu/testbed/files/1out");
			expWF.addInputData(uri1in);
			assertTrue(expWF.getInputData().contains(uri1in));
			assertEquals(1,expWF.getInputData().size());
			expWF.setOutputData(uri1in,uri1out);
			Entry<URI,URI> entry1 = expWF.getDataEntry(uri1in);
			assertEquals(uri1in,entry1.getKey());
			assertEquals(uri1out,entry1.getValue());
		
			assertEquals(1,expWF.getDataEntries().size());
		}
		catch(URISyntaxException e){
			System.out.println("Error in ExperimentWorkflowTest: "+e.toString());
			assertEquals(true,false);
		}
		
	}
	
}
