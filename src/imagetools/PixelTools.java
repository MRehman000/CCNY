package imagetools;

/**
 * Created by codyboppert on 4/15/14.
 */
public class PixelTools {
    static int PIXEL_VALUES = 255;

    @FunctionalInterface
    static interface ValueOperation {
        int pixelOperation(int pixelValue);
    }
    static int getValue(int pixelValue, ValueOperation operation) {
        return operation.pixelOperation(pixelValue);
    }

    @FunctionalInterface
    static interface RgbOperation {
        int rgbOperation(int red, int green, int blue);
    }
    public static int getValue(int red, int green, int blue, RgbOperation operation) {
        return operation.rgbOperation(red, green, blue);
    }

    @FunctionalInterface
    static interface RgbaOperation {
        int rgbaOperation(int red, int green, int blue, int alpha);
    }
    public static int getValue(int red, int green, int blue, int alpha, RgbaOperation operation) {
        return operation.rgbaOperation(red, green, blue, alpha);
    }

    public static ValueOperation identity = (rgba) -> rgba;

    public static ValueOperation redFromRgb = (rgb) -> (rgb >> 16) & 0x000000FF;
    public static ValueOperation rgbRedFromRedValue = (redValue) -> redValue > PIXEL_VALUES ? PIXEL_VALUES : redValue  << 16;
    public static ValueOperation rgbRedFromRgbValue = (rgb) -> getValue(getValue(rgb, redFromRgb), rgbRedFromRedValue);

    public static ValueOperation blueFromRgb = (rgb) -> (rgb >> 8) & 0x000000FF;
    public static ValueOperation rgbBlueFromBlueValue = (blueValue) -> blueValue > PIXEL_VALUES ? PIXEL_VALUES : blueValue;
    public static ValueOperation rgbBlueFromRgbValue = (rgb) -> getValue(getValue(rgb, blueFromRgb), rgbBlueFromBlueValue);

    public static ValueOperation greenFromRgb = (rgb) -> (rgb) & 0x000000FF;
    public static ValueOperation rgbGreenFromGreenValue = (greenValue) -> greenValue > PIXEL_VALUES ? PIXEL_VALUES : greenValue << 8;
    public static ValueOperation rgbGreenFromRgbValue = (rgb) -> getValue(getValue(rgb, greenFromRgb), rgbGreenFromGreenValue);

    public static ValueOperation rgbGrayFromGrayValue = (grayValue) -> grayValue > 255 ?
            shiftAndOr(shiftAndOr(255, 255), 255) :
            shiftAndOr(shiftAndOr(grayValue,grayValue),grayValue);
    public static RgbOperation rgbFromValues = (r, g, b) -> shiftAndOr(shiftAndOr(r, g), b);
    public static RgbaOperation rgbaFromValues = (r, g, b, a) -> shiftAndOr(shiftAndOr(shiftAndOr(r,g),b),a);
    public static ValueOperation grayValueFromRgbGray = (rgbGrayValue) -> (rgbGrayValue) & 0x000000FF;

    /* Get intensity - 0 < I < 1 = 0.299R + 0.587G + 0.114B */
    public static ValueOperation intensityFromRgb = (rgb) -> (int) Math.round(0.299 * getValue(rgb, redFromRgb) +
            0.587 * getValue(rgb, greenFromRgb) + 0.114 * getValue(rgb, blueFromRgb));
    public static ValueOperation rgbIntensityFromRgb = (rgb) -> getValue(getValue(rgb, intensityFromRgb), rgbGrayFromGrayValue);

    /* Get lightness - (max(r, g, b) + min(r, g, b))/2*/
    public static ValueOperation lightnessFromRgb = (rgb) -> (int) Math.round((max(max(getValue(rgb, redFromRgb),
            getValue(rgb, greenFromRgb)), getValue(rgb, blueFromRgb)) +
            min(min(getValue(rgb, redFromRgb), getValue(rgb, greenFromRgb)), getValue(rgb, blueFromRgb)))/2.0);
    public static ValueOperation rgbLightnessFromRgb = (rgb) -> getValue(getValue(rgb, lightnessFromRgb), rgbGrayFromGrayValue);

