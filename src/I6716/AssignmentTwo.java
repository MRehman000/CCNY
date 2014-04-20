package I6716;

import imagetools.ImageFileTools;
import imagetools.PixelTools;

import java.awt.image.BufferedImage;

/**
 * Created by codyboppert on 4/13/14.
 */
public class AssignmentTwo {
    private static final String BASE_FILE_PATH = "/Users/codyboppert/Workspace/CCNY/i6716 - Computer Vision/AssignmentTwo/";
    private static final String BASE_SAVE_FILE_PATH = BASE_FILE_PATH + "Images/";
    private static final String ORIGINAL_IMAGE_FILE_PATH = BASE_FILE_PATH + "NorthWest.png";

    int[][] originalImageArray = ImageFileTools.readImageToIntArray(ORIGINAL_IMAGE_FILE_PATH);
    BufferedImage originalImage = ImageFileTools.intToBI(originalImageArray, PixelTools.identity, BufferedImage.TYPE_INT_RGB);

}
