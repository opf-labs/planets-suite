/*
 * © The National Archives 2005-2006.  All rights reserved.
 * See Licence.txt for full licence details.
 *
 * Developed by:
 * Tessella Support Services plc
 * 3 Vineyard Chambers
 * Abingdon, OX14 3PX
 * United Kingdom
 * email: info@tessella.com
 * web:   www.tessella.com
 *
 * Project Number:  Tessella/NPD/4950
 *                  
 */

package uk.gov.nationalarchives.droid.stats;
import java.util.Date;

/**
 * Defines classes that export stats results in a single format
 * @author ZEIP
 */
public interface StatsExportFormat {
    
    /**
     * Retrieve stats from the StatsLogger and write to the given filename
     * @param logger The StatsLogger from which results are extracted
     * @param startTime Time that processing began
     * @param endTime Time that processing finished
     * @param filename The filename to write results
     * @return true if export successful, false otherwise
     */
    public boolean export(StatsLogger logger, Date startTime, Date endTime, String filename);
    
    /**
     * Return any error message that was generated during export
     * @return error message if there is one, otherwise 0-length String. Delimited 
     * by newline if multiple
     */
    public String getCompilationError();
}
