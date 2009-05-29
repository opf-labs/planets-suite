<%@ page import= "java.io.*, java.net.URLDecoder, eu.planets_project.services.utils.FileUtils, eu.planets_project.tb.impl.data.util.DataHandlerImpl, eu.planets_project.tb.api.data.util.DigitalObjectRefBean" %><% 

// Pick up the parameters:
String fid = request.getParameter("fid");

// Decode the file name (might contain spaces and on) and prepare file object.
fid = URLDecoder.decode(fid, "UTF-8");

// Start up the testbed data handler:
DigitalObjectRefBean dh = new DataHandlerImpl().get(fid);

// Get the full filename, and the real filename:
String filename = dh.getName(); 

// Guess the mime type:
String mimetype = dh.getMimeType();

// Set the headers appropriately:
response.setContentType( (mimetype != null) ? mimetype : "application/octet-stream" );
if( dh.getSize() >= 0 ) {
    response.setContentLength( ((Long)dh.getSize()).intValue() );
}
// This should allow the content to be rendered by the browser, but the filename is ignored.
response.setHeader( "Content-Disposition", "inline; filename=\"" + filename + "\"" );
// The following alternative forces a download:
//response.setHeader( "Content-Disposition", "attachment; filename=\"" + filename + "\"" );

// Now stream out the data:
InputStream in = dh.getContentAsStream();
ServletOutputStream op = response.getOutputStream();

try {
    
    FileUtils.writeInputStreamToOutputStream(in,op);
    
} finally {
    if( in != null ) in.close();
    op.flush();
    op.close();
}

%>