package eu.planets_project.tb.impl.services.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.api.data.util.DataHandler;
import eu.planets_project.tb.api.services.util.ServiceRequestBuilder;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;

/**
 * @author Andrew Lindley, ARC
 * The following restrictions apply:
 *  - File, FileArrays and Base64ByteArray are currently supported
 *  - if FileArray, it must contain the all three tokens it requires
 *  - no multiple TBTokens within a file allowed (e.g. two tbFile tokens not supported)
 */
public class ServiceRequestBuilderImpl implements ServiceRequestBuilder{
	
	private String xmlRequestTemplate = "";
	//Map<position i+"", localFileRef> to keep position of the fileRef's position
	private Map<String,String> hmLocalFileRefs = new HashMap<String,String>();
	//A logger for this:
    private Log log = LogFactory.getLog(ServiceRequestBuilderImpl.class);
    //DataHandler util class for e.g. base64 encoding
    DataHandler dh = new DataHandlerImpl();
	
	public ServiceRequestBuilderImpl(String xmlRequestTemplate, Map<String,String> localFileRefs){
		this.xmlRequestTemplate = xmlRequestTemplate;
		this.hmLocalFileRefs = this.convertToAbsoluteLocalFileRefs(localFileRefs);
	}
	
	/**
	 * Constructor for the case that we're only using a fileRef and not an Array
	 * @param xmlRequestTemplate
	 * @param localFileRef
	 */
	public ServiceRequestBuilderImpl(String xmlRequestTemplate, String localFileRef){
		this.xmlRequestTemplate = xmlRequestTemplate;
		this.hmLocalFileRefs.put("0", this.convertToAbsoluteFileRef(localFileRef));
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceRequestBuilder#buildXMLServiceRequest()
	 */
	public String buildXMLServiceRequest(){
		
		//scenario1: build for FILE:
		if((isFileTemplate())&&(!isFileArrayTemplate())){
			return buildFileXMLRequest();
		}
		
		//scenario2: build for FILE_ARRAY:
		if(isFileArrayTemplate()){
			return buildFileArrayXMLRequest();
		}
		
		//scenario3: build for BASE64BYTEARRAY
		if(isBase64ByteArrayTemplate()){
			return buildBase64ByteArrayXMLRequest();
		}
		
		return "";
	}
	
	/**
	 * Parse through the XML, look for the token and add one file reference
	 * @return
	 */
	private String buildFileXMLRequest(){
		String XMLRequest = "";
		String sMessageStart ="", sMessageEnd ="";
		
	 //1build the message parts
		String tokenizer[] =this.xmlRequestTemplate.split(TAG_FILE);
		if(tokenizer.length!=1){
			//e.g. "<item>"
			sMessageStart = tokenizer[0];
			sMessageEnd = tokenizer[1];
		}
		
	 //2add the file ref:
		String sFileRef ="";
		//contains only one reference
		Iterator<String> itLocalFileRefs = this.hmLocalFileRefs.values().iterator();
		while(itLocalFileRefs.hasNext()){
			sFileRef = itLocalFileRefs.next();
		}
		
	 //3 complete the xml request message
		XMLRequest = sMessageStart + sFileRef + sMessageEnd;
		return XMLRequest;
	}
	
	/**
	 * Parse through the XML, look for tokens and add file references
	 * @return
	 */
	private String buildFileArrayXMLRequest(){
		
		String XMLRequest = "";
		String sMessageStart ="", sMessageEnd ="", sArrayLineStart="", sArrayLineEnd="";
		String temp = "";
		
	 //1build the message parts
		String tokenizer[] = this.xmlRequestTemplate.split(TAG_FILEARRAYLINE_START);
		if(tokenizer.length!=1){
			sMessageStart = tokenizer[0];
			temp = tokenizer[1];
		}

		//temp.split(TAG_FILE)[0];
		tokenizer = temp.split(TAG_FILE);
		if(tokenizer.length!=1){
			//e.g. "<item>"
			sArrayLineStart = tokenizer[0];
			temp = tokenizer[1];
		}
		tokenizer = temp.split(TAG_FILEARRAYLINE_END);
		if(tokenizer.length!=1){
			//e.g. "</item>"
			sArrayLineEnd = tokenizer[0];
			sMessageEnd = tokenizer[1];
		}
		
	 //2add the file refs:
		String sArrayLines = "";
		int elements = this.hmLocalFileRefs.size();
		for(int i=0;i<elements;i++){
			String fileRef = this.hmLocalFileRefs.get(i+"");
			if(fileRef!=null){
				//build an arrayLine and add it
				sArrayLines += sArrayLineStart + fileRef + sArrayLineEnd;
			}
			else{
				//not item was added for this position - we have to look on
				elements++;
			}
		}
		
	 //3 complete the xml request message
		XMLRequest = sMessageStart + sArrayLines + sMessageEnd;
		return XMLRequest;
	}
	
	/**
	 * Parse through the XML, look for the token and add the base64ByteArray file
	 * @return
	 */
	private String buildBase64ByteArrayXMLRequest(){
		String XMLRequest = "";
		String sMessageStart ="", sMessageEnd ="";
		
	 //1build the message parts
		String tokenizer[] =this.xmlRequestTemplate.split(TAG_BASE64BYTEARRAY);
		if(tokenizer.length!=1){
			//e.g. "<item>"
			sMessageStart = tokenizer[0];
			sMessageEnd = tokenizer[1];
		}
		
	 //2add the file ref:
		String sFileRef ="";
		//contains only one reference
		Iterator<String> itLocalFileRefs = this.hmLocalFileRefs.values().iterator();
		while(itLocalFileRefs.hasNext()){
			sFileRef = itLocalFileRefs.next();
		}
		
	 //3 encode the file to base64
		String sBase64File = "";
		try {
			sBase64File = dh.encodeToBase64ByteArrayString(new File(sFileRef));
		} catch (IOException e) {
			log.error("Failure in encoding the provided fileReference "+sFileRef+" to a Base64ByteArray");
		}

		
	 //4 complete the xml request message
		XMLRequest = sMessageStart + sBase64File + sMessageEnd;
		return XMLRequest;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceRequestBuilder#isFileTemplate()
	 */
	public boolean isFileTemplate(){	
		boolean bFound = true;
		if(xmlRequestTemplate!=null){
			bFound=xmlRequestTemplate.indexOf(TAG_FILE)>0 ? true : false;
		}
		return bFound;
	}
	

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceRequestBuilder#isFileArrayTemplate()
	 */
	public boolean isFileArrayTemplate(){
		boolean b1=true,b2=true,b3 = true;
		if(xmlRequestTemplate!=null){
			b1=xmlRequestTemplate.indexOf(TAG_FILE)>0 ? true : false;
			b2=xmlRequestTemplate.indexOf(TAG_FILEARRAYLINE_END)>0 ? true : false;
			b3=xmlRequestTemplate.indexOf(TAG_FILEARRAYLINE_START)>0 ? true : false;
		}
		return b1&&b2&&b3;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceRequestBuilder#isBase64ByteArrayTemplate()
	 */
	public boolean isBase64ByteArrayTemplate() {
		boolean bFound = true;
		if(xmlRequestTemplate!=null){
			bFound=xmlRequestTemplate.indexOf(TAG_BASE64BYTEARRAY)>0 ? true : false;
		}
		return bFound;
	}
	
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceRequestBuilder#getSupportedTagValues()
	 */
	public List<String> getSupportedTagValues() {
		List<String> ret = new Vector<String>();
		ret.add(TAG_FILE);
		ret.add(TAG_FILEARRAYLINE_START);
		ret.add(TAG_FILEARRAYLINE_END);
		ret.add(TAG_BASE64BYTEARRAY);
		return ret;
	}
	
	/**
	 * Takes given file refs, converts them into File objects and returns
	 * the proper object that's used within this class
	 * e.g. ../data/file1.doc --> C:/input/data/file1.doc
	 * @param localFileRef
	 * @return
	 */
	private Map<String,String> convertToAbsoluteLocalFileRefs(Map<String,String> refs){
		Map<String,String> ret = new HashMap<String,String>();
		Iterator<String> sKeys = refs.keySet().iterator();
		while(sKeys.hasNext()){
			String key = sKeys.next();
			String fileRef = refs.get(key);
			ret.put(key, convertToAbsoluteFileRef(fileRef));
		}
		return ret;
	}

	/**
	 * Takes a given file ref, converts it into a File object and returns
	 * its abolsut path
	 * e.g. ../data/file1.doc --> C:/input/data/file1.doc
	 * @param localFileRef
	 * @return
	 */
	private String convertToAbsoluteFileRef(String localFileRef){
		File f = new File(localFileRef);
		if(!f.canRead()){
			log.error("error retrieving file ref "+localFileRef+" to absolute path");
			//in this case add the non-absolut path
			return localFileRef;
		}
		else{
			return f.getAbsolutePath();
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.services.util.ServiceRequestBuilder#isCallByValue()
	 */
	public boolean isCallByValue() {
		boolean bByValue = false;
		if(this.isFileTemplate()||this.isFileArrayTemplate())
			bByValue = false;
		
		if(this.isBase64ByteArrayTemplate())
			bByValue = true;
		
		return bByValue;
	}


}
