package eu.planets_project.tb.gui.backing;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.component.html.HtmlDataTable;

import org.apache.commons.logging.Log;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.mail.PlanetsMailMessage;
import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.ExperimentPhase;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.backing.exp.ExpBeanReqManager;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.gui.util.SortableList;
import eu.planets_project.tb.impl.AdminManagerImpl;
import eu.planets_project.tb.impl.model.ExperimentImpl;
import eu.planets_project.tb.impl.system.TestbedBatchProcessor;

import java.util.Collection;
import eu.planets_project.ifr.core.security.api.model.User;

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
        // Get the experiments-to-approve list:
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
                                    Date c1_startDate = null;
                                    if( c1.getStartDate() != null )
                                        c1_startDate = c1.getStartDate().getTime();
                                    Date c2_startDate = null;
                                    if( c2.getStartDate() != null )
                                        c2_startDate = c2.getStartDate().getTime();
                                    if (c1_startDate==null) c1_startDate=Calendar.getInstance().getTime();
                                    if (c2_startDate==null) c2_startDate=Calendar.getInstance().getTime();
                                    return ascending ? c1_startDate.compareTo(c2_startDate) : c2_startDate.compareTo(c1_startDate);
				}
                                if (column.equals("exDate"))
				{
                                    Date c1_exDate = null;
                                    if( c1.getExperimentExecutable().getExecutionEndDate() != null )
                                        c1_exDate = c1.getExperimentExecutable().getExecutionEndDate().getTime();
                                    Date c2_exDate = null;
                                    if( c2.getExperimentExecutable().getExecutionEndDate() != null )
                                        c2_exDate = c2.getExperimentExecutable().getExecutionEndDate().getTime();
                                    if (c1_exDate==null) c1_exDate=Calendar.getInstance().getTime();
                                    if (c2_exDate==null) c2_exDate=Calendar.getInstance().getTime();
                                    return ascending ? c1_exDate.compareTo(c2_exDate) : c2_exDate.compareTo(c1_exDate);
				}
                                if (column.equals("currentStage"))
				{
                                        if ((c1.getCurrentPhase() != null) && (c2.getCurrentPhase() != null)) {
                                            return ascending ? new Integer(c1.getCurrentPhaseIndex()).compareTo(new Integer(c2.getCurrentPhaseIndex())) 
                                                    : new Integer(c2.getCurrentPhaseIndex()).compareTo(new Integer(c1.getCurrentPhaseIndex()));
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
	      
          ExpBeanReqManager.putExperimentIntoSessionExperimentBean(selectedExperiment);
	      
	      // Abort and go to View page if this is an old experiment:
	      if( AdminManagerImpl.isExperimentDeprecated( selectedExperiment ) ) {
	          return "viewExp";
	      }
	      
	      //reinit the ontologyDnDBean
	      Manager.initOntologDnDBean();
	              
	      // go to edit page
	      return "editExp";
	    }
	    
	    public String exportMyExperimentAction() {
            Experiment selectedExperiment = (Experiment) this.getMyExp_data().getRowData();
	        DownloadManager dm = (DownloadManager)JSFUtil.getManagedObject("DownloadManager");
	        return dm.downloadExperiment( (ExperimentImpl)selectedExperiment );
	    }

        public String viewExperimentToApprove()
        {
        
          Experiment selectedExperiment = (Experiment) this.getToAppExp_data().getRowData();
          ExpBeanReqManager.putExperimentIntoSessionExperimentBean(selectedExperiment);
          
          // go to edit page
          return "viewExperimentExeManager";
        }
        
        public String viewExperimentToExecute()
        {
        
          Experiment selectedExperiment = (Experiment) this.getToExecExp_data().getRowData();
          ExpBeanReqManager.putExperimentIntoSessionExperimentBean(selectedExperiment);
                  
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

          ExpBeanReqManager.putExperimentIntoSessionExperimentBean(selectedExperiment);
                  
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

            ExpBeanReqManager.putExperimentIntoSessionExperimentBean(selectedExperiment);
                    
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
	      
          ExpBeanReqManager.putExperimentIntoSessionExperimentBean(selectedExperiment);
              
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
          
          //send email to exp contact to inform them of deletion
          PlanetsMailMessage mailer = new PlanetsMailMessage();
          mailer.setSender("noreply@planets-project.eu");
          mailer.setSubject("Testbed Experiment Deleted");
          mailer.setBody("Experiment "+expBean.getEname()+" deleted.");
          mailer.addRecipient(expBean.getEcontactemail());
          mailer.send();

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
        
        public void sendDeletionRequest() {
            try {
            	Experiment selectedExperiment = (Experiment) this.getMyExp_data().getRowData();
            	if (selectedExperiment != null){
	            	String expName = selectedExperiment.getExperimentSetup().getBasicProperties().getExperimentName();
	            	String contactName = selectedExperiment.getExperimentSetup().getBasicProperties().getContactName();
	            	String contactEmail = selectedExperiment.getExperimentSetup().getBasicProperties().getContactMail();
	            	String experimenter = selectedExperiment.getExperimentSetup().getBasicProperties().getExperimenter();
	            	//send email to admin
	            	String body = "Experiment deletion request received for experiment:\r\n";
	            	body += expName+"\r\n";
	            	body += "The contact for this experiment is "+contactName+" ("+contactEmail+")\r\n";
	            	//body += "Reason given for deletion: "+reason;
	                PlanetsMailMessage mailer = new PlanetsMailMessage();
	                mailer.setSender("noreply@planets-project.eu");
	                mailer.setSubject("Experiment deletion request: "+expName);
	                mailer.setBody(body);

	                User user = UserBean.getUser("admin");
	                mailer.addRecipient(user.getFullName() + "<" + user.getEmail() + ">");
	                mailer.send();
	                
	                //send email to user, explaining that they have *requested* deletion
	                body = "Experiment deletion request sent for experiment:\r\n";
	            	body += expName+"\r\n";
	            	body += "\r\nThe administrator will verify your request and delete the experiment if appropriate.";
	            	//body += "Reason given for deletion: "+reason;
	                mailer = new PlanetsMailMessage();
	                mailer.setSender("noreply@planets-project.eu");
	                mailer.setSubject("Experiment deletion request: "+expName);
	                mailer.setBody(body);

	                user = UserBean.getUser(experimenter);
	                mailer.addRecipient(user.getFullName() + "<" + user.getEmail() + ">");
	                mailer.send();
	                
	                log.info("Deletion request email sent successfully.");
            	}
            } catch( Exception e ) {
                log.info("Deletion email sending failed. Details: "+ e);
            }
        }

}
