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

/**
 * Factory class to create the relevant statistics exporter for the given format
 * @author ZEIP
 */
public class StatsExporterFactory {
    
    private static final String[] knownExtensions = {"csv","xml"};
    /**
     * Create an exporter class
     * @param type The file format to export (currently csv or xml).
     * @throws FormatUnknownException If the given file format is unknown
     * @return StatsExportFormat object
     */
    public static StatsExportFormat createExportFormat(String type) throws FormatUnknownException {
        if (type.toLowerCase().equals("csv")){
            return new CSVExportFormat();
        }else if (type.toLowerCase().equals("xml")){
            return new XMLExportFormat();
        }else{
            throw new FormatUnknownException(type);
        }
    }
    
    public static String[] getKnownExtensions(){
        return knownExtensions;
    }
       
    /**
     * Exception thrown for unknown file formats
     */
    public static class FormatUnknownException extends Exception {
        public FormatUnknownException(String type){
            super("An exporter was not found for type "+type);
        }
        public FormatUnknownException(){
            super();
        }
    }
   
}
