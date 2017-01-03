package image_transpose.helper;

import java.io.File;
import java.util.ArrayList;
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
}
