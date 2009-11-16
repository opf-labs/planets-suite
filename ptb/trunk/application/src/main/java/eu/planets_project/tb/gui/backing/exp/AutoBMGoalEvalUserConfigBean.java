package eu.planets_project.tb.gui.backing.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.event.MethodExpressionValueChangeListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.ajax4jsf.component.html.HtmlAjaxSupport;
import org.richfaces.event.DropEvent;

import eu.planets_project.tb.api.model.ExperimentEvaluation;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoalsHandler;
import eu.planets_project.tb.api.model.eval.TBEvaluationTypes;
import eu.planets_project.tb.api.services.ServiceTemplateRegistry;
import eu.planets_project.tb.impl.model.benchmark.BenchmarkGoalsHandlerImpl;
import eu.planets_project.tb.impl.services.EvaluationTestbedServiceTemplateImpl;
import eu.planets_project.tb.impl.services.ServiceTemplateRegistryImpl;

/**
 * The gui backing bean for the user-defined auto-evaluation service settings
 * i.e. mapping of TB states (very good, etc.) and EvaluationService metric values
 * 
 * Please note to distinguish between 
 * a) all available metrics - this information is provided within a template (metric name must be unique)
 * b) the metric Bean used within this class for outputting information on the gui
 * c) the configured metric to evaluate the Testbed's experiment result: this info is part of a benchmark goal
 * @author lindleyA
 *
 */
public class AutoBMGoalEvalUserConfigBean{
	
	public final String MATH_EXP_GT = ">";
	public final String MATH_EXP_LT = "<";
	public final String MATH_EXP_EQ = "=";
	
	public String sBMGoalID;
	public EvaluationTestbedServiceTemplateImpl evalSerTemplate;
	private ServiceTemplateRegistry registry;
	private Map<String,String> mapMetricNameType = new HashMap<String,String>();
	
	
	public void initBean(String bmGoalID, String autoEvalSerUUID){
		this.sBMGoalID = bmGoalID;
		registry = ServiceTemplateRegistryImpl.getInstance();
		//load the EvaluationTBSerTemplate that contains the information about the available metrics for this goal
		//note: currently every BMGoal is only max. backed by one auto eval service but 1..n metrics contained in it
		evalSerTemplate = (EvaluationTestbedServiceTemplateImpl)registry.getServiceByID(autoEvalSerUUID);
		//populates the mapMetricNameType table
		this.getAllSupportedEvalMetrics();
	}

	/**
	 * Returns the ID of the goal which is currently being backed by the gui
	 * @return
	 */
	public String getBMGoalID(){
		return this.sBMGoalID;
	}

	public String getBMGoalName(){
		BenchmarkGoalsHandler bmGoalHandler = BenchmarkGoalsHandlerImpl.getInstance();
		return bmGoalHandler.getBenchmarkGoal(this.getBMGoalID()).getName();
	}
	
    public List<MetricBean> getAllSupportedEvalMetrics(){
    	List<MetricBean> lret = new ArrayList<MetricBean>();
    	if(evalSerTemplate!=null){
    		Map<String,List<Map<String,String>>> mapMetricInfo = evalSerTemplate.getAllAvailableMetricsForBMGoal(sBMGoalID);
    		if((mapMetricInfo!=null)&&(mapMetricInfo.size()>0)){
    			Iterator<String> itKeys = mapMetricInfo.keySet().iterator();
    			//iterate over all parameter names e.g. imageWidth
    			while(itKeys.hasNext()){
    				List<Map<String,String>> l = mapMetricInfo.get(itKeys.next());
    				for(Map<String,String> m : l){
	    				String sMname = m.get("metricName");
	    				String sMDescr = m.get("metricDescription");
	    				String sMretType = m.get("javaobjecttype");
	    				//create metricbean object and add into the return list
	    				MetricBean mb = new MetricBean(sMname,sMDescr);
	    				mb.setDescription(sMDescr);
	    				this.mapMetricNameType.put(sMname, sMretType);
	    				lret.add(mb);
    				}
    			}    			
    		}
    	}
    	return lret;
    }
    
    
    //this bean stores the information which metric is contained in the table 'very good'
    private List<MetricBean> lVeryGoodMT = new ArrayList<MetricBean>();
    private List<MetricBean> lGoodMT = new ArrayList<MetricBean>();
    private List<MetricBean> lBadMT = new ArrayList<MetricBean>();
    private List<MetricBean> lVeryBadMT = new ArrayList<MetricBean>();
    //the metric bean which was dragged on the gui for being configured. only one active at a time
    private MetricBean mbToConfigure = null;
    
