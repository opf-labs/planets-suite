/**
 * 
 */
package eu.planets_project.ifr.core.techreg.formats;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.richfaces.component.html.HtmlDataTable;



/**
 * GUI backing bean for the format registry.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class RegistryBackingBean {
    private static Logger log = Logger.getLogger(RegistryBackingBean.class.getName());

    FormatRegistryImpl ftr = null;
    
    private String searchStr = "pdf";
    
    private HtmlDataTable formatsDataTable;
    
    private MutableFormat currentFormat = null;
    
    /**
     * Constructor, initialised the format registry.
     */
    public RegistryBackingBean() {
    	log.fine("Instanciating the Format Registry.");
        ftr = (FormatRegistryImpl)FormatRegistryFactory.getFormatRegistry();
    }
    
    /**
     * @return the List of matching formats
     */
    public synchronized List<Format> getFormats() {
        ArrayList<Format> fmts = new ArrayList<Format>();
        Set<URI> uris = ftr.getUrisForExtension(searchStr);
        if( uris == null ) return fmts;
        for( URI puri : uris ) {
            Format fmt = ftr.getFormatForUri(puri);
            fmts.add(fmt);
        }
        return fmts;
    }

    /**
     * @return the search string
     */
    public String getSearchStr() {
        return searchStr;
    }

    /**
     * @param searchStr the search string to set
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
    public MutableFormat getCurrentFormat() {
        return currentFormat;
    }

    /**
     * @param currentFormat the currentFormat to set
     */
    public void setCurrentFormat(MutableFormat currentFormat) {
        this.currentFormat = currentFormat;
    }

    /* ----------------- Actions ---------------------- */
    
    /**
     * Select the current format from the table.
     * @return the jsf outcome
     */
    public String selectAFormat() {
        currentFormat = (MutableFormat) this.formatsDataTable.getRowData();
        return "success";
    }

}
