/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.ifr.core.common.api;


/**
 * PlanetsLogger Interface
 *
 * @author Klaus Rechert, ALUF
 */
public interface PlanetsLogger
{
	public boolean isDebugEnabled();
	public boolean isErrorEnabled();
	public boolean isFatalEnabled();
	public boolean isInfoEnabled();
	public boolean isWarnEnabled();
	public boolean isTraceEnabled();
	public void trace(Object message);
	public void trace(Object message, Throwable t);
	public void debug(Object message);
	public void debug(Object message, Throwable t);
	public void warn(Object message);
	public void warn(Object message, Throwable t);
	public void error(Object message);
	public void error(Object message, Throwable t);
	public void fatal(Object message);
	public void fatal(Object message, Throwable t);
	public void info(Object message);
	public void info(Object message, Throwable t);
}
