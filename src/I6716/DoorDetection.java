package I6716;

import imagetools.ConvolutionEnum;
import imagetools.ImageFileTools;
import imagetools.ImageTools;
import imagetools.PixelTools;
import utilities.MathTools;

import javax.activation.MimetypesFileTypeMap;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

/**
 * Created by codyboppert on 5/11/14.
 */
public class DoorDetection {

    private final static String IMAGE_FOLDER_PATH = "/users/codyboppert/Workspace/CCNY/i6716-ComputerVision/Project/Data/";
    private final static String IMAGE_POSITIVE_PATH = IMAGE_FOLDER_PATH + "Positive/";
    private final static String IMAGE_NEGATIVE_PATH = IMAGE_FOLDER_PATH + "Negative/";
    private final static String IMAGE_RESIZED_POSITIVE_PATH = IMAGE_FOLDER_PATH + "RePositive/";
    private final static String IMAGE_RESIZED_NEGATIVE_PATH = IMAGE_FOLDER_PATH + "ReNegative/";
    private final static String BW_POSITIVE_PATH = IMAGE_FOLDER_PATH + "bwPositive/";
    private final static String BW_NEGATIVE_PATH = IMAGE_FOLDER_PATH + "bwNegative/";
    private final static String BW_EDGE_POSITIVE_PATH = IMAGE_FOLDER_PATH + "bwEdgePositive/";
    private final static String BW_EDGE_NEGATIVE_PATH = IMAGE_FOLDER_PATH + "bwEdgeNegative/";
    private final static String BW_POSITIVE_RESIZED_PATH = IMAGE_FOLDER_PATH + "bwRePositive/";
    private final static String BW_NEGATIVE_RESIZED_PATH = IMAGE_FOLDER_PATH + "bwReNegative/";
    private final static String BW_EDGE_RESIZED_POSITIVE_PATH = IMAGE_FOLDER_PATH + "bwEdgeRePositive/";
    private final static String BW_EDGE_RESIZED_NEGATIVE_PATH = IMAGE_FOLDER_PATH + "bwEdgeReNegative/";

    public static void main(String[] args) {
        generateDatasets();
    }

    private static void generateDatasets() {
        File positiveFolder = new File(IMAGE_POSITIVE_PATH);
        File negativeFolder = new File(IMAGE_NEGATIVE_PATH);
        File rePositiveFolder = new File(IMAGE_RESIZED_POSITIVE_PATH);
        File reNegativeFolder = new File(IMAGE_RESIZED_NEGATIVE_PATH);
        File bwPositiveFolder = new File(BW_POSITIVE_PATH);
        File bwNegativeFolder = new File(BW_NEGATIVE_PATH);
        File bwPositiveEdgeFolder = new File(BW_EDGE_POSITIVE_PATH);
        File bwNegativeEdgeFolder = new File(BW_EDGE_NEGATIVE_PATH);
        File bwRePositiveFolder = new File(BW_POSITIVE_RESIZED_PATH);
        File bwReNegativeFolder = new File(BW_NEGATIVE_RESIZED_PATH);
        File bwEdgeRePositiveFolder = new File(BW_EDGE_RESIZED_POSITIVE_PATH);
        File bwEdgeReNegativeFolder = new File(BW_EDGE_RESIZED_NEGATIVE_PATH);

        if (!rePositiveFolder.exists()) createFolder(rePositiveFolder);
        if (!reNegativeFolder.exists()) createFolder(reNegativeFolder);
        if (!bwPositiveFolder.exists()) createFolder(bwPositiveFolder);
        if (!bwNegativeFolder.exists()) createFolder(bwNegativeFolder);
        if (!bwPositiveEdgeFolder.exists()) createFolder(bwPositiveEdgeFolder);
        if (!bwNegativeEdgeFolder.exists()) createFolder(bwNegativeEdgeFolder);
        if (!bwRePositiveFolder.exists()) createFolder(bwRePositiveFolder);
        if (!bwReNegativeFolder.exists()) createFolder(bwReNegativeFolder);
        if (!bwEdgeRePositiveFolder.exists()) createFolder(bwEdgeRePositiveFolder);
        if (!bwEdgeReNegativeFolder.exists()) createFolder(bwEdgeReNegativeFolder);

        int[] mins = generateImages(positiveFolder.listFiles(), true, 987654321, 987654321);
        mins = generateImages(negativeFolder.listFiles(), false, mins[0], mins[1]);
        int minW = mins[0];
        int minH = mins[1];
        generateResizedImages(positiveFolder.listFiles(), true, minW, minH);
        generateResizedImages(negativeFolder.listFiles(), false, minW, minH);

    }

