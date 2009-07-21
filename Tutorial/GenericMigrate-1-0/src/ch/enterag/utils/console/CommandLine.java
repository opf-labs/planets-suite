/*== CommandLine.java ==================================================
Trivial command-line parser 
Version     : $Id$
Application : PLANETS PA/4 migration services
Description : The command-line is parsed into options (introduced by "-"
              or "/" identified by name and unnamed arguments
              identified by position. 
Platform    : JAVA SE 1.5 or higher, JAX-WS2.1.5 (2008/10/30)  
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland 
Created    : May 13, 2009, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/*===================================================================*/
/** This class parses the command line and makes the arguments
 * accessible as named options by name and unnamed arguments by
 * position.
 * As opposed to tradition all options must have values, unless
 * it is the last option on the command line (e.g. -h).
 * In the name of simplicity this class does support switches that 
 * indicate a boolean value just by their presence.
 * @author Hartwig Thomas
 */
public class CommandLine
{
  /** container of named options */
  private Map<String,String> m_mapOptions = null;
  public String getOption(String sName) { return m_mapOptions.get(sName); }
  /** container of unnamed arguments */
  private String[] m_asArgument = null;
  public String getArgument(int iPosition) { return m_asArgument[iPosition]; }
  /** @return number of unnamed arguments. */
  public int getArguments() { return m_asArgument.length; }
  /** error string */
  private String m_sError = null;
  public String getError() { return m_sError; }
  
  /*------------------------------------------------------------------*/
  /** constructor parsed the command-line arguments
   * @param args command-line arguments.
   */
  public CommandLine(String[] args)
  {
    List<String> listArgument = new ArrayList<String>();
    m_mapOptions = new HashMap<String,String>();
    int iArgument = 0;
    while (iArgument < args.length)
    {
      String sArgument = args[iArgument]; 
      if (sArgument.startsWith("-") || sArgument.startsWith("/"))
      {
        /* named argument: name is terminated by special character */
        int iPosition = 1;
        for (;
          ((iPosition < sArgument.length() &&
           Character.isLetterOrDigit(sArgument.charAt(iPosition))));
          iPosition++) {}
        if (iPosition > 1)
        {
          String sName = sArgument.substring(1,iPosition);
          String sValue = "";
          if (iPosition < sArgument.length())
          {
            if ((sArgument.charAt(iPosition) == ':') || 
                (sArgument.charAt(iPosition) == '='))
              sValue = sArgument.substring(iPosition+1); /* skip one ":", "=" */
            else
              m_sError = "Option " + sName + " must be terminated by colon, equals or blank!";
          }
          else
          {
            if (iArgument < args.length-1)
            {
              iArgument++;
              sValue = args[iArgument]; /* next argument */
            }
            else
              m_sError = "Option " + sName + " without a value encountered!";
          }
          m_mapOptions.put(sName, sValue);
        }
        else
          m_sError = "Empty option encountered!";
      }
      else
        listArgument.add(args[iArgument]);
      iArgument++;
    }
    m_asArgument = listArgument.toArray(new String[]{});
  } /* constructor CommandLine */

} /* class CommandLine */