    public String processDrop(DropEvent ev) {
		String sTableClassifier = (String)ev.getDropValue();
		FacesContext context = FacesContext.getCurrentInstance();
		//from the list of all available metrics (name in all available metrics must be unique)
		String sDroppedMetricName = context.getExternalContext().getRequestParameterMap().get("droppedMetricName").toString();
		String sType = this.mapMetricNameType.get(sDroppedMetricName);
		
		List<MetricBean> lMT = null;
		UIComponent uiParent = null;
		//for classification 'very good'
		if(sTableClassifier.equals("tableVeryGood")){
			lMT = lVeryGoodMT;
			uiParent = this.getComponent("pConfigVeryGood");
		}
		if(sTableClassifier.equals("tableGood")){
			lMT = lVeryGoodMT;
			uiParent = this.getComponent("pConfigGood");
		}
		if(sTableClassifier.equals("tableBad")){
			lMT = lBadMT;
			uiParent = this.getComponent("pConfigBad");
		}
		if(sTableClassifier.equals("tableVeryBad")){
			lMT = lVeryBadMT;
			uiParent = this.getComponent("pConfigVeryBad");
		}
		
		//check the rules if this metric may be added for the table
		if(helperMetricNameAddable(sDroppedMetricName,lMT)){
			//create a new (gui) metric bean. an auto-id is being created
			MetricBean m = new MetricBean(sDroppedMetricName,sType);
			//add this bean for being the element that's configured
			this.mbToConfigure = m;
			//create the used config UI components as selectors, dropdown boxes, etc.
			helperCreateUICompsForMetricConfig(m,uiParent);	
		}
		
		return "reload-page";
	}
    
    
    public void command_saveMetricConfiguration(ActionEvent e){
    	//first checks if all required information was properly set (e.g. value according to expected type, etc.)
    	if(!this.mbToConfigure.isInputOK()){
    		return;
    	}
    		   	
    	//saves the dropped and afterwards configured metricBean for the proper TB evaluation
    	//the table name the metric belongs to is provided as parameter
    	boolean bFound = false;
		String sPConfigPanel = null;
    	for(UIComponent comp:((List<UIComponent>)e.getComponent().getChildren())){
    		try{
    			UIParameter param = (UIParameter)comp;
    			if(param.getName().equals("pConfigPanel")){
    				sPConfigPanel = param.getValue().toString();
    			}
    		}
    		catch(Exception ex){}
    	}
    	//now add the bean in the table's list
    	if(sPConfigPanel!=null){
    		if(sPConfigPanel.equals("pConfigVeryGood")){
    			this.lVeryGoodMT.add(mbToConfigure);
    		}
    		if(sPConfigPanel.equals("pConfigGood")){
    			this.lGoodMT.add(mbToConfigure);
    		}
    		if(sPConfigPanel.equals("pConfigBad")){
    			this.lBadMT.add(mbToConfigure);
    		}
    		if(sPConfigPanel.equals("pConfigVeryBad")){
    			this.lVeryBadMT.add(mbToConfigure);
    		}
    		//finally remove the input elements
    		this.removeAllInputHelpersForMetricConfig();
    	}
    }
    
    /**
     * This helper fetches the pConfig'name' panels where the UIComponents
     * for creating a metric config are rendered, and removes all child elements
     * on them
     */
    private void removeAllInputHelpersForMetricConfig(){
    	List<UIComponent> lPanels = new ArrayList<UIComponent>();
    	lPanels.add(this.getComponent("pConfigVeryGood"));
    	lPanels.add(this.getComponent("pConfigGood"));
    	lPanels.add(this.getComponent("pConfigBad"));
    	lPanels.add(this.getComponent("pConfigVeryBad"));

    	
    	//iterate over all these panels and remove their child elements
    	for(UIComponent uiPanel : lPanels){
	    	if((uiPanel!=null)&&(uiPanel.getChildCount()>0)){
	    		List<UIComponent> lToRemove = new ArrayList<UIComponent>();
	    		for(UIComponent comp : ((List<UIComponent>)uiPanel.getChildren())){
	    			lToRemove.add(comp);
	    		}
	    		for(UIComponent comp : lToRemove){
	    			uiPanel.getChildren().remove(comp);
	    		}
	    	}
    	}
    }
    