    private static int[] generateImages(File[] images, boolean positive, int minW, int minH) {
        String bwPath = positive ? BW_POSITIVE_PATH : BW_NEGATIVE_PATH;
        String edgePath = positive ? BW_EDGE_POSITIVE_PATH : BW_EDGE_NEGATIVE_PATH;

        int smallestWidth = minW;
        int smallestHeight = minH;
        for (File image : images) {
            if (checkIfImage(image)) {
                String imageName = image.getName();
                imageName = imageName.substring(0, imageName.lastIndexOf("."));
                String bwImageName = imageName + "bw.png";
                String bwEdgeImageName = imageName + "bwEdge.png";

                File bwImage = new File(bwPath + bwImageName);
                File bwEdgeImage = new File(edgePath + bwEdgeImageName);

                BufferedImage bImage = ImageFileTools.readImageToBI(image.getAbsolutePath());
                smallestWidth = (bImage.getWidth() < smallestWidth) ? bImage.getWidth() : smallestWidth;
                smallestHeight = (bImage.getHeight() < smallestHeight) ? bImage.getHeight() : smallestHeight;

                if (!bwImage.exists() || !bwEdgeImage.exists()) {
                    int[][] intensityArray = ImageFileTools.biToIntArray(bImage, PixelTools.intensityFromRgb);

                    ImageFileTools.saveImage(ImageFileTools.intToBI(
                            intensityArray, PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB),
                            bwPath + bwImageName);

                    ImageFileTools.saveImage(
                            ImageFileTools.intToBI(ImageFileTools.combineIntArrays(
                                    ImageTools.convolution2d(intensityArray, ImageTools.generateSobelMask(3, false),
                                            ConvolutionEnum.COVER_ALL_POINTS,
                                            ImageTools.generateSobelScalingFactor(3), true, PixelTools.multiplication),
                                    ImageTools.convolution2d(intensityArray, ImageTools.generateSobelMask(3, true),
                                            ConvolutionEnum.COVER_ALL_POINTS,
                                            ImageTools.generateSobelScalingFactor(3), true, PixelTools.multiplication),
                                    MathTools.add), PixelTools.rgbGrayFromGrayValue, BufferedImage.TYPE_INT_RGB),
                            edgePath + bwEdgeImageName);
                }
            } else {
                image.delete();
            }
        }

        return new int[]{smallestWidth, smallestHeight};
    }

    private static void generateResizedImages(File[] images, boolean positive, int width, int height) {
        String rePath = positive ? IMAGE_RESIZED_POSITIVE_PATH : IMAGE_RESIZED_NEGATIVE_PATH;
        String bwPath = positive ? BW_POSITIVE_PATH : BW_NEGATIVE_PATH;
        String edgePath = positive ? BW_EDGE_POSITIVE_PATH : BW_EDGE_NEGATIVE_PATH;
        String bwRePath = positive ? BW_POSITIVE_RESIZED_PATH : BW_NEGATIVE_RESIZED_PATH;
        String bwEdgeRePath = positive ? BW_EDGE_RESIZED_POSITIVE_PATH : BW_EDGE_RESIZED_NEGATIVE_PATH;

        for (File image : images) {
            String imageName = image.getName();
            imageName = imageName.substring(0, imageName.lastIndexOf("."));
            String reImageName = rePath + imageName + "re.png";
            String bwReImageName = bwRePath + imageName + "bwRe.png";
            String bwEdgeReImageName = bwEdgeRePath + imageName + "bwEdgeRe.png";
            String bwImageName = bwPath + imageName + "bw.png";
            String bwEdgeImageName = edgePath + imageName + "bwEdge.png";

            File reImage = new File(reImageName);
            File bwReImage = new File(bwReImageName);
            File bwEdgeReImage = new File(bwEdgeReImageName);

            if (!reImage.exists() || !bwReImage.exists() || !bwEdgeReImage.exists()) {
                ImageFileTools.saveImage(
                        ImageFileTools.resizeBufferedImage(
                                ImageFileTools.readImageToBI(image.getAbsolutePath()), width, height
                        ), reImageName
                );

                File bwImage = new File(bwImageName);
                File bwEdgeImage = new File(bwEdgeImageName);

                if (bwImage.exists()){
                    ImageFileTools.saveImage(
                            ImageFileTools.resizeBufferedImage(
                                    ImageFileTools.readImageToBI(bwImageName), width, height
                            ), bwReImageName);
                }

                if (bwEdgeImage.exists()) {
                    ImageFileTools.saveImage(
                            ImageFileTools.resizeBufferedImage(
                                    ImageFileTools.readImageToBI(bwEdgeImageName), width, height
                            ), bwEdgeReImageName
                    );
                }
            }
        }
    }

    private static boolean checkIfImage(File image) {
        String mimeType = new MimetypesFileTypeMap().getContentType(image);
        return mimeType.substring(0, 5).equalsIgnoreCase("image");
    }

    private static void createFolder(File folder) {
        boolean create = folder.mkdir();
        if (create) {
            System.out.println("Created folder " + folder.getAbsolutePath());
        }
    }
}
