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
                "data/outpilot1_/",
                "data/my_result/",
                140,
                21,
                60,
                0.07,
                0.02,
                0.003
        );
    }

    public void getOgibs(
            String dirt,
            String outFolder,
            int num,
            int cntr_sig,
            int cntr_og,
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
        double[][] r1 = FunctionHelper.copyMatrix(r);
        double[][] g1 = FunctionHelper.copyMatrix(r);
        double[][] b1 = FunctionHelper.copyMatrix(r);
        double[][] r2 = FunctionHelper.copyMatrix(r);
        double[][] g2 = FunctionHelper.copyMatrix(r);
        double[][] b2 = FunctionHelper.copyMatrix(r);

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

            List<double[]> temp1 = get_og3_fst(r0,  rrk, rrkb, rrkb2);
            double[] r32 = temp1.get(0);
            double[] r3 = temp1.get(1);
            List<double[]> temp2 = get_og3_fst(g0, rrk, rrkb, rrkb2);
            double[] g32 = temp2.get(0);
            double[] g3 = temp2.get(1);
            List<double[]> temp3 = get_og3_fst(b0, rrk, rrkb, rrkb2);
            double[] b32 = temp3.get(0);
            double[] b3 = temp3.get(1);

            for (int yn = 0; yn < YN; yn++) {
                double res = 127 + r32[yn] * cntr_sig;
                if (res < 0) {
                    res = 0;
                }
                if (res > 255) {
                    res = 255;
                }

                r1[yn][x] = res;

                res = 127 + g32[yn] * cntr_sig;
                if (res < 0) {
                    res = 0;
                }
                if (res > 255) {
                    res = 255;
                }

                g1[yn][x] = res;

                res = 127 + b32[yn] * cntr_sig;
                if (res < 0) {
                    res = 0;
                }
                if (res > 255) {
                    res = 255;
                }

                res = 127 + r3[yn] * cntr_sig;
                if (res < 0) {
                    res = 0;
                }
                if (res > 255) {
                    res = 255;
                }

                r2[yn][x] = res;

                res = 127 + g3[yn] * cntr_sig;
                if (res < 0) {
                    res = 0;
                }
                if (res > 255) {
                    res = 255;
                }

                g2[yn][x] = res;

                res = 127 + b3[yn] * cntr_sig;
                if (res < 0) {
                    res = 0;
                }
                if (res > 255) {
                    res = 255;
                }

                b2[yn][x] = res;
            }
        }

        Pixel[][] rgb1 = BmpHelper.convertToPixels(r1, g1, b1);
        String name1 = outFolder + num + "___test1.bmp";
        System.out.println("saving " + name1);
        BmpHelper.writeBmp(name1, rgb1);

        Pixel[][] rgb2 = BmpHelper.convertToPixels(r2, g2, b2);
        String name2 = outFolder + num + "___test2.bmp";
        System.out.println("saving " + name2);
        BmpHelper.writeBmp(name2, rgb2);

        System.err.println("Complete!");
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


    private List<double[]> get_og3_fst(double[] s1Temp, double rrk, double rrkb, double rrkb2) {
        int N = s1Temp.length;
        double[] s1 = sig_cfft_rec2(s1Temp, rrk);
        double[] sm1 = sig_cfft_rec2(s1Temp, rrkb);

        double[] s2 = new double[N];
        for (int n = 0; n <= N - 1; n++) {
            s2[n] = 1.55 * Math.abs(s1[n] - sm1[n]);
        }

        double[] s32 = sig_cfft_rec2(s2, rrkb2);

        double[] s3 = MathHelper.minus(s1, sm1);

        return Arrays.asList(s3, s32);
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
//        FunctionHelper.writeDoublesList("test1_input.txt", s1Temp);
        double[] s = MathHelper.cfft(s1Temp);
//        FunctionHelper.writeDoublesList("test1_cfft.txt", s);
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
//        FunctionHelper.writeDoublesList("test1_cfft_rrk.txt", s);
        double[] s1 = MathHelper.icfft(s);

//        FunctionHelper.writeDoublesList("test1_icfft_rrk.txt", s);
        double[] sRes = new double[K];
        for (int k = 0; k <= K - 1; k++) {
            double res = Math.abs(s1[k]);
            sRes[k] = res;
        }

        return sRes;
    }
}