    /**
     * Takes a parent UIComponent and adds all UIComponents which are required 
     * for configuring a metric. i.e. metric name, math expression, value, etc.
     * @param metric
     * @param parent
     */
    private void helperCreateUICompsForMetricConfig(MetricBean metric, UIComponent parent){
    	
    	//1)remove all existing child elements that may have been created previously from ALL pConfigVeryGood, pConfigGood, etc.
    	removeAllInputHelpersForMetricConfig();
    	
    	//2) now build the gui elements for building a metric configuration
    	//2a) Add an output text with the metric's name
    	HtmlOutputText mnameText = new HtmlOutputText();
    	mnameText.setId("metricName"+metric.getInternalID());
    	mnameText.setValue(metric.getName());
    	//add on parent
    	parent.getChildren().add(mnameText);
    	
    	//2b) Select a math expression
    	HtmlSelectOneMenu select2 = new HtmlSelectOneMenu();
		select2.setId("mathSelect"+metric.getInternalID());
		UISelectItems items2 = new UISelectItems();
		items2.setId("mathvals"+metric.getInternalID()); 
	    items2.setValue(metric.getAllAvailableTypes());
		Class[] parms2 = new Class[]{ValueChangeEvent.class};
        ExpressionFactory ef = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
        MethodExpression mb = ef.createMethodExpression(FacesContext.getCurrentInstance().getELContext(), 
                "#{AutoEvalSerUserConfigBean.processMathExprChange}", null, parms2);
        MethodExpressionValueChangeListener vcl = new MethodExpressionValueChangeListener(mb);
 		select2.addValueChangeListener(vcl);
		select2.getChildren().add(items2);
		select2.setImmediate(true);
 		
 		//place an ajax support on the selectonemenu field
 		HtmlAjaxSupport ajaxSupport2 = new HtmlAjaxSupport();
 		//Class[] parms = new Class[]{ActionEvent.class};
 		//ajaxSupport.setAction(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{AutoEvalSerUserConfigBean.sliderValue}", parms));
 		ajaxSupport2.setEvent("onchange");
 		//to add multiple ids for rerendering, separate them with a ","
 		ajaxSupport2.setReRender(select2.getId());
 		ajaxSupport2.setEventsQueue("foo");
 		select2.getFacets().put("a4jsupport2", ajaxSupport2);
 		
 		//add to parent
 		parent.getChildren().add(select2);
    	
    	//2c) For all input types except boolean values:
    	if(metric.isHtmlInputTextUsed()){
	    	//enter a boundary value for a specific added metric
	 		HtmlInputText inputText = new HtmlInputText();
	 		inputText.setId("metricBoundary"+metric.getInternalID());
	 		inputText.setValue(metric.getEvalBoundary());
	 		inputText.setSize(10);
	 		Class[] parms = new Class[]{ValueChangeEvent.class};
	        MethodExpression mb2 = ef.createMethodExpression(FacesContext.getCurrentInstance().getELContext(), 
	                "#{AutoEvalSerUserConfigBean.processMetricBoundaryValueChange}", null, parms);
	        MethodExpressionValueChangeListener vcl2 = new MethodExpressionValueChangeListener(mb2);
	 		inputText.addValueChangeListener(vcl2);
	 		inputText.setImmediate(true);
	 		
	 		//place an ajax support on the InputText field
	 		HtmlAjaxSupport ajaxSupport = new HtmlAjaxSupport();
	 		//Class[] parms = new Class[]{ActionEvent.class};
	 		//ajaxSupport.setAction(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{AutoEvalSerUserConfigBean.sliderValue}", parms));
	 		ajaxSupport.setEvent("onchange");
	 		//to add multiple ids for rerendering, separate them with a ","
	 		ajaxSupport.setReRender(inputText.getId());
	 		ajaxSupport.setEventsQueue("foo");
	 		inputText.getFacets().put("a4jsupport", ajaxSupport);
	 		
	 		//add to parent
	 		parent.getChildren().add(inputText);
    	}
    	else{
    		// add a drop-down box for boolean values
    		HtmlSelectOneMenu select = new HtmlSelectOneMenu();
    		select.setId("booleanSelect"+metric.getInternalID());
    		UISelectItems items = new UISelectItems();
    		items.setId("vals"+metric.getInternalID());
    		List<SelectItem> l = new ArrayList<SelectItem>();
    	        l.add(new SelectItem("true")); 
    	        l.add(new SelectItem("false")); 
    	    items.setValue(l);
    		Class[] parms = new Class[]{ValueChangeEvent.class};
            MethodExpression mb3 = ef.createMethodExpression(FacesContext.getCurrentInstance().getELContext(), 
                    "#{AutoEvalSerUserConfigBean.processMetricBoundaryValueChange}", null, parms);
            MethodExpressionValueChangeListener vcl3 = new MethodExpressionValueChangeListener(mb3);
            select.addValueChangeListener(vcl3);
    		select.getChildren().add(items);
    		select.setImmediate(true);
	 		
	 		//place an ajax support on the selectonemenu field
	 		HtmlAjaxSupport ajaxSupport = new HtmlAjaxSupport();
	 		//Class[] parms = new Class[]{ActionEvent.class};
	 		//ajaxSupport.setAction(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{AutoEvalSerUserConfigBean.sliderValue}", parms));
	 		ajaxSupport.setEvent("onchange");
	 		//to add multiple ids for rerendering, separate them with a ","
	 		ajaxSupport.setReRender(select.getId());
	 		ajaxSupport.setEventsQueue("foo");
	 		select.getFacets().put("a4jsupport", ajaxSupport);
	 		
	 		//add to parent
	 		parent.getChildren().add(select);
    	}
 		
    	//2d) finally the submit button for saving the configuration
 		HtmlCommandButton button_save = new HtmlCommandButton();
 		button_save.setId("buttonSave"+metric.getInternalID());
 		button_save.setValue("add config");
 		Class[] parms3 = new Class[]{ActionEvent.class};
        MethodExpression mb4 = ef.createMethodExpression(FacesContext.getCurrentInstance().getELContext(), 
                "#{AutoEvalSerUserConfigBean.command_saveMetricConfiguration}", null, parms3);
        MethodExpressionActionListener vcl4 = new MethodExpressionActionListener(mb4);
        button_save.addActionListener(vcl4);
 		UIParameter p = new UIParameter();
 		p.setId("param_save_button"+metric.getInternalID());
 		p.setName("pConfigPanel");
 		p.setValue(parent.getId());
 		button_save.getChildren().add(p);
 		
 		parent.getChildren().add(button_save);
 		
 		HtmlOutputText message = new HtmlOutputText();
 		message.setId("message"+metric.getInternalID());
 	    message.setStyle("color:red;");
 	    parent.getChildren().add(message);

 		
    }
    
    
    /**
     * Defines the rules for adding metrics to TB evaluations "for e.g. very good"
     * as e.g. max two metric may get added per table. currently no restrictions are applied
     * @param sMetricName
     * @param l
     * @return
     */
    public boolean helperMetricNameAddable(String sMetricName,List<MetricBean> l){
    	
    	/*//1. check that only one element is max. added
    	if(!(l.size()==0)){
    		return false;
    	}
    	//2.check if already was added once
    	Iterator<MetricBean> it = l.iterator();
    	boolean bFound = false;
    	while(it.hasNext()){
    		MetricBean m = it.next();
    		if(m.getName().equals(sMetricName)){
    			bFound = true;
    		}
    	}
    	return bFound;*/
    	return true;
    }
    
