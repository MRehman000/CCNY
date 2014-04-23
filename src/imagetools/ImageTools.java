package imagetools;

import java.awt.image.BufferedImage;

/**
 * Created by codyboppert on 4/20/14.
 */
public class ImageTools {
    /* Use with square convolution matrices or 1x2 or 2x1 sized matrices */
    public static int[][] convolution2d(int[][] array, int[][] mask, ConvolutionEnum type, double scale, boolean absoluteValue) {
        int[][] convolutedMatrix;
        int startRow, startColumn, endRow, endColumn;
        int maskWidth = mask.length, maskHeight = mask[0].length, arrayWidth = array.length, arrayHeight = array[0].length;
        int offsetWidth = (maskWidth % 2 == 0 ? 1 : (maskWidth - 1) / 2),
                offsetHeight = (maskHeight % 2 == 0 ? 1 : (maskHeight - 1) / 2);

        if (type.equals(ConvolutionEnum.MASK_FITS_ONLY)) {
            convolutedMatrix = new int[arrayWidth - ((maskWidth % 2 == 0) ? maskWidth : (maskWidth - 1))]
                    [arrayHeight - ((maskHeight % 2 == 0) ? maskHeight : maskHeight - 1)];
            startRow = ((maskWidth % 2 == 0) ? maskWidth / 2 : (maskWidth - 1) / 2);
            startColumn = ((maskHeight % 2 == 0) ? maskHeight / 2 : (maskHeight - 1) / 2);
            endRow = arrayWidth - ((maskWidth % 2 == 0) ? maskWidth / 2 : (maskWidth - 1) / 2);
            endColumn = arrayHeight - ((maskHeight % 2 == 0) ? maskHeight / 2 : (maskHeight - 1) / 2);
        } else if (type.equals(ConvolutionEnum.COVER_ALL_POINTS)) {
            convolutedMatrix = new int[arrayWidth][arrayHeight];
            startRow = ((maskWidth % 2 == 0) ? -(maskWidth / 2) : -((maskWidth - 1)) / 2);
            startColumn = ((maskHeight % 2 == 0) ? -(maskHeight / 2) : -(maskHeight - 1) / 2);
            endRow = arrayWidth + startRow;
            endColumn = arrayHeight + startColumn;
        } else {
            convolutedMatrix = new int[arrayWidth + 2][arrayHeight + 2];
            startRow = -((maskWidth % 2 == 0) ? maskWidth / 2 : (maskWidth - 1) / 2);
            startColumn = -((maskHeight % 2 == 0) ? maskHeight / 2 : (maskHeight - 1) / 2);
            endRow = -(startRow) + arrayWidth;
            endColumn = -(startColumn) + arrayHeight;
        }

        for (int i = startRow; i < endRow; i++) {
            for (int j = startColumn; j < endColumn; j++) {
                int sum = 0;
                for (int k = -(offsetWidth); k <= (maskWidth == 2 ? 0 : offsetWidth); k++) {
                    for (int l = -(offsetHeight); l <= (maskHeight == 2 ? 0 : offsetHeight); l++) {
                        if (i + k >= 0 && j + l >= 0 && i + k < arrayWidth && j + l < arrayHeight) {
                            sum += array[i + k][j + l] * mask[k + offsetWidth][l + offsetHeight];
                        }
                    }
                }
                if (type.equals(ConvolutionEnum.CREATE_LARGER_MATRIX)) {
                    if (i >= -1 && j >= -1 && i < arrayWidth + 1 && j < arrayHeight + 1) {
                        convolutedMatrix[i + 1][j + 1] = absoluteValue ? (int) Math.round(absoluteValue(sum) * scale) :
                                                                            (int) Math.round(sum * scale);
                    }
                } else {
                    if (scale < 1) {
                        System.out.println((absoluteValue) ? (int) Math.round(absoluteValue(sum) * scale) + " - " + mask.length + " - " + scale :
                                (int) Math.round(sum * scale) + " - " + mask.length + " - " + scale);
                    }
                    convolutedMatrix[i + offsetWidth][j + offsetHeight] = absoluteValue ?
                                                                            (int) Math.round(absoluteValue(sum) * scale)
                                                                            :(int) Math.round(sum * scale);

                }
            }
        }
        return convolutedMatrix;
    }

    private static int[][] generateSobelMask(int dimensions, int[][] mask, int[][] smoothingKernel) {
        if (dimensions == mask.length) {
            return mask;
        } else  {
            return generateSobelMask(dimensions,
                    convolution2d(mask, smoothingKernel, ConvolutionEnum.CREATE_LARGER_MATRIX, 1, false), smoothingKernel);
        }
    }

    public static int[][] generateSobelMask(int dimensions) {
        if (dimensions % 2 != 0 && dimensions > 2) {
            int[][] threeByThree = new int[][] {
                    {1, 0, -1},
                    {2, 0, -2},
                    {1, 0, -1}
            };

            return dimensions == 3 ? threeByThree : generateSobelMask(dimensions, threeByThree, new int[][]{
                    {1, 2, 1},
                    {2, 4, 2},
                    {1, 2, 1}
            });
        }
        throw new RuntimeException("Tried to generate an improper sobel mask of dimension " + dimensions);
    }

    /* Fooling around with the scaling factor generator can be fun */
    public static double generateSobelScalingFactor(int dimensions) {
        double scalingFactor = .75;
        if (dimensions % 2 != 0 && dimensions > 2) {
            for (int a = 3; a <= dimensions; a += 2) {
                scalingFactor *= 0.25;
            }
            return scalingFactor;
        }
        throw new RuntimeException("Tried to generate an improper sobel scaling factor for dimension " + dimensions);
    }

    public static float[] histogram(int[][] image) {
        int width = image.length;
        int height = image[0].length;

        float[] histogram = new float[256];

        float max = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                histogram[(image[i][j])] += 1;
                if (histogram[(image[i][j])] > max) {
                    max = histogram[(image[i][j])];
                }
            }
        }
        for (int i = 0; i < 256; i++) {
            histogram[i] = (histogram[i] * 100) / max;
        }
        return histogram;
    }

    private static int absoluteValue(int value) {
        return (value < 0) ? value * -1 : value;
    }

}
