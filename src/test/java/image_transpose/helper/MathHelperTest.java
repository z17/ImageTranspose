package image_transpose.helper;

import org.junit.Test;

public class MathHelperTest {
    @Test
    public void ccft() throws Exception {
        double[] a = new double[]{1.0, 2.0, 3.0, 4.0};

        MathHelper.cfft(a);
    }

}