    public List<MetricBean> getMetricTableVeryGood(){
    	return this.lVeryGoodMT;
    }
    
    public List<MetricBean> getMetricTableGood(){
    	return this.lGoodMT;
    }
    
    public List<MetricBean> getMetricTableBad(){
    	return this.lBadMT;
    }
    
    public List<MetricBean> getMetricTableVeryBad(){
    	return this.lVeryBadMT;
    }
    
    /**
     * A method for exporting the beans data into the model's backend classes
     * @param type
     * @return
     */
    public List<MetricBean> getMetricConfigFor(TBEvaluationTypes evalType){
    	List<MetricBean> metricConfig = null;
    	if(evalType.name().equals(TBEvaluationTypes.VERYGOOD.name()))
			metricConfig = this.getMetricTableVeryGood();
		if(evalType.name().equals(TBEvaluationTypes.GOOD.name()))
			metricConfig = this.getMetricTableGood();
		if(evalType.name().equals(TBEvaluationTypes.BAD.name()))
			metricConfig = this.getMetricTableBad();
		if(evalType.name().equals(TBEvaluationTypes.VERYBAD.name()))
			metricConfig = this.getMetricTableVeryBad();
		
		return metricConfig;
    }
    
    public void command_removeSelMetric(){
    	FacesContext context = FacesContext.getCurrentInstance();
		Object o1 = context.getExternalContext().getRequestParameterMap().get("removeMetricInternalID");
		Object o2 = context.getExternalContext().getRequestParameterMap().get("table");
		
		if((o1!=null)&&(o2!=null)){
			String removeMetricID = o1.toString();
			String fromTable = o2.toString();
			
			List<MetricBean> dataTable = null;
			if(fromTable.equals("table_verygood")){
				dataTable = this.lVeryGoodMT;
			}
			if(fromTable.equals("table_good")){
				dataTable = this.lGoodMT;
			}
			if(fromTable.equals("table_bad")){
				dataTable = this.lBadMT;
			}
			if(fromTable.equals("table_verybad")){
				dataTable = this.lVeryBadMT;
			}
			//check from which table to remove the data
			MetricBean del =null;
			Iterator<MetricBean> itTableData = dataTable.iterator();
				while(itTableData.hasNext()){
					MetricBean mb = itTableData.next();
					if(mb.getInternalID().equals(removeMetricID)){
						del = mb;
					}
				}
			if(del!=null)
				dataTable.remove(del);
		}
    }
    
