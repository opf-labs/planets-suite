package eu.planets_project.ifr.core.services.validation.impl;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.validate.Validate;
import eu.planets_project.services.validate.ValidateResult;


/**
 * Simple TIFF validation service
 * 
 * @author Klaus Rechert
 * 
 */
@WebService(
	name = TiffValidation.NAME, 
	serviceName = Validate.NAME, 
	targetNamespace = PlanetsServices.NS,
	endpointInterface = "eu.planets_project.services.validate.Validate"
	)
@Local(Validate.class)
@Remote(Validate.class)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, style = SOAPBinding.Style.RPC)
@Stateless()
public class TiffValidation implements Validate, Serializable 
{
	private static final long serialVersionUID = -7116493742376868779L;
	/**
	 * the service name
	 */
	public static final String NAME = "TiffValidation";
	
	private static final Logger log = Logger.getLogger(TiffValidation.class.getName());

	private byte[] bytes;

	/**
	 * PRONOM UIDs 
	 */ 
	@SuppressWarnings("unused")
	private static final String PRONOM_TIFF_V3 = "info:pronom/fmt/7";	
	@SuppressWarnings("unused")
	private static final String PRONOM_TIFF_V4 = "info:pronom/fmt/8";	
	@SuppressWarnings("unused")
	private static final String PRONOM_TIFF_V5 = "info:pronom/fmt/9";	
	@SuppressWarnings("unused")
	private static final String PRONOM_TIFF_V6 = "info:pronom/fmt/10";	


	/**
	 * TIFF Baseline Tags
	 *
	 * @see "http://www.awaresystems.be/imaging/tiff/tifftags/baseline.html"
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
	 * {@inheritDoc}
     * @see Validate#validate(eu.planets_project.services.datatypes.DigitalObject, java.net.URI, eu.planets_project.services.datatypes.Parameter)
	 */
	public ValidateResult validate(final DigitalObject o, final URI fmt, List<Parameter> paramenters)
	{
		ValidateResult result;
		File tempFile =  FileUtils.writeInputStreamToTmpFile(o.getContent().read(), 
			"image", "tif");
		boolean valid = basicValidateOneBinary(tempFile, fmt);

		result = new ValidateResult.Builder(fmt, new ServiceReport(Type.INFO,
                Status.SUCCESS, "OK")).ofThisFormat(valid).build();

		return result;
	}

	/**
	 * {@inheritDoc}
	 * @see eu.planets_project.services.validate.Validate#describe()
	 */
	public ServiceDescription describe() {
		ServiceDescription.Builder sd = new ServiceDescription.Builder(NAME,
			this.getClass().getCanonicalName());
		sd.description("Validation service using JNA/libtiff");
		return sd.build();
	}
	
	/**
	 * Validata a TIFF file by opening a byte array with native libtiff
	 * 
	 * @param digitalObject The digital object file to validate        
	 * @param fmt PRONOM UID: 
	 * 	info:pronom/fmt/7  - TIFF V3
	 * 	info:pronom/fmt/8  - TIFF V4
	 * 	info:pronom/fmt/9  - TIFF V5
	 * 	info:pronom/fmt/10 - TIFF V6
	 *
	 * @return verification (boolean)
	 * @see eu.planets_project.services.validate.BasicValidateOneBinary#basicValidateOneBinary(byte[], java.net.URI)
	 */
	private boolean  basicValidateOneBinary(File tempFile, final URI fmt)
	{
		if (fmt != null && !TIFF_PRONOM.contains(fmt.toString())) 
		{
			throw new IllegalArgumentException(fmt.toString() 
				+ " is not a valid PRONOM UID for TIFF validation service");
		}

		Pointer tiff = TiffLibrary.INSTANCE.TIFFOpen(tempFile.getAbsolutePath(), "r");
		if(tiff == null)
			return false;

		/* example how to read TIFF tags */
		IntByReference iref = new IntByReference();
		if(TiffLibrary.INSTANCE.TIFFGetField(tiff, TIFF_IMAGE_WIDTH, iref) != 0)
			log.info("TIFF width " + iref.getValue());

		if(TiffLibrary.INSTANCE.TIFFGetField(tiff, TIFF_IMAGE_LENGTH, iref) != 0)
			log.info("TIFF length " + iref.getValue());

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
		File tempFile = new File(fileName);
		return basicValidateOneBinary(tempFile, null);
	}

	/**
	 * Interface wrapping libTiff's functions
	 *
	 * Requires libtiff.so or libtiff.dll in system path.
	 */
	public interface TiffLibrary extends Library 
	{
		/**
		 * the lib tiff instance
		 */
		TiffLibrary INSTANCE = 
			(TiffLibrary)Native.loadLibrary(
				(Platform.isWindows() ? "libtiff3" : "tiff"), 
				TiffLibrary.class);
	
		/**
		 * @param file
		 * @param mode
		 * @return pointer
		 */
		Pointer TIFFOpen(String file, String mode);
		/**
		 * @param p
		 */
		void TIFFClose(Pointer p);
	
		/**
		 * @param handler
		 * @return error function
		 */
		ErrorFunc TIFFSetErrorHandler(ErrorFunc handler);
		/**
		 * @param handler
		 * @return warning function
		 */
		WarnFunc TIFFSetWarningHandler(WarnFunc handler);

		/**
		 * @param tif
		 * @param tag
		 * @param va
		 * @return the field requested
		 */
		int TIFFGetField(Pointer tif, int tag, ByReference... va);
		/**
		 * @param tif
		 * @param tag
		 * @param va
		 * @return the field tag
		 */
		int TIFFSetField(Pointer tif, int tag, ByReference... va);
	
