/**
 * 
 */
package eu.planets_project.tb.gui.backing.exp;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;
import eu.planets_project.tb.gui.util.JSFUtil;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ResultsForDigitalObjectBean {
    private PlanetsLogger log = PlanetsLogger.getLogger(ResultsForDigitalObjectBean.class, "testbed-log4j.xml");
    
    private String digitalObject;
    
    private String downloadURL;
    
    private String digitalObjectName;
    
    private List<ExecutionRecordImpl> executionRecords = new Vector<ExecutionRecordImpl>();
    
    /**
     * @return the digitalObject
     */
    public String getDigitalObject() {
        return digitalObject;
    }

    /**
     * @param digitalObject the digitalObject to set
     */
    public void setDigitalObject(String digitalObject) {
        this.digitalObject = digitalObject;
    }

    /**
     * @return the downloadURL
     */
    public String getDownloadURL() {
        return downloadURL;
    }

    /**
     * @param downloadURL the downloadURL to set
     */
    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    /**
     * @return the digitalObjectName
     */
    public String getDigitalObjectName() {
        return digitalObjectName;
    }

    /**
     * @param digitalObjectName the digitalObjectName to set
     */
    public void setDigitalObjectName(String digitalObjectName) {
        this.digitalObjectName = digitalObjectName;
    }

    /**
     * @return the executionRecords
     */
    public List<ExecutionRecordImpl> getExecutionRecords() {
        return executionRecords;
    }

    /**
     * @param executionRecords the executionRecords to set
     */
    public void setExecutionRecords(List<ExecutionRecordImpl> executionRecords) {
        this.executionRecords = executionRecords;
    }
    
    /**
     * @return
     */
    public String getFormatSummary() {
        ExpTypeIdentify eti = (ExpTypeIdentify)JSFUtil.getManagedObject("ExpTypeIdentify");
        
        if( this.getExecutionRecords().size() == 0 ) return "unknown";
        if( this.getExecutionRecords().get(0).getStages().size() == 0 ) return "unknown";
        
        for( MeasurementRecordImpl m : this.getExecutionRecords().get(0).getStages().get(0).getMeasurements() ) {
            if( m.getIdentifier().equals(TecRegMockup.URIDigitalObjectPropertyRoot+"basic/format")) {
                try {
                    Format f = eti.fr.getFormatForURI(new URI(m.getValue()));
                    if( f.getExtensions() != null && f.getExtensions().size() > 0 ) {
                        String fs = ""; // Use the (1st) longest extension:
                        for( String ext : f.getExtensions() ) {
                            if( ext.length() > fs.length() ) fs = ext;
                        }
                        fs = fs.toUpperCase();
                        if( f.getVersion() != null ) fs += " v."+f.getVersion();
                        fs += " - "+f.getSummary();
                        return fs;
                    } else {
                        return f.getSummaryAndVersion();
                    }
                } catch (URISyntaxException e) {
                    log.error("Could not understand format URI: "+m.getValue());
                }
            }
        }
        return "unknown";
    }

}
