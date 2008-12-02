package eu.planets_project.tb.gui.backing;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.component.html.HtmlDataTable;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.gui.util.SortableList;
import eu.planets_project.tb.impl.AdminManagerImpl;

import java.util.Collection;


public class ListExp extends SortableList {
    
    private static Log log = PlanetsLogger.getLogger(ListExp.class, "testbed-log4j.xml");

    private List<Experiment> currExps;
	private static final String DEFAULT_COLUMN = "startDate";
	private String column = DEFAULT_COLUMN;
	private boolean ascending = false;
    private HtmlDataTable myExp_data = null;
    private HtmlDataTable allExp_data = null;
    private HtmlDataTable toAppExp_data = null;
    private HtmlDataTable toExecExp_data = null;
	// Value to hold link ids.
	private String linkEid = null;
	// Value to hold the search string:
	private String toFind = "";
	// Paging table sizes:
    private int myExpPageSize = 20;
    private int allExpPageSize = 20;
	

	public ListExp()
	{
		super(DEFAULT_COLUMN);
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
		  Collection<Experiment> myExps = testbedMan.getAllExperimentsOfUsers(userid, true);
		  currExps = Collections.list(Collections.enumeration(myExps));
		  sort(getSort(), isAscending());
		  return currExps;
	  }
          
      public int getNumExperimentsOfUser()
      {
          UserBean managedUserBean = (UserBean)JSFUtil.getManagedObject("UserBean");    
          String userid = managedUserBean.getUserid();       
          TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");         
          int num = testbedMan.getNumberOfExperiments(userid, true);   
          return num; 
      }
          
      public PagedListDataModel getAllExperiments()
      {    
          TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");  
          List exps = testbedMan.getPagedExperiments(allExp_data.getFirst(), allExp_data.getRows(), getSort(), !isAscending() );
          return new PagedListDataModel( exps, testbedMan.getNumberOfExperiments(), allExp_data.getRows());
      }
                
    /**
     * @return the toFind
     */
    public String getToFind() {
        return toFind;
    }

    /**
     * @param toFind the toFind to set
     */
    public void setToFind(String toFind) {
        this.toFind = toFind;
    }
    
    public String clearSearchStringAction() {
        setToFind("");
        return "browse_experiments";
    }

    public Collection<Experiment> getAllMatchingExperiments()
      {
          // Otherwise, search for the string toFind:
          log.debug("Searching experiments for: " + toFind );
          TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");  
          // Only go if there is a string to search for:
          if( toFind == null || "".equals(toFind)) return testbedMan.getAllExperiments();
          Collection<Experiment> allExps = testbedMan.searchAllExperiments(toFind);
          log.debug("Found "+allExps.size()+" matching experiment(s).");
           currExps = Collections.list(Collections.enumeration(allExps));
          sort(getSort(), isAscending());
          return currExps;
      }
    
    public Collection<Experiment> getAllExpAwaitingAuth()
    {    
        TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");  
        Collection<Experiment> myExps = testbedMan.getAllExperimentsAwaitingApproval();
         currExps = Collections.list(Collections.enumeration(myExps));
        sort(getSort(), isAscending());
        return currExps;
    }
    
