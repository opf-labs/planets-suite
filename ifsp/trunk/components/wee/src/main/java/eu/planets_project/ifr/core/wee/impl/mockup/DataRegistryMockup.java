/**
 * 
 */
package eu.planets_project.ifr.core.wee.impl.mockup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.xml.soap.SOAPException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.ifr.core.wee.impl.utils.FileUtil;
import eu.planets_project.ifr.core.wee.impl.utils.RegistryUtils;

/**
 * This is a mock implementation of the DataManagerLocal Interface which is used
 * as interim solution to overcome the problem of programmatically authenticating against the SSO.
 * The implementation closely hooked up between registry paths and QNames, which probably need
 * to be checked and reworked after switching to the Planets data registry.
 * TODO: delete this class
 */

public class DataRegistryMockup implements DataManagerLocal{
	
	private static final Log log = LogFactory.getLog(DataRegistryMockup.class);
	 //e.g. ../server/default/data/wee
    private String weeDirBase ="";
    private String weewfTemplatesDir ="";
    private String weeTmpDir ="";
    
    public DataRegistryMockup(){
    	readDirectorySettings();
    	createWFTemplateDir();
    }
    
    private void readDirectorySettings(){
    	weeDirBase = RegistryUtils.getWeeDirBase();
    	weewfTemplatesDir = RegistryUtils.getWeeWFTemplateDir();
    	weeTmpDir = RegistryUtils.getWeeTmpDir();
    }
    
    private void createWFTemplateDir() {
   	
			/* 
			File root = new File(this.weewfTemplatesDir);
    	String dir = root.getAbsolutePath();
    	if(!(root.canRead()&&root.isDirectory())){
    	  root.mkdirs();
    		log.error("DataRegistryMockup: created WFTemplateDir: "+root.getAbsolutePath() + " exists: "+root.exists());
    	}
    	*/

			File fWeeDirBase = new File(weeDirBase);
			if( !fWeeDirBase.exists() ) {
      	if(fWeeDirBase.mkdir()) {
      		log.info("DataRegistryMockup: created weeDirBase: "+weeDirBase);
      	} else {
      		log.error("DataRegistryMockup: unable to create weeDirBase: "+weeDirBase);
      		return;
      	}
			} 
			
			String templatesPath = weeDirBase+System.getProperty("file.separator")+weewfTemplatesDir;
			File fTemplatesPath = new File(templatesPath);
			if( !fTemplatesPath.exists() ) {
      	if(fTemplatesPath.mkdir()) {
      		log.info("DataRegistryMockup: created weeTemplatesDir: "+ templatesPath);
      	} else {
      		log.error("DataRegistryMockup: unable to create weeTemplatesDir: "+templatesPath);
      		return;
      	}
			} 
			    	
    }
	
	/**
	 * Lists the PDURIs that are children of the URI passed.
	 * The passed PDURI should be that of a DataRegistry folder.<p/>
	 * If the user passes a <code>null</code> URI as the parameter the method returns the root PDURI for the Data Registry.<p/
	 * 
	 * @param	pdUri
	 *			The PDURI that the caller wishes to find the children of.
	 * @return	An array of PDURIs that are children of the parameter PDURI. An empty list is returned for an
	 * empty directory. <code>null</code> is returned if pdURI is not a folder.
	 * @throws 	SOAPException
	 */
	public URI[] list(URI pdUri) throws SOAPException{
		// not implemented
		return null;
	}

	public URI createLocalSandbox() throws URISyntaxException {
		// not implemented
		return null;
	}

	public InputStream retrieve(URI pdURI) throws PathNotFoundException,
			URISyntaxException {
		// not implemented
		return null;
	}

	public void store(URI pdURI, InputStream stream) throws LoginException,
			RepositoryException, URISyntaxException {
		// not implemented
	}

