package image_transpose.helper;

import image_transpose.Pixel;

import java.util.ArrayList;
import java.util.List;

public final class TransposeHelper {
    public static List<Pixel[][]> transpose(List<Pixel[][]> images) {
        List<Pixel[][]> result = new ArrayList<>();
        Pixel[][] first = images.get(0);
        for (Pixel[] ignored : first) {
            result.add(new Pixel[images.size()][first[0].length]);
        }

        for (int fileNumber = 0; fileNumber < images.size(); fileNumber++) {
            Pixel[][] image = images.get(fileNumber);
            for (int lineNumber = 0; lineNumber < image.length; lineNumber++) {
                Pixel[] line = image[lineNumber];
                Pixel[][] pixels = result.get(lineNumber);
                pixels[fileNumber] = line;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        final String folderName = "data/outPilot1";

        List<String> names = FunctionHelper.getAllImages(folderName);

        names = names.subList(0, 100);
        List<Pixel[][]> pixels = BmpHelper.readFiles(names);
        List<Pixel[][]> transpose = TransposeHelper.transpose(pixels);
        transpose = TransposeHelper.transpose(transpose);
        BmpHelper.writeFiles(transpose, "out", "result");
    }
}
