package eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.utils;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.utils.MigrationResults;
import eu.planets_project.services.utils.PlanetsLogger;

/**
 * 
 *  The purpose of this class is to carry out the migration process on the byte[] it receives
 *  as an argument.
 *  The Converter could be configured by passing the src format and the desired target format.
 *  Under the hood, this class uses JMagick, which again utilizes ImageMagick.
 *  
 *  Currently the compression setting is "hardwired" and set to "No Compression".
 *  This should be changed in future versions in order to let the user specify a compression setting he likes.
 *
 *  @author : Peter Melms
 *  Email  : peter.melms@uni-koeln.de
 *  Created : 27.05.2008
 */
public class GeneralImageConverter {
    /** Array of compression type strings */
    public static String[] compressionTypes = new String[11];

    // Default-Constructor
    /**
     * The default constructor, initializing the helper array with compression
     * types, corresponding to the types of compression used by ImageMagick.
     */
    public GeneralImageConverter() {
    	
	    System.setProperty("jmagick.systemclassloader","no"); // Use the JBoss-Classloader, instead of the Systemclassloader.

	    compressionTypes[0] = "Undefined Compression";
		compressionTypes[1] = "No Compression";
		compressionTypes[2] = "BZip Compression";
		compressionTypes[3] = "Fax Compression";
		compressionTypes[4] = "Group4 Compression";
		compressionTypes[5] = "JPEG Compression";
		compressionTypes[6] = "JPEG2000 Compression";
		compressionTypes[7] = "LosslessJPEG Compression";
		compressionTypes[8] = "LZW Compression";
		compressionTypes[9] = "RLE Compression";
		compressionTypes[10] = "Zip Compression";
    }

    /**
     * This Method is called from one of the specific ImageConverters (e.g.
     * JpgToTiffconverter, PngToTiffconverter, TiffToPngConverter). It Carries
     * out the Migration of the image, which is passed as a byte[], using
     * ImageMagick (via JMagick-API)
     * 
     * @param srcFileData this is the byte[] handed over from one of the above
     *                mentioned ImageConveters, containing the source image.
     * 
     * @param requiredSrcFormat As this is a GeneralImageConverter, it could be configured
     *                from and to which format it migrates images. Also, the
     *                requiredSrcFormat is tested, and if the image format does
     *                not match the required src-format (i.e. for
     *                JpgToTiffConverter: JPEG) it throws a PlanetsException,
     *                with a corresponding warning.
     * 
     * @param targetFormat Configures the target format of the image, i.e. the format
     *                the image will be migrated to.
     * 
     * @param plogger a PlanetsLogger instance passed from the specific
     *                ImageConverter, to log the migration process.
     * @return Returns a MigrationResults object, which contains 1) the
     *         migrated image as a byte[], 2) an optional message and 3) a flag
     *         indicating migrationSuccess (or not).
     */
    public MigrationResults convertImage(byte[] srcFileData,
	    String requiredSrcFormat, String targetFormat, PlanetsLogger plogger) {
    	plogger.debug("Starting Migration from " + requiredSrcFormat +" to " + targetFormat);
    	MigrationResults results = new MigrationResults();

	try {
		plogger.debug("Initialising ImageInfo Object");
	    ImageInfo mInfo = new ImageInfo();
	    MagickImage image = new MagickImage();
	    // Creating an MagickImage object from "srcFileData" byte[].
	    plogger.debug("Create image object from blob");
	    image.blobToImage(mInfo, srcFileData);
	    // Reading src-image format
	    String srcImageFormat = image.getMagick();

	    // Checks if the SourceFile-Format of the image matches the
	    // requiredSrcFormat
	    if (srcImageFormat.equalsIgnoreCase(requiredSrcFormat) || srcImageFormat.equalsIgnoreCase(requiredSrcFormat)) {

			plogger.debug("Source Image-Format is: " + srcImageFormat);
			
			plogger.debug("SrcFile uses Compression: " + "'" + compressionTypes[image.getCompression()] + "'");
			// Setting the compression to 'No Compression'
			image.setCompression(1);
			plogger.debug("Compression now set to: " + "'" + compressionTypes[image.getCompression()] + "'");
	
			// Setting the image format to targetFormat
			image.setImageFormat(targetFormat);
			plogger.debug("Starting Conversion to: " + image.getMagick());
	
			
			// Testing ImageTransparency..
			// PixelPacket pp = new PixelPacket(0,0,0,0);
			// pp.setOpacity(0);
			// image.transparentImage(pp, pp.getOpacity());
			// ...end testing ;-)
	
			// Writing the converted file to a byte[] and storing that in MigrationResults instance.
			results.setByteArray(image.imageToBlob(mInfo));
			results.setMessage("Migration-Result: Image successfully converted to: " + image.getMagick());
			// plogger.debug("Successfully converted to: " + image.getMagick());
			// Cleans up the ImageMagick-Objects we don't need anymore.
			image.destroyImages();
			results.setMigrationSuccess(true);
		    }
	    // Image has wrong file format, and has not been converted, so return "null", setSuccess = false and set message...
	    else {
			// plogger.debug("Image has wrong Format: " + image.getMagick() + " and has not been converted!");
			// resultFile = srcFile;
			results.setMigrationSuccess(false);
			results.setByteArray(null);
			results.setMessage("Migration-Result: Source-Image has wrong Format: '" + srcImageFormat + "' and has not been converted!");
	    }
	} catch (MagickException e) {
	    // TODO Auto-generated catch block
	    plogger.debug("GeneralImageConverter: Something went wrong with ImageMagick!");
	    e.printStackTrace();
	}
	// Return MigrationResults...
	return results;
    }

}
