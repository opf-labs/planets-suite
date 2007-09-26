package eu.planets_project.tb.gui.backing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIData;
import javax.faces.context.FacesContext;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.BasicProperties;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.api.model.mockups.Workflow;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.gui.util.SortableList;
import eu.planets_project.tb.impl.model.BasicPropertiesImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.model.ExperimentSetupImpl;
import eu.planets_project.tb.impl.TestbedManagerImpl;

import java.util.Collection;


public class ListExp extends SortableList {

	private Collection<Experiment> myExps = new ArrayList<Experiment>();
	private Collection<Experiment> allExps = new ArrayList<Experiment>();
	private List<Experiment> currExps;
	private String column = "name";
	private boolean ascending = true;
	private UIData myExp_data = null;
	private UIData allExp_data = null;	

	public ListExp()
	{
		super("name");
        myExps = this.getExperimentsOfUser();
        allExps = this.getAllExperiments();

	}
	
	  public Collection<Experiment> getExperimentsOfUser()
	  {
		  // Get all experiments created/owned by the current user  
		  // get user id from MB facility
		  // get UserBean - grab userid   
		  /*List<Experiment> usersExpList = new ArrayList<Experiment>();*/
		  UserBean managedUserBean = (UserBean)JSFUtil.getManagedObject("UserBean");    
		  String userid = managedUserBean.getUserid();	     
		  TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");		  
		  /*Iterator<Experiment> iter = testbedMan.getAllExperiments().iterator();		  
		  while (iter.hasNext()) {
			  Experiment exp = iter.next();
			  if (userid.equals(exp.getExperimentSetup().getBasicProperties().getExperimenter()))
				  usersExpList.add(exp);
		  }
		  myExps = usersExpList; */
		  myExps = testbedMan.getAllExperimentsOfUsers(userid, true);
		  currExps = Collections.list(Collections.enumeration(myExps));
		  sort(getSort(), isAscending());
		  return currExps;
	  }
          
      public int getNumExperimentsOfUser()
      {
          int num = myExps.size();              
          return num; 
      }
          
	  public Collection<Experiment> getAllExperiments()
	  {    
		  TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");  
		  allExps = testbedMan.getAllExperiments();
		  currExps = Collections.list(Collections.enumeration(allExps));
		  sort(getSort(), isAscending());
		  return currExps;
	  }
          
      public int getNumAllExperiments()
      {
          int num = allExps.size();              
          return num; 
      }
	  
		protected void sort(final String column, final boolean ascending)
		{
			this.column = column;
			this.ascending = ascending;
			Comparator<Object> comparator = new MyComparator();
			Collections.sort(currExps, comparator);
		}

		protected boolean isDefaultAscending(String sortColumn)
		{
			return true;
		}
		
		class MyComparator implements Comparator<Object> {

			public int compare(Object o1, Object o2)
			{
				Experiment c1 = (Experiment) o1;
				Experiment c2 = (Experiment) o2;
				if (column == null)
				{
					return 0;
				}
				if (column.equals("name"))
				{
					return ascending ? c1.getExperimentSetup().getBasicProperties().getExperimentName().compareToIgnoreCase(c2.getExperimentSetup().getBasicProperties().getExperimentName()) : c2.getExperimentSetup().getBasicProperties().getExperimentName()
									.compareToIgnoreCase(c1.getExperimentSetup().getBasicProperties().getExperimentName());
				}
				if (column.equals("type"))
				{
					String c1_type = c1.getExperimentSetup().getExperimentTypeName();
					String c2_type = c2.getExperimentSetup().getExperimentTypeName();
					if (c1_type==null) c1_type="";
					if (c2_type==null) c2_type="";
					return ascending ? c1_type.compareTo(c2_type) : c2_type.compareTo(c1_type);
				}	
				if (column.equals("experimenter"))
				{
					return ascending ? c1.getExperimentSetup().getBasicProperties().getExperimenter().compareTo(c2.getExperimentSetup().getBasicProperties().getExperimenter()) : c2.getExperimentSetup().getBasicProperties().getExperimenter()
									.compareTo(c1.getExperimentSetup().getBasicProperties().getExperimenter());
				}
				else
					return 0;
			}			
		}
		
