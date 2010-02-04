/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.bl.dls.uitls;

import java.io.*;
import org.apache.soap.*;
import org.apache.soap.util.StringUtils;
import org.apache.soap.util.xml.*;
import org.apache.soap.rpc.SOAPContext;

/**
 * @author carl
 *
 */
public class GetMessageBody extends Body {
	private String sequenceId = "";
	private String domId = "";
	private String deliveryFileName = "";
	
	@SuppressWarnings("unused")
	private GetMessageBody() {/** Empty block to prevent no arg construction */};
	/**
	 * @param sequenceId
	 * @param domId
	 * @param deliveryName
	 */
	public GetMessageBody(String sequenceId, String domId, String deliveryName) {
		super();
		this.sequenceId = sequenceId;
		this.domId = domId;
		this.deliveryFileName = deliveryName;
	}
	@Override
	public void marshall(String strEncodeStyle,
						 Writer msgSink,
						 NSStack nameSpaceStack,
						 XMLJavaMappingRegistry registry,
						 SOAPContext context) throws IOException{
		// Start Element
		msgSink.write("<soapenv:Envelope>" + StringUtils.lineSeparator);
		msgSink.write("<soapenv:Body>" + StringUtils.lineSeparator);
		msgSink.write("<q0:Get>" + StringUtils.lineSeparator);
		msgSink.write("<q0:accessRequestCollectionXml>" + StringUtils.lineSeparator);
		msgSink.write("<?xml version=\"1.0\"?><AccessRequestCollection xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		msgSink.write("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" sequenceID=\"");
		msgSink.write(this.sequenceId + "\">");
		msgSink.write("<Access><domID>");
		msgSink.write(this.domId + "</domID>");
		msgSink.write("<deliveryFilename>" + this.deliveryFileName + "</deliveryFilename>");
		msgSink.write("</Access></AccessRequestCollection>" + StringUtils.lineSeparator);
		msgSink.write("</q0:accessRequestCollectionXml>" + StringUtils.lineSeparator);
		msgSink.write("</q0:Get>" + StringUtils.lineSeparator);
		msgSink.write("</soapenv:Body>" + StringUtils.lineSeparator);
		msgSink.write("</soapenv:Envelope>" + StringUtils.lineSeparator);
		nameSpaceStack.popScope();
	}
}