    public Collection<Experiment> getAllExpApproved()
    {    
        TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");  
        Collection<Experiment> allExps = testbedMan.getAllExperimentsAtPhase(ExperimentPhase.PHASE_EXPERIMENTEXECUTION);
        currExps = Collections.list(Collections.enumeration(allExps));
        sort(getSort(), isAscending());
        return currExps;
    }
              
                
      public int getNumAllExperiments()
      {
          TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");  
          int num = testbedMan.getNumberOfExperiments();
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
		    if( "startDate".equals(sortColumn)) return false;
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
                                if (column.equals("startDate"))
				{
                                    String c1_startDate = null;
                                    if( c1.getCurrentPhase() != null && c1.getCurrentPhase().getStartDate() != null )
                                        c1_startDate = c1.getCurrentPhase().getStartDate().toString();
                                    String c2_startDate = null;
                                    if( c2.getCurrentPhase() != null && c2.getCurrentPhase().getStartDate() != null )
                                        c2_startDate = c2.getCurrentPhase().getStartDate().toString();
                                    if (c1_startDate==null) c1_startDate="";
                                    if (c2_startDate==null) c2_startDate="";
                                    return ascending ? c1_startDate.compareTo(c2_startDate) : c2_startDate.compareTo(c1_startDate);
				}
                                if (column.equals("exDate"))
				{
                                    String c1_exDate = null;
                                    if( c1.getExperimentExecution().getExecutionEndedDate() != null )
                                        c1_exDate = c1.getExperimentExecution().getExecutionEndedDate().toString();
                                    String c2_exDate = null;
                                    if( c2.getExperimentExecution().getExecutionEndedDate() != null )
                                        c2_exDate = c2.getExperimentExecution().getExecutionEndedDate().toString();
                                    if (c1_exDate==null) c1_exDate="";
                                    if (c2_exDate==null) c2_exDate="";                                    
                                    return ascending ? c1_exDate.compareTo(c2_exDate) : c2_exDate.compareTo(c1_exDate);
				}
                                if (column.equals("currentStage"))
				{
                                        if ((c1.getCurrentPhase() != null) && (c2.getCurrentPhase() != null)) {
                                            return ascending ? c1.getCurrentPhase().getPhaseName().compareTo(c2.getCurrentPhase().getPhaseName()) 
                                                    : c2.getCurrentPhase().getPhaseName().compareTo(c1.getCurrentPhase().getPhaseName());
                                        }
                                        else
                                            return 0;
				}
				else
					return 0;
			}			
		}
		
	    public String editExperimentAction()
	    {	    
	      Experiment selectedExperiment = (Experiment) this.getAllExp_data().getRowData();
	      return this.editExperimentAction(selectedExperiment);
	    }

	    public String editMyExperimentAction()
	    {	    
	      Experiment selectedExperiment = (Experiment) this.getMyExp_data().getRowData();
	      return this.editExperimentAction(selectedExperiment);
	    }
	    
	    private String editExperimentAction(Experiment selectedExperiment) {
	      System.out.println("exp name: "+ selectedExperiment.getExperimentSetup().getBasicProperties().getExperimentName());
	      FacesContext ctx = FacesContext.getCurrentInstance();

	      ExperimentBean expBean = new ExperimentBean();
	      expBean.fill(selectedExperiment);
	      
	      //Store selected Experiment Row accessible later as #{Experiment} 
	      ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
	              
	      // go to edit page
	      return "editExp";
	    }
	    
	    public String exportMyExperimentAction() {
            Experiment selectedExperiment = (Experiment) this.getMyExp_data().getRowData();
	        DownloadManager dm = (DownloadManager)JSFUtil.getManagedObject("DownloadManager");
	        return dm.downloadExperiment(selectedExperiment);
	    }

        public String viewExperimentToApprove()
        {
        
          Experiment selectedExperiment = (Experiment) this.getToAppExp_data().getRowData();
          FacesContext ctx = FacesContext.getCurrentInstance();

          ExperimentBean expBean = new ExperimentBean();
          expBean.fill(selectedExperiment);

          //Store selected Experiment Row accessible later as #{Experiment} 
          ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
                  
          // go to edit page
          return "viewExperimentExeManager";
        }
        
        public String viewExperimentToExecute()
        {
        
          Experiment selectedExperiment = (Experiment) this.getToExecExp_data().getRowData();
          FacesContext ctx = FacesContext.getCurrentInstance();

          ExperimentBean expBean = new ExperimentBean();
          expBean.fill(selectedExperiment);

          //Store selected Experiment Row accessible later as #{Experiment} 
          ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
                  
          // go to edit page
          return "viewExperimentExeManager";
        }
        
        public String adminApproveExperiment() {
            ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
            AdminManagerImpl.approveExperimentManually(expBean.getExperiment());
            return "viewExperimentExeManager";
        }
        
        public String adminDenyExperiment() {
            ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
            AdminManagerImpl.denyExperimentManually(expBean.getExperiment());
            return "viewExperimentExeManager";
        }
        
        public String readerExperimentAction()
        {
        
          Experiment selectedExperiment = (Experiment) this.getAllExp_data().getRowData();
          System.out.println("exp name: "+ selectedExperiment.getExperimentSetup().getBasicProperties().getExperimentName());
          FacesContext ctx = FacesContext.getCurrentInstance();

          ExperimentBean expBean = new ExperimentBean();
          expBean.fill(selectedExperiment);

          //Store selected Experiment Row accessible later as #{Experiment} 
          ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
                  
          // go to edit page
          return "viewExp";
        }
	    
        public String readerExperimentLinkAction() {
            
            FacesContext ctx = FacesContext.getCurrentInstance();
            this.linkEid = (String) (String) ctx.getExternalContext().getRequestParameterMap().get("linkEid");
            if( this.linkEid == null || "".equals(this.linkEid))
                return "goToBrowseExperiments";
            
            TestbedManager testbedMan = (TestbedManager)JSFUtil.getManagedObject("TestbedManager");
            Experiment selectedExperiment = testbedMan.getExperiment(Long.parseLong(linkEid));
            System.out.println("exp name: "+ selectedExperiment.getExperimentSetup().getBasicProperties().getExperimentName());

            ExperimentBean expBean = new ExperimentBean();
            expBean.fill(selectedExperiment);
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
	      Experiment selectedExperiment = (Experiment) this.getAllExp_data().getRowData();
	      System.out.println("exp name: "+ selectedExperiment.getExperimentSetup().getBasicProperties().getExperimentName());
	      FacesContext ctx = FacesContext.getCurrentInstance();

	      ExperimentBean expBean = new ExperimentBean();
	      expBean.fill(selectedExperiment);
	      //Store selected Experiment Row accessible later as #{Experiment} 
	      ctx.getExternalContext().getSessionMap().put("ExperimentBean", expBean);
              
            //go to page for confirming deletion
            return "selectDelete";
        }
        
        public String approveExperiment() {
            Experiment exp = (Experiment) this.getToAppExp_data().getRowData();
            TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
            exp.getExperimentApproval().setState(Experiment.STATE_COMPLETED);
            exp.getExperimentExecution().setState(Experiment.STATE_IN_PROGRESS);
            testbedMan.updateExperiment(exp);
            return "success";
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

	    public void setMyExp_data(HtmlDataTable data)
	    {
	      this.myExp_data = data;
	    }


	    public HtmlDataTable getMyExp_data()
	    {
	      return myExp_data;
	    }

	    public void setAllExp_data(HtmlDataTable data)
	    {
	      this.allExp_data = data;
	    }


	    public HtmlDataTable getAllExp_data()
	    {
	      return allExp_data;
	    }
	    
	    

        /**
         * @return the toAppExp_data
         */
        public HtmlDataTable getToAppExp_data() {
            return toAppExp_data;
        }

        /**
         * @param toAppExp_data the toAppExp_data to set
         */
        public void setToAppExp_data(HtmlDataTable toAppExp_data) {
            this.toAppExp_data = toAppExp_data;
        }

        /**
         * @return the toExecExp_data
         */
        public HtmlDataTable getToExecExp_data() {
            return toExecExp_data;
        }

        /**
         * @param toExecExp_data the toExecExp_data to set
         */
        public void setToExecExp_data(HtmlDataTable toExecExp_data) {
            this.toExecExp_data = toExecExp_data;
        }

        /**
         * @return the linkEid
         */
        public String getLinkEid() {
            return linkEid;
        }

        /**
         * @param linkEid the linkEid to set
         */
        public void setLinkEid(String linkEid) {
            this.linkEid = linkEid;
        }
        

        /**
         * @return the myExpPageSize
         */
        public int getMyExpPageSize() {
            return myExpPageSize;
        }

        /**
         * @param myExpPageSize the myExpPageSize to set
         */
        public void setMyExpPageSize(int myExpPageSize) {
            this.myExpPageSize = myExpPageSize;
        }

        /**
         * @return the allExpPageSize
         */
        public int getAllExpPageSize() {
            return allExpPageSize;
        }

        /**
         * @param allExpPageSize the allExpPageSize to set
         */
        public void setAllExpPageSize(int allExpPageSize) {
            this.allExpPageSize = allExpPageSize;
        }

}
