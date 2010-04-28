package eu.planets_project.tb.impl.data.util;

/**
 * Inspired by http://www.geocities.com/marcoschmidt.geo/java-save-jpeg-thumbnail.html
 */

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.utils.DigitalObjectUtils;

/**
 * Creates a reduced jpeg version (thumbnail) of an image using the Java 2D API
 */

public class ImageThumbnail {
    static private Log log = LogFactory.getLog(ImageThumbnail.class);

    /**
     * Create a reduced jpeg version of an image. The width/height ratio is
     * preserved.
     * 
     * @param data
     *            raw data of the image
     * @param thumbWidth
     *            maximum width of the reduced image
     * @param thumbHeight
     *            maximum heigth of the reduced image
     * @param quality
     *            jpeg quality of the reduced image
     * @return a reduced jpeg image if the image represented by data is bigger
     *         than the maximum dimensions of the reduced image, otherwise data
     *         is returned
     */
    public static byte[] createThumbArray(byte[] data, int thumbWidth,
            int thumbHeight ) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        createThumb(data, thumbWidth, thumbHeight, result);
        return result.toByteArray();
    }

    /**
     * Create a reduced jpeg version of an image. The width/height ratio is
     * preserved.
     * 
     * @param data
     *            raw data of the image
     * @param thumbWidth
     *            maximum width of the reduced image
     * @param thumbHeight
     *            maximum heigth of the reduced image
     * @param quality
     *            jpeg quality of the reduced image
     * @param out
     *            produce a reduced jpeg image if the image represented by data
     *            is bigger than the maximum dimensions of the reduced image,
     *            otherwise data is written to this stream
     */
    public static void createThumb(byte[] data, int thumbWidth,
            int thumbHeight, OutputStream out) throws Exception {
        // NOTE that this support JPEG, PNG or GIF only.
        Image image = Toolkit.getDefaultToolkit().createImage(data);
        MediaTracker mediaTracker = new MediaTracker(new Frame());
        int trackID = 0;
        mediaTracker.addImage(image, trackID);
        mediaTracker.waitForID(trackID);
        if (image.getWidth(null) <= thumbWidth
                && image.getHeight(null) <= thumbHeight)
            out.write(data);
        else
            createThumb(image, thumbWidth, thumbHeight, out);
    }

    /**
     * Create a scaled jpeg of an image. The width/height ratio is preserved.
     * 
     * <p>
     * If image is smaller than thumbWidth x thumbHeight, it will be magnified,
     * otherwise it will be scaled down.
     * </p>
     * 
     * @param image
     *            the image to reduce
     * @param thumbWidth
     *            the maximum width of the thumbnail
     * @param thumbHeight
     *            the maximum heigth of the thumbnail
     * @param quality
     *            the jpeg quality ot the thumbnail
     * @param out
     *            a stream where the thumbnail data is written to
     */
    public static void createThumb(Image image, int thumbWidth,
            int thumbHeight, OutputStream out) throws Exception {
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double thumbRatio = (double) thumbWidth / (double) thumbHeight;
        double imageRatio = (double) imageWidth / (double) imageHeight;
        if (thumbRatio < imageRatio) {
            thumbHeight = (int) (thumbWidth / imageRatio);
        } else {
            thumbWidth = (int) (thumbHeight * imageRatio);
        }
        // draw original image to thumbnail image object and
        // scale it to the new size on-the-fly
        BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight,
                BufferedImage.TYPE_INT_ARGB );
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        graphics2D.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
        graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );
        graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
        // save thumbnail image to out stream
        log.debug("Start writing rescaled image...");
        ImageIO.write(thumbImage, "png", out);
        log.debug("DONE writing rescaled image...");
    }

    /**
     * @param dob
     * @param thumbWidth
     * @param thumbHeight
     * @param out
     * @throws Exception
     */
    public static void createThumb(DigitalObject dob, OutputStream out) throws Exception {
        log.debug("Loading in the image...");
        File imgFile = DigitalObjectUtils.toFile(dob);
        byte[] data = FileUtils.readFileToByteArray(imgFile);
        log.debug("Writing image to stream...");
        ImageThumbnail
                .createThumb(data, THUMB_WIDTH, THUMB_HEIGHT, out);
        log.debug("Image has been written to stream.");
        return;
    }

    /** */
    public static final int THUMB_WIDTH = 64;
    
    /** */
    public static final int THUMB_HEIGHT = 64;
    
}
