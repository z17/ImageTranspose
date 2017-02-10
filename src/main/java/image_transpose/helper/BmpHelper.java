package image_transpose.helper;

import image_transpose.Pixel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class BmpHelper {

    public static List<Pixel[][]> readFiles(List<String> names) {
        List<Pixel[][]> result = new ArrayList<>();
        for (String name : names) {
            result.add(readFile(name));
        }
        return result;
    }

    public static Pixel[][] readFile(final String name) {
        try {
            BufferedImage image = ImageIO.read(new File(name));
            int[] nullArray = null;
            final Pixel[][] result = new Pixel[image.getHeight()][image.getWidth()];

            int[] pixels = image.getData().getPixels(0, 0, image.getWidth(), image.getHeight(), nullArray);

            for (int i = 0; i < pixels.length; i = i + 3) {
                int positionNumber = i / 3;
                int row = positionNumber / image.getWidth();
                int col = positionNumber % image.getWidth();
                result[row][col] = new Pixel(pixels[i], pixels[i + 1], pixels[i + 2]);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Integer[][] readFileColor(final String name, final Color color) {
        Pixel[][] pixels = readFile(name);
        int cols = cols(pixels);
        int rows = rows(pixels);
        Integer[][] colors = new Integer[rows][cols];

        for (int c = 0; c < cols; c ++) {
            for (int r = 0; r < cols; r++) {
                colors[c][r] = color.getColor.apply(pixels[c][r]);
            }
        }
        return colors;
    }

    public static Integer[][] readFileRed(final String name) {
        return readFileColor(name, Color.R);
    }

    public static Integer[][] readFileBlue(final String name) {
        return readFileColor(name, Color.B);
    }

    public static Integer[][] readFileGreen(final String name) {
        return readFileColor(name, Color.G);
    }

    public static void writeFile(final String name, final Pixel[][] pixels) {
        int[] preparedArray = new int[pixels.length * pixels[0].length * 3];

        int index = 0;
        for (Pixel[] pLine : pixels) {
            for (Pixel p : pLine) {
                preparedArray[index] = p.getR();
                index++;
                preparedArray[index] = p.getG();
                index++;
                preparedArray[index] = p.getB();
                index++;
            }
        }

        BufferedImage img = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_3BYTE_BGR);
        img.getRaster().setPixels(0, 0, pixels[0].length, pixels.length, preparedArray);

        try {
            ImageIO.write(img, "BMP", new File(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeFiles(final List<Pixel[][]> images, final String folder, final String prefix) {
        try {
            Files.createDirectories(Paths.get(folder));
            int i = 0;
            for (Pixel[][] image : images) {
                writeFile(folder + "/" + prefix + "_" + i + ".bmp", image);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private enum Color {
        R(Pixel::getR),
        G(Pixel::getG),
        B(Pixel::getB);

        public Function<Pixel, Integer> getColor;
        Color(Function<Pixel, Integer> supplier) {
            this.getColor = supplier;
        }
    }
}
