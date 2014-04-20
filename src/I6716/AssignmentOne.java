package I6716;


import imagetools.ImageFileTools;
import imagetools.ImageTools;

import java.awt.image.BufferedImage;

/**
 * Created by codyboppert on 4/13/14.
 */
public class AssignmentOne {

    private static final String BASE_FILE_PATH = "/Users/codyboppert/Workspace/CCNY/i6716 - Computer Vision/AssignmentOne/";
    private static final String BASE_SAVE_FILE_PATH = BASE_FILE_PATH + "images/";
    private static final String ORIGINAL_IMAGE_FILE_PATH = BASE_FILE_PATH + "IDPicture.bmp";

    public static void main(String... args) {
        /* Part A: Original Image */
        BufferedImage originalImage = ImageFileTools.readImageToBI(ORIGINAL_IMAGE_FILE_PATH);
        ImageFileTools.displayAndSave(originalImage, BASE_SAVE_FILE_PATH + "01_original.bmp", "Original");

        int[][] originalImageArray = ImageFileTools.biToIntArray(originalImage);

        /* Red, Green, and Blue Value Images */
        BufferedImage redImage = ImageFileTools.intToBI(originalImageArray,
                ImageTools.rgbRedFromRgbValue, BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(redImage, BASE_SAVE_FILE_PATH + "02_red.bmp", "Red Values");

        BufferedImage greenImage = ImageFileTools.intToBI(originalImageArray,
                ImageTools.rgbGreenFromRgbValue, BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(greenImage, BASE_SAVE_FILE_PATH + "03_green.bmp", "Green Values");

        BufferedImage blueImage = ImageFileTools.intToBI(originalImageArray,
                ImageTools.rgbBlueFromRgbValue, BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(blueImage, BASE_SAVE_FILE_PATH + "04_blue.bmp", "Blue Values");

        /* Intensity Image */
        BufferedImage intensityImage = ImageFileTools.intToBI(originalImageArray,
                ImageTools.rgbIntensityFromRgb, BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(intensityImage, BASE_SAVE_FILE_PATH + "05_intensity.bmp", "Intensity");

        /* Three other small algorithms for converting to grayscale using lightness, average, and luminosity) */
        BufferedImage lightnessImage = ImageFileTools.intToBI(originalImageArray,
                ImageTools.rgbLightnessFromRgb, BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(lightnessImage, BASE_SAVE_FILE_PATH + "06_lightness.bmp", "Lightness");

        BufferedImage averageImage = ImageFileTools.intToBI(originalImageArray,
                ImageTools.rgbAverageFromRgb, BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(averageImage, BASE_SAVE_FILE_PATH + "07_average.bmp", "Average");

        BufferedImage luminosityImage = ImageFileTools.intToBI(originalImageArray,
                ImageTools.rgbLuminosityFromRgb, BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(luminosityImage, BASE_SAVE_FILE_PATH + "08_luminosity.bmp", "Luminosity");

        /* Quantized intensity images */
        int[][] intensityImageArray = ImageFileTools.biToIntArray(intensityImage);

        BufferedImage fourLevelIntensityImage = ImageFileTools.intToBI(intensityImageArray,
                ImageTools.quantizedGrayRgbFromGrayRgb(4), BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(fourLevelIntensityImage,
                BASE_SAVE_FILE_PATH + "09_fourLevelIntensity.bmp", "Four Level Intensity");

        BufferedImage sixteenLevelIntensityImage = ImageFileTools.intToBI(intensityImageArray,
                ImageTools.quantizedGrayRgbFromGrayRgb(16), BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(sixteenLevelIntensityImage,
                BASE_SAVE_FILE_PATH + "10_sixteenLevelIntensity.bmp", "Sixteen Level Intensity");

        BufferedImage thirtytwoLevelIntensityImage = ImageFileTools.intToBI(intensityImageArray,
                ImageTools.quantizedGrayRgbFromGrayRgb(32), BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(thirtytwoLevelIntensityImage,
                BASE_SAVE_FILE_PATH + "11_thirtytwoLevelIntensity.bmp", "Thirty Two Level Intensity");

        BufferedImage sixtyfourLevelIntensityImage = ImageFileTools.intToBI(intensityImageArray,
                ImageTools.quantizedGrayRgbFromGrayRgb(64), BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(sixtyfourLevelIntensityImage,
                BASE_SAVE_FILE_PATH + "12_sixtyfourLevelIntensity.bmp", "Sixty Four Level Intensity");

        BufferedImage twoLevelsPerColorIntensityImage = ImageFileTools.intToBI(originalImageArray,
                ImageTools.quantizedRgbFromRgb(2), BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(twoLevelsPerColorIntensityImage,
                BASE_SAVE_FILE_PATH + "13_twoLevelPerColorIntensity.bmp", "Two Levels Per Color Intensity");

        BufferedImage fourLevelsPerColorIntensityImage = ImageFileTools.intToBI(originalImageArray,
                ImageTools.quantizedRgbFromRgb(4), BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(fourLevelsPerColorIntensityImage,
                BASE_SAVE_FILE_PATH + "14_fourLevelsPerColorIntensity.bmp", "Four Levels Per Color Intensity");

        /* Logarithmically Quantized Image */
        BufferedImage colorLogarithmicallyQuantizedImage = ImageFileTools.intToBI(originalImageArray,
                ImageTools.rgbLogarithmicallyQuantizedIntesityFromRgb, BufferedImage.TYPE_INT_RGB);
        ImageFileTools.displayAndSave(colorLogarithmicallyQuantizedImage,
                BASE_SAVE_FILE_PATH + "15_colorLogarithmicallyQuantized.bmp", "Color Logaithmically Quantized");
    }
}
