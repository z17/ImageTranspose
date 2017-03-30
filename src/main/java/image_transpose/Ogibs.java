package image_transpose;

import image_transpose.helper.BmpHelper;
import image_transpose.helper.FunctionHelper;
import image_transpose.helper.MathHelper;
import org.apache.commons.math3.complex.Complex;

import java.util.Arrays;
import java.util.List;

public class Ogibs {

    private static List<Double> G1;

    private static void g1(int mean, int sigma) {
//        Gaussian gaussian = new Gaussian(1, mean / 2, sigma);
//        int scale2 = mean / 2;
//        G1 = new double[scale2];
//        G1[scale2 - 1] = 0;
//        for (int n = 0; n < scale2; n++) {
//            G1[n] = gaussian.value(n + scale2);
//        }

        G1 = FunctionHelper.readDoublesList("g1_file.txt");

    }

    public static void main(String[] args) {
        g1(200, 4);

        Ogibs ogibs = new Ogibs();

        ogibs.getOgibs(
                "tmp/outpilot1_/",
                "tmp/my_result/",
                140,
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
            String outFolder,
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
        FunctionHelper.checkOutputFolders(outFolder);
        String name = dirt + num + ".bmp";
        System.err.println("reading " + name);
        double[][] r = FunctionHelper.convertToDouble(BmpHelper.readFileRed(name));
        double[][] g = FunctionHelper.convertToDouble(BmpHelper.readFileGreen(name));
        double[][] b = FunctionHelper.convertToDouble(BmpHelper.readFileBlue(name));
        double[][] r1 = r.clone();
        double[][] g1 = r.clone();
        double[][] b1 = r.clone();
        double[][] r1b = r.clone();
        double[][] g1b = r.clone();
        double[][] b1b = r.clone();
        double[][] r1ph = r.clone();
        double[][] g1ph = r.clone();
        double[][] b1ph = r.clone();

        int X = FunctionHelper.cols(r);
        int YN = FunctionHelper.rows(r);
        for (int x = 0; x <= X - 1; x++) {
            if (x % 20 == 0) {
                System.out.println("num = " + num + ", x = " + x + "/" + X);
            }

            double[] r0 = new double[YN];
            double[] g0 = new double[YN];
            double[] b0 = new double[YN];
            for (int yn = 0; yn < YN; yn++) {
                r0[yn] = r[yn][x];
                g0[yn] = g[yn][x];
                b0[yn] = b[yn][x];
            }

            List<double[]> temp1 = get_og2(r0, mean1, mean2, dm1, dm2, rrk, rrkb, rrkb2);
            double[] r32 = temp1.get(0);
            double[] r32b = temp1.get(1);
            double[] r32ph = temp1.get(2);
            List<double[]> temp2 = get_og2(g0, mean1, mean2, dm1, dm2, rrk, rrkb, rrkb2);
            double[] g32 = temp2.get(0);
            double[] g32b = temp2.get(1);
            double[] g32ph = temp2.get(2);
            List<double[]> temp3 = get_og2(b0, mean1, mean2, dm1, dm2, rrk, rrkb, rrkb2);
            double[] b32 = temp3.get(0);
            double[] b32b = temp3.get(1);
            double[] b32ph = temp3.get(2);

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
        String name1 = outFolder + num + "___test1.bmp";
        System.out.println("saving " + name1);
//        BmpHelper.writeBmp(name1, BmpHelper.normalizeBmp(r1));
        BmpHelper.writeBmp(name1, rgb1);

        Pixel[][] rgb2 = BmpHelper.convertToPixels(r1b, g1b, b1b);
        String name2 = outFolder + num + "___test2.bmp";
        System.out.println("saving " + name2);
//        BmpHelper.writeBmp(name2, BmpHelper.normalizeBmp(r1b));
        BmpHelper.writeBmp(name2, rgb2);

        Pixel[][] rgb3 = BmpHelper.convertToPixels(r1ph, g1ph, b1ph);
        String name3 = outFolder + num + "___test3.bmp";
        System.out.println("saving " + name3);
//        BmpHelper.writeBmp(name3, BmpHelper.normalizeBmp(r1ph));
        BmpHelper.writeBmp(name3, rgb3);
    }

    private List<double[]> get_og2(double[] s1Temp, int mean1, int mean2, int dm1, int dm2, double rrk, double rrkb, double rrkb2) {
        int N = s1Temp.length;
        double[] s1 = sig_cfft_rec2(s1Temp, rrk);
        double[] sm1 = sig_cfft_rec2(s1Temp, rrkb);

        double[] s2 = new double[N];
        for (int n = 0; n <= N - 1; n++) {
            s2[n] = 1.55 * Math.abs(s1[n] - sm1[n]);
        }

        double[] s32 = sig_cfft_rec2(s2, rrkb2);

        double[] s3 = MathHelper.minus(s1, sm1);

        double[] ph = new double[s3.length];
        double[] phase = get_phase(s3);
        for (int i = 0; i < s3.length; i++) {
            ph[i] = (phase[i] + Math.PI) / 2 * Math.PI * 255;
        }
        return Arrays.asList(s32, sm1, ph);

    }

    private double[] get_phase(double[] array) {
        int N = array.length;
        Complex[] S = MathHelper.cfftComplex(array);
        for (int i = N / 2; i <= N - 1; i++) {
            S[i] = new Complex(0);
        }
        Complex[] sig = MathHelper.icfftComplex(S);
        List<Double> sig_sin = FunctionHelper.Re(sig);
        List<Double> sig_cos = FunctionHelper.Im(sig);

        Double B = FunctionHelper.mean(sig_sin);

        double[] f = new double[N];
        for (int j = 0; j < N; j++) {
            f[j] = Math.atan2(sig_sin.get(j) - B, sig_cos.get(j) - B);
        }
        return f;
    }

    private double[] sig_cfft_rec2(double[] s1Temp, double rrk) {
        FunctionHelper.writeDoublesList("test1_input.txt", s1Temp);
        double[] s = MathHelper.cfft(s1Temp);
        FunctionHelper.writeDoublesList("test1_cfft.txt", s);
        int K = s1Temp.length;
        int rr = (int) Math.round(K * rrk);
        for (int k = rr; k < K - rr; k++) {
            s[k] = 0;
        }
        for (int k = 0; k < rr; k++) {
            double gr = G1.get(k * 100 / rr);
            s[k] = s[k] * gr;
//            s[K - 1 - k] = s[K - 1 - k] * gr;
        }
        FunctionHelper.writeDoublesList("test1_cfft_rrk.txt", s);
        double[] s1 = MathHelper.icfft(s);

        FunctionHelper.writeDoublesList("test1_icfft_rrk.txt", s);
        System.exit(1);
        double[] sRes = new double[K];
        for (int k = 0; k <= K - 1; k++) {
            double res = Math.abs(s1[k]);
            sRes[k] = res;
        }

        return sRes;
    }
}
