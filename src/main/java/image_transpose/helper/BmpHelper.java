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
import java.util.OptionalDouble;
import java.util.function.Function;

public class BmpHelper {

    public static List<Pixel[][]> readFiles(List<String> names) {
        List<Pixel[][]> result = new ArrayList<>();
        for (String name : names) {
            result.add(readFile(name));
        }
        return result;
    }

    @SuppressWarnings("ConstantConditions")
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
        int cols = FunctionHelper.cols(pixels);
        int rows = FunctionHelper.rows(pixels);
        Integer[][] colors = new Integer[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                colors[i][j] = color.getColor.apply(pixels[i][j]);
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

    public static void writeBmp(final String name, final int[][] matrix) {

        int[] preparedArray = new int[matrix.length * matrix[0].length];

        int index = 0;
        for (int[] aMatrix : matrix) {
            for (int value : aMatrix) {
                preparedArray[index] = value;
                index++;
            }
        }

        BufferedImage img = new BufferedImage(matrix[0].length, matrix.length, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setPixels(0, 0, matrix[0].length, matrix.length, preparedArray);

        try {
            ImageIO.write(img, "BMP", new File(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static double[][] normalizeBmp(double[][] matrix) {
        double min = matrix[0][0];
        double max = matrix[0][0];
        for (double[] aMatrix : matrix) {
            OptionalDouble tMin = Arrays.stream(aMatrix).min();
            OptionalDouble tMax = Arrays.stream(aMatrix).max();
            if (!tMin.isPresent() || !tMax.isPresent()) {
                throw new RuntimeException("Error min/max value finding");
            }
            if (tMin.getAsDouble() < min) {
                min = tMin.getAsDouble();
            }
            if (tMax.getAsDouble() < max) {
                max = tMax.getAsDouble();
            }
        }
        System.err.println(max);
        System.err.println(min);
        double[][] result = new double[matrix.length][matrix[0].length];
        double coefficient = ((double) max - min) / 255;
        for (int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[0].length; j++) {
                result[i][j] = (int) Math.round((matrix[i][j] - min) / coefficient);
            }
        }
        return result;
    }

    public static double[][] copyMatrix(final double[][] matrix) {
        double[][] result = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return result;
    }

    public static void writeBmp(final String name, final double[][] matrix) {

        double[] preparedArray = new double[matrix.length * matrix[0].length];

        int index = 0;
        for (double[] aMatrix : matrix) {
            for (Double value : aMatrix) {
                preparedArray[index] = value;
                index++;
            }
        }

        BufferedImage img = new BufferedImage(matrix[0].length, matrix.length, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setPixels(0, 0, matrix[0].length, matrix.length, preparedArray);

        try {
            ImageIO.write(img, "BMP", new File(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public static Pixel[][] convertToPixels(double[][] r, double[][] g, double[][] b) {
        int cols = FunctionHelper.cols(r);
        int rows = FunctionHelper.rows(r);


        Pixel[][] res = new Pixel[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res[i][j] = new Pixel(
                        (int) Math.round(r[i][j]),
                        (int) Math.round(g[i][j]),
                        (int) Math.round(b[i][j])
                );
            }
        }
        return res;
    }
}
