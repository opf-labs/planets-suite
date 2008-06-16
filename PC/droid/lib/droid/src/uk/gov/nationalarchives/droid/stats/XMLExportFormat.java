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

import javax.xml.datatype.XMLGregorianCalendar;      
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import uk.gov.nationalarchives.droid.stats.results.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Iterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Export statistics results as XML
 * @author Peter Zeidman
 */
public class XMLExportFormat implements StatsExportFormat {

    private String xmlCreationMessage = "";
    private String fileWriteMessage = "";
    private StatsLogger logger;
    private Date startTime;
    private Date endTime;    
    
    /**
     * Retrieve stats from the StatsLogger and write to the given filename
     * @param logger The StatsLogger from which results are extracted
     * @param filename The filename to write results
     * @return true if export successful, false otherwise
     */    
    public boolean export(StatsLogger logger, Date startTime, Date endTime, String filename){
        boolean xmlCreationSuccess = false;
        this.logger = logger;
        this.startTime = startTime;
        this.endTime = endTime;
        
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            xmlCreationSuccess = writeXML(out);
            out.close();           
        } catch (java.io.IOException e) {
            this.fileWriteMessage = e.getMessage();
            return false;
        }
        return xmlCreationSuccess;
    }

    /**
     * Compile the XML and write to the given Writer
     * @param out The open and ready Writer to export XML
     * @return success or failure of compilation / write
     */
    private boolean writeXML(Writer out){
        
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("uk.gov.nationalarchives.droid.stats.results");
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT ,
                   new Boolean(true));
           
            DatatypeFactory dataTypefactory = DatatypeFactory.newInstance();
                        
            ObjectFactory objFactory = new ObjectFactory();
            
            FileProfileType stats = (FileProfileType) objFactory.createFileProfileType();
            
            // Populate PathsProcessedType object
            PathsProcessedType pathsProcessed = objFactory.createPathsProcessedType();
            List<String> pathsList = pathsProcessed.getPathItem();
            
            Iterator paths = logger.getPathIterator();
            String path;
            while (paths.hasNext()){
                path = (String)paths.next();
                pathsList.add(path);
            }
            
            // Populate 'by format' object
            ByFormatType byFormat = objFactory.createByFormatType();
            List<FormatItemType> byFormatList = byFormat.getFormatItem();
            
            Iterator formats = logger.getFormatKeys();
            String format;
            while (formats.hasNext()){
                format = (String)formats.next();    
                // Create and populate formatItem object
                FormatItemType formatItem = (FormatItemType) objFactory.createFormatItemType(); 
                formatItem.setPUID(format);
                formatItem.setNumFiles(BigInteger.valueOf(logger.getNumFilesByFormat(format)));
                formatItem.setMIME(logger.getMIMEByFormat(format));
                formatItem.setFormatName(logger.getFormatNameByFormat(format));
                formatItem.setFormatVersion(logger.getVersionByFormat(format));
                formatItem.setTotalFileSize(BigDecimal.valueOf(logger.getTotalVolumeByFormat(format))); 
                byFormatList.add(formatItem);
            }
            
            // Populate 'by year' object
            ByYearType byYear = objFactory.createByYearType();
            List<YearItemType> byYearList = byYear.getYearItem();
            
        // Output 'by year' rows
        String[] years = logger.getSortedYears();
        for (String year : years){
                // Create and populate yearItem object
                YearItemType yearItem = (YearItemType) objFactory.createYearItemType(); 
                XMLGregorianCalendar cal = dataTypefactory.newXMLGregorianCalendar();
                cal.setYear(Integer.valueOf(year));
                yearItem.setYear(cal);
                yearItem.setNumFiles(BigInteger.valueOf(logger.getNumFilesByYear(year)));
                yearItem.setTotalFileSize(BigDecimal.valueOf(logger.getTotalVolumeByYear(year))); 
                byYearList.add(yearItem);
            }

            // Get current date / time
            XMLGregorianCalendar calendar =  DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(new GregorianCalendar());
                        
            stats.setProfilingSaveDate(calendar);

            XMLGregorianCalendar calendar2 =  DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(new GregorianCalendar());            
            calendar2 = XMLGregToDate(this.startTime,calendar2);
            stats.setProfilingStartDate(calendar2);

            XMLGregorianCalendar calendar3 =  DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(new GregorianCalendar());            
            calendar3 = XMLGregToDate(this.endTime,calendar3);
            stats.setProfilingEndDate(calendar3);
            
            stats.setByFormat(byFormat);
            stats.setByYear(byYear);
            stats.setPathsProcessed(pathsProcessed);
            stats.setTotalReadableFiles(BigInteger.valueOf(logger.getTotalFiles()));
            stats.setTotalUnreadableFiles(BigInteger.valueOf(logger.getNumBadFiles()));
            stats.setTotalUnreadableFolders(BigInteger.valueOf(logger.getNumBadFolders()));
            stats.setLargestSize(BigDecimal.valueOf(logger.getLargestFileSize()));
            stats.setSmallestSize(BigDecimal.valueOf(logger.getSmallestFileSize()));
            stats.setTotalSize(BigDecimal.valueOf(logger.getGlobalTotalBytes()));
            stats.setMeanSize(BigDecimal.valueOf(logger.getMeanFileSize()));;
            
            marshaller.setEventHandler(new XMLCreationEventHandler());
            marshaller.marshal( new JAXBElement(new QName("uri","local"), FileProfileType.class, stats),out);
      
        }catch (javax.xml.bind.JAXBException e){
            this.xmlCreationMessage = e.getMessage();
            return false;
        }catch (javax.xml.datatype.DatatypeConfigurationException e) {
            this.xmlCreationMessage = e.getMessage();
            return false;            
        }
        
        return (this.xmlCreationMessage.length() == 0);
        
    }
      
    /**
     * Set XMLGregorianCalendar to date / time set in a java.util.Date object
     * @param theDate the date to set the calendar
     * @param cal the calendar to load with the date
     * @return the loaded calendar
     */
    private XMLGregorianCalendar XMLGregToDate(Date theDate, XMLGregorianCalendar cal){
        java.util.Calendar javaCalendar = GregorianCalendar.getInstance();
        javaCalendar.setTime(theDate);
        
        cal.setDay(javaCalendar.get(java.util.Calendar.DAY_OF_MONTH));
        cal.setMonth(javaCalendar.get(java.util.Calendar.MONTH)+1);
        cal.setYear(javaCalendar.get(java.util.Calendar.YEAR));
        
        cal.setTime( javaCalendar.get(java.util.Calendar.HOUR_OF_DAY),
                javaCalendar.get(java.util.Calendar.MINUTE),
                javaCalendar.get(java.util.Calendar.SECOND));
        return cal;
    }
    /**
     * Return any error message that was generated during export
     * @return error message if there is one, delimited by newline if multiple
     */
    public String getCompilationError(){
        String error = "";
        if (this.xmlCreationMessage.length() > 0) 
            error += this.xmlCreationMessage + "\n";
        if (this.fileWriteMessage.length() > 0) 
            error += this.fileWriteMessage;
        return error;
    }
    
    /**
     * Event handler to deal with errors that occur during XML validation
     */
    private class XMLCreationEventHandler implements ValidationEventHandler {
      public boolean handleEvent(ValidationEvent ve) {

          if (ve.getSeverity()==ValidationEvent.FATAL_ERROR ||  
            ve .getSeverity()==ValidationEvent.ERROR){
              xmlCreationMessage = ve.getMessage();
          }        
         return true;
         
       }
   }    
}