		/**
		 * @param tif
		 * @return the current row
		 */
		// query details
		long TIFFCurrentRow(Pointer tif);
		/**
		 * @param tif
		 * @return the current strip
		 */
		long TIFFCurrentStrip(Pointer tif);
		/**
		 * @param tif
		 * @return the current tile
		 */
		long TIFFCurrentTile(Pointer tif);
		/**
		 * @param tif
		 * @return the current directory
		 */
		int TIFFCurrentDirectory(Pointer tif);
		/**
		 * @param tif
		 * @return the last dir
		 */
		int TIFFLastDirectory(Pointer tif);
		/**
		 * @param tif
		 * @return the file no
		 */
		int TIFFFileno(Pointer tif);
		/**
		 * @param tif
		 * @return the file name
		 */
		String TIFFFileName(Pointer tif);
		/**
		 * @param tif
		 * @return the mode
		 */
		int TIFFGetMode(Pointer tif);
		/**
		 * @param tif
		 * @return is tiff tiled
		 */
		int TIFFIsTiled(Pointer tif);
		/**
		 * @param tif
		 * @return is the tiff byte swapped
		 */
		int TIFFIsBytSwapped(Pointer tif);
		/**
		 * @param tif
		 * @return is the tiff upsampled
		 */
		int TIFFIsUpSampled(Pointer tif);
		/**
		 * @param tif
		 * @return is tiff most sig bit to least sig
		 */
		int TIFFIsMSB2LSB(Pointer tif);
		/**
		 * @return the tiff version
		 */
		String TIFFGetVersion();
	 
		/**
		 * @param tiff
		 * @return status
		 */
		int TIFFReadDirectory(Pointer tiff);
		/**
		 * @param tif
		 * @param strip
		 * @param buffer
		 * @param size
		 * @return status
		 */
		long TIFFReadEncodedStrip(Pointer tif, long strip, Memory buffer, long size);
		/**
		 * @param tif
		 * @param tile
		 * @param buffer
		 * @param size
		 * @return status
		 */
		int TIFFReadEncodedTile(Pointer tif, int tile, Memory buffer, long size);
		/**
		 * @param tif
		 * @param strip
		 * @param buffer
		 * @param size
		 * @return status
		 */
		long TIFFReadRawStrip(Pointer tif, long strip, Memory buffer, long size);
		/**
		 * @param tif
		 * @param tile
		 * @param buffer
		 * @param size
		 * @return status
		 */
		long TIFFReadRawTile(Pointer tif, long tile, Memory buffer, long size);
		/**
		 * @param tif
		 * @param w
		 * @param h
		 * @param buffer
		 * @param stopOnError
		 * @return status
		 */
		int TIFFReadRGBAImage(Pointer tif, long w, long h, 
			Memory buffer, int stopOnError);
		/**
		 * @param tif
		 * @param w
		 * @param h
		 * @param buffer
		 * @param orientation
		 * @param stopOnError
		 * @return status
		 */
		int TIFFReadRGBAImageOriented(Pointer tif, long w, long h, 
			Memory buffer,int orientation, int stopOnError);

		/**
		 * @param tif
		 * @param row
		 * @param buffer
		 * @return status
		 */
		int TIFFReadRGBAStrip(Pointer tif, long row, Memory buffer);
		/**
		 * @param tif
		 * @param x
		 * @param y
		 * @param buffer
		 * @return status
		 */
		int TIFFReadRGBATile(Pointer tif, long x, long y, Memory buffer);
		/**
		 * @param tif
		 * @param buffer
		 * @param row
		 * @param sample
		 * @return status
		 */
		int TIFFReadScanline(Pointer tif, Memory buffer, long row, int sample);
		/**
		 * @param tif
		 * @param buffer
		 * @param x
		 * @param y
		 * @param z
		 * @param sample
		 * @return status
		 */
		long TIFFReadTile(Pointer tif, Memory buffer, long x, long y, long z, int sample);

		/**
		 * @param tiff
		 * @param message
		 * @return is RGBA image ok
		 */
		int TIFFRGBAImageOK(Pointer tiff, byte[] message);
		/**
		 * @param img
		 * @param tiff
		 * @param stopOnError
		 * @param msg
		 * @return the image sart
		 */
		int TIFFRGBAImageBegin(Pointer img, Pointer tiff, 
			int stopOnError, byte[] msg);
		/**
		 * @param img
		 * @param buffer
		 * @param w
		 * @param h
		 * @return the image
		 */
		int TIFFRGBAImageGet(Pointer img, Memory buffer, long w, long h);
		/**
		 * @param img
		 */
		void TIFFRGBAImageEnd(Pointer img);

		/**
		 * @param tif
		 * @param dirnum
		 * @return status
		 */
		int TIFFSetDirectory(Pointer tif, int dirnum);
		/**
		 * @param tif
		 * @param offset
		 * @return status
		 */
		int TIFFSetSubDirectory(Pointer tif, long offset);
	}

	static ErrorFunc eFunc = new TiffErrorFunc();
	static WarnFunc wFunc = new TiffWarnFunc();

	/**
	 *
	 */
	public static interface ErrorFunc extends Callback
	{
		/**
		 * @param module
		 * @param fmt
		 */
		// XXX: varargs not possible for interfaces
		void callback(String module, String fmt); 
	}

	/**
	 *
	 */
	public static interface WarnFunc extends Callback
	{
		/**
		 * @param module
		 * @param fmt
		 */
		// XXX: varargs not possible for interfaces
		void callback(String module, String fmt); 
	}

	
	static class TiffErrorFunc implements ErrorFunc
	{
		public void callback(String module, String fmt)
		{
			log.severe(module + fmt);
		}
	}

	static class TiffWarnFunc implements WarnFunc
	{
		public void callback(String module, String fmt)
		{
			log.severe(module + fmt);
		}
	}
}
