/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.tb.gui.backing.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DataRegistry;
import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
import eu.planets_project.services.characterise.Characterise;
import eu.planets_project.services.characterise.CharacteriseResult;
import eu.planets_project.services.compare.Compare;
import eu.planets_project.services.compare.CompareProperties;
import eu.planets_project.services.compare.CompareResult;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.gui.backing.exp.ExperimentInspector;
import eu.planets_project.tb.gui.backing.exp.MeasuredComparisonEventBean;
import eu.planets_project.tb.gui.backing.exp.MeasurementBean;
import eu.planets_project.tb.gui.backing.exp.MeasurementEventBean;
import eu.planets_project.tb.gui.backing.exp.ResultsForDigitalObjectBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementAgent;
import eu.planets_project.tb.impl.model.measure.MeasurementEventImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementTarget;
import eu.planets_project.tb.impl.model.measure.MeasurementAgent.AgentType;
import eu.planets_project.tb.impl.model.measure.MeasurementTarget.TargetType;
import eu.planets_project.tb.impl.services.mockups.workflow.IdentifyWorkflow;
import eu.planets_project.tb.impl.services.wrappers.CharacteriseWrapper;
import eu.planets_project.tb.impl.services.wrappers.ComparePropertiesWrapper;
import eu.planets_project.tb.impl.services.wrappers.CompareWrapper;
import eu.planets_project.tb.impl.services.wrappers.IdentifyWrapper;

/**
 * @author AnJackson
 *
 */
public class DigitalObjectCompare {
    
    static private Log log = LogFactory.getLog(DigitalObjectCompare.class);
    
    // The data sources are managed here:
    DataRegistry dataRegistry = DataRegistryFactory.getDataRegistry();
    
    private String dobUri1;
    private String dobUri2;


    /**
     * @return the dob1
     */
    public String getDobUri1() {
        return dobUri1;
    }

    /**
     * @param dobUri the dobUri to set
     */
    public void setDobUri1(String dobUri1) {
        dobUri1 = DigitalObjectInspector.uriEncoder(dobUri1).toASCIIString();
        this.dobUri1 = dobUri1;
    }
    
    /**
     * @return the dob1
     */
    public String getDobUri2() {
        return dobUri2;
    }

    /**
     * @param dobUri the dobUri to set
     */
    public void setDobUri2(String dobUri2) {
        dobUri2 = DigitalObjectInspector.uriEncoder(dobUri2).toASCIIString();
        this.dobUri2 = dobUri2;
    }
    
