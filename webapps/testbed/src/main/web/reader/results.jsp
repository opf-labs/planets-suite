<%@ page import= "java.io.*, java.net.URLDecoder, javax.activation.MimetypesFileTypeMap, eu.planets_project.tb.utils.ExperimentUtils" %><% 

// Pick up the parameters:
String eid = request.getParameter("eid");
String set = request.getParameter("set");

// Decode the file name (might contain spaces and on) and prepare file object.
eid = URLDecoder.decode(eid, "UTF-8");

// Set the headers appropriately:
response.setContentType( "text/csv" );

//response.setContentLength( ((Long)f.length()).intValue() );

// Now stream out the data:
ServletOutputStream op = response.getOutputStream();

if( set == null || "".equals(set) || "exec".equals(set ) ) {
    response.setHeader( "Content-Disposition", "attachment; filename=\"" + "results.csv" + "\"" );
    ExperimentUtils.outputResults( op, eid, ExperimentUtils.DATA_FORMAT.CSV );
} else if( "ana".equals(set) ) {
    response.setHeader( "Content-Disposition", "attachment; filename=\"" + "analysis.csv" + "\"" );
    ExperimentUtils.outputAnalysis( op, eid, ExperimentUtils.DATA_FORMAT.CSV );
}
%>