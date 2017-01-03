package image_transpose.helper;

import image_transpose.Pixel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
}
