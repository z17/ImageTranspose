package image_transpose.helper;

import org.apache.commons.math3.complex.Complex;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;

public final class MathHelper {
    public static Complex[] cfft(double[] a) {
        double[] data = convertToArrayForFft(a);
        DoubleFFT_1D fft = new DoubleFFT_1D(a.length);
        fft.complexForward(data);
        return convertFftResultToComplex(data);
    }

    public static Complex[] icfft(double[] a) {
        double[] data = convertToArrayForFft(a);
        DoubleFFT_1D fft = new DoubleFFT_1D(a.length);
        fft.realInverse(data, false);
        return convertFftResultToComplex(data);
    }

    public static Complex[] icfft(Double[] a) {
        double[] doubles = Arrays.stream(a).mapToDouble(v -> v).toArray();
        return icfft(doubles);
    }

    public static Complex[] icfft(Complex[] s) {
        double[] doubles = convertComplexToFftData(s);
        DoubleFFT_1D fft = new DoubleFFT_1D(s.length);
        fft.realInverse(doubles, false);
        return convertFftResultToComplex(doubles);
    }

    public static Complex[] cfft(Double[] a) {
        double[] doubles = Arrays.stream(a).mapToDouble(v -> v).toArray();
        return cfft(doubles);
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

    private static double[] convertToArrayForFft(double[] a) {
        double[] data = new double[a.length * 2];
        for (int i = 0; i < a.length; i++) {
            data[2 * i] = a[i];
            data[2 * i + 1] = 0;
        }
        return data;
    }

    private static Complex[] convertFftResultToComplex(double[] data) {
        Complex[] result = new Complex[data.length / 2];
        for (int i = 0; i < data.length / 2; i++) {
            result[i] = new Complex(data[2 * i], data[2 * i + 1]);
        }
        return result;
    }

    private static double[] convertComplexToFftData(Complex[] data) {
        double[] result = new double[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            result[2 * i] = data[i].getArgument();
            result[2 * i + 1] = data[i].getImaginary();
        }
        return result;
    }
}