    public String getLabelTBEvalVeryGood(){
    	return ExperimentEvaluation.EVALUATION_VALUE_VERY_GOOD;
    }
    
    public String getLabelTBEvalGood(){
    	return ExperimentEvaluation.EVALUATION_VALUE_GOOD;
    }
    
    public String getLabelTBEvalBad(){
    	return ExperimentEvaluation.EVALUATION_VALUE_BAD;
    }
    
    public String getLabelTBEvalVeryBad(){
    	return ExperimentEvaluation.EVALUATION_VALUE_VERY_BAD;
    }
    
    
    /**
     * Processes the changes of an value change event of an added metric value
     * e.g. intDiff < 35
     */
    public void processMetricBoundaryValueChange(ValueChangeEvent vce){
    	//there may only be one config_panel active at a time
    	this.mbToConfigure.setEvalBOundary(vce.getNewValue().toString());
    	//check if the input was what we were expecting
    	if(!this.mbToConfigure.isInputOK()){
    		//create an error message
    		HtmlOutputText message = (HtmlOutputText)this.getComponent("message"+this.mbToConfigure.getInternalID());
    		message.setValue("input '"+vce.getNewValue()+ "'not of type "+this.mbToConfigure.getType()+"!");
    	}
    	else{
    		HtmlOutputText message = (HtmlOutputText)this.getComponent("message"+this.mbToConfigure.getInternalID());
    		message.setValue("");
    	}
    }
    
    /**
     * Processes operation changes for MathExpressions as ">","<" and "="
     * @param vce
     */
    public void processMathExprChange(ValueChangeEvent vce){
    	//String compID = vce.getComponent().getClientId(FacesContext.getCurrentInstance());
    	this.mbToConfigure.setMathExpr(vce.getNewValue().toString());
    }
    
    
	/**
	 * Get the component from the JSF view model - it's id is registered withinin the page
	 * @return
	 */
	private UIComponent getComponent(String sID){

			FacesContext facesContext = FacesContext.getCurrentInstance();
			
			Iterator<UIComponent> it = facesContext.getViewRoot().getChildren().iterator();
			UIComponent returnComp = null;
			
			while(it.hasNext()){
				UIComponent guiComponent = it.next().findComponent(sID);
				if(guiComponent!=null){
					returnComp = guiComponent;
				}
			}
			
			//changes on the object are directly reflected within the GUI
			return returnComp;
	  }

    
/**
 * 
 * Internal class for capturing all values related to a single MetricBean object
 * which is used (and dynamically created) in the GUI for defining a single
 * autoEvaluation setting.
 * @author lindleyA
 *
 */	
public class MetricBean{
	
	private String sName="";
	private String sJavaType="";
	private String sMathExpr="";
	private String sEvalBoundary="";
	private String sInternalID="";
	private String sDescription="There is currently no description available for the selected item";
	
	public MetricBean(String name, String javatype){
		if(name!=null)
			this.setName(name);
		if(javatype!=null)
			this.setType(javatype);
		
		createInternalID();
	}
	
	public String getInternalID(){
		return this.sInternalID;
	}
	
	private void createInternalID(){
		java.util.Random random = new java.util.Random();
		this.sInternalID = random.nextInt()+"";
	}
	
	public String getName(){
		return sName;
	}
	public void setName(String name){
		this.sName = name;
	}
	
	/**
	 * The java type of expected input.
	 * e.g. java.lang.Integer
	 * @return
	 */
	public String getType(){
		return this.sJavaType;
	}
	public void setType(String type){
		this.sJavaType = type;
	}
	
