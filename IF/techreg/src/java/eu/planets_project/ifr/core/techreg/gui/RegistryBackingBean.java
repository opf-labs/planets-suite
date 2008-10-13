/**
 * 
 */
package eu.planets_project.ifr.core.techreg.gui;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.component.html.HtmlDataTable;

import eu.planets_project.ifr.core.techreg.api.formats.Format;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistry;
import eu.planets_project.ifr.core.techreg.api.formats.FormatRegistryFactory;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class RegistryBackingBean {
    private static Log log = LogFactory.getLog(RegistryBackingBean.class);

    FormatRegistry ftr = null;
    
    private String searchStr = "pdf";
    
    private HtmlDataTable formatsDataTable;
    
    private Format currentFormat = null;
    
    /**
     * Constructor, initialised the format registry:
     */
    public RegistryBackingBean() {
    	log.debug("Instanciating the Format Registry.");
        ftr = FormatRegistryFactory.getFormatRegistry();
    }
    
    /**
     * 
     * @return
     */
    public synchronized List<Format> getFormats() {
        ArrayList<Format> fmts = new ArrayList<Format>();
        Set<URI> uris = ftr.getURIsForExtension(searchStr);
        if( uris == null ) return fmts;
        for( URI puri : uris ) {
            Format fmt = ftr.getFormatForURI(puri);
            fmts.add(fmt);
        }
        return fmts;
    }

    /**
     * @return the searchStr
     */
    public String getSearchStr() {
        return searchStr;
    }

    /**
     * @param searchStr the searchStr to set
     */
    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    /**
     * @return the formatsDataTable
     */
    public HtmlDataTable getFormatsDataTable() {
        return formatsDataTable;
    }

    /**
     * @param formatsDataTable the formatsDataTable to set
     */
    public void setFormatsDataTable(HtmlDataTable formatsDataTable) {
        this.formatsDataTable = formatsDataTable;
    }
       
    /**
     * @return the currentFormat
     */
    public Format getCurrentFormat() {
        return currentFormat;
    }

    /**
     * @param currentFormat the currentFormat to set
     */
    public void setCurrentFormat(Format currentFormat) {
        this.currentFormat = currentFormat;
    }

    /* ----------------- Actions ---------------------- */
    
    /**
     * Select the current format from the table.
     */
    public String selectAFormat() {
        currentFormat = (Format) this.formatsDataTable.getRowData();
        return "success";
    }

}
