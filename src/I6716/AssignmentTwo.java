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
        int index = 1;
        long time = System.nanoTime();

        /* Read original image to array, convert, save and display after converting to Buffered Image */
        int[][] originalImageArray = ImageFileTools.readImageToIntArray(ORIGINAL_IMAGE_FILE_PATH);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(originalImageArray, PixelTools.identity, BufferedImage.TYPE_INT_RGB)
                , BASE_SAVE_FILE_PATH + index(index) + "_original.bmp", "Original");
        printTimeDiff(time, "Generate, display, and save original image - ");
        time = System.nanoTime();
        index++;

        /* Generate histogram by converting to intensity image */
        int[][] intensityImage = ImageFileTools.intArrayToIntArray(originalImageArray, PixelTools.intensityFromRgb);
        ImageFileTools.displayAndSave(ImageFileTools.displayHistogram(ImageTools.histogram(intensityImage)),
                BASE_SAVE_FILE_PATH + index(index) + "_histogram.bmp", "Histogram of Original Image by Intensity");
        printTimeDiff(time, "Generate intensity image, generate histogram, display and save histogram image - ");
        time = System.nanoTime();
        index++;

        /* Apply 2x1 operators. */
        int[][] twoByOne = ImageTools.convolution2d(intensityImage, new int[][]{{1, -1}}, ConvolutionEnum.COVER_ALL_POINTS, 1, true, PixelTools.multiplication);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(
                twoByOne,PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB)
                , BASE_SAVE_FILE_PATH + index(index) + "_2x1.bmp", "2x1");
        printTimeDiff(time, "Generate, display, and save 2x1 gradient - ");
        time = System.nanoTime();
        index++;

        int[][] oneByTwo = ImageTools.convolution2d(intensityImage, new int[][]{{1},{-1}}, ConvolutionEnum.COVER_ALL_POINTS, 1, true, PixelTools.multiplication);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(oneByTwo,
                PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB)
                , BASE_SAVE_FILE_PATH + index(index) + "_1x2.bmp", "1x2");
        printTimeDiff(time, "Generate, display, and save 1x2 gradient - ");
        time = System.nanoTime();
        index++;

        int[][] combinedGradientOperators = ImageFileTools.combineIntArrays(twoByOne, oneByTwo, MathTools.add);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(
                combinedGradientOperators, PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB)
                , BASE_SAVE_FILE_PATH + index(index) + "_combinedGradientOperators.bmp", "Combined Gradient Operators");
        printTimeDiff(time, "combine, display and save combined gradient - ");
        time = System.nanoTime();
        index++;

        /* generate Sobel images */
        /* Fun to fool around with the scaling factor generator */
        int[][] sobel3x3 = ImageFileTools.combineIntArrays(
                ImageTools.convolution2d(intensityImage, ImageTools.generateSobelMask(3, false),
                        ConvolutionEnum.COVER_ALL_POINTS, ImageTools.generateSobelScalingFactor(3), true, PixelTools.multiplication),
                ImageTools.convolution2d(intensityImage, ImageTools.generateSobelMask(3, true),
                        ConvolutionEnum.COVER_ALL_POINTS, ImageTools.generateSobelScalingFactor(3), true, PixelTools.multiplication),
                MathTools.add);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(sobel3x3, PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB),
                BASE_SAVE_FILE_PATH + index(index) + "_sobel3x3.bmp", "Sobel 3x3");
        printTimeDiff(time, "Generate horizontal sobel, generate vertical sobel, combine sobels, display, and save - ");
        time = System.nanoTime();
        index++;

        /* Subtract 1x2 from sobel */
        int[][] sobelMinusGradient = ImageFileTools.combineIntArrays(
                sobel3x3, combinedGradientOperators, MathTools.subtractToZero
        );
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(sobelMinusGradient,
                PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB
        ), BASE_SAVE_FILE_PATH + "0" + index + "_sobel_minus_gradient.bmp", "Sobel Minus Gradient");
        printTimeDiff(time, "Subtract 1x2 from sobel, display, and save - ");
        time = System.nanoTime();
        index++;

        /* Generate edge maps by taking histograms and keep top X percentage of values */
        float[] sobelHistogram = ImageTools.histogram(ImageFileTools.intArrayToIntArray(
                sobel3x3, PixelTools.grayValueFromRgbGray));
        ImageFileTools.displayAndSave(ImageFileTools.displayHistogram(sobelHistogram),
                BASE_SAVE_FILE_PATH + index(index) + "_sobelHistogram.bmp", "Sobel Histogram");
        printTimeDiff(time, "Generate sobel histogram, display and save - ");
        time = System.nanoTime();
        index++;

        float[] gradientHistogram = ImageTools.histogram(ImageFileTools.intArrayToIntArray(
                combinedGradientOperators, PixelTools.grayValueFromRgbGray));
        ImageFileTools.displayAndSave(ImageFileTools.displayHistogram(gradientHistogram),
                BASE_SAVE_FILE_PATH + index(index) + "_gradientHistogram.bmp", "Gradient Histogram");
        printTimeDiff(time, "Generate gradient histogram, display and save - ");
        time = System.nanoTime();
        index++;

        float[] sobelMinusGradientHistogram = ImageTools.histogram(ImageFileTools.intArrayToIntArray(
                sobelMinusGradient, PixelTools.grayValueFromRgbGray));
        ImageFileTools.displayAndSave(ImageFileTools.displayHistogram(sobelMinusGradientHistogram),
                BASE_SAVE_FILE_PATH + index(index) + "_sobelMinusGradientHistogram.bmp",
                "Sobel Minus Gradient Histogram");

        printTimeDiff(time, "Generate histogram from Sobel - 1x2, display, and save - ");
        time = System.nanoTime();
        index++;


        for (int i = 3; i < 10; i++) {
            ImageFileTools.displayAndSave(ImageFileTools.intToBI(ImageTools.edgeMapFromHistogramAndImage(
                                    sobelHistogram, sobel3x3, PixelTools.grayValueFromRgbGray, i),
                            PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB
                    ),
                    BASE_SAVE_FILE_PATH + index(index) + "_sobelEdgeMap" + i + "percent.bmp",
                    "Sobel Edge Map - " + i + "percent"
            );
            printTimeDiff(time, "Generate edge map from Sobel with " + i + " + percent of values, display, and save - ");
            time = System.nanoTime();
            index++;
        }

        for (int i = 3; i < 10; i++) {
            ImageFileTools.displayAndSave(ImageFileTools.intToBI(ImageTools.edgeMapFromHistogramAndImage(
                                    gradientHistogram, combinedGradientOperators, PixelTools.grayValueFromRgbGray, i),
                            PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB
                    ),
                    BASE_SAVE_FILE_PATH + index(index) + "_gradientEdgeMap" + i + "percent.bmp",
                    "Gradient Edge Map - " + i + " percent"
            );
            printTimeDiff(time, "Generate edge map from Gradient with " + i + " + percent of values, display, and save - ");
            time = System.nanoTime();
            index++;
        }

        for (int i = 3; i < 10; i++) {
            ImageFileTools.displayAndSave(ImageFileTools.intToBI(ImageTools.edgeMapFromHistogramAndImage(
                                    sobelMinusGradientHistogram, sobelMinusGradient, PixelTools.grayValueFromRgbGray, i),
                            PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB
                    ),
                    BASE_SAVE_FILE_PATH + index(index) + "_sobelMinusGradientEdgeMap" + i + "percent.bmp",
                    "Sobel Minus Gradient Edge Map - " + i + "percent"
            );
            printTimeDiff(time, "Generate edge map from Sobel with " + i + " percent of values, display, and save - ");
            time = System.nanoTime();
            index++;
        }

        /* Increase kernel size */
        for (int i = 5; i <= 9; i += 2) {
            ImageFileTools.displayAndSave(ImageFileTools.intToBI(
                            ImageFileTools.combineIntArrays(
                                    ImageTools.convolution2d(
                                            intensityImage, ImageTools.generateSobelMask(i, false), ConvolutionEnum.COVER_ALL_POINTS,
                                            ImageTools.generateSobelScalingFactor(i), true, PixelTools.multiplication),
                                    ImageTools.convolution2d(intensityImage, ImageTools.generateSobelMask(i, false),
                                            ConvolutionEnum.COVER_ALL_POINTS, ImageTools.generateSobelScalingFactor(i), true, PixelTools.multiplication),
                                    MathTools.add), PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB),
                    BASE_SAVE_FILE_PATH + index(index) + "_sobel" + i + "x" + i + ".bmp",
                    "Sobel " + i + "x" + i);
            printTimeDiff(time, "Generate horizontal sobel, generate vertical sobel, combine sobels, display, and save where mask is " + i + "x" + i +" - ");
            time = System.nanoTime();
            index++;
        }

        /* Generate differences from individual color differences */
        /* Perform sobel on each of the color planes. Then combine? */
        int[][] redOriginal = ImageFileTools.intArrayToIntArray(originalImageArray, PixelTools.redFromRgb);
        int[][] greenOriginal = ImageFileTools.intArrayToIntArray(originalImageArray, PixelTools.greenFromRgb);
        int[][] blueOriginal = ImageFileTools.intArrayToIntArray(originalImageArray, PixelTools.blueFromRgb);

        /* Lets see what edges appear from each */
        int[][] redSobel = ImageFileTools.combineIntArrays(
                        ImageTools.convolution2d(redOriginal, ImageTools.generateSobelMask(3, false),
                                ConvolutionEnum.COVER_ALL_POINTS, ImageTools.generateSobelScalingFactor(3), true, PixelTools.multiplication),
                        ImageTools.convolution2d(redOriginal, ImageTools.generateSobelMask(3, true),
                                ConvolutionEnum.COVER_ALL_POINTS, ImageTools.generateSobelScalingFactor(3), true, PixelTools.multiplication),
                        MathTools.addTo255);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(redSobel, PixelTools.rgbRedFromRedValue, BufferedImage.TYPE_INT_RGB),
                        BASE_SAVE_FILE_PATH + index(index) + "_sobelDifferenceRed.bmp", "Sobel RGB Differences Red");
        index++;


        int[][] greenSobel = ImageFileTools.combineIntArrays(
                        ImageTools.convolution2d(greenOriginal, ImageTools.generateSobelMask(3, false),
                                ConvolutionEnum.COVER_ALL_POINTS, ImageTools.generateSobelScalingFactor(3), true, PixelTools.multiplication),
                        ImageTools.convolution2d(greenOriginal, ImageTools.generateSobelMask(3, true),
                                ConvolutionEnum.COVER_ALL_POINTS, ImageTools.generateSobelScalingFactor(3), true, PixelTools.multiplication),
                        MathTools.addTo255);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(greenSobel, PixelTools.rgbGreenFromGreenValue, BufferedImage.TYPE_INT_RGB),
                        BASE_SAVE_FILE_PATH + index(index) + "_sobelDifferenceGreen.bmp", "Sobel RGB Differences Green");
        index++;

        int[][] blueSobel = ImageFileTools.combineIntArrays(
                        ImageTools.convolution2d(blueOriginal, ImageTools.generateSobelMask(3, false),
                                ConvolutionEnum.COVER_ALL_POINTS, ImageTools.generateSobelScalingFactor(3), true, PixelTools.multiplication),
                        ImageTools.convolution2d(blueOriginal, ImageTools.generateSobelMask(3, true),
                                ConvolutionEnum.COVER_ALL_POINTS, ImageTools.generateSobelScalingFactor(3), true, PixelTools.multiplication),
                        MathTools.addTo255);
        ImageFileTools.displayAndSave(ImageFileTools.intToBI(blueSobel, PixelTools.rgbBlueFromBlueValue, BufferedImage.TYPE_INT_RGB),
                        BASE_SAVE_FILE_PATH + index(index) + "_sobelDifferenceBlue.bmp", "Sobel RGB Differences Blue");
        index++;


        int[][] combinedSobel = new int[blueSobel.length][blueSobel[0].length];
        for (int i = 0; i < blueSobel.length; i++) {
            for (int j = 0; j < blueSobel[0].length; j++) {
                combinedSobel[i][j] = PixelTools.getValue(redSobel[i][j], greenSobel[i][j], blueSobel[i][j], PixelTools.rgbFromValues);
            }
        }

        ImageFileTools.displayAndSave(ImageFileTools.intToBI(combinedSobel, PixelTools.identity, BufferedImage.TYPE_INT_RGB),
                BASE_SAVE_FILE_PATH + index(index) + "_sobelDifferenceRGB.bmp", "Sobel RGB Differences All");

        index++;

        int[][] combinedSobelGray = new int[blueSobel.length][blueSobel[0].length];
        for (int i = 0; i < blueSobel.length; i++) {
            for (int j = 0; j < blueSobel[0].length; j++) {
                combinedSobelGray[i][j] = PixelTools.getValue(blueSobel[i][j] + greenSobel[i][j] + redSobel[i][j]/3, PixelTools.rgbGrayFromGrayValue);
            }
        }

        ImageFileTools.displayAndSave(ImageFileTools.intToBI(combinedSobelGray, PixelTools.identity, BufferedImage.TYPE_INT_RGB),
                BASE_SAVE_FILE_PATH + index(index) + "_sobelDifferenceRGBGray.bmp", "Sobel RGB Differences All");

        index++;


    }

    private static String index(int index) {
        return index > 9 ? "" + index : "0" + index;
    }

    private static void printTimeDiff(long begin, String message) {
        System.out.println(message + (System.nanoTime() - begin) / 100000000.0);
    }

}
