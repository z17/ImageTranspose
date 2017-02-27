package image_transpose;


import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.DoubleFFT_2D;
import org.jtransforms.fft.RealFFTUtils_2D;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        {
            double[] data = new double[]{1, 0, 2, 0, 3, 0};

            DoubleFFT_1D df1 = new DoubleFFT_1D(data.length / 2);
            df1.complexForward(data);
            System.out.println(Arrays.toString(data));
        }

        {
            double[] data = new double[]{1, 0, 2, 0, 3, 0};

            DoubleFFT_1D df1 = new DoubleFFT_1D(data.length / 2);
            df1.complexInverse(data, false);
            System.out.println(Arrays.toString(data));
        }

        {
            double[] data = new double[]{1, 2, 3};

            DoubleFFT_1D df1 = new DoubleFFT_1D(data.length );
            df1.realForward(data);
            System.out.println(Arrays.toString(data));
        }

    }
}