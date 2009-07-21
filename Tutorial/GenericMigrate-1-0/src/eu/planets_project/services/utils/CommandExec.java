/*== CommandExec.java ==================================================
CommandExec executes external commands capturing stdout and stderr.
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : CommandExec executes external commands 
              capturing stdout and stderr.
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland 
             (pending PLANETS copyright agreements)
Created    : July 16, 2009, Hartwig Thomas, Enter AG, Zurich
Sponsor    : Swiss Federal Archives, Berne, Switzerland
======================================================================*/
package eu.planets_project.services.utils;

import java.lang.InterruptedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

/*===================================================================*/
/** This class executes external commands capturing stdout and stderr.
 * @author Hartwig Thomas
 */
public class CommandExec
{
  /** content of stdout after execute() has terminated */
  private String m_sOutput = null;
  
  /*------------------------------------------------------------------*/
  /** return the content of stdout of the last execute.
   * @return content of stdout.
   */
  public String getOutput()
  { 
    return m_sOutput;
  } /* getOutput */
  
  /** content of stderr after execute() has terminated */
  private String m_sError = null;
  
  /*------------------------------------------------------------------*/
  /** return the content of stderr of the last execute.
   * @return content of stderr.
   */
  public String getError()
  { 
    return m_sError;
  } /* getError */
  
  /*------------------------------------------------------------------*/
  /** execute an external command
   * @param sCommand command to be executed including all arguments
   * @param lMsTimeout timeout after which command execution is to
   *        be interrupted. (Currently ignored.)
   * @return return code from command execution.
   * @throws IOException on I/O errors.
   * @throws InterruptedException if command was interrupted.  
   */
  public int execute(String sCommand, long lMsTimeout) 
    throws IOException, InterruptedException
  {
    m_sOutput = null;
    m_sError = null;
    /* start executing the command as a separate process */
    Process process = Runtime.getRuntime().exec(sCommand);
    /* create the stream reading threads */
    InputStreamReaderThread isrtOut = new InputStreamReaderThread(process.getInputStream());
    InputStreamReaderThread isrtErr = new InputStreamReaderThread(process.getErrorStream());
    /* start those threads running */
    isrtOut.start();
    isrtErr.start();
    /* wait for the process to finish */
    int iReturn = process.waitFor();
    /* wait for stream reading threads to finish properly */
    isrtOut.waitForCompletion();
    isrtErr.waitForCompletion();
    /* get the stream contents */
    m_sOutput = isrtOut.getBuffer();
    m_sError = isrtErr.getBuffer();
    return iReturn;
  } /* execute */
  
  /*===================================================================*/
  /** This inner class implements a thread for reading an input stream
   * and storing its contents in a string.
   */
  private static class InputStreamReaderThread
    extends Thread
  {
    private static final int iBUFFER_SIZE = 8120;
    private static final long lMS_SLEEP = 100; 
    /** input stream to be read and buffered in this thread */
    private InputStream m_is;
    /** buffer for capturing content of stream */
    private StringBuffer m_sb;
    /** true, if thread is running */
    private boolean m_bRunning = false;
    /** true, if end of file has not been reached in input stream */
    private boolean m_bCompleted = false;

    /*----------------------------------------------------------------*/
    /** construct a thread for reading and buffering an input stream.
     * @param is an input stream to read and buffered.
     */
    public InputStreamReaderThread(InputStream is)
    {
      super();
      // setDaemon(true);   // must not hold up the VM if it is terminating
      m_is = is;
      m_sb = new StringBuffer(iBUFFER_SIZE);
      m_bRunning = false;
      m_bCompleted = false;
    } /* constructor InputStreamReaderThread */
      
    /*----------------------------------------------------------------*/
    /** run the thread.
     */
    @Override
    public synchronized void run()
    {
      // mark this thread as running
      m_bRunning = true;
      m_bCompleted = false;
        
      BufferedInputStream bis = null;
      try
      {
        bis = new BufferedInputStream(m_is, iBUFFER_SIZE);
        byte[] buffer = new byte[iBUFFER_SIZE];
        for (int iCount = bis.read(buffer); iCount != -1; iCount = bis.read(buffer))
        {
          if (iCount == 0)
          {
            /* sleep a bit to avoid too busy polling */
            try { wait(lMS_SLEEP); }
            catch(InterruptedException ie) {}
          }
          else
            m_sb.append(new String(buffer, 0, iCount));
        }
        m_bCompleted = true;
        m_bRunning = false;
      }
      catch(IOException ie)
      {
        throw new Error("IOException: "+ie.getMessage());
      }
      finally
      {
         /* close the input stream */
         if (bis != null)
         {
            try { bis.close(); } catch (IOException ie) {}
         }
      }
    } /* run */

    /*----------------------------------------------------------------*/
    /** wait until stream has reached end of file and thread has completed.
     */
    public synchronized void waitForCompletion()
    {
      while (!m_bCompleted && !m_bRunning)
      {
        /* sleep a bit to avoid too busy polling */
        try { wait(lMS_SLEEP); }
        catch(InterruptedException ie) {}
      }
    } /* waitForCompletion */

    /*----------------------------------------------------------------*/
    /** access to content of input stream.
     * @return content of input stream.
     */
    public String getBuffer()
    {
      return m_sb.toString();
    } /* getBuffer */

  } /* class InputStreamReaderThread */

} /* class CommandExec */
