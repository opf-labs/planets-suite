package eu.planets_project.services.utils;

import java.util.*;

import org.apache.commons.logging.*; 
import org.apache.log4j.*;

/**
 * PlanetsLogger
 *
 * @author Klaus Rechert, ALUF
 */
public class PlanetsLogger implements Log
{
	/**
	 * the planets log root namespace
	 */
	public static final String PLANETS_LOG_ROOT = "eu.planets_project";
	private static HashMap<String, PlanetsLogger> logTable = new HashMap<String, PlanetsLogger>();
	
	static {
	    PlanetsLoggerConfigurator.configure("eu/planets_project/ifr/core/common/logging/planets-log4j.xml");
	}    
	
	private Logger log = null;

	/**
	* PlanetsLogger constructor
	*/ 	
	private PlanetsLogger(String name, boolean pass)
	{
		log = Logger.getLogger(name);
		log.setAdditivity(pass);
	}
		
	/**
	* Returns a PlanetsLogger instance. 
	* eu.planets-project is used as fixed prefix. 
	*
	* @param name Name of the logger instance. 
	* @return PlanetsLogger
	*/
	public static PlanetsLogger getLogger(String name)
	{	
		return getLogger(name, null, true);
	}

	/**
	* Returns a PlanetsLogger instance.  
	*
	* @param clazz Name of the logger instance. 
	* @return PlanetsLogger
	*/
	public static PlanetsLogger getLogger(Class<?> clazz)
	{
		if(clazz == null)
			getRootLogger();
		return getLogger(clazz.getName(), null, true);
	}
	
	
	/**
	* Returns a PlanetsLogger instance. 
	* eu.planets-project is used as fixed prefix. 
	*
	* @param name Name of the logger instance. 
	* @param config Log4J xml configuration file. 	
	* @return PlanetsLogger
	*/
	public static PlanetsLogger getLogger(String name, String config)
	{	
		return getLogger(name, config, true);
	}

	/**
	* Returns a PlanetsLogger instance.  
	*
	* @param clazz Name of the logger instance. 
	* @param config Log4J xml configuration file. 	
	* @return PlanetsLogger
	*/
	public static PlanetsLogger getLogger(Class<?> clazz, String config)
	{
		if(clazz == null)
			getRootLogger();
		return getLogger(clazz.getName(), config, true);
	}
	
	
	/**
	* Returns a PlanetsLogger instance. 
	* eu.planets-project is used as fixed prefix. Also adds additional 
	* configuration directives to the PlanetsLogger backend. 
	*
	* @param name Name of the logger instance.
	* @param config Log4J xml configuration file. 
	* @param pass messages to ancestors.
	* @return PlanetsLogger
	*/
	public static PlanetsLogger getLogger(String name, String config, boolean pass)
	{
		String loggerName = name;
		if(!loggerName.startsWith(PLANETS_LOG_ROOT))
			loggerName = PLANETS_LOG_ROOT + "." + loggerName;

		if(!logTable.containsKey(loggerName))
		{
			if(config != null)
				PlanetsLoggerConfigurator.configure(config);
			
			PlanetsLogger logger = new PlanetsLogger(loggerName, pass);
			logTable.put(loggerName, logger);
			return logger;
		}
		return logTable.get(loggerName);
	}

	/**
	* Returns a PlanetsLogger instance. 
	* Also adds additional 
	* configuration directives to the PlanetsLogger backend. 
	*
	* @param clazz Name of the logger instance.
	* @param config Log4J xml configuration file. 
	* @param pass messages to ancestors.
	* @return PlanetsLogger
	*/
	public static PlanetsLogger getLogger(Class<?> clazz, String config, boolean pass)
	{
		if(clazz == null)
			return getRootLogger();

		return getLogger(clazz.getName(), config, pass);
	}

	/**
	* Returns the PlanetsLogger root instance. 
	* eu.planets-project is used as name. 
	*
	* @return PlanetsLogger
	*/
	public static PlanetsLogger getRootLogger()
	{
		return getLogger(PLANETS_LOG_ROOT, null, true);
	}

	/**
	 * @see org.apache.commons.logging.Log#isDebugEnabled()
	 */
	public boolean isDebugEnabled()
	{
		return log.isDebugEnabled();
	}

	/**
	 * @see org.apache.commons.logging.Log#isErrorEnabled()
	 */
	public boolean isErrorEnabled()
	{
		return log.isEnabledFor(Level.ERROR);
	}

	/**
	 * @see org.apache.commons.logging.Log#isFatalEnabled()
	 */
	public boolean isFatalEnabled()
	{
		return log.isEnabledFor(Level.FATAL);
	}

	/**
	 * @see org.apache.commons.logging.Log#isInfoEnabled()
	 */
	public boolean isInfoEnabled()
	{
		return log.isEnabledFor(Level.INFO);
	}
	
	/**
	 * @see org.apache.commons.logging.Log#isWarnEnabled()
	 */
	public boolean isWarnEnabled()
	{
		return log.isEnabledFor(Level.WARN);
	}

	/**
	 * @see org.apache.commons.logging.Log#isTraceEnabled()
	 */
	public boolean isTraceEnabled()
	{
		return false; // XXX
	//	return log.isEnabledFor(Level.TRACE);
	}

	/**
	 * @see org.apache.commons.logging.Log#trace(java.lang.Object)
	 */
	public void trace(Object message)
	{
	//	log.trace(message); //XXX
	}

	/**
	 * @see org.apache.commons.logging.Log#trace(java.lang.Object, java.lang.Throwable)
	 */
	public void trace(Object message, Throwable t)
	{
	//	log.trace(message, t); //XXX
	}

	/**
	 * @see org.apache.commons.logging.Log#debug(java.lang.Object)
	 */
	public void debug(Object message)
	{
		log.debug(message);
	}

	/**
	 * @see org.apache.commons.logging.Log#debug(java.lang.Object, java.lang.Throwable)
	 */
	public void debug(Object message, Throwable t)
	{
		log.debug(message, t);
	}

	/**
	 * @see org.apache.commons.logging.Log#warn(java.lang.Object)
	 */
	public void warn(Object message)
	{
		log.warn(message);
	}

	/**
	 * @see org.apache.commons.logging.Log#warn(java.lang.Object, java.lang.Throwable)
	 */
	public void warn(Object message, Throwable t)
	{
		log.warn(message, t);
	}

	/**
	 * @see org.apache.commons.logging.Log#error(java.lang.Object)
	 */
	public void error(Object message)
	{
		log.error(message);
	}

	/**
	 * @see org.apache.commons.logging.Log#error(java.lang.Object, java.lang.Throwable)
	 */
	public void error(Object message, Throwable t)
	{	
		log.error(message, t);
	}

	/**
	 * @see org.apache.commons.logging.Log#fatal(java.lang.Object)
	 */
	public void fatal(Object message)
	{
		log.fatal(message);
	}

	/**
	 * @see org.apache.commons.logging.Log#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public void fatal(Object message, Throwable t)
	{
		log.fatal(message, t);
	}

	/**
	 * @see org.apache.commons.logging.Log#info(java.lang.Object)
	 */
	public void info(Object message)
	{
		log.info(message);
	}

	/**
	 * @see org.apache.commons.logging.Log#info(java.lang.Object, java.lang.Throwable)
	 */
	public void info(Object message, Throwable t)
	{
		log.info(message, t);
	}
}
