package image_transpose;

import image_transpose.helper.BmpHelper;
import image_transpose.helper.FunctionHelper;
import image_transpose.helper.MathHelper;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.analysis.integration.gauss.GaussIntegrator;
import org.apache.commons.math3.complex.Complex;

import java.util.Arrays;
import java.util.List;

public class Ogibs {

    private static void g1(int scale, int shift) {
        int mean = 5;
        Gaussian gaussian = new Gaussian(mean, 0.7);
        for (int i = 0; i <= mean * 2; i++) {
            System.out.println(i + " = " + gaussian.value(i));

        }
    }

    public static void main(String[] args) {
        g1(200, 4);
    }

    public void getOgibs(
            String dirt,
            String dir2,
            String dirt2b,
            String dirt2bh,
            int num,
            int mean1,
            int mean2,
            int dm1,
            int dm2,
            int ctrn,
            int rrk,
            int rrkb,
            int rrkb2
    ) {
        String name = dirt + num + ".bmp";
        System.err.println("reading " + name);
        Integer[][] r = BmpHelper.readFileRed(name);
        Integer[][] g = BmpHelper.readFileGreen(name);
        Integer[][] b = BmpHelper.readFileBlue(name);
        Integer[][] r1 = r.clone();
        Integer[][] g1 = r.clone();
        Integer[][] b1 = r.clone();
        Integer[][] r1b = r.clone();
        Integer[][] g1b = r.clone();
        Integer[][] b1b = r.clone();
        Integer[][] r1bh = r.clone();
        Integer[][] g1bh = r.clone();
        Integer[][] b1bh = r.clone();

        int X = BmpHelper.cols(r);
        int YN = BmpHelper.rows(r);
        for (int x = 0; x <= X - 1; x++) {
            if (x % 20 == 0) {
                System.out.println("num = " + num + ", x = " + x + "/" + X);
            }

            Double[] r0 = new Double[YN];
            Double[] g0 = new Double[YN];
            Double[] b0 = new Double[YN];
            for (int yn = 0; yn < YN; yn++) {
                r0[yn] = Double.valueOf(r[yn][x]);
                g0[yn] = Double.valueOf(g[yn][x]);
                b0[yn] = Double.valueOf(b[yn][x]);
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

            }
        }

    }

    private List<Double[]> get_og2(Double[] s1Temp, int mean1, int mean2, int dm1, int dm2, int rrk, int rrkb, int rrkb2) {
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
        Complex[] S = MathHelper.ccft(array);
        for (int i = N / 2; i <= N - 1; i++) {
            S[i] = new Complex(0);
        }
        Complex[] sig = MathHelper.iccft(S);
        List<Double> sig_sin = FunctionHelper.Re(sig);
        List<Double> sig_cos = FunctionHelper.Im(sig);

        Double B = FunctionHelper.mean(sig_sin);

        double[] f = new double[N];
        for (int j = 0; j < N; j++) {
            f[j] = Math.atan2(sig_sin.get(j) - B, sig_cos.get(j) - B);
        }
        return f;
    }

    private Double[] sig_cfft_rec2(Double[] s1Temp, int rrk) {
        Complex[] s = MathHelper.ccft(s1Temp);
        int K = s1Temp.length;
        int rr = Math.round(K * rrk);
        for (int k = rr; k <= K - 1 - rr; k++) {
            s[k] = new Complex(0);
        }
        for (int k = 0; k <= rr - 1; k++) {
            int gr = 1;
            s[k] = s[k].multiply(gr);
            s[K - 1 - k] = s[K - 1 - k].multiply(gr);
//            gr = G1[(k*100/rr)];
        }
        Complex[] s1 = MathHelper.iccft(s1Temp);

        Double[] sRes = new Double[K];
        for (int k = 0; k <= K - 1; k++) {
            double res = s1[k].abs();
            sRes[k] = res;
        }

        return sRes;
    }
}
