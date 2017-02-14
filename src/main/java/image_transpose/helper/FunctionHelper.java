package image_transpose.helper;

import org.apache.commons.math3.complex.Complex;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FunctionHelper {

    public static List<String> getAllImages(final String folderName) {
        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            throw new RuntimeException("not a folder");
        }

        List<String> names = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".bmp")) {
                names.add(file.getPath());
            }
        }
        return names;
    }

    public static List<Double> Re(Complex[] sig) {
        List<Double> result = new ArrayList<>(sig.length);
        for (Complex s : sig) {
            result.add(s.getReal());
        }
        return result;
    }

    public static List<Double> Im(Complex[] sig) {
        List<Double> result = new ArrayList<>(sig.length);
        for (Complex s : sig) {
            result.add(s.getImaginary());
        }
        return result;
    }

    public static Double mean(List<Double> array) {
        return array.stream().reduce(0D, (a, b) -> a + b) / array.size();
    }

    public static Double[][] convertToDouble(Integer[][] array) {
        int cols = cols(array);
        int rows = rows(array);

        Double[][] res = new Double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res[i][j] = array[i][j].doubleValue();
            }
        }
        return res;
    }

    public static <T> int cols(T[][] matrix) {
        long count = Arrays.stream(matrix).map(array -> array.length).distinct().count();
        if (count > 1) {
            throw new IllegalArgumentException("Not a matrix");
        }
        return matrix[0].length;
    }

    public static <T> int rows(T[][] matrix) {
        return matrix.length;
    }
}
