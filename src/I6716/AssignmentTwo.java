package I6716;

import imagetools.ImageFileTools;
import imagetools.ImageTools;
import imagetools.PixelTools;
import sun.awt.image.BufferedImageDevice;

import java.awt.image.BufferedImage;

/**
 * Created by codyboppert on 4/13/14.
 */
public class AssignmentTwo {
    private static final String BASE_FILE_PATH = "/Users/codyboppert/Workspace/CCNY/i6716 - Computer Vision/AssignmentTwo/";
    private static final String BASE_SAVE_FILE_PATH = BASE_FILE_PATH + "Images/";
    private static final String ORIGINAL_IMAGE_FILE_PATH = BASE_FILE_PATH + "NorthWest.png";

    public static void main(String... args) {
        /* Read original image to array, convert, save and display after converting to Buffered Image */
        int[][] originalImageArray = ImageFileTools.readImageToIntArray(ORIGINAL_IMAGE_FILE_PATH);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(originalImageArray, PixelTools.identity, BufferedImage.TYPE_INT_RGB)
                , BASE_SAVE_FILE_PATH + "01_original.bmp", "Original");

        /* Generate histogram by converting to intensity image */
        int[][] intensityImage = ImageFileTools.intArrayToIntArray(originalImageArray, PixelTools.intensityFromRgb);

        ImageFileTools.displayAndSave(ImageFileTools.displayHistogram(ImageTools.histogram(intensityImage)),
                BASE_SAVE_FILE_PATH + "02_histogram.bmp", "Histogram of Original Image by Intensity");


    }

}
