package eu.planets_project.tb.gui.backing;


import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;

import eu.planets_project.tb.api.TestbedManager;
import eu.planets_project.tb.gui.UserBean;
import eu.planets_project.tb.gui.util.JSFUtil;

import eu.planets_project.tb.api.model.*;
import eu.planets_project.tb.impl.model.*;

public class CreateExp {
    
    private HtmlInputText ename;
    private HtmlInputTextarea esummary;
    private HtmlInputText econtact;
    private HtmlInputTextarea epurpose;
    private HtmlInputTextarea efocus;
    private HtmlInputTextarea erefs;
    private HtmlInputTextarea escope;
    private HtmlInputTextarea eapproach;
    private HtmlInputTextarea econsiderations;
    
    
    
    public CreateExp() {
    }
    
    public String CreateExpAction(){
        
        // Get userid info from managed bean
        UserBean currentUser = (UserBean) JSFUtil.getManagedObject("UserBean");
        
        // Create new Experiment
        Experiment newExp = new ExperimentImpl();
        BasicProperties props = new BasicPropertiesImpl();
        props.setExperimentName(ename.getValue().toString());
        // set current User as experimenter
        props.setExperimenter(currentUser.getUserid());
        props.setSummary(esummary.getValue().toString());
        props.setConsiderations(econsiderations.getValue().toString());
        props.setPurpose(epurpose.getValue().toString());
        props.setSpecificFocus(efocus.getValue().toString());
        props.setScope(escope.getValue().toString());
        
        ExperimentSetup expSetup = new ExperimentSetupImpl();
        expSetup.setBasicProperties(props);
        newExp.setExperimentSetup(expSetup);
        
        
        TestbedManager testbedMan = (TestbedManager) JSFUtil.getManagedObject("TestbedManager");
        testbedMan.registerExperiment(newExp);
        
        
        return "success";
    }
    
    
    public void setEname(HtmlInputText ename) {
        this.ename = ename;
    }
    
    public HtmlInputText getEname() {
        return ename;
    }
    
    public void setEsummary(HtmlInputTextarea esummary) {
        this.esummary = esummary;
    }
    
    public HtmlInputTextarea getEsummary() {
        return esummary;
    }
    
    public HtmlInputText getEcontact() {
        return econtact;
    }
    
    public void setEcontact(HtmlInputText econtact) {
        this.econtact = econtact;
    }
    
    public HtmlInputTextarea getEpurpose() {
        return epurpose;
    }
    
    public void setEpurpose(HtmlInputTextarea epurpose) {
        this.epurpose = epurpose;
    }
    
    public HtmlInputTextarea getEfocus() {
        return efocus;
    }
    
    public void setEfocus(HtmlInputTextarea efocus) {
        this.efocus = efocus;
    }
    
    public HtmlInputTextarea getErefs() {
        return erefs;
    }
    
    public void setErefs(HtmlInputTextarea erefs) {
        this.erefs = erefs;
    }
    
    public HtmlInputTextarea getEscope() {
        return escope;
    }
    
    public void setEscope(HtmlInputTextarea escope) {
        this.escope = escope;
    }
    
    public HtmlInputTextarea getEapproach() {
        return eapproach;
    }
    
    public void setEapproach(HtmlInputTextarea eapproach) {
        this.eapproach = eapproach;
    }
    
    public HtmlInputTextarea getEconsiderations() {
        return econsiderations;
    }
    
    public void setEconsiderations(HtmlInputTextarea econsiderations) {
        this.econsiderations = econsiderations;
    }
    
    
    
    
    
    
}
