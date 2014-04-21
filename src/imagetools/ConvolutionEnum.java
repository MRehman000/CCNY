package imagetools;

/**
 * Created by codyboppert on 4/20/14.
 */
public enum ConvolutionEnum {
    /*
     * MASK_FITS_ONLY - mask matrix must have all values within the image
     * COVER_ALL_POINTS - assume zeros outside image matrix but use mask on all values of image matrix
     * CREATE_LARGER_MATRIX - generates a larger image matrix. Used to generate convolution matrices for sobel operator
     * */
    MASK_FITS_ONLY,
    COVER_ALL_POINTS,
    CREATE_LARGER_MATRIX;
}
