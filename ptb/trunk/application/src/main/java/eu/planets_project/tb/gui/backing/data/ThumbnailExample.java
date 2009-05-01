package eu.planets_project.tb.gui.backing.data;

import com.sun.image.codec.jpeg.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

/**
 * Thumbnail.java (requires Java 1.2+)
 * Load an image, scale it down and save it as a JPEG file.
 * @author Marco Schmidt
 */
public class ThumbnailExample {
  public static void main(String[] args) throws Exception {
    if (args.length != 5) {
      System.err.println("Usage: java Thumbnail INFILE " +
        "OUTFILE WIDTH HEIGHT QUALITY");
      System.exit(1);
    }
    // load image from INFILE
    Image image = Toolkit.getDefaultToolkit().getImage(args[0]);
    MediaTracker mediaTracker = new MediaTracker(new Container());
    mediaTracker.addImage(image, 0);
    mediaTracker.waitForID(0);
    // determine thumbnail size from WIDTH and HEIGHT
    int thumbWidth = Integer.parseInt(args[2]);
    int thumbHeight = Integer.parseInt(args[3]);
    double thumbRatio = (double)thumbWidth / (double)thumbHeight;
    int imageWidth = image.getWidth(null);
    int imageHeight = image.getHeight(null);
    double imageRatio = (double)imageWidth / (double)imageHeight;
    if (thumbRatio < imageRatio) {
      thumbHeight = (int)(thumbWidth / imageRatio);
    } else {
      thumbWidth = (int)(thumbHeight * imageRatio);
    }
    // draw original image to thumbnail image object and
    // scale it to the new size on-the-fly
    BufferedImage thumbImage = new BufferedImage(thumbWidth, 
      thumbHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = thumbImage.createGraphics();
    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
      RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
    // save thumbnail image to OUTFILE
    BufferedOutputStream out = new BufferedOutputStream(new
      FileOutputStream(args[1]));
    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    JPEGEncodeParam param = encoder.
      getDefaultJPEGEncodeParam(thumbImage);
    int quality = Integer.parseInt(args[4]);
    quality = Math.max(0, Math.min(quality, 100));
    param.setQuality((float)quality / 100.0f, false);
    encoder.setJPEGEncodeParam(param);
    encoder.encode(thumbImage);
    out.close(); 
    System.out.println("Done.");
    System.exit(0);
  }
}
