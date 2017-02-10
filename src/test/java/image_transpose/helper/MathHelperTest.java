package image_transpose.helper;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MathHelperTest {
    @Test
    public void ccft() throws Exception {
        double[] a = new double[]{1.0, 2.0, 3.0, 4.0};

        MathHelper.ccft(a);
    }

}