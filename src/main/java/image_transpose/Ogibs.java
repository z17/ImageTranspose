package image_transpose;

import image_transpose.helper.BmpHelper;
import image_transpose.helper.FunctionHelper;
import image_transpose.helper.MathHelper;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.complex.Complex;

import java.util.Arrays;
import java.util.List;

public class Ogibs {

    private static double[] G1;

    private static void g1(int mean, int sigma) {
        Gaussian gaussian = new Gaussian(1, mean / 2, sigma);
        int scale2 = mean / 2;
        G1 = new double[scale2];
        G1[scale2 - 1] = 0;
        for (int n = 0; n < scale2; n++) {
            G1[n] = gaussian.value(n + scale2);
        }
    }

    public static void main(String[] args) {
        g1(200, 4);

        Ogibs ogibs = new Ogibs();

        ogibs.getOgibs(
                "tmp/outpilot1_/",
                "tmp/outpilot1__/",
                "tmp/outpilot1__b/",
                "tmp/outpilot1__ph/",
                0,
                14,
                64,
                2,
                4,
                60,
                0.2,
                0.015,
                0.005
        );
    }

    public void getOgibs(
            String dirt,
            String dirt2,
            String dirt2b,
            String dirt2ph,
            int num,
            int mean1,
            int mean2,
            int dm1,
            int dm2,
            int ctrn,
            double rrk,
            double rrkb,
            double rrkb2
    ) {
        String name = dirt + num + ".bmp";
        System.err.println("reading " + name);
        Double[][] r = FunctionHelper.convertToDouble(BmpHelper.readFileRed(name));
        Double[][] g = FunctionHelper.convertToDouble(BmpHelper.readFileGreen(name));
        Double[][] b = FunctionHelper.convertToDouble(BmpHelper.readFileBlue(name));
        Double[][] r1 = r.clone();
        Double[][] g1 = r.clone();
        Double[][] b1 = r.clone();
        Double[][] r1b = r.clone();
        Double[][] g1b = r.clone();
        Double[][] b1b = r.clone();
        Double[][] r1ph = r.clone();
        Double[][] g1ph = r.clone();
        Double[][] b1ph = r.clone();

        int X = FunctionHelper.cols(r);
        int YN = FunctionHelper.rows(r);
        for (int x = 0; x <= X - 1; x++) {
            if (x % 20 == 0) {
                System.out.println("num = " + num + ", x = " + x + "/" + X);
            }

            Double[] r0 = new Double[YN];
            Double[] g0 = new Double[YN];
            Double[] b0 = new Double[YN];
            for (int yn = 0; yn < YN; yn++) {
                r0[yn] = r[yn][x];
                g0[yn] = g[yn][x];
                b0[yn] = b[yn][x];
            }

            List<Double[]> temp1 = get_og2(r0, mean1, mean2, dm1, dm2, rrk, rrkb, rrkb2);
            Double[] r32 = temp1.get(0);
            Double[] r32b = temp1.get(1);
            Double[] r32ph = temp1.get(2);
            List<Double[]> temp2 = get_og2(g0, mean1, mean2, dm1, dm2, rrk, rrkb, rrkb2);
            Double[] g32 = temp2.get(0);
            Double[] g32b = temp2.get(1);
            Double[] g32ph = temp2.get(2);
            List<Double[]> temp3 = get_og2(b0, mean1, mean2, dm1, dm2, rrk, rrkb, rrkb2);
            Double[] b32 = temp3.get(0);
            Double[] b32b = temp3.get(1);
            Double[] b32ph = temp3.get(2);

            for (int yn = 0; yn < YN; yn++) {
                double res = r32[yn] * ctrn;
                if (res < 0) {
                    res = 0;
                }
                if (res > 255) {
                    res = 255;
                }

                r1[yn][x] = res;

                res = g32[yn] * ctrn;
                if (res < 0) {
                    res = 0;
                }
                if (res > 255) {
                    res = 255;
                }

                g1[yn][x] = res;

                res = b32[yn] * ctrn;
                if (res < 0) {
                    res = 0;
                }
                if (res > 255) {
                    res = 255;
                }

                b1[yn][x] = res;

                r1b[yn][x] = r32b[yn];
                g1b[yn][x] = g32b[yn];
                b1b[yn][x] = b32b[yn];
                r1ph[yn][x] = r32ph[yn];
                g1ph[yn][x] = g32ph[yn];
                b1ph[yn][x] = b32ph[yn];
            }
        }

        Pixel[][] rgb1 = BmpHelper.convertToPixels(r1, g1, b1);
        String name1 = dirt2 + num + "___test.bmp";
        System.out.println("saving " + name1);
        BmpHelper.writeFile(name1, rgb1);

        Pixel[][] rgb2 = BmpHelper.convertToPixels(r1b, g1b, b1b);
        String name2 = dirt2b + num + "___test.bmp";
        System.out.println("saving " + name2);
        BmpHelper.writeFile(name2, rgb2);

        Pixel[][] rgb3 = BmpHelper.convertToPixels(r1ph, g1ph, b1ph);
        String name3 = dirt2ph + num + "___test.bmp";
        System.out.println("saving " + name3);
        BmpHelper.writeFile(name3, rgb3);
    }

    private List<Double[]> get_og2(Double[] s1Temp, int mean1, int mean2, int dm1, int dm2, double rrk, double rrkb, double rrkb2) {
        int N = s1Temp.length;
        Double[] s1 = sig_cfft_rec2(s1Temp, rrk);
        Double[] sm1 = sig_cfft_rec2(s1Temp, rrkb);

        Double[] s2 = new Double[N];
        for (int n = 0; n <= N - 1; n++) {
            s2[n] = 1.55 * Math.abs(s1[n] - sm1[n]);
        }

        Double[] s32 = sig_cfft_rec2(s2, rrkb2);

        Double[] s3 = MathHelper.minus(s1, sm1);

        Double[] ph = new Double[s3.length];
        double[] phase = get_phase(s3);
        for (int i = 0; i < s3.length; i++) {
            ph[i] = (phase[i] + Math.PI) / 2 * Math.PI * 255;
        }
        return Arrays.asList(s32, sm1, ph);

    }

    private double[] get_phase(Double[] array) {
        int N = array.length;
        Complex[] S = MathHelper.cfft(array);
        for (int i = N / 2; i <= N - 1; i++) {
            S[i] = new Complex(0);
        }
        Complex[] sig = MathHelper.icfft(S);
        List<Double> sig_sin = FunctionHelper.Re(sig);
        List<Double> sig_cos = FunctionHelper.Im(sig);

        Double B = FunctionHelper.mean(sig_sin);

        double[] f = new double[N];
        for (int j = 0; j < N; j++) {
            f[j] = Math.atan2(sig_sin.get(j) - B, sig_cos.get(j) - B);
        }
        return f;
    }

    private Double[] sig_cfft_rec2(Double[] s1Temp, double rrk) {
        Complex[] s = MathHelper.cfft(s1Temp);
        int K = s1Temp.length;
        int rr = (int) Math.round(K * rrk);
        for (int k = rr; k < K - rr; k++) {
            s[k] = new Complex(0);
        }
        for (int k = 0; k < rr; k++) {
            double gr = G1[(k*100/rr)];
            s[k] = s[k].multiply(gr);
            s[K - 1 - k] = s[K - 1 - k].multiply(gr);
        }
        Complex[] s1 = MathHelper.icfft(s1Temp);

        Double[] sRes = new Double[K];
        for (int k = 0; k <= K - 1; k++) {
            double res = s1[k].abs();
            sRes[k] = res;
        }

        return sRes;
    }
}
