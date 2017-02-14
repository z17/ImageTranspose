package image_transpose.helper;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.Arrays;

public final class MathHelper {
    public static Complex[] ccft(double[] a) {
        final FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.UNITARY);
        return transformer.transform(a, TransformType.INVERSE);
    }

    public static Complex[] iccft(double[] a) {
        final FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.UNITARY);
        return transformer.transform(a, TransformType.FORWARD);
    }

    public static Complex[] ccft(int[] a) {
        double[] doubles = Arrays.stream(a).mapToDouble(v -> v).toArray();
        return ccft(doubles);
    }

    public static Complex[] ccft(Integer[] a) {
        double[] doubles = Arrays.stream(a).mapToDouble(v -> v).toArray();
        return ccft(doubles);
    }

    public static Complex[] iccft(Integer[] a) {
        double[] doubles = Arrays.stream(a).mapToDouble(v -> v).toArray();
        return iccft(doubles);
    }

    public static Complex[] iccft(Double[] a) {
        double[] doubles = Arrays.stream(a).mapToDouble(v -> v).toArray();
        return iccft(doubles);
    }

    public static Complex[] iccft(Complex[] s) {
        final FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.UNITARY);
        return transformer.transform(s, TransformType.FORWARD);
    }

    public static Complex[] ccft(Double[] a) {
        double[] doubles = Arrays.stream(a).mapToDouble(v -> v).toArray();
        return ccft(doubles);
    }

    public static Double[] minus(Double[] a, Double[] b) {
        Double[] res = new Double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    public static Double[][] augument(Double[][]... args) {
        int cols = FunctionHelper.cols(args[0]);
        int rows = FunctionHelper.rows(args[0]);

        int newCols = cols * args.length;

        Double[][] res = new Double[rows][newCols];

        int currentStartCol = 0;
        for (Double[][] m : args) {
            for (int i = 0; i < rows; i++) {
                System.arraycopy(m[i], 0, res[i], currentStartCol * cols, cols);
            }
        }
        return res;
    }

}
