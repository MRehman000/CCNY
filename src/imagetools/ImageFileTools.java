package imagetools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by codyboppert on 4/20/14.
 */
public class ImageFileTools {

    public static int[][] readImageToIntArray(String filepath) {
        return biToIntArray(readImageToBI(filepath));
    }

    public static int[][] biToIntArray(BufferedImage image) {
        int[][] imageArray = new int[image.getWidth()][image.getHeight()];
        for (int i = 0; i < image.getWidth(); i ++) {
            for (int j = 0; j < image.getHeight(); j++) {
                imageArray[i][j] = image.getRGB(i, j);
            }
        }

        return imageArray;
    }

    public static BufferedImage intToBI(int[][] pixelArray, ImageTools.ValueOperation valueOperation, int bufferedImageType) {
        int width = pixelArray.length;
        int height = pixelArray[0].length;

        BufferedImage bufferedImage = new BufferedImage(width, height, bufferedImageType);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedImage.setRGB(i, j, ImageTools.getValue(pixelArray[i][j], valueOperation));
            }
        }

        return bufferedImage;
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
            ImageIO.write(image, "bmp", file);
        } catch (IOException e) {
            throw new RuntimeException("Could not save file! " + path + " ----> " + e);
        }
    }

    public static void displayAndSave(BufferedImage image, String path, String title) {
        displayImage(image, title);
        saveImage(image, path);
    }

}