    /**
     * @param dobUri
     * @return
     */
    static public DigitalObjectTreeNode lookupDob( String dobUri ) {
        if( dobUri == null ) return null;
        // Create as a URI:
        URI item;
        try {
            item = new URI(dobUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        // Lookup and return:
        DigitalObjectTreeNode itemNode = new DigitalObjectTreeNode(item, DataRegistryFactory.getDataRegistry() );
        return itemNode;
    }
    
    public DigitalObjectTreeNode getDob1() {
        return lookupDob(this.dobUri1);
    }
    
    public DigitalObjectTreeNode getDob2() {
        return lookupDob(this.dobUri2);
    }
    
    /* -------------------- Additional code for deeper inspection --------------------- */

    /** */
    private String compareService;
    private ServiceReport compareServiceReport;
    private String compareServiceException;
    private String compareServiceStackTrace;
    private MeasurementEventImpl me;

    /**
     * @return the compareService
     */
    public String getCompareService() {
        return compareService;
    }

    /**
     * @param compareService the compareService to set
     */
    public void setCompareService(String compareService) {
        this.compareService = compareService;
    }
    
    public void runCompareService() {
        log.info("Looking for properties using: "+this.getCompareService());
        
        // Reset:
        this.compareServiceReport = null;
        this.compareServiceException = null;
        this.compareServiceStackTrace = null;
        
        // Return nothing if no service is selected:
        if( this.getCompareService() == null ) return;
        
        // Run the service:
        try {
            URL surl = new URL(this.getCompareService());
            
            Map<URL, ServiceDescription> compareServices = this.getCompareServices();
            ServiceDescription sd = compareServices.get(surl);
            
            if( sd.getType().equals(Compare.class.getCanonicalName()) ) {

                Compare chr = new CompareWrapper(surl);
                CompareResult cr = chr.compare( this.getDob1().getDob(), this.getDob2().getDob(), null);
                this.compareServiceReport = cr.getReport();

                me = this.createMeasurementEvent();
                if( me != null ) {
                    me.setAgent(new MeasurementAgent(chr.describe()));
                    me.setDate(Calendar.getInstance());
                    for( Property p : cr.getProperties() ) {
                        MeasurementImpl m = new MeasurementImpl(me,p);
                        m.setTarget( new MeasurementTarget() );
                        m.getTarget().setType(TargetType.DIGITAL_OBJECT_PAIR);
                        m.getTarget().getDigitalObjects().add( this.getDobUri1() );
                        m.getTarget().getDigitalObjects().add( this.getDobUri2() );
                        me.addMeasurement(m);
                    }
                    DigitalObjectCompare.persistExperiment();
                }
                
            } else if(sd.getType().equals(Identify.class.getCanonicalName())) {
                Identify idf = new IdentifyWrapper(surl);
                IdentifyResult ir1 = idf.identify( this.getDob1().getDob(), null);
                this.compareServiceReport = ir1.getReport();
                IdentifyResult ir2 = idf.identify( this.getDob2().getDob(), null);
                this.compareServiceReport = ir2.getReport();
                
                me = this.createMeasurementEvent();
                if( me != null ) {
                    me.setAgent(new MeasurementAgent(idf.describe()));
                    me.setDate(Calendar.getInstance());
                    this.recordIdentifyMeasurement(me, ir1, this.getDobUri1() );
                    this.recordIdentifyMeasurement(me, ir2, this.getDobUri2() );
                    
                    DigitalObjectCompare.persistExperiment();
                }
                
            } else if(sd.getType().equals(Characterise.class.getCanonicalName())) {
                Characterise chr = new CharacteriseWrapper( surl );
                CharacteriseResult cr1 = chr.characterise( this.getDob1().getDob(), null);
                this.compareServiceReport = cr1.getReport();
                CharacteriseResult cr2 = chr.characterise( this.getDob2().getDob(), null);
                this.compareServiceReport = cr2.getReport();
                
                me = this.createMeasurementEvent();
                if( me != null ) {
                    me.setAgent(new MeasurementAgent(chr.describe()));
                    me.setDate(Calendar.getInstance());
                    this.recordPropertyMeasurements(me, cr1.getProperties(), this.getDobUri1() );
                    this.recordPropertyMeasurements(me, cr2.getProperties(), this.getDobUri2() );
                    
                    DigitalObjectCompare.persistExperiment();
                }
                
            } else {
                this.compareServiceException = "ERROR: Do not know how to invoke this service!";
                log.error("Could not invoke service: "+this.getCompareService());
            }
            
            return;
        } catch( Exception e ) {
            log.error("FAILED! "+e);
            this.compareServiceException = e.toString();
            
            this.compareServiceStackTrace = this.stackTraceToString(e);
            e.printStackTrace();
            
            return;
        }
    }
    
    private void recordIdentifyMeasurement( MeasurementEventImpl me, IdentifyResult ir, String dob ) {
        for( URI fmt : ir.getTypes() ) {
            MeasurementImpl m = new MeasurementImpl(IdentifyWorkflow.MEASURE_IDENTIFY_FORMAT);
            m.setValue(fmt.toASCIIString());
            m.setTarget( new MeasurementTarget() );
            m.getTarget().setType(TargetType.DIGITAL_OBJECT);
            m.getTarget().getDigitalObjects().add( dob );
            me.addMeasurement(m);

        }
        // Also record the method:
        if( ir.getMethod() != null ) {
            MeasurementImpl m = new MeasurementImpl(IdentifyWorkflow.MEASURE_IDENTIFY_METHOD);
            m.setValue(ir.getMethod().name());
            m.setTarget( new MeasurementTarget() );
            m.getTarget().setType(TargetType.DIGITAL_OBJECT);
            m.getTarget().getDigitalObjects().add( dob );
            me.addMeasurement(m);
        }
    }
    
    private void recordPropertyMeasurements( MeasurementEventImpl me, List<Property> props, String dob ) {
        for( Property p : props ) {
            MeasurementImpl m = new MeasurementImpl(me,p);
            m.setTarget( new MeasurementTarget() );
            m.getTarget().setType(TargetType.DIGITAL_OBJECT);
            m.getTarget().getDigitalObjects().add(dob);
            me.addMeasurement(m);
        }
    }
    
    /**
     * @return
     */
    public ServiceReport getCompareServiceReport() {
        return compareServiceReport;
    }

    /**
     * @return the compareServiceException
     */
    public String getCompareServiceException() {
        return compareServiceException;
    }

    /**
     * @return the compareServiceStackTrace
     */
    public String getCompareServiceStackTrace() {
        return compareServiceStackTrace;
    }

    /**
     * @return
     */
    public MeasurementEventImpl getMeasurementEvent() {
        return this.me;
    }

    /**
     * @return
     */
    public List<SelectItem> getCompareServiceList() {
        log.info("IN: getCompareServiceList");
/*
        String input = this.getInputFormat();
        if( ! this.isInputSet() ) input = null;
        String output = this.getOutputFormat();
        if( ! this.isOutputSet() ) output = null;
*/
        List<ServiceDescription> sdl = new ArrayList<ServiceDescription>(this.getCompareServices().values());

        return ServiceBrowser.mapServicesToSelectList( sdl);
    }
    
    private Map<URL,ServiceDescription> getCompareServices() {
        Map<URL,ServiceDescription> map = new HashMap<URL,ServiceDescription>();
        
        // Find all services:
        ServiceBrowser sb = (ServiceBrowser)JSFUtil.getManagedObject("ServiceBrowser");
        List<ServiceDescription> sdl = sb.getCompareServices();
        sdl.addAll(sb.getCharacteriseServices());
        sdl.addAll(sb.getIdentifyServices());

        // Create map:
        for( ServiceDescription sd : sdl ) {
            map.put(sd.getEndpoint(), sd);
        }
        
        return map;
    }
    
    /* ------------------------------------------ */

    protected String stackTraceToString( Exception e ) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    
    /**
     * Truncates the property values at a max. fixed length of 500 chars
     * @return
     */
    private static List<Property> truncatePropertyValues(List<Property> in){
    	List<Property> ret = new ArrayList<Property>();
    	 //iterate over the properties and truncate when more than 500 chars 
        if((in==null)||(in.size()<=0)){
        	return ret;
        }
    	for(Property p : in){
        	if(p.getValue().length()>500){
        		//add a truncated value String
        		ret.add(new Property(p.getUri(),p.getName(),p.getValue().substring(0, 500)+"[..]"));
        	}else{
        		//add the original result
        		ret.add(p);
        	}
        }
    	return ret;
    }
    
    /**
     * Get any stored measurements:
     * 
     * @return
     */
    public List<MeasurementEventBean> getExperimentMeasurements() {
        List<MeasurementEventBean> ms = new ArrayList<MeasurementEventBean>();
        ResultsForDigitalObjectBean res = new ResultsForDigitalObjectBean(this.getDobUri1());
        if( res == null || res.getExecutionRecord() == null ) {
            if( this.me != null ) {
                log.info("Pulling getExperimentMeasurements from the temporary space.");
                MeasuredComparisonEventBean mb = new MeasuredComparisonEventBean( this.me, this.getDobUri1(), this.getDobUri2() );
                ms.add(mb);
            }
            log.info("Got getExperimentMeasurements "+ms.size());
            return ms;
        }
        // Otherwise, pull from DB:
        log.info("Pulling getExperimentMeasurements from the DB.");
        int i = 0;
        Set<MeasurementEventImpl> measurementEvents = res.getExecutionRecord().getMeasurementEvents();
        List<MeasurementEventImpl> mevl = new ArrayList<MeasurementEventImpl>(measurementEvents);
        Collections.sort(mevl, Collections.reverseOrder());
        for( MeasurementEventImpl me : mevl ) {
            if( me.getMeasurements() != null ) {
                MeasuredComparisonEventBean mb = new MeasuredComparisonEventBean( me, this.getDobUri1(), this.getDobUri2() );
                mb.setOdd((i%2 == 0));
                ms.add( mb );
            }
            i++;
        }
        log.info("Got getExperimentMeasurements "+ms.size());
        return ms;
    }
    
    private MeasurementEventImpl createMeasurementEvent() {
        ResultsForDigitalObjectBean res = new ResultsForDigitalObjectBean(this.getDobUri1());
        // If there is no experiment, return a non-DB event:
        if( res == null ) return new MeasurementEventImpl((ExecutionRecordImpl)null);
        if( res.getExecutionRecord() == null ) return new MeasurementEventImpl((ExecutionRecordImpl)null);
        
        // Otherwise, create an event that is attached to the experiment:
        MeasurementEventImpl me = new MeasurementEventImpl(res.getExecutionRecord());
        res.getExecutionRecord().getMeasurementEvents().add(me);
        return me;
    }

    /**
     * Persist any changes
     */
    public static void persistExperiment() {
        ExperimentInspector ei = (ExperimentInspector)JSFUtil.getManagedObject("ExperimentInspector");
        ExperimentBean expBean = ei.getExperimentBean();
        if(expBean != null ) expBean.updateExperiment();
    }
    
    /**
     * 
     */
    public void redirectBackToCompare() {
        ExperimentBean expBean = (ExperimentBean)JSFUtil.getManagedObject("ExperimentBean");
        if( expBean == null ) {
            JSFUtil.redirect("/reader/dob_compare.faces?eid=dobUri1="+this.getDobUri1()+"&dobUri2="+this.getDobUri2());
        }
        JSFUtil.redirect("/exp/dob_compare.faces?eid="+expBean.getExperiment().getEntityID()+"&dobUri1="+this.getDobUri1()+"&dobUri2="+this.getDobUri2());
   }
    
}