	public List<SelectItem> getAllAvailableTypes(){
		List<SelectItem> lTypes = new ArrayList<SelectItem>();
		//this is added for all types
		lTypes.add(new SelectItem(AutoBMGoalEvalUserConfigBean.this.MATH_EXP_EQ,"="));
		
		//for image types additionally lt and gt comparators are added
		if((this.getType().equals("java.lang.Integer"))||
		   (this.getType().equals("java.lang.Long"))||
		   (this.getType().equals("java.lang.Double"))||
		   (this.getType().equals("java.lang.Float"))){
			
				lTypes.add(new SelectItem(AutoBMGoalEvalUserConfigBean.this.MATH_EXP_LT,"<"));
				lTypes.add(new SelectItem(AutoBMGoalEvalUserConfigBean.this.MATH_EXP_GT,">"));
		}
		
		return lTypes;
	}
	
	/**
	 * The math expression as gt, lt, eq
	 * @return
	 */
	public String getMathExpr(){
		return this.sMathExpr;
	}
	
	public void setMathExpr(String expr){
		this.sMathExpr = expr;
	}
	
	/**
	 * The specified boundary value for evaluating the success
	 * e.g. 10 (=extracted autoEvalValue) < (=getMathExpr) 20 (=getEvalBoundary)
	 * @return
	 */
	public String getEvalBoundary(){
		return this.sEvalBoundary;
	}

	/**
	 * @param boundary
	 * value must be of it's expected type
	 * e.g. "xyz" for java.lang.Integer
	 */
	public void setEvalBOundary(String boundary){
		//check if the boundary value matches the required type (e.g. 10 matches java.lang.Integer)
		if(checkType(boundary,this.getType())){
			this.sEvalBoundary = boundary;
		}
	}
	
	public void setDescription(String description){
		this.sDescription = description;
	}
	
	public String getDescription(){
		return this.sDescription;
	}
	
	
	/**
	 * Checks if all required parameters have been set properly and if the input corresponds to the 
	 * expected type
	 * @return
	 */
	public boolean isInputOK(){
		if((this.getMathExpr()==null)||(this.getMathExpr().equals("")))
			return false;
		if((this.getEvalBoundary()==null)||(this.getEvalBoundary().equals("")))
			return false;
		return checkType(this.getEvalBoundary(),this.getType());
	}
	
	/**
	 * Validates if the information provided as input, corresponds to the specified javaType
	 * java.lang.Integer, java.lang.Long, java.lang.Float, java.lang.String, java.Boolean
	 * @return
	 */
	private boolean checkType(String value, String sType){
		boolean bRet = false;
		//check if the type matches java.lang.Integer
		if(Integer.class.getCanonicalName().equals(sType)){
			try {
				Integer.valueOf(value);
				bRet = true;
			} catch (NumberFormatException e) {}
		}
			
		//check if the value input matches the supported type: java.lang.Long
		if(Long.class.getCanonicalName().equals(sType)){
			try {
				Long.valueOf(value);
				bRet = true;
			} catch (NumberFormatException e) {}
		}
			
		//check if the value input matches the supported type: java.lang.Float
		if(Float.class.getCanonicalName().equals(sType)){
			try {
				Float.valueOf(value);
				bRet = true;
			} catch (NumberFormatException e) {}
		}
		
		//check if the value input matches the supported type: java.lang.Double
		if(Double.class.getCanonicalName().equals(sType)){
			try {
				Double.valueOf(value);
				bRet = true;
			} catch (NumberFormatException e) {}
		}
		
		//check if the value input matches the supported type: java.lang.String
		if(String.class.getCanonicalName().equals(sType))
			bRet = true;
		
		//check if the value input matches the supported type: java.lang.Boolean
		if(Boolean.class.getCanonicalName().equals(sType)){
			boolean b = Boolean.valueOf(value);
			if(value.equalsIgnoreCase(b+"")){
				bRet = true;
			}
		}
		
		return bRet;
	}
    
	
    public boolean isHtmlInputTextUsed(){
    	if((this.getType().equals("java.lang.Integer"))||
    	   (this.getType().equals("java.lang.Long"))||
    	   (this.getType().equals("java.lang.Float"))||
    	   (this.getType().equals("java.lang.Double"))||
    	   (this.getType().equals("java.lang.String"))){
    		//for these add an HtmlInputText UI item is added
    		return true;
    	}
    	
    	if((this.getType().equals("java.lang.Boolean"))){
    		//for these add an SelectOne UI item is added
    		return false;
    	}
    	return true;
    }

}

}
