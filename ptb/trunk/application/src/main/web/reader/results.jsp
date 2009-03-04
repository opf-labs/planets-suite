<%@ page import= "java.io.*, java.net.URLDecoder, javax.activation.MimetypesFileTypeMap, eu.planets_project.tb.utils.ExperimentUtils" %><% 

// Pick up the parameters:
String eid = request.getParameter("eid");

// Decode the file name (might contain spaces and on) and prepare file object.
eid = URLDecoder.decode(eid, "UTF-8");

// Set the headers appropriately:
response.setContentType( "text/csv" );

//response.setContentLength( ((Long)f.length()).intValue() );

// This should allow the content to be rendered by the browser, but the filename is ignored.
//response.setHeader( "Content-Disposition", "inline; filename=\"" + "results.csv" + "\"" );

// The following alternative forces a download:
response.setHeader( "Content-Disposition", "attachment; filename=\"" + "results.csv" + "\"" );

// Now stream out the data:
ServletOutputStream op = response.getOutputStream();

ExperimentUtils.outputResults( op, eid, ExperimentUtils.DATA_FORMAT.CSV );

%>