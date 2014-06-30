package imagetools;

import utilities.MathTools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

/**
 * Created by codyboppert on 4/20/14.
 */
public class ImageFileTools {

    public static int[][] readImageToIntArray(String filepath) {
        return biToIntArray(readImageToBI(filepath), PixelTools.identity);
    }

    public static int[][] biToIntArray(BufferedImage image, PixelTools.ValueOperation valueOperation) {
        int[][] imageArray = new int[image.getWidth()][image.getHeight()];
        for (int i = 0; i < image.getWidth(); i ++) {
            for (int j = 0; j < image.getHeight(); j++) {
                imageArray[i][j] = PixelTools.getValue(image.getRGB(i, j), valueOperation);
            }
        }

        return imageArray;
    }

    public static BufferedImage intToBI(int[][] pixelArray, PixelTools.ValueOperation valueOperation, int bufferedImageType) {
        int width = pixelArray.length;
        int height = pixelArray[0].length;

        BufferedImage bufferedImage = new BufferedImage(width, height, bufferedImageType);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedImage.setRGB(i, j, PixelTools.getValue(pixelArray[i][j], valueOperation));
            }
        }

        return bufferedImage;
    }

    public static BufferedImage biToBI(BufferedImage image, PixelTools.ValueOperation valueOperation, int bufferedImageType) {
        int width = image.getWidth(), height = image.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, bufferedImageType);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedImage.setRGB(i, j, PixelTools.getValue(image.getRGB(i, j), valueOperation));
            }
        }
        return bufferedImage;
    }

    public static int[][] intArrayToIntArray(int[][] array, PixelTools.ValueOperation valueOperation) {
        int width = array.length, height = array[0].length;

        int[][] returnArray = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                returnArray[i][j] = PixelTools.getValue(array[i][j], valueOperation);
            }
        }
        return returnArray;
    }

    public static int[][] combineIntArrays(int[][] array, int[][] arrayTwo, MathTools.BinaryOp binaryOp) {
        int width = array.length < arrayTwo.length ? array.length : arrayTwo.length;
        int height = array[0].length < arrayTwo[0].length ? array[0].length : arrayTwo[0].length;
        int[][] combined = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                combined[i][j] = MathTools.op(array[i][j], arrayTwo[i][j], binaryOp);
            }
        }
        return combined;
    }

    public static BufferedImage readImageToBI(String filepath) {
        try {
            BufferedImage image = ImageIO.read(new File(filepath));
            return image;
        } catch (IOException e) {
            System.out.println("Error reading file at " + filepath + " e: " + e);
        }
        throw new RuntimeException("File reading error");
    }

    public static void displayImage(BufferedImage image, String title) {
        ImageIcon imageIcon = new ImageIcon(image);
        JFrame jFrame = new JFrame(title);
        jFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JLabel jLabel = new JLabel(imageIcon);
        jFrame.getContentPane().add(jLabel, BorderLayout.CENTER);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public static void saveImage(BufferedImage image, String path) {
        File file = new File(path);
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException("Could not save file! " + path + " ----> " + e);
        }
    }

    public static BufferedImage displayHistogram(float[] counts) {
        int width = 768;
        int height = 700;

        BufferedImage histogram = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i += 3) {
            for (int j = 0; j < height; j++) {
                if (i == width - 3) {
                    for (int k = i; k < i + 3; k++) {
                        if (j < ((((k - i)*(counts[(i/3)] - counts[i/3 - 1]))/3) + counts[i/3]) * 7) {
                            histogram.setRGB(k, 699 - j, 0x000000);
                        } else {
                            histogram.setRGB(k, 699 - j, 0xFFFFFF);
                        }
                    }
                } else {
                    for (int k = i; k < i + 3; k++) {
                        if (j < ((((k - i)*(counts[(i/3) + 1] - counts[i/3]))/3) + counts[i/3]) * 7) {
                            histogram.setRGB(k, 699 - j, 0x000000);
                        } else {
                            histogram.setRGB(k, 699 - j, 0xFFFFFF);
                        }
                    }
                }
            }
        }

        return histogram;
    }

    public static void displayAndSave(BufferedImage image, String path, String title) {
        displayImage(image, title);
        saveImage(image, path);
    }

    public static BufferedImage resizeBufferedImage(BufferedImage image, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = resized.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();

        return resized;
    }

}
