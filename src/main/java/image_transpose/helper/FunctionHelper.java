package image_transpose.helper;

import org.apache.commons.math3.complex.Complex;
import sun.plugin.javascript.navig.Array;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
        return array.stream().reduce(0D, (a, b) -> a+b ) / array.size();
    }
}
