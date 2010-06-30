/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.tb.gui.backing.service;

import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.techreg.formats.Format;
import eu.planets_project.services.datatypes.MigrationPath;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.data.util.DigitalObjectRefBean;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.gui.backing.ServiceBrowser;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
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
    /** */
    private static final Log log = LogFactory.getLog(ServiceRecordBean.class);

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

    int invocations = 0;
    int no_result = 0;
    int result = 0;
    double average_call_time_s = 0.0;
    double min_call_time_s = Double.MAX_VALUE;
    double max_call_time_s = Double.MIN_VALUE;
    double throughput_bytes_per_s = 0.0;
    DecimalFormat df = new DecimalFormat("#.#####");
    
    /**
     * @param sr
     */
    public ServiceRecordBean(ServiceRecordImpl sr) {
        this.setServiceRecord(sr);
        this.experiments = sr.getExperiments();
        // Look for data:
        double size_total = 0.0;
        double thru_time_total = 0.0;
        for( Experiment exp : experiments ) {
            if( exp.getExperimentExecutable().getBatchExecutionRecords().size() > 0 ) {
                BatchExecutionRecordImpl ber = exp.getExperimentExecutable().getBatchExecutionRecords().iterator().next();
                if( ber.isBatchRunSucceeded() ) {
                    for( ExecutionRecordImpl run : ber.getRuns() ) {
                        Double time_s = null;
                        if( run.getStartDate() != null && run.getEndDate() !=null ) {
                            time_s = ( run.getEndDate().getTimeInMillis() - run.getStartDate().getTimeInMillis() ) / 1000.0;
                        }
                        // Look for digital object:
                        long size = -1;
                        try {
                            DataHandler dh = DataHandlerImpl.findDataHandler();
                            DigitalObjectRefBean digitalObjectRefBean = dh.get( run.getDigitalObjectReferenceCopy() );
                            size = digitalObjectRefBean.getSize();
                        } catch ( Exception e) {
                            log.error("Failed to look up object "+run.getDigitalObjectReferenceCopy()+" "+e);
                        }
                        // Look for matching records:
                        boolean matches = false;
                        for(  ExecutionStageRecordImpl stage: run.getStages() ) {
                            if( this.getServiceHash() != null && stage.getServiceRecord() != null &&
                                    this.getServiceHash().equals( stage.getServiceRecord().getServiceHash() ) ) {
                                matches = true;
                                break;
                            }
                        }
                        if( time_s != null && matches ) {
                            if( run.getResult() == null ) {
                                no_result += 1;
                            } else {
                                result += 1;
                            }
                            invocations += 1;
                            average_call_time_s += time_s;
                            if( time_s > this.max_call_time_s ) this.max_call_time_s = time_s;
                            if( time_s < this.min_call_time_s ) this.min_call_time_s = time_s;
                            if( size != -1 ) {
                                size_total += size;
                                thru_time_total += time_s;
                            }
                        }
                    }
                }
            }
        }
        if( invocations != 0 ) average_call_time_s /= invocations;
        if( thru_time_total != 0.0 ) this.throughput_bytes_per_s = size_total/thru_time_total;
    }

    /**
     * @param sd
     */
    public ServiceRecordBean(ServiceDescription sd) {
        this.sd = sd;
    }

    /**
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
     * @return
     */
    public String getResultRate() {
        if( invocations == 0 ) return "No data";
        return ""+result+"/"+invocations;
    }

    /**
     * @return
     */
    public String getAverageCallTime() {
        if( invocations == 0 ) return "No data";
        return df.format(average_call_time_s);
    }
    
    /**
     * @return
     */
    public String getMinCallTime() {
        if( invocations == 0 ) return "No data";
        return df.format(min_call_time_s);
    }

    /**
     * @return
     */
    public String getMaxCallTime() {
        if( invocations == 0 ) return "No data";
        return df.format(max_call_time_s);
    }
    
    /**
     * @return as KB/s
     */
    public String getThroughput() {
        if( throughput_bytes_per_s == 0.0 ) return "No data";
        return df.format( throughput_bytes_per_s / 1024.0);
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
     * @return
     */
    public boolean isMigrationService() {
        if( "Migrate".equals(this.getType()) ) return true;
        return false;
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
        return formatList(this.getInputs(true));
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
    public List<FormatBean> getOutputFormats() {
       return this.urisToFormats( this.getOutputs() );
    }
    
    /**
     * @return
     */
    public List<FormatBean> getInputFormats() {
       return this.urisToFormats( this.getInputs( true ) );
    }
    
    /**
     * @param uris
     * @return
     */
    private List<FormatBean> urisToFormats( List<URI> uris ) {
        if( uris == null ) return null;
        List<FormatBean> fmts = new ArrayList<FormatBean>();
        for( URI fmturi: uris ) {
            Format fmt = ServiceBrowser.fr.getFormatForUri( fmturi );
            fmts.add(new FormatBean(fmt) );
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
    
    public boolean getHasParameters() {
        if( this.getServiceDescription() != null ) {
            if( this.getServiceDescription().getParameters() != null &&
            this.getServiceDescription().getParameters().size() > 0 ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return
     */
    public List<PathwayBean> getPathways() {
        ServiceDescription sd = this.getServiceDescription();
        List<PathwayBean> paths = new ArrayList<PathwayBean>();
        if( sd != null ) {
            for( MigrationPath mp : sd.getPaths() ) {
                paths.add(new PathwayBean(this, 
                        new FormatBean( ServiceBrowser.fr.getFormatForUri(mp.getInputFormat())), 
                        new FormatBean( ServiceBrowser.fr.getFormatForUri(mp.getOutputFormat())) ) );
            }
        }
        return paths;
    }
    
    public PathwayMatrixEntry[][] getPathwayMatrix() {
        Set<URI> uriset = new HashSet<URI>();
        if( sd != null ) {
            for( MigrationPath mp : sd.getPaths() ) {
                uriset.add(mp.getInputFormat());
                uriset.add(mp.getOutputFormat());
            }
        }
        List<URI> uris = new ArrayList<URI>(uriset);
        // Build:
        PathwayMatrixEntry[][] matrix = new PathwayMatrixEntry[uris.size()+1][uris.size()+1];
        matrix[0][0] = new PathwayMatrixEntry( null );
        for( int i = 0; i < uris.size(); i++ ) {
            URI outUri = uris.get(i);
            PathwayMatrixEntry iFormatEntry = new PathwayMatrixEntry(
                    new FormatBean( ServiceBrowser.fr.getFormatForUri(outUri))
                    );
            matrix[i+1][0] = iFormatEntry;
            for( int j = 0; j < uris.size(); j++ ) {
                URI inUri = uris.get(j);
                if( i == 0 ) {
                    PathwayMatrixEntry jFormatEntry = new PathwayMatrixEntry(
                            new FormatBean( ServiceBrowser.fr.getFormatForUri(inUri))
                    );
                    matrix[0][j+1] = jFormatEntry;
                }
                // Pathway:
                PathwayMatrixEntry entry = new PathwayMatrixEntry();
                matrix[i+1][j+1] = entry;
                for( MigrationPath mp : sd.getPaths() ) {
                    if( inUri.equals(mp.getInputFormat()) && outUri.equals(mp.getOutputFormat())) {
                        PathwayBean pathway = new PathwayBean(this,
                                new FormatBean( ServiceBrowser.fr.getFormatForUri(inUri)),
                                new FormatBean( ServiceBrowser.fr.getFormatForUri(outUri))
                                );
                        entry.setPathway(pathway);
                    }
                }
            }
        }
        // Map to Lists:
        return matrix;
    }
    /*
        List<PathwayMatrixColumns> mat = new ArrayList<PathwayMatrixColumns>(uris.size());
        List<PathwayMatrixEntry> formatRow = new ArrayList<PathwayMatrixEntry>(uris.size());
        mat.add(0, new PathwayMatrixColumns(formatRow));
        for( int i = 0; i < uris.size(); i++ ) {
            URI inUri = uris.get(i);
            List<PathwayMatrixEntry> rows = new ArrayList<PathwayMatrixEntry>(uris.size());
            PathwayMatrixEntry formatEntry = new PathwayMatrixEntry();
            rows.add(0, formatEntry);
            for( int j = 0; j < uris.size(); j++ ) {
                URI outUri = uris.get(j);
                PathwayMatrixEntry entry = new PathwayMatrixEntry();
                rows.add(j+1, entry);
                for( MigrationPath mp : sd.getPaths() ) {
                    if( inUri.equals(mp.getInputFormat()) && outUri.equals(mp.getOutputFormat())) {
                        PathwayBean pathway = new PathwayBean(this,
                                new FormatBean( ServiceBrowser.fr.getFormatForUri(inUri)),
                                new FormatBean( ServiceBrowser.fr.getFormatForUri(outUri))
                                );
                        entry.setPathway(pathway);
                    }
                }
            }
            mat.add(i+1, new PathwayMatrixColumns(rows));
        }
        // Map to Lists:
        return mat;
     * 
     */
    
    
    public class PathwayMatrixColumns {
        List<PathwayMatrixEntry> rows;
        public PathwayMatrixColumns(List<PathwayMatrixEntry> rows) {
            this.rows = rows;
        }
        public List<PathwayMatrixEntry> getRows() {
            return rows;
        }
    }
    
    public class PathwayMatrixEntry {
        private PathwayBean pathway;
        private FormatBean format;
        private boolean isFormatEntry = false;
        
        public PathwayMatrixEntry() {
        }
        public PathwayMatrixEntry(FormatBean format) {
            this.format = format;
            this.isFormatEntry = true;
        }
        public FormatBean getFormat() {
            return this.format;
        }
        public void setPathway( PathwayBean pathway ) {
            this.pathway = pathway;
        }
        public PathwayBean getPathway() {
            return this.pathway;
        }
        public boolean isPathway() {
            if( this.isFormatBean() ) return false;
            if( this.pathway == null ) return false;
            return true;
        }
        public boolean isFormatBean() {
            if( this.isFormatEntry ) return true;
            return false;
        }
        public String getStyleClass() {
            if( this.isFormatBean() ) return "formatField";
            return "pathwayField";
        }
        public String getPathwayString() {
            if( ! this.isPathway() ) return null;
            return "From '"+this.pathway.getInputFormat().getSummary()+"' to '"+this.pathway.getOutputFormat().getSummary()+"'";
        }
    }

    /**
     * @return
     */
    public List<URI> getInputs( boolean includeMigrations ) {
        ServiceDescription sd = this.getServiceDescription();
        List<URI> uris = new ArrayList<URI>();
        if( sd != null ) {
            for( URI fmturi : sd.getInputFormats() ) {
                if( ! uris.contains( fmturi ) ) uris.add(fmturi);
            }
            if( includeMigrations ) {
                for( MigrationPath mp : sd.getPaths() ) {
                    if( ! uris.contains( mp.getInputFormat() ) ) uris.add(mp.getInputFormat());
                }
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
