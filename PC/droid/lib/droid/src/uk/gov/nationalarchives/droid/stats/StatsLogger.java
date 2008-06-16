/*
 * © The National Archives 2005-2006.  All rights reserved.
 * See Licence.txt for full licence details.
 *
 * Developed by:
 * Tessella Support Services plc
 * 3 Vineyard Chambers
 * Abingdon, OX14 3PX
 * United Kingdom
 * http://www.tessella.com
 *
 * Tessella/NPD/4305
 *
 * 
 * PRONOM 4
 *
 */

package uk.gov.nationalarchives.droid.stats;
import uk.gov.nationalarchives.droid.FileCollection;
import uk.gov.nationalarchives.droid.IdentificationFile;
import uk.gov.nationalarchives.droid.binFileReader.InputStreamByteReader;
import uk.gov.nationalarchives.droid.binFileReader.UrlByteReader;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Enumeration;
import java.io.Writer;

import java.util.Set;
import java.util.HashSet;

/**
 * Class to calculate statistics on sets of files, and forms a data structure
 * to store those statistics. Files are provided one at a time, and their
 * relevant details collated.
 *
 * Maximum allowed total file size: 8,388,608 terabytes
 *
 * @author ZEIP
 */
public class StatsLogger {
    
    private Calendar calendar;
    
    /** Size in bytes of files logged */
    private long globalTotalBytes = 0;
    /** Size in bytes of smallest file logged */
    private long smallestFileSize = -1;
    /** Size in bytes of largest file logged */
    private long largestFileSize = -1;
    /** Total number of files logged */
    private long totalFiles = 0;
    /** Mean size in bytes over all files logged */
    private long meanFileSize = 0;
    
    /** Map to store file details by year */ 
    private Map<String,CategoryDetail> byYear;
    /** Map to store file details by file format */     
    private Map<String,CategoryDetail> byFormat;
    
    /* Flag for whether a marshalling error occurred */
    private boolean xmlCreationFailed = false;
    /* Error message if one was generated during XML creation */
    private String xmlCreationMessage = "";
    
    /* High-Level paths of files recorded */    
    private Set<String> paths;
    
    // Recording of errors in reading files / folders
    private int numBadFolders = 0;
    private int numBadFiles = 0;    
    
    /** 
     * Creates a new instance of StatsLogger 
     *
     * @param numFiles Estimated number of files that will be analysed
     */
    public StatsLogger(int numFiles) {
        // Default map size assumes 10 years' file records
        this.byYear = new HashMap<String,CategoryDetail>(15);
        // Default map size assumes number of formats is 50% number of files
        this.byFormat = new HashMap<String,CategoryDetail>( (int)(1.25 * numFiles) );
        
        this.paths = new HashSet<String>(numFiles);
        calendar = GregorianCalendar.getInstance();
    }

    public Map<String, StatsLogger.CategoryDetail> getByFormat() {
        return byFormat;
    }
    