	    public String editExperimentAction()
	    {
	    
	      Experiment selectedExperiment = (Experiment) this.getMyExp_data().getRowData();
	      System.out.println("exp name: "+ selectedExperiment.getExperimentSetup().getBasicProperties().getExperimentName());
	      FacesContext ctx = FacesContext.getCurrentInstance();

	      ExperimentBean expBean = new ExperimentBean();
	      expBean.fill(selectedExperiment);
	      // if experiment workflow already specified, set a workflow bean into session as backing bean for inputfields
	      if (expBean.getWorkflowTypeId()!=null) {
	    	  Workflow wf = selectedExperiment.getExperimentSetup().getExperimentWorkflow().getWorkflow();
	    	  ctx.getExternalContext().getSessionMap().put("Workflow",wf);
	      }
	      //Store selected Experiment Row accessible later as #{Experiment} 
	      ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
	              
	      // go to edit page
	      return "editExp";
	    }
            
	    public String readerExperimentAction()
	    {
	    
	      Experiment selectedExperiment = (Experiment) this.getAllExp_data().getRowData();
	      System.out.println("exp name: "+ selectedExperiment.getExperimentSetup().getBasicProperties().getExperimentName());
	      FacesContext ctx = FacesContext.getCurrentInstance();

	      ExperimentBean expBean = new ExperimentBean();
	      expBean.fill(selectedExperiment);
	      // if experiment workflow already specified, set a workflow bean into session as backing bean for inputfields
	      if (expBean.getWorkflowTypeId()!=null) {
	    	  Workflow wf = selectedExperiment.getExperimentSetup().getExperimentWorkflow().getWorkflow();
	    	  ctx.getExternalContext().getSessionMap().put("Workflow",wf);
	      }
	      //Store selected Experiment Row accessible later as #{Experiment} 
	      ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
	              
	      // go to edit page
	      return "viewExp";
	    }
            
//            public void chooseView()
//            {
//                UserBean managedUserBean = (UserBean)JSFUtil.getManagedObject("UserBean");  
//                Experiment selectedExperiment = (Experiment) this.getData().getRowData();
//                
//                String selectedExperimenter = selectedExperiment.getExperimentSetup().getBasicProperties().getExperimenter();
//                
//                if(selectedExperiment.equals(managedUserBean.getUserid()))
//                    this.editExperimentAction();
//                else
//                    this.readerExperimentAction();
//            }
            
        public String selectExperimentForDeletion()
        {
	      Experiment selectedExperiment = (Experiment) this.getMyExp_data().getRowData();
	      System.out.println("exp name: "+ selectedExperiment.getExperimentSetup().getBasicProperties().getExperimentName());
	      FacesContext ctx = FacesContext.getCurrentInstance();

	      ExperimentBean expBean = new ExperimentBean();
	      expBean.fill(selectedExperiment);
	      //Store selected Experiment Row accessible later as #{Experiment} 
	      ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
              
            //go to page for confirming deletion
            return "selectDelete";
        }
	    
        public String deleteExperimentAction()
	    {
	    ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
		  TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");  
          testbedMan.removeExperiment(expBean.getID());
          
          //remove this experiment from the session
          FacesContext ctx = FacesContext.getCurrentInstance();
          ctx.getExternalContext().getSessionMap().remove("ExperimentBean");

          // Workaround
          // update in cached lists
          this.getExperimentsOfUser();
          this.getAllExperiments();
          
	      // go back to 'my experiments' page
	      return "expDeleted";
	    }
	    
//	  Property getters - setters

	    public void setMyExp_data(UIData data)
	    {
	      this.myExp_data = data;
	    }


	    public UIData getMyExp_data()
	    {
	      return myExp_data;
	    }

	    public void setAllExp_data(UIData data)
	    {
	      this.allExp_data = data;
	    }


	    public UIData getAllExp_data()
	    {
	      return allExp_data;
	    }

}
