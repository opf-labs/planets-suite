package eu.planets_project.tb.impl.services.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import eu.planets_project.tb.api.services.util.ServiceRequestBuilder;

/**
 * @author Andrew Lindley, ARC
 * The following restrictions apply:
 *  - only File or FileArrays are currently supported
 *  - if FileArray, it must contain the all three tokens it requires
 *  - no multiple TBTokens within a file allowed (e.g. two tbFile tokens not supported)
 */
public class ServiceRequestBuilderImpl implements ServiceRequestBuilder{
	
	private String xmlRequestTemplate = "";
	//Map<position i+"", localFileRef> to keep position of the fileRef's position
	private Map<String,String> hmLocalFileRefs = new HashMap<String,String>();
	
	public ServiceRequestBuilderImpl(String xmlRequestTemplate, Map<String,String> localFileRefs){
		this.xmlRequestTemplate = xmlRequestTemplate;
		this.hmLocalFileRefs = localFileRefs;
	}
	
	/**
	 * Constructor for the case that we're only using a fileRef and not an Array
	 * @param xmlRequestTemplate
	 * @param localFileRef
	 */
	public ServiceRequestBuilderImpl(String xmlRequestTemplate, String localFileRef){
		this.xmlRequestTemplate = xmlRequestTemplate;
		this.hmLocalFileRefs.put("0", localFileRef);
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
		tokenizer = this.xmlRequestTemplate.split(TAG_FILE);
		if(tokenizer.length!=1){
			//e.g. "<item>"
			sArrayLineStart = tokenizer[0];
			temp = tokenizer[1];
		}
		tokenizer = this.xmlRequestTemplate.split(TAG_FILEARRAYLINE_END);
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
	 * @see eu.planets_project.tb.api.services.util.ServiceRequestBuilder#getSupportedTagValues()
	 */
	public List<String> getSupportedTagValues() {
		List<String> ret = new Vector<String>();
		ret.add(TAG_FILE);
		ret.add(TAG_FILEARRAYLINE_START);
		ret.add(TAG_FILEARRAYLINE_END);
		return ret;
	}

}
