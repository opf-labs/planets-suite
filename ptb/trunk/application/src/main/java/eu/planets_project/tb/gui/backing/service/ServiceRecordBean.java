/**
 * 
 */
package eu.planets_project.tb.gui.backing.service;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eu.planets_project.ifr.core.techreg.formats.Format;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.impl.model.exec.ServiceRecordImpl;

/**
 * Encapsulates what is known about an endpoint, 
 * it's current ServiceDescription (if present) 
 * and it's record of use (if any).
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ServiceRecordBean {

    /** Any active service has a description. */
    private ServiceDescription sd = null;
    
    /** Any tested service should have a service record. */
    private ServiceRecordImpl sr = null;

    /** The description to display, drawn from the sr if no sd */
    private ServiceDescription sr_sd = null;

    /** Is this record currently selected, in the GUI? */
    private boolean selected = false;
    
    /** Is this service usable given current GUI parameters? */
    private boolean enabled = true;
    
    /** Experiments that used this service: */
    private List<Experiment> experiments = new ArrayList<Experiment>();;

    /**
     * @param sr
     */
    public ServiceRecordBean(ServiceRecordImpl sr) {
        this.setServiceRecord(sr);
        this.experiments = sr.getExperiments();
    }

    /**
     * @param sd
     */
    public ServiceRecordBean(ServiceDescription sd) {
        this.sd = sd;
    }

    /**
     * 
     * @return
     */
    public String getName() {
        if( sd != null ) {
            return sd.getName();
        }
        if( sr != null ) {
            return sr.getServiceName();
        }
        return "Unknown";
    }

    /**
     * 
     * @return
     */
    public String getType(){
        // FIXME Map these to pretty strings.
        if( sd != null ) {
            return mapTypeName( sd.getType() );
        }
        if( sr != null ) {
            return mapTypeName( sr.getServiceDescription().getType() );
        }
        return "Unknown";
    }

    /**
     * @param type
     * @return
     */
    private String mapTypeName( String type ) {
        if( type == null ) return "";
        return type.substring( type.lastIndexOf(".")+1 );
    }

    /**
     * 
     * @return
     */
    public String getDescription(){
        if( sd != null ) {
            return sd.getDescription();
        }
        if( sr != null ) {
            return sr.getServiceDescription().getDescription();
        }
        return "Unknown";
    }
    
    /**
     * @return
     */
    public URI getFurtherInfo() {
        if( sd != null ) {
            return sd.getFurtherInfo();
        }
        if( sr != null ) {
            return sr.getServiceDescription().getFurtherInfo();
        }
        return null;
    }
    
    /**
     * 
     * @return
     */
    public boolean isActive() {
        return (sd != null);
    }
    
    /**
     * 
     * @return
     */
    public boolean isUsed() {
        return (sr != null);
    }
    
    /**
     * 
     * @return
     */
    public List<Experiment> getExperiments() {
        return experiments;
    }

    /**
     * @return
     */
    public long getNumberOfExperiments() {
        return experiments.size();
    }

    /**
     * @return the sd
     */
    public ServiceDescription getServiceDescription() {
        if( sd == null ) {
            return sr_sd;
        }
        return sd;
    }

    /**
     * @return the service description as formatted XML:
     */
    public String getServiceDescriptionAsXml() {
        if( this.getServiceDescription() == null ) return null;
        return this.getServiceDescription().toXmlFormatted();
    }

    /**
     * @param sd the sd to set
     */
    public void setServiceDescription(ServiceDescription sd) {
        this.sd = sd;
    }

    /**
     * @return the sr
     */
    public ServiceRecordImpl getServiceRecord() {
        return sr;
    }

    /**
     * @param sr the sr to set
     */
    public void setServiceRecord(ServiceRecordImpl sr) {
        this.sr = sr;
        this.sr_sd = sr.getServiceDescription();
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
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return
     */
    public URL getEndpoint() {
        if( sd != null ) {
            return sd.getEndpoint();
        }
        if( sr != null ) {
            return sr.getServiceDescription().getEndpoint();
        }
        return null;
    }

    /**
     * @return
     */
    public URI getLogo() {
        if( sd != null ) return sd.getLogo();
        return null;
    }

    /**
     * @return
     */
    public String getToolSummary() {
        Tool st = null;
        if( sd != null ) 
            st = sd.getTool();
        if( sr != null ) 
            st = sr.getServiceDescription().getTool();
        if( st == null )
            return null;
        // Return a tool description;
        String desc = "";
        if( st.getName() != null ) {
            desc += st.getName();
            if( st.getVersion() != null ) {
                desc += " "+st.getVersion();
            }
        } else {
            if( st.getIdentifier() != null ) {
                desc += st.getIdentifier().toString();
            }
        }
        return desc;
    }

    /**
     * @return
     */
    public String getInputsSummary() {
        return formatList(this.getInputs());
    }

    /**
     * @return
     */
    public String getOutputsSummary() {
        return formatList(this.getOutputs());
    }

    /**
     * @return
     */
    public List<Format> getOutputFormats() {
       return this.urisToFormats( this.getOutputs() );
    }
    
    /**
     * @return
     */
    public List<Format> getInputFormats() {
       return this.urisToFormats( this.getInputs() );
    }
    
    /**
     * @param uris
     * @return
     */
    private List<Format> urisToFormats( List<URI> uris ) {
        if( uris == null ) return null;
        List<Format> fmts = new ArrayList<Format>();
        for( URI fmturi: uris ) {
            Format fmt = ServiceBrowser.fr.getFormatForUri( fmturi );
            fmts.add(fmt);
        }
        return fmts;
    }
    
    /**
     * @return
     */
    public List<URI> getOutputs() {
        ServiceDescription sd = this.getServiceDescription();
        List<URI> uris = new ArrayList<URI>();
        if( sd != null ) {
            for( MigrationPath mp : sd.getPaths() ) {
                if( ! uris.contains( mp.getOutputFormat() ) ) uris.add(mp.getOutputFormat());
            }
        }
        if( uris.size() == 0 ) return null;
        return uris;
    }

    /**
     * @return
     */
    public List<URI> getInputs() {
        ServiceDescription sd = this.getServiceDescription();
        List<URI> uris = new ArrayList<URI>();
        if( sd != null ) {
            for( URI fmturi : sd.getInputFormats() ) {
                if( ! uris.contains( fmturi ) ) uris.add(fmturi);
            }
            for( MigrationPath mp : sd.getPaths() ) {
                if( ! uris.contains( mp.getInputFormat() ) ) uris.add(mp.getInputFormat());
            }
        }
        if( uris.size() == 0 ) return null;
        return uris;
    }

    /**
     * @param uris
     * @return
     */
    private String formatList( List<URI> uris ) {
        String fmts = "";
        for( URI uri : uris ) {
            if( ! "".equals(fmts) ) fmts += "<br/> ";
            fmts += this.formatSummary(uri);
        }
        return fmts;
    }
    
    /**
     * @param fmturi
     * @return
     */
    private String formatSummary( URI fmturi ) {
        Format fmt = ServiceBrowser.fr.getFormatForUri( fmturi );
        if( fmt.getSummary() != null ) {
            return fmt.getSummary() + " " + fmt.getVersion();
        } else {
            return fmt.getUri().toString();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     * FIXME Is this appropriate?
     */
    @Override
    public int hashCode() {
        if( this.getEndpoint() != null ) {
            return this.getEndpoint().hashCode();
        }
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     * FIXME Is this appropriate?
     */
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof ServiceRecordBean ) {
            ServiceRecordBean srb = (ServiceRecordBean) obj;
            return this.getEndpoint().equals(srb.getEndpoint());
        }
        return super.equals(obj);
    }

    /**
     * @return
     */
    public String getServiceHash() {
        if( sd != null ) return ""+sd.hashCode();
        if( this.sr != null ) return sr.getServiceHash();
        return null;
    }
    
    
}
