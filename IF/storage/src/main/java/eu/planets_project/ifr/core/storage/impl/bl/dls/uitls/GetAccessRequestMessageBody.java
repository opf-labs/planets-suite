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
 * @author CFWilson
 *
 */
public class GetAccessRequestMessageBody extends Body {
	private String sequenceId = "";
	@SuppressWarnings("unused")
	private GetAccessRequestMessageBody() {/** Empty private not to be called */};
	/**
	 * @param sequenceId
	 */
	public GetAccessRequestMessageBody(String sequenceId) {
		super();
		this.sequenceId = sequenceId;
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
		msgSink.write("<q0:GetAccessRequest>" + StringUtils.lineSeparator);
		msgSink.write("<q0:SequenceId>" + StringUtils.lineSeparator);
		msgSink.write(this.sequenceId + StringUtils.lineSeparator);
		msgSink.write("</q0:SequenceId>" + StringUtils.lineSeparator);
		msgSink.write("</q0:GetAccessRequest>" + StringUtils.lineSeparator);
		msgSink.write("</soapenv:Body>" + StringUtils.lineSeparator);
		msgSink.write("</soapenv:Envelope>" + StringUtils.lineSeparator);
		nameSpaceStack.popScope();
	}

}
