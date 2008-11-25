package eu.planets_project.ifr.core.services.validation.libtiff.impl;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Arrays;
import java.io.File;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Callback;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.Memory;
import com.sun.jna.PointerType;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.utils.ByteArrayHelper;
import eu.planets_project.services.utils.PlanetsLogger;
import eu.planets_project.services.validate.BasicValidateOneBinary;

/**
 * Simple TIFF validation service
 * 
 * @author Klaus Rechert
 * 
 */
@WebService(
	name = TiffValidation.NAME, 
	serviceName = BasicValidateOneBinary.NAME, 
	targetNamespace = PlanetsServices.NS, 
	endpointInterface = "eu.planets_project.services.validate.BasicValidateOneBinary"
	)
@Local(BasicValidateOneBinary.class)
@Remote(BasicValidateOneBinary.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public class TiffValidation implements BasicValidateOneBinary, Serializable 
{
	private static final long serialVersionUID = -7116493742376868779L;
	public static final String NAME = "TiffValidation";
	
	private static final PlanetsLogger LOG = 
		PlanetsLogger.getLogger(TiffValidation.class);

	private byte[] bytes;

	/**
	 * PRONOM UIDs 
	 */ 
	private static final String PRONOM_TIFF_V3 = "info:pronom/fmt/7";	
	private static final String PRONOM_TIFF_V4 = "info:pronom/fmt/8";	
	private static final String PRONOM_TIFF_V5 = "info:pronom/fmt/9";	
	private static final String PRONOM_TIFF_V6 = "info:pronom/fmt/10";	


	/**
	 * TIFF Baseline Tags
	 *
	 * @see http://www.awaresystems.be/imaging/tiff/tifftags/baseline.html
	 */ 	
	private static final int TIFF_IMAGE_WIDTH = 256;
	private static final int TIFF_IMAGE_LENGTH = 257;

	/**
	 * A list of pronom URIs describing TIFF files; the URI given to this service
	 * must be one of these or null.
	 */
	private static final List<String> TIFF_PRONOM = Arrays.asList(
		"info:pronom/fmt/7", 
		"info:pronom/fmt/8", 
		"info:pronom/fmt/9", 
		"info:pronom/fmt/10");

	/**
	 * Validata a TIFF file by opening a byte array with native libtiff
	 * 
	 * @param binary The file to check (as a byte array)
	 *        
	 * @param fmt PRONOM UID: 
	 * 	info:pronom/fmt/7  - TIFF V3
	 * 	info:pronom/fmt/8  - TIFF V4
	 * 	info:pronom/fmt/9  - TIFF V5
	 * 	info:pronom/fmt/10 - TIFF V6
	 *
	 * @return verification (boolean)
	 */
	@WebMethod(operationName = BasicValidateOneBinary.NAME, 
		action = PlanetsServices.NS + "/" + BasicValidateOneBinary.NAME)
	@WebResult(name = BasicValidateOneBinary.NAME + "Result", 
		targetNamespace = PlanetsServices.NS + "/" + BasicValidateOneBinary.NAME, 
		partName = BasicValidateOneBinary.NAME + "Result")
	public boolean  basicValidateOneBinary(
		@WebParam(name = "binary", 
			targetNamespace = PlanetsServices.NS + "/" + BasicValidateOneBinary.NAME,
			partName = "binary") final byte[] binary,
		@WebParam(name = "fmt", 
			targetNamespace = PlanetsServices.NS + "/" + BasicValidateOneBinary.NAME,
			partName = "fmt") final URI fmt) 
	{
		if (fmt != null && !TIFF_PRONOM.contains(fmt.toString())) 
		{
			throw new IllegalArgumentException(fmt.toString() 
				+ " is not a valid PRONOM UID for TIFF validation service");
		}

		File tempFile = ByteArrayHelper.write(binary);

		Pointer tiff = TiffLibrary.INSTANCE.TIFFOpen(tempFile.getAbsolutePath(), "r");
		if(tiff == null)
			return false;

		/* example how to read TIFF tags */
		IntByReference iref = new IntByReference();
		if(TiffLibrary.INSTANCE.TIFFGetField(tiff, TIFF_IMAGE_WIDTH, iref) != 0)
			LOG.info("TIFF width " + iref.getValue());

		if(TiffLibrary.INSTANCE.TIFFGetField(tiff, TIFF_IMAGE_LENGTH, iref) != 0)
			LOG.info("TIFF length " + iref.getValue());

		TiffLibrary.INSTANCE.TIFFClose(tiff);
		return true;
	}

	/**
	 * Method for testing purpose: takes a file name as the only parameter,
	 * converts the file into a byte array and calls the actual identification
	 * method with that array.
	 * @param fileName The local (where the service is running) location of the
	 *        TIFF file to validate
	 * @return Returns true if the file with the given name is a TIFF file, else
	 *         false
	 */
	@WebMethod
	public boolean basicValidateOneBinary(final String fileName) 
	{
		bytes = ByteArrayHelper.read(new File(fileName));
		return basicValidateOneBinary(bytes, null);
	}


	/**
	 * Interface wrapping libTiff's functions
	 *
	 * Requires libtiff.so or libtiff.dll in system path.
	 */
	public interface TiffLibrary extends Library 
	{
		TiffLibrary INSTANCE = 
			(TiffLibrary)Native.loadLibrary("tiff", TiffLibrary.class);
	
		Pointer TIFFOpen(String file, String mode);
		void TIFFClose(Pointer p);
	
		ErrorFunc TIFFSetErrorHandler(ErrorFunc handler);
		WarnFunc TIFFSetWarningHandler(WarnFunc handler);

		int TIFFGetField(Pointer tif, int tag, ByReference... va);
		int TIFFSetField(Pointer tif, int tag, ByReference... va);
	
		// query details
		long TIFFCurrentRow(Pointer tif);
		long TIFFCurrentStrip(Pointer tif);
		long TIFFCurrentTile(Pointer tif);
		int TIFFCurrentDirectory(Pointer tif);
		int TIFFLastDirectory(Pointer tif);
		int TIFFFileno(Pointer tif);
		String TIFFFileName(Pointer tif);
		int TIFFGetMode(Pointer tif);
		int TIFFIsTiled(Pointer tif);
		int TIFFIsBytSwapped(Pointer tif);
		int TIFFIsUpSampled(Pointer tif);
		int TIFFIsMSB2LSB(Pointer tif);
		String TIFFGetVersion();
	 
		int TIFFReadDirectory(Pointer tiff);
		long TIFFReadEncodedStrip(Pointer tif, long strip, Memory buffer, long size);
		int TIFFReadEncodedTile(Pointer tif, int tile, Memory buffer, long size);
		long TIFFReadRawStrip(Pointer tif, long strip, Memory buffer, long size);
		long TIFFReadRawTile(Pointer tif, long tile, Memory buffer, long size);
		int TIFFReadRGBAImage(Pointer tif, long w, long h, 
			Memory buffer, int stopOnError);
		int TIFFReadRGBAImageOriented(Pointer tif, long w, long h, 
			Memory buffer,int orientation, int stopOnError);

		int TIFFReadRGBAStrip(Pointer tif, long row, Memory buffer);
		int TIFFReadRGBATile(Pointer tif, long x, long y, Memory buffer);
		int TIFFReadScanline(Pointer tif, Memory buffer, long row, int sample);
		long TIFFReadTile(Pointer tif, Memory buffer, long x, long y, long z, int sample);

		int TIFFRGBAImageOK(Pointer tiff, byte[] message);
		int TIFFRGBAImageBegin(Pointer img, Pointer tiff, 
			int stopOnError, byte[] msg);
		int TIFFRGBAImageGet(Pointer img, Memory buffer, long w, long h);
		void TIFFRGBAImageEnd(Pointer img);

		int TIFFSetDirectory(Pointer tif, int dirnum);
		int TIFFSetSubDirectory(Pointer tif, long offset);
	}

	static ErrorFunc eFunc = new TiffErrorFunc();
	static WarnFunc wFunc = new TiffWarnFunc();

	public static interface ErrorFunc extends Callback
	{
		// XXX: varargs not possible for interfaces
		void callback(String module, String fmt); 
	}

	public static interface WarnFunc extends Callback
	{
		// XXX: varargs not possible for interfaces
		void callback(String module, String fmt); 
	}

	
	static class TiffErrorFunc implements ErrorFunc
	{
		public void callback(String module, String fmt)
		{
			LOG.error(module + fmt);
		}
	}

	static class TiffWarnFunc implements WarnFunc
	{
		public void callback(String module, String fmt)
		{
			LOG.debug(module + fmt);
		}
	}
}