    /* Get average - (r + g + b) / 3 */
    public static ValueOperation averageFromRgb = (rgb) -> (int) Math.round((getValue(rgb, redFromRgb) +
            getValue(rgb, greenFromRgb) + getValue(rgb, blueFromRgb))/3.0);
    public static ValueOperation rgbAverageFromRgb = (rgb) -> getValue(getValue(rgb, averageFromRgb), rgbGrayFromGrayValue);

    /* Get luminosity - 0 < L < 1 = 0.21 R + 0.71 G + 0.07 B */
    public static ValueOperation luminosityFromRgb = (rgb) -> (int) Math.round(0.21 * getValue(rgb, redFromRgb) +
            0.71 * getValue(rgb, greenFromRgb) + 0.07 * getValue(rgb, blueFromRgb));
    public static ValueOperation rgbLuminosityFromRgb = (rgb) -> getValue(getValue(rgb, luminosityFromRgb), rgbGrayFromGrayValue);

    public static ValueOperation quantizedValue(int intervals) {
        return (value) -> {
            int increment = 256 / intervals;
            int count = 1;
            while (value > count * increment) count++;
            return (count == intervals) ? 255 : (count - 1) * increment;
        };
    }

    public static ValueOperation quantizedGrayRgbFromGrayRgb(int intervals) {
        return (rgb) -> getValue(getValue(getValue(rgb, grayValueFromRgbGray), quantizedValue(intervals)), rgbGrayFromGrayValue);
    }

    public static ValueOperation quantizedValueFromRgb(int intervals, ValueOperation valueOperation) {
        return (rgb) -> getValue(getValue(rgb, valueOperation), quantizedValue(intervals));
    }

    public static ValueOperation quantizedRgbFromRgb(int intervals) {
        return (rgb) -> getValue(getValue(rgb, quantizedValueFromRgb(intervals, redFromRgb)),
                getValue(rgb, quantizedValueFromRgb(intervals, greenFromRgb)),
                getValue(rgb, quantizedValueFromRgb(intervals, blueFromRgb)), rgbFromValues);
    }

    /* Logarithmically Quantized Intensity
        Formula: I' = C ln (intensity + 1)
        Where 0 <= intensity < 256
        ln(1) = 0; ln(256) = 5.54517744448
        choose C = 255 / 5.54517744448 = 45.9859044283
     */
    public static ValueOperation logarithmicallyQuantizedIntensityFromValue = (value) -> (int) Math.round(
            45.9859044283 * Math.log(value + 1.0));
    public static ValueOperation grayRgbLogarithmicallyQuantizedIntensityFromGrayRgb = (rgb) -> getValue(getValue(
            getValue(rgb, rgbGrayFromGrayValue), logarithmicallyQuantizedIntensityFromValue), rgbGrayFromGrayValue);
    public static ValueOperation rgbLogarithmicallyQuantizedIntesityFromRgb = (rgb) -> getValue(
            getValue(getValue(rgb, redFromRgb), logarithmicallyQuantizedIntensityFromValue),
            getValue(getValue(rgb, greenFromRgb), logarithmicallyQuantizedIntensityFromValue),
            getValue(getValue(rgb, blueFromRgb), logarithmicallyQuantizedIntensityFromValue),
            rgbFromValues);

    private static int shiftAndOr(int intToShift, int intToOr) {
        intToShift <<= 8;
        intToShift |= intToOr;
        return intToShift;
    }

    private static int max(int valueOne, int valueTwo) {
        return (valueOne > valueTwo) ? valueOne : valueTwo;
    }

    private static int min(int valueOne, int valueTwo) {
        return (valueTwo < valueOne) ? valueOne : valueTwo;
    }
}
