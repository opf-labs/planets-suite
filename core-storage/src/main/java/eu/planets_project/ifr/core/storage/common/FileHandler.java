package eu.planets_project.ifr.core.storage.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * 
 * @author CFwilson
 *
 */
public class FileHandler {
	// Statics for the xpath expressions
	private static final String XPATH_ENCODED_DATA = "/file/binData";
	private static final String XPATH_MD5 = "/file/@checksum";
	private static final String MD5_ALGORITHM_IDENTIFER = "MD5";

	// the decoded byte sequence
	private byte[] decodedBytes = null;

	/**
	 * 
	 * @param inStream
	 */
	public FileHandler(InputStream inStream) throws Exception {
		try {
			ByteArrayOutputStream _byteStream = new ByteArrayOutputStream();
			int _byte;
			while((_byte = inStream.read()) != -1) {
				_byteStream.write(_byte);
			}
			this.decodedBytes = _byteStream.toByteArray();
		} catch (IOException _exp) {
			throw new RuntimeException(_exp);
		}
	}
	
	/**
	 * 
	 * @param xmlEncoded
	 */
	public FileHandler(String xmlEncoded) throws Exception {
		try {
	        // XPath Factory
	        XPath _xpath = XPathFactory.newInstance().newXPath();
	
	        // Use xpath to retrieve the binary data value
	        XPathExpression _expBinData = _xpath.compile(XPATH_ENCODED_DATA);
	        // use xpath to retrieve the checksum
	        XPathExpression _expMD5 = _xpath.compile(XPATH_MD5);

	        // Get the decoded bytestream
	        this.decodedBytes = FileHandler.decodeBase64EncodedString(_expBinData.evaluate(new InputSource(new StringReader(xmlEncoded))));
	        // And the MD5 checksum in the document
	        String _checksum = _expMD5.evaluate(new InputSource(new StringReader(xmlEncoded)));
	        // Now check the integrity
			if (!_checksum.equals(FileHandler.getMD5HexString(this.decodedBytes))) {
				throw new Exception("Transmitted bytestream failed MD5 integrity check.");
			}
		} catch (XPathExpressionException _exp) {
			System.out.println("XPATH EXCEPTION");
			throw new RuntimeException(_exp);
		}
	}

	/**
	 * 
	 * @param encodedString
	 * @return
	 */
	public static byte[] decodeBase64EncodedString(String encodedString) {
		return Base64.decodeBase64(encodedString);
	}
	
	/**
	 * 
	 * @param byteSequence
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] getMD5Checksum(byte[] byteSequence) throws NoSuchAlgorithmException {
		MessageDigest _md5Algorithm = MessageDigest.getInstance(FileHandler.MD5_ALGORITHM_IDENTIFER);
		_md5Algorithm.update(byteSequence);
		return _md5Algorithm.digest();		
	}
	
	/**
	 * 
	 * @param byteSequence
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getMD5HexString(byte[] byteSequence)  throws NoSuchAlgorithmException {
		byte[] _checksum = FileHandler.getMD5Checksum(byteSequence);
		String _retVal = "";
		for (int _loop = 0; _loop < _checksum.length; _loop++) {
			_retVal += Integer.toString((_checksum[_loop] & 0xff) + 0x100, 16).substring(1);
		}
		return _retVal;
	}

	/**
	 * 
	 * @param decodedBytes
	 * @return
	 */
	public static String base64EncodeBytes(byte[] decodedBytes) {
		return Base64.encodeBase64String(decodedBytes);
	}
	/**
	 * 
	 * @return
	 */
	public String getBase64EncodedString() {
		if (decodedBytes != null)
			return FileHandler.base64EncodeBytes(this.decodedBytes);
		else
			return null;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] getDecodedBytes() {
		return this.decodedBytes.clone();
	}

	/**
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 * @throws UnsupportedEncodingException
	 */
	public String getXmlDocument() throws ParserConfigurationException, TransformerConfigurationException, TransformerException, UnsupportedEncodingException, NoSuchAlgorithmException {
		// Create a new DOM Document
		Document _document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		// Add a file element with a size attribute 
		Element _fileElement = _document.createElement("file");
		_fileElement.setAttribute("size", Integer.toString(this.decodedBytes.length));
		_fileElement.setAttribute("checksumType", FileHandler.MD5_ALGORITHM_IDENTIFER);
		_fileElement.setAttribute("checksum", FileHandler.getMD5HexString(this.decodedBytes));
		// Add the node containing the base64 encoded data
		Node _fileContent = _fileElement.appendChild(_document.createElement("binData"));
		_fileContent.appendChild(_document.createTextNode(this.getBase64EncodedString()));
		_document.appendChild(_fileElement);

		// Now convert to a string for the wire
		Transformer _transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter _writer = new StringWriter(128);
		_transformer.transform(new DOMSource(_document), new StreamResult(_writer));
		return _writer.getBuffer().toString();
	}
}
