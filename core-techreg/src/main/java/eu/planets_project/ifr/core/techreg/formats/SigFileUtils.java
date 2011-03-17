package eu.planets_project.ifr.core.techreg.formats;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

import uk.gov.nationalarchives.pronom.FileFormatType;
import uk.gov.nationalarchives.pronom.PronomService;
import uk.gov.nationalarchives.pronom.PronomService_Service;
import uk.gov.nationalarchives.pronom.SigFile;
import uk.gov.nationalarchives.pronom.SignatureFileType;

/**
 * 
 * TODO When writing to XML, the JAXB bindings wrap the Signature File up in some 
 * extra XML that is not present when the file is downloaded by DROID. This could
 * be fixed by adding an appropriate XmlRootElement to the SignatureFileType, perhaps 
 * via the JAXB binding file.
 * 
 * @author Andrew Jackson
 *
 */
public class SigFileUtils {
    private static Logger log = Logger.getLogger(SigFileUtils.class.getName());

	private static JAXBContext jc;
	
	static {
		try {
			jc  = JAXBContext.newInstance(SignatureFileType.class.getPackage().getName());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @return
	 */
	public static SigFile getLatestSigFile() {
		try {
			PronomService_Service pss = new PronomService_Service();
			PronomService pronomService = pss.getPronomServiceSoap();
			return pronomService.getSignatureFileV1();
		} catch ( Exception e ) {
			log.warning("Could not download the latest DROID Signature File. Using an embedded one instead.");
			log.warning("Exception was "+e);
		}
		// Load from resource path
		return getEmbeddedSigFile();
	}
	
	/**
	 * @return
	 */
	private static SigFile getEmbeddedSigFile() {
		try {
			return readSigFile(DroidConfig.class.getResourceAsStream("/droid/DROID_SignatureFile.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param sigFile.getFFSignatureFile()
	 * @param os
	 * @throws JAXBException
	 */
	public static void writeSigFileTypeToOutputStream( SignatureFileType sigFile, OutputStream os ) throws JAXBException {
		//Create marshaller
		Marshaller m = jc.createMarshaller();
		m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		// Marshal object into file.
		// This override uses a different root element:
	    JAXBElement<SignatureFileType> rootElement = 
			new JAXBElement<SignatureFileType>(
					new QName("http://www.nationalarchives.gov.uk/pronom/SignatureFile","FFSignatureFile"), 
					SignatureFileType.class, sigFile );
		m.marshal( rootElement , os);
	}
	
	public static SignatureFileType readSigFileType( File input ) throws FileNotFoundException, JAXBException {
		// Override the root element, as shown at:
		//  https://jaxb.dev.java.net/guide/_XmlRootElement_and_unmarshalling.html
		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<SignatureFileType> root = u.unmarshal(new StreamSource(input),SignatureFileType.class);
		return root.getValue();
	}
	
	
	public static void writeSigFileToOutputStream( SigFile sigFile, OutputStream os ) throws JAXBException {
		//Create marshaller
		Marshaller m = jc.createMarshaller();
		m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		// This override uses a different root element:
	    JAXBElement<SigFile> rootElement = 
			new JAXBElement<SigFile>(
					new QName("http://www.nationalarchives.gov.uk/pronom/SignatureFile","SigFile"), 
					SigFile.class, sigFile );
		// Marshal object into file.
		m.marshal( rootElement, os );
	}	

	public static SigFile readSigFile( InputStream input ) throws FileNotFoundException, JAXBException {
		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<SigFile> root = u.unmarshal(new StreamSource(input),SigFile.class);
		return root.getValue();
	}
	
	public static void downloadAllPronomFormatRecords(File outputFolder) throws Exception {
		//File outputFolder = new File("src/main/resources/uk/gov/nationalarchives/pronom/");
		//outputFolder.mkdirs();
		SigFile sigFile = SigFileUtils.getLatestSigFile();
		BigInteger version = sigFile.getFFSignatureFile().getVersion();
		System.out.println("Got version "+version);
		String filename = "droid-signature-file.xml";
		try {
			writeSigFileTypeToOutputStream( sigFile.getFFSignatureFile(),
					new FileOutputStream( new File( outputFolder, filename) ) );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw(e);
		}
		//
		File xmlFolder = new File(outputFolder,"xml"); xmlFolder.mkdir();
		try {
			for( FileFormatType fft : sigFile.getFFSignatureFile().getFileFormatCollection().getFileFormat() ) {
				String puid = fft.getPUID();
				String puidFilename = "puid."+puid.replace("/", ".")+".xml";
				FileOutputStream fos = new FileOutputStream(new File(xmlFolder, puidFilename) );
				URL repurl = getPronomUrlForPUID(puid);
				System.out.println("Downloading "+repurl);
				IOUtils.copy( repurl.openStream(), fos );
				fos.flush();
				fos.getChannel().force(true);
				fos.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw(e);
		}

	}
	
	public static URL getPronomUrlForPUID( String puid ) throws MalformedURLException {
	 return new URL("http://www.nationalarchives.gov.uk/PRONOM/"+puid+".xml");
	}
	
	public static String downloadPronomRecordForPUID( String puid ) throws IOException {
		// Validate that form is fmt/# or fmt/x-#
		URL repurl = getPronomUrlForPUID(puid);
		// Create the URL:
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOUtils.copy( new BufferedInputStream(repurl.openStream()), bos );
		return bos.toString();
	}

	static void downloadSigFile() {
		// To make java.net.URL cope with an authenticating proxy.
		// Apache HTTPClient does this automatically, but we're not using that here at the moment.
		String proxyUser = System.getProperty("http.proxyUser");
		if (proxyUser != null) {
			System.out.println("Setting");
            Authenticator.setDefault(
            		new ProxyAuth( proxyUser, System.getProperty("http.proxyPassword") ) );
		}
		
		SigFile sigFile = SigFileUtils.getLatestSigFile();
		try {
			SigFileUtils.writeSigFileToOutputStream(sigFile, new FileOutputStream("signaturefile.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	static class ProxyAuth extends Authenticator {
	    private PasswordAuthentication auth;

	    protected ProxyAuth(String user, String password) {
	        this.auth = new PasswordAuthentication(user, password == null ? new char[]{} : password.toCharArray());
	    }

	    protected PasswordAuthentication getPasswordAuthentication() {
	        return this.auth;
	    }
	}

	/**
	 * @throws JAXBException 
	 * @throws IOException
	 */
	public static void main(String[] args) throws JAXBException, IOException {
		// Do it....
		downloadSigFile();
		
		// Sig file other download...
		SigFile sigFile = getLatestSigFile();
		System.out.println("SigFile v"+sigFile.getFFSignatureFile().getVersion());
		for( FileFormatType fft : sigFile.getFFSignatureFile().getFileFormatCollection().getFileFormat()) {
			System.out.println("PUID "+fft.getPUID());
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		writeSigFileTypeToOutputStream(sigFile.getFFSignatureFile(),bos);
		// Turn it into a string:
		String xml = bos.toString("UTF-8");
		System.out.println(xml.substring(0, 500));
	
		// Write it to a file:
		String filename = "droid-signature_"+sigFile.getFFSignatureFile().getVersion()+".xml";
		writeSigFileTypeToOutputStream( sigFile.getFFSignatureFile(), new FileOutputStream(filename) );
		
		// Write it out raw:
		File raw = new File("droid-signature-raw_"+sigFile.getFFSignatureFile().getVersion()+".xml");
		writeSigFileToOutputStream( sigFile, new FileOutputStream(raw) );
		
		// Read it back:
		SignatureFileType s2 = readSigFileType( new File(filename) );
		System.out.println("Read back sigfile: "+s2.getVersion());
		
		System.out.println(downloadPronomRecordForPUID("fmt/1").substring(0, 500));
		
		// This downloads all the valid PRONOM record files.
		//downloadAllPronomFormatRecords();
	}
	
}
