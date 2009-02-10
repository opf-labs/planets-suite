package eu.planets_project.services.identification.imagemagick;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageMagickIdentifyTestHelper {
	
	private ImageMagickIdentifyTestHelper() {
		
	}
	
	/** output for stand alone tests */
	public static final String STANDALONE_TEST_OUT = "IMAGE_MAGICK_IDENTIFY_STANDALONE_TEST";
	/** output for server tests */
	public static final String SERVER_TEST_OUT = "IMAGE_MAGICK_MIGRATIONS_IDENTIFY_TEST";
	/** output for local tests */
	public static final String LOCAL_TEST_OUT = "IMAGE_MAGICK_MIGRATIONS_IDENTIFY_TEST";
	/** the location of the jped test file */
	public static final String JPG_TEST_FILE = "PA/ImageMagickIdentification/test/resources/test_images/test_jpg/2325563045_788d83ee16.jpg";
	/** location of the PNG test file */
	public static final String PNG_TEST_FILE = "PA/ImageMagickIdentification/test/resources/test_images/test_png/2325563735_58947e544f.png";
	/** location of the TIFF test file */
	public static final String TIFF_TEST_FILE = "PA/ImageMagickIdentification/test/resources/test_images/test_tiff/2325559153_34d1edc3d9.tif";
	/** location of the GIF test file */
	public static final String GIF_TEST_FILE = "PA/ImageMagickIdentification/test/resources/test_images/test_gif/2325559153_34d1edc3d9.gif";
	/** location of the BMP test file */
	public static final String BMP_TEST_FILE = "PA/ImageMagickIdentification/test/resources/test_images/test_bmp/2303389189_1da7b9b419.bmp";
	/** location of the TGA test file */
	public static final String TGA_TEST_FILE = "PA/ImageMagickIdentification/test/resources/test_images/test_tga/2290434251_5a0ec92db0.tga";
	/** location of the PCX test file */
	public static final String PCX_TEST_FILE = "PA/ImageMagickIdentification/test/resources/test_images/test_pcx/2325559153_34d1edc3d9.pcx";
	/** location of the JPEG 2000 test file */
	public static final String JP2_TEST_FILE = "PA/ImageMagickIdentification/test/resources/test_images/test_jp2/2274192346_4a0a03c5d6.jp2";
	
	private static HashMap<File, String> files = new HashMap<File, String>();
	
	public static HashMap<File, String> getTestFiles() {
		files.put(new File(JPG_TEST_FILE), "jpg");
		files.put(new File(PNG_TEST_FILE), "png");
		files.put(new File(TIFF_TEST_FILE), "tiff");
		files.put(new File(GIF_TEST_FILE), "gif");
		files.put(new File(BMP_TEST_FILE), "bmp");
		files.put(new File(TGA_TEST_FILE), "tga");
		files.put(new File(PCX_TEST_FILE), "pcx");
		files.put(new File(JP2_TEST_FILE), "jp2");
		return files;
	}

}
