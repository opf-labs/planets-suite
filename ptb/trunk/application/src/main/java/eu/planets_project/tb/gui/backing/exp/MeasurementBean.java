
/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.gui.backing.ExperimentBean;
import eu.planets_project.tb.gui.backing.service.FormatBean;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.TestbedManagerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementAgent;
import eu.planets_project.tb.impl.model.measure.MeasurementEventImpl;
import eu.planets_project.tb.impl.model.measure.MeasurementImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * Adds some extra features to the MeasurmentImpl, of use when rendering.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class MeasurementBean extends MeasurementImpl {
    /** */
    private static final Log log = LogFactory.getLog(MeasurementBean.class);

    /** */
    private static final long serialVersionUID = -9007264139415572106L;
    
    /** */
    protected boolean selected = true;
    
    /** */
    protected boolean odd = false;
    
    /** Allow the 'stage' to be copied here, for easier presentation */
    private String stage;
    
    /** Copy of the measurement DB record */
    private long measurementRecordId = -1;
    
    /** The Measurement Event */
    MeasurementEventImpl event = null;
    
    /**
     * @param me
     * @param m
     */
    public MeasurementBean(MeasurementEventImpl me, MeasurementImpl m) {
        super(me, m);
        this.event = me;
        this.measurementRecordId = m.getId();
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the stage
     */
    public String getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(String stage) {
        this.stage = stage;
    }
    
    /**
     * @return the event
     */
    public MeasurementEventImpl getEvent() {
        return event;
    }

    /**
     * @return
     */
    public MeasurementAgent getAgent() {
        if( this.getEvent() != null ) {
            return getEvent().getAgent();
        }
        return null;
    }
    
    /**
     * @return
     */
    public Calendar getDate() {
        if( getEvent() != null ) return getEvent().getDate();
        return null;
    }
    
    /**
     * @return the odd
     */
    public boolean isOdd() {
        return odd;
    }

    /**
     * @param odd the odd to set
     */
    public void setOdd(boolean odd) {
        this.odd = odd;
    }
    
    /* (non-Javadoc)
     * @see eu.planets_project.tb.impl.model.measure.MeasurementImpl#getName()
     */
    @Override
    public String getName() {
        String name = super.getName();
        if( name == null || "".equals(name)) name = "[unnamed]";
        return name;
    }

    public String getValueString() {
        String value = this.getValue();
        if( value != null && value.length() > 200 ) value = value.substring(0, 200) + " ...";
        return value;
    }
    
    public boolean isValueEmpty() {
        if( this.getValue() == null || "".equals( this.getValue().trim() ))
            return true;
        return false;
    }

    public FormatBean getAsFormat() {
        return MeasurementImpl.getFormat( this.getProperty() );
    }

    public boolean isFormatProperty() {
        return MeasurementImpl.isFormatProperty( this.getProperty() );
    }
    

    /** ACTIONS **/

    /**
     * 
     */
    public void deleteMeasurement() {
        if( this.getEvent() != null && this.measurementRecordId != -1 ) {
            TestbedManagerImpl tbm = (TestbedManagerImpl) JSFUtil.getManagedObject("TestbedManager");
            ExperimentPersistencyRemote db = tbm.getExperimentPersistencyRemote();
            // Remove the record.
            MeasurementImpl m = null;
            log.info("For measurment "+this.measurementRecordId );
            for( MeasurementImpl fm : getEvent().getMeasurements() ) {
                log.info("Looking at Measurement "+fm.getId());
                if( fm.getId() == this.measurementRecordId ) 
                    m = fm;
            }
            this.getEvent().getMeasurements().remove(m);
            db.removeMeasurement(m);
            // Now update experiment.
            ExperimentInspector.persistExperiment();
        }
    }
    
}
