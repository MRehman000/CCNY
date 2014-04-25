package I6716;

import imagetools.ConvolutionEnum;
import imagetools.ImageFileTools;
import imagetools.ImageTools;
import imagetools.PixelTools;
import sun.awt.image.BufferedImageDevice;
import utilities.MathTools;

import java.awt.image.BufferedImage;

/**
 * Created by codyboppert on 4/13/14.
 */
public class AssignmentTwo {
    private static final String BASE_FILE_PATH = "/Users/codyboppert/IdeaProjects/CCNY/src/I6716/Images/";
    private static final String BASE_SAVE_FILE_PATH = BASE_FILE_PATH + "AssignmentTwo/";
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

        /* Apply 2x1 operators. */
        int[][] twoByOne = ImageTools.convolution2d(intensityImage, new int[][]{{1, -1}}, ConvolutionEnum.COVER_ALL_POINTS, 1, true);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(
                twoByOne,PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB)
                , BASE_SAVE_FILE_PATH + "03_2x1.bmp", "2x1");

        int[][] oneByTwo = ImageTools.convolution2d(intensityImage, new int[][]{{1},{-1}}, ConvolutionEnum.COVER_ALL_POINTS, 1, true);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(oneByTwo,
                PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB)
                , BASE_SAVE_FILE_PATH + "04_1x2.bmp", "1x2");

        int[][] combinedGradientOperators = ImageFileTools.combineIntArrays(twoByOne, oneByTwo, MathTools.add);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(
                combinedGradientOperators, PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB)
                , BASE_SAVE_FILE_PATH + "05_combinedGradientOperators.bmp", "Combined Gradient Operators");


        /* generate Sobel images */
        /* Fun to fool around with the scaling factor generator */
        int[][] sobel3x3 = ImageTools.convolution2d(intensityImage, ImageTools.generateSobelMask(3),
                ConvolutionEnum.COVER_ALL_POINTS, ImageTools.generateSobelScalingFactor(3), true);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(sobel3x3, PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB),
                BASE_SAVE_FILE_PATH + "06" + "_sobel3x3.bmp", "Sobel 3x3");

        int index = 7;
        for (int i = 5; i <= 9; i += 2) {
            ImageFileTools.displayAndSave(ImageFileTools.intToBI(ImageTools.convolution2d(
                    intensityImage, ImageTools.generateSobelMask(i), ConvolutionEnum.COVER_ALL_POINTS,
                    ImageTools.generateSobelScalingFactor(i), true), PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB),
                    BASE_SAVE_FILE_PATH + "0" + index + "_sobel" + i + "x" + i + ".bmp",
                    "Sobel " + i + "x" + i);
            index++;
        }

        /* Subtract sobel from 1x2 */
        


    }

}
