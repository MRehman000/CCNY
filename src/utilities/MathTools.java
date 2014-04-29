package utilities;

/**
 * Created by codyboppert on 4/22/14.
 */
public class MathTools {
    @FunctionalInterface
    public static interface UnaryOp {
        int op(int a);
    }
    public static int op(int a, UnaryOp unaryOp) { return unaryOp.op(a); }

    @FunctionalInterface
    public static interface BinaryOp {
        int op(int a, int b);
    }
    public static int op(int a, int b, BinaryOp binaryOp) { return binaryOp.op(a, b); }

    public static UnaryOp absoluteValue = (a) -> (a < 0) ? -(a) : a;

    public static BinaryOp subtract = (a, b) -> a - b;

    public static BinaryOp add = (a, b) -> a + b;

    public static BinaryOp addTo255 = (a, b) -> a + b > 255 ? 255 : a + b;

    public static BinaryOp subtractToZero = (a, b) -> a - b > 0 ? a - b : 0;

}