    /**
     * Note a file's details (size, type and age) for stats generation
     *
     * @param fileFormatPUID the file type identifier (PUID)
     * @param lastModified date the file was last modified
     */
    public void analyseFile(String fileFormatPUID, String mimeType, String fileFormat, String fileFormatVersion,
            long totalBytes, Date lastModified){
                
        if (fileFormatPUID == null) fileFormatPUID = "Unrecognised";
        
        // Record format statistics
        if (this.byFormat.containsKey(fileFormatPUID)){
            // Update category
            CategoryDetail details = this.byFormat.get(fileFormatPUID);
            details.numFiles++;
            details.totalFileVolume += totalBytes;
            this.byFormat.put(fileFormatPUID,details);            
        }else{
            // New category
            this.byFormat.put(fileFormatPUID,new CategoryDetail(1,totalBytes,fileFormatPUID,mimeType,fileFormat,fileFormatVersion) );
        }
        
        // Calculate file's year
        this.calendar.setTime(lastModified);
        int year = this.calendar.get(Calendar.YEAR);
        String yearName = String.valueOf(year);
        
        // Record size statistics               
        if (this.byYear.containsKey(yearName)){
            // Update year
            CategoryDetail details = this.byYear.get(yearName);
            details.numFiles++;
            details.totalFileVolume += totalBytes;
            this.byYear.put(yearName,details);            
        }else{
            // New year
            this.byYear.put(yearName,new CategoryDetail(1,totalBytes,null,null,null,null) );
        }
        
        // Record global stats
        if (totalBytes > largestFileSize){
            this.largestFileSize = totalBytes;
        }
        if (totalBytes < this.smallestFileSize || this.smallestFileSize == -1){
            this.smallestFileSize = totalBytes;
        }        
        this.totalFiles++;        
        this.globalTotalBytes += totalBytes;
        this.meanFileSize = this.globalTotalBytes / this.totalFiles;
    }

       
    /**
     * Produce report on stored data
     */    
    public void outputTables(){
        System.out.println("ANALYSIS RESULTS SUMMARY");
        System.out.println("==================================");
        System.out.println("Total readable files: " + this.totalFiles);
        System.out.println("Total un-readable files: " + this.getNumBadFiles());
        System.out.println("Total un-readable folders: " + this.getNumBadFolders());
        System.out.println("Total file size: " + this.globalTotalBytes + " bytes");
        System.out.println("Largest file size: " + this.largestFileSize + " bytes");
        System.out.println("Smallest file size: " + this.smallestFileSize + " bytes");
        System.out.println("Mean file size: " + this.meanFileSize + " bytes");
        System.out.println("==================================");
        
        Iterator yearKeys = this.byYear.keySet().iterator();
        String key;
        while (yearKeys.hasNext()){
            key = (String)yearKeys.next();
            System.out.println(key);
            System.out.println("  Number of files: "+this.byYear.get(key).numFiles);
            System.out.println("  Total size: "+this.byYear.get(key).totalFileVolume+" bytes");
        }
        System.out.println();
        System.out.println("==================================");
        
        Iterator typeKeys = this.byFormat.keySet().iterator();
        while (typeKeys.hasNext()){
            key = (String)typeKeys.next();
            System.out.println(key);
            System.out.println("  Number of files: "+this.byFormat.get(key).numFiles);
            System.out.println("  Total size: "+this.byFormat.get(key).totalFileVolume + " bytes");
        }        
        System.out.println();
    }
    /**
     * Get total number of distinct years indexed
     * @return number of years
     */
    public int getTotalYears(){
        return this.byYear.size();
    }
    /**
     * Get total number of distinct formats indexed
     * @return number of formats
     */    
    public int getTotalFormats(){
        return this.byFormat.size();
    }    
    /**
     * Get an enumeration of the distinct years indexed
     * @return Enumeration of years as four character strings (e.g. "2004")
     */    
    public Iterator<String> getYearKeys(){
        return this.byYear.keySet().iterator();
    }
    /**
     * Get an enumeration of the distinct formats indexed
     * @return Enumeration of PUID strings
     */        
    public Iterator<String> getFormatKeys(){
        return this.byFormat.keySet().iterator();
    }
    /**
     * Get a MIME type given the PUID, assuming it has been indexed
     * @return MIME type, as generated by DROID
     */
    public String getMIMEByFormat(String format){
        return this.byFormat.get(format).mime;
    }
    /**
     * Get the format's name given the PUID, assuming it has been indexed
     * @return Format name, as generated by DROID
     */    
    public String getFormatNameByFormat(String format){
        return this.byFormat.get(format).format;
    }    
    /**
     * Get file format version given the PUID, assuming it has been indexed
     * @return File format version, as generated by DROID
     */    
    public String getVersionByFormat(String format){
        return this.byFormat.get(format).version;
    }        
    /**
     * Get the number of files indexed for the given format
     * @return The number of files, or -1 if the format hasn't been indexed
     */    
    public long getNumFilesByFormat(String format){
        if (this.byFormat.containsKey(format)){
            return this.byFormat.get(format).numFiles;
        }else{
            return -1;
        }
    }
    /**
     * Get the total size of all files indexed for the given format
     * @return The total size in bytes, or -1 if the format hasn't been indexed
     */      
    public long getTotalVolumeByFormat(String format){
        if (this.byFormat.containsKey(format)){
            return this.byFormat.get(format).totalFileVolume;
        }else{
            return -1;
        }
    }
    /**
     * Get the number of files indexed for the given year
     * @return The number of files, or -1 if the year hasn't been indexed
     */    
    public long getNumFilesByYear(String year){
        if (this.byYear.containsKey(year)){
            return this.byYear.get(year).numFiles;
        }else{
            return -1;
        }
    }
    /**
     * Get the total size of all files indexed for the given year
     * @return The total size in bytes, or -1 if the year hasn't been indexed
     */      
    public long getTotalVolumeByYear(String year){
        if (this.byYear.containsKey(year)){
            return this.byYear.get(year).totalFileVolume;
        }else{
            return -1;
        }
    }
    /**
     * Get the total size of all files indexed
     * @return The total size in bytes
     */          
    public long getGlobalTotalBytes(){
        return this.globalTotalBytes;
    }
    /**
     * Get the size of the smallest file found
     * @return The size in bytes
     */              
    public long getSmallestFileSize(){
        return Math.max(this.smallestFileSize,0);
    }
    /**
     * Get the size of the largest file found
     * @return The size in bytes
     */            
    public long getLargestFileSize(){
        return Math.max(this.largestFileSize,0);
    }
    /**
     * Get the total number of files indexed
     * @return The number of files
     */
    public long getTotalFiles(){
        return this.totalFiles;
    }
    /**
     * Get the mean size of all files indexed
     * @return Mean size in bytes
     */
    public long getMeanFileSize(){
        return this.meanFileSize;
    }  
    /**
     * Get an array of distinct years indexed, sorted into ascending order
     * @return array of years as four-character strings, e.g. "2004"
     */
    public String[] getSortedYears(){
        
        String[] yearStrings = new String[getTotalYears()];
        int counter = 0;
        
        if (yearStrings.length > 0){
            Iterator years = getYearKeys();
            while (years.hasNext()){
                yearStrings[counter] = (String)years.next();
                counter++;
            }
        }
        java.util.Arrays.sort(yearStrings);
        return yearStrings;
    }   
    
