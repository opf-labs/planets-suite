<%@ page import= "java.io.*, java.net.URLDecoder, javax.activation.MimetypesFileTypeMap, eu.planets_project.tb.impl.data.util.DataHandlerImpl" %><% 

//
// For a model download servlet, see http://balusc.blogspot.com/2007/07/fileservlet.html
//

// Pick up the parameters:
String fid = request.getParameter("fid");

// Decode the file name (might contain spaces and on) and prepare file object.
fid = URLDecoder.decode(fid, "UTF-8");

// Start up the testbed data handler:
DataHandlerImpl dh = new DataHandlerImpl();

// Get the full filename, and the real filename:
String filename = dh.getName(fid); 

// Open the file:
File f = dh.getFile(fid);

// Guess the mime type:
String mimetype = new MimetypesFileTypeMap().getContentType(f);

// Set the headers appropriately:
response.setContentType( (mimetype != null) ? mimetype : "application/octet-stream" );
response.setContentLength( ((Long)f.length()).intValue() );
// This should allow the content to be rendered by the browser, but the filename is ignored.
response.setHeader( "Content-Disposition", "inline; filename=\"" + filename + "\"" );
// The following alternative forces a download:
//response.setHeader( "Content-Disposition", "attachment; filename=\"" + filename + "\"" );

// Now stream out the data:
byte[] bbuf = new byte[2*1024];
DataInputStream in = new DataInputStream(new FileInputStream(f));
int length = 0;
ServletOutputStream op = response.getOutputStream();

try {
    while ((in != null) && ((length = in.read(bbuf)) != -1))
    {
        op.write(bbuf,0,length);
    }
} finally {
    in.close();
    op.flush();
    op.close();
}

%>