	public void storeBinary(URI pdURI, byte[] binary) throws LoginException,
			RepositoryException, URISyntaxException {
		//as we're mapping to a file based system we need to extract a name for the node's data
		String uri = pdURI+"";
		int p = uri.lastIndexOf("/");
		if(p==-1){
			throw new RepositoryException("DataRegistryMockup error storing wftemplate name");
		}
		String templateName = uri.substring(p+1, uri.length());
							
		try {
			FileUtil.writeFile(binary, this.weeDirBase+"/"+uri.substring(0,p), templateName);
		} catch (IOException e) {
			throw new RepositoryException("DataRegistryMockup error storing wftemplate: ",e);
		}
	}

	/**
	 * Performs a recursive search below the folder identified by the passed PDURI and returns a
	 * list of URIs identifying all files with an extension matching the string passed.<p/>
	 * If passed the root data registry URI and the string "doc" this method would return
	 * an array of PDURIs, one for each file in the entire data registry with a name ending <code>doc</code>.
	 *
	 * @param	pdURI
	 *			A PDURI identifying the root location of the search
	 * @param	ext
	 *		A search string used to filter the returned list of PDURIs.  Files will only be returned
	 *		if their name ends with the value of the passed string.
	 * @return	A <code>URI[]</code> containing PDURIs for every file matching the search criteria
	 * @throws SOAPException
	 */
	public URI[] findFilesWithExtension(URI pdURI, String ext){
		File rootDir;
		if(pdURI==null){
			rootDir = new File(this.weeDirBase);
		}else{
			rootDir = new File(this.weeDirBase+"/"+pdURI);
		}
		try {
			List<File> files = FileUtil.getFileListing(rootDir);
			List<String> ret = new ArrayList<String>();
			for(File f:files){
				int p = f.getAbsolutePath().indexOf(new File(this.weewfTemplatesDir).getName());
				int ex = -1;
				if(p!=-1){
					//check extention flag
					ex = f.getName().indexOf("."+ext);
					if(((ext!=null)&&(ex!=-1)) || (ext==null)){
						String uri = f.getAbsolutePath().substring(p).replace('\\', '/');
						//uri = uri.substring(uri.indexOf("wfTemplates")+12,uri.length());
						uri = uri.substring(uri.indexOf(this.weewfTemplatesDir.substring(1))+this.weewfTemplatesDir.length(),uri.length());
						ret.add(uri);
					}
				}
			}
			URI[] retURI = new URI[ret.size()];
			int count =0;
			for(String s : ret){
				try {
					retURI[count] = new URI(s);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			return retURI;
		} catch (FileNotFoundException e) {
			return null;
		}
		
	}

	public URI[] findFilesWithNameContaining(URI pdURI, String name)
			throws SOAPException {
		// not implemented
		return null;
	}

	public URI listDownladURI(URI pdURI) throws SOAPException {
		// not implemented
		return null;
	}

	public String read(URI pdURI) throws SOAPException {
		// not implemented
		return null;
	}

	/**
	 * Returns the binary identified by the passed URI as a <code>byte[]</code>.
	 * 
	 * @param	pdURI
	 *          A PDURI identifying the binary to be retrieved
 	 * @return	A byte array containing the retrieved binary data
 	 * @throws	SOAPException
	 */
	public byte[] retrieveBinary(URI pdURI) throws SOAPException {
		File f = new File(this.weeDirBase+"/"+pdURI);
		if(f.exists()&&f.canRead()){
			try {
				InputStream in = new FileInputStream(f);
		        byte[] bytes = new byte[(int) f.length()];
		        int offset = 0;
		        int numRead = 0;
		        while (offset < bytes.length
		               && (numRead=in.read(bytes, offset, bytes.length-offset)) >= 0) {
		            offset += numRead;
		        }
		        if (offset < bytes.length) {
		            throw new IOException("Could not completely read file "+f.getName());
		        }
		        in.close();
		        return bytes;

			} catch (Exception e) {
				log.error(e);
			}
		}
		return null;
	}

	public void store(URI pdURI, String encodedFile) throws SOAPException {
		// not implemented
	}
	
}