    /**
     * Get the number of folders which could not be read
     */
    public int getNumBadFolders(){
        return this.numBadFolders;
    }
    
    /**
     * Get the number of files which could not be read
     */    
    public int getNumBadFiles(){
        return this.numBadFiles;
    }

    /**
     * Increment number of unreadable files
     */
    public void incNumBadFiles(){
        this.numBadFiles++;
    }    

    /**
     * Increment number of unreadable files
     */
    public void incNumBadFolders(){
        this.numBadFolders++;
    }      
    
    /**
     * Record paths of files recorded
     */
    public void recordPath(String path){
        this.paths.add(path);
    }  
    
    /**
     * Get iterator to cycle through paths
     */
    public java.util.Iterator getPathIterator(){
        return this.paths.iterator();
    }  
    
    /**
     * Set the paths object
     */
    public void setPaths(Set paths){
        this.paths = paths;
    }
    
    /**
     * Storage for details of all files within a category, e.g. within a year
     * (Set public for JUnit)
     */
    public class CategoryDetail {
        /* Number of files in the category */
        public long numFiles = 0;
        /* Total size of the files in the category in bytes */
        public long totalFileVolume = 0;
        /* PUID of the category */
        public String puid = "";
        /* MIME type of the category */
        public String mime = "";
        /* File format of the category */
        public String format = "";
        /* File format version of the category */
        public String version = "";
        
        public CategoryDetail(long numFiles, long totalFileVolume, String puid, 
                String mime, String format, String version){
            
            // Numeric attributes
            this.numFiles = numFiles;
            this.totalFileVolume = totalFileVolume;
            
            // String attributes - set as zero length if null
            this.puid =     ( (puid == null) ? "" : puid);
            this.mime =     ( (mime == null) ? "" : mime);
            this.format =   ( (format == null) ? "" : format);
            this.version =  ( (version == null) ? "" : version);
        }
    }
    
}
