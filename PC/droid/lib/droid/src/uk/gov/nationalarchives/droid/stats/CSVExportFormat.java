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

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

import java.util.Iterator;

/**
 * Export statistics results as CSV
 * @author ZEIP
 */
public class CSVExportFormat implements StatsExportFormat {
    
    private StatsLogger logger;
    private String errorMessage = "";
    private Date startTime;
    private Date endTime;
    
    /**
     * Retrieve stats from the StatsLogger and write to the given filename
     * @param logger The StatsLogger from which results are extracted
     * @param filename The filename to write results
     * @return true if export successful, false otherwise
     */  
    public boolean export(StatsLogger logger, Date startTime, Date endTime, String filename){
        this.logger = logger;
        this.startTime = startTime;
        this.endTime = endTime;
        
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(genString());
            out.close();
            return true;
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }       
    
    /**
     * Generate the string to be written to the CSV
     * @return complete CSV file string
     */
    public String genString(){
        StringBuffer output = new StringBuffer();        
        
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        // Date and time headers
        output.append("Profiling began,Profiling completed,Profile saved\n");
        
        // Start time
        if (this.startTime != null){
            output.append( dateFormat.format(this.startTime) );
        }
        output.append(",");      

        // End time
        if (this.endTime != null){
            output.append( dateFormat.format(this.endTime) );
        }
        output.append(",");      

        // Save time
        Date date = new Date();
        output.append( dateFormat.format(date) ).append("\n\n");
        
        // Paths recorded 
        output.append("Paths Indexed\n");
        Iterator<String> paths = logger.getPathIterator();
        while (paths.hasNext()){
            output.append(paths.next()).append("\n");
        }
        output.append("\n");
        
        // Output summary stats
        output.append("Total Readable Files, Total Un-Readable Files, Total Un-Readable Folders, Total Files' Size (bytes), Smallest File's Size, Largest File's Size, Mean File Size\n");
        output.append(logger.getTotalFiles()).append(",");
        output.append(logger.getNumBadFiles()).append(",");
        output.append(logger.getNumBadFolders()).append(",");
        output.append(logger.getGlobalTotalBytes()).append(",");
        output.append(logger.getSmallestFileSize()).append(",");
        output.append(logger.getLargestFileSize()).append(",");
        output.append(logger.getMeanFileSize()).append("\n");
        
        output.append("\n\n");
        
        // Output 'by year' rows
        output.append("Modification Year,Number of Files,Total Files' Size (Bytes)\n");
        String[] years = logger.getSortedYears();
        for (String year : years){
            output.append(year).append(",");
            output.append(logger.getNumFilesByYear(year)).append(",");
            output.append(logger.getTotalVolumeByYear(year)).append("\n");
        }

        output.append("\n\n");
              
        // Output 'by format' rows        
        output.append("File Type (PUID),File Type (MIME),File Format,File Version,Number of Files,Total Files' Size (Bytes)\n");
        Iterator formats = logger.getFormatKeys();
        String format; String mime;
        while (formats.hasNext()){
            format = (String)formats.next();
                       
            output.append(format.replaceAll(",",";")).append(",");
            output.append(logger.getMIMEByFormat(format).replaceAll(",",";")).append(",");
            output.append(logger.getFormatNameByFormat(format).replaceAll(",",";")).append(",");
            output.append(logger.getVersionByFormat(format).replaceAll(",",";")).append(",");
            output.append(logger.getNumFilesByFormat(format)).append(",");
            output.append(logger.getTotalVolumeByFormat(format)).append("\n");
        }
                
        return output.toString();
    }
    
    
    /**
     * Return any error message that was generated during export
     * @return error message if there is one, otherwise 0-length String
     */
    public String getCompilationError(){
        return this.errorMessage;
    }
}
