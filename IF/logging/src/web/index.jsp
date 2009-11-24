<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="org.apache.log4j.Level"%>
<%@ page import="org.apache.log4j.LogManager"%>
<%@ page import="org.apache.log4j.Logger"%>
<%@ page import="org.apache.log4j.Appender"%>
<%@ page import="org.apache.log4j.FileAppender"%>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.io.*" %>
<%@ page import="eu.planets_project.ifr.core.common.logging.PlanetsLogger" %>

<%@include file="inc/header.inc" %>

<%!
HashMap getLoggerMap()
{
	Enumeration loggers = LogManager.getCurrentLoggers();
	HashMap loggersMap = new HashMap(16);  
	Logger rootLogger = LogManager.getRootLogger();

	loggersMap.put(rootLogger.getName(), rootLogger);
	while(loggers.hasMoreElements())
	{
		Logger logger = (Logger)loggers.nextElement();    
		String name = logger.getName();
		if(logger.getName().startsWith("eu.planets"))
			loggersMap.put(logger.getName(), logger);
		
	}
	return loggersMap;
}

String getLogFile(Logger logger)
{
	Enumeration apps = logger.getAllAppenders();
	while (apps.hasMoreElements()) {
		Appender app = (Appender)apps.nextElement();
		if ( app instanceof FileAppender )  {
			return ( (FileAppender) app ).getFile();	
		}
	}
	return null;
}

String getLogTail(String file, int length)
{
	if(file == null)
		return null;

	FileInputStream fin = null;
	String mess = "";
	try {
		File f = new File( file );
		if ( !f.exists() )
			return null;
		
		fin = new FileInputStream( f );
		long max = f.length();
		if ( max < 0 )
			throw new RuntimeException( "length of " + f + " is less than 0" );
		
		long pos = max - length - 1;
		if ( pos < 0 ) {
			pos = 0;
			length = (int) f.length();
		}
		
		byte[] buffer = new byte[length];
		fin.skip( pos );
		int len = fin.read(buffer, 0, length);
		if ( len > 0 )
			mess = new String(buffer, 0, len);
	} 
	catch ( Exception e ) 
	{
		throw new RuntimeException( "cannot show log tail: " + e );
	} 
	finally 
	{
		if ( fin != null )
			try { fin.close(); } catch ( Exception ex ) {}
	}
	return mess;
}

void setLogLevel(HashMap loggersMap, String loggerName, String level)
{
	Logger logger = (Logger)loggersMap.get(loggerName);
	if(logger != null)
		logger.setLevel(Level.toLevel(level));
}

String[] logLevels = { "debug", "info", "warn", "error", "fatal", "off" };      
%>

<%
String paramLogSelection = (String)request.getParameter("logFile");
HashMap loggersMap = getLoggerMap();
Set loggerKeys = loggersMap.keySet();
String[] keys = new String[loggerKeys.size()];
keys = (String[])loggerKeys.toArray(keys);
Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);

String paramAction = request.getParameter("action");
if(paramAction != null)
{
	if(paramAction.equals("newLogLevel"))
	{
		String pLogger = request.getParameter("logger");
		String pLogLevel = request.getParameter("newLogLevel");
		if(pLogger != null && pLogLevel != null)
			setLogLevel(loggersMap, pLogger, pLogLevel);
	}
	else if(paramAction.equals("hide"))
	{
		String pLogger = request.getParameter("logger");
		if(pLogger != null)
			session.setAttribute(pLogger, "hide");
	}
	else if(paramAction.equals("show"))
	{
		String pLogger = request.getParameter("logger");
		if(pLogger != null)
			session.removeAttribute(pLogger);
	}
}
/*
%>
<form name="logSelection" action="index.jsp">
	Select logger &nbsp;
	<select name="logFile">
		<option value="all">all</option>
	<%
	for(int i = 0; i < keys.length; i++)
	{
		Logger logger = (Logger)loggersMap.get(keys[i]);
		if(logger == null)
			continue;
		String loggerName = logger.getName();
		String[] __test = loggerName.split("\\.");
		if(__test != null)
		   loggerName = __test[0];

		String selected = "";
		if(loggerName.equals(paramLogSelection))
			selected = "selected";
		
	%>
		<option <%=selected %> value="<%=loggerName%>"><%=loggerName%></option>
	<%
	}
	%>
	</select>
	&nbsp;
	<input type="submit">
</form>
<p>
<%
*/          
for(int i=0; i < keys.length; i++)
{
	String loggerName = null;
	String loggerEffectiveLevel = null;
	String loggerParent = null;
		
	Logger logger = (Logger)loggersMap.get(keys[i]);
	if(logger != null)
	{
		loggerName = logger.getName();
		loggerEffectiveLevel = String.valueOf(logger.getEffectiveLevel());
		loggerParent = (logger.getParent() == null ? null : logger.getParent().getName());
	}
	if(paramLogSelection != null && !paramLogSelection.equals("all") && !paramLogSelection.equals(loggerName))
		continue;
%>

<table class="standardTable"cellspacing="1" width="100%">
	<tr class="crumb">
		<th width="50%">Logger</th>
		<th width="50%">Parent Logger</th>
	</tr>
	<tr class="crumb">
		<td><%=loggerName%></td>
		<td><%=loggerParent%></td>
	</tr>
</table>

<table class="standardTable"cellspacing="1" width="100%">
	<tr class="crumb">
		<td align=left width=50%>Current Level</td>
		<td align=left><%=loggerEffectiveLevel%></td>
	</tr>
	<tr class="crumb">
		<td align=left width=50%>Change log level to</td>
		<td align=left>
	<%                            
	for(int cnt=0; cnt<logLevels.length; cnt++)
	{
		String url = "index.jsp?";
		// url += request.getQueryString();
		url += "action=newLogLevel&logger=" + loggerName;
		url += "&newLogLevel=" + logLevels[cnt];

		if(logger.getLevel() == Level.toLevel(logLevels[cnt]) || 
			logger.getEffectiveLevel() == Level.toLevel(logLevels[cnt]))		
			{%> [<%=logLevels[cnt].toUpperCase()%>] <% }
		else 
			{%> <a href='<%=url%>'>[<%=logLevels[cnt]%>]</a>&nbsp;<%}
	}
	%>
		</td>
	</tr>

</table>
<table class="standardTable"cellspacing="1" width="100%">
	<% 
	String logFile = getLogFile(logger);
	if(logFile != null)
	{ 
	%>
	<tr class="crumb">
		<th width=50%>Log file</th>		
		<th  align="left">
		<%	
		if(session.getAttribute(loggerName) == null)
		{
			String url = "index.jsp?";
			url += "action=hide&logger=" + loggerName;
			%><a href='<%=url%>'>[ hide log]</a><%
		}
		else
		{
			String url = "index.jsp?";
			url += "action=show&logger=" + loggerName;
			%><a href='<%=url%>'>[ show log ]</a><%
		}
		%>
		</th>
	</tr>
	<tr class="crumb">
		<td colspan=2><%=logFile%></td>
	</tr>
	<%
		if(session.getAttribute(loggerName) == null)
		{%>
	<tr>
		<td colspan="2" align="center">
		<textarea cols="80" rows="25"><%= getLogTail(logFile, 2048) %>
		</textarea>
		</td>
	</tr>
	<% 
		}
	}
	else 
	{ %><tr><td colspan="4"><hr></td></tr><%} 
	%>
	<tr><td colspan=2>&nbsp;</td></tr>
</table>
<hr>
<% 
}

%>
<%@include file="inc/footer.inc" %>
