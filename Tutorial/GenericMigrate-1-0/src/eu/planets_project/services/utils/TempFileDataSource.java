/*== TempFileDataSource.java ===========================================
FileDataSource for "read-once" files that are to be deleted after usage.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : TempFileDataSource implements a FileDataSource
              for streaming which deletes the file when it is
              closed.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 16, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.activation.FileDataSource;

/*===================================================================*/
/** This class implements a FileDataSource which deletes the file 
 * when it is closed.
 * @author Hartwig Thomas
 */
public class TempFileDataSource 
  extends FileDataSource
{
  private final static String sSTREAM_MIME_TYPE = "application/octet-stream";
  private Logger m_log = Logger.getLogger(TempFileDataSource.class.getName());
  private final File m_fileSource;

  /*------------------------------------------------------------------*/
  /** constructor
   * @param fileSource input file which will be deleted on close.
   */
  public TempFileDataSource(File fileSource)
  {
    super(fileSource);
    m_fileSource = fileSource;
  } /* constructor TempFileDataSource */
  
  /*------------------------------------------------------------------*/
  /** open the file as an input stream
   * @return open input stream which deletes the file on close.
   * @throws FileNotFoundException if the file cannot be opened. 
   */
  @Override 
  public InputStream getInputStream()
    throws FileNotFoundException
  {
    m_log.info("getInputStream");
    return new TempFileInputStream(m_fileSource);
  } /* getInputStream */
  
  /*------------------------------------------------------------------*/
  /** MIME type of file stream is used for streaming support.
   * @return application/octet-stream.
   */
  @Override 
  public String getContentType()
  {
    m_log.info("Content-type: "+sSTREAM_MIME_TYPE);
    return sSTREAM_MIME_TYPE;
  } /* getContentType */
  
  /*------------------------------------------------------------------*/
  /** file name is returned as name of DataSource.
   * (Possibly super.getName() returns the same result.)
   * @return file name.
   */
  @Override 
  public String getName()
  {
    m_log.info("Name: "+m_fileSource.getPath());
    return m_fileSource.getPath();
  } /* getName */
  
  /*------------------------------------------------------------------*/
  /** no output stream.
   * (Probably super.getOutputStream() returns the same result.)
   * @return null.
   */
  @Override
  public OutputStream getOutputStream()
  {
    return null;
  } /* getOutputStream */

  /*===================================================================*/
  /** inner class that handles the delete on close.
   * Most of the code is just here to log the activity on this InputStream. 
   */
  private static final class TempFileInputStream 
    extends FileInputStream
  {
    private Logger m_log = Logger.getLogger(TempFileInputStream.class.getName());
    /** the file to be read and deleted on close */
    private File m_file;
    /** the number of bytes read */
    private long m_lRead;
    
    /*------------------------------------------------------------------*/
    /** constructor
     * @param file the file to be opened and deleted on close.
     * @return open input stream
     * @throws FileNotFoundException if file cannot be opened.
     */
    public TempFileInputStream(File file) 
      throws FileNotFoundException
    {
      super(file);
      m_file = file;
      m_lRead = 0;
    } /* constructor TempFileInputStream */
    
    /*------------------------------------------------------------------*/
    /** closes the input stream and deletes the file. 
     * @throws IOException if an I/O exception occurred.
     */
    @Override
    public void close()
      throws IOException
    {
      super.close();
      m_log.info("close");
      if (m_file != null)
      {
        if (m_lRead != m_file.length())
          m_log.info("File of size " + String.valueOf(m_file.length()) +
              " was closed after reading " + String.valueOf(m_lRead) + " bytes.");
        m_file.delete();
        if (!m_file.exists())
          m_log.info("File "+m_file.getAbsolutePath()+" deleted.");
        m_file = null;
      }
    } /* close */
    
    /*------------------------------------------------------------------*/
    /** reads a byte and keeps count of number of bytes read.
     * @throws IOException if an I/O exception occurred.
     */
    @Override
    public int read()
      throws IOException
    {
      int iRead = super.read();
      if (iRead != -1)
        m_lRead++;
      else 
        m_log.info("EOF in read() reached.");
      return iRead;
    } /* read */
    
    /*------------------------------------------------------------------*/
    /** reads a buffer and keeps count of number of bytes read.
     * @throws IOException if an I/O exception occurred.
     */
    @Override
    public int read(byte[] buffer)
      throws IOException
    {
      int iRead = super.read(buffer);
      if (iRead != -1)
        m_lRead += iRead;
      else
        m_log.info("EOF in read(buffer) reached.");
      return iRead;
    } /* read */
    
    /*------------------------------------------------------------------*/
    /** reads a partial buffer and keeps count of number of bytes read.
     * @throws IOException if an I/O exception occurred.
     */
    @Override
    public int read(byte[] buffer, int iOffset, int iLength)
      throws IOException
    {
      int iRead = super.read(buffer,iOffset,iLength);
      if (iRead != -1)
        m_lRead += iRead;
      else
        m_log.info("EOF in read(buffer,offset,length) reached.");
      return iRead;
    } /* read */
    
  } /* class TempFileInputStream */
  
} /* TempFileDataSource */
