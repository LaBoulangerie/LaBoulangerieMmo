package fr.laboulangerie.laboulangeriemmo.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {



    public static void save(String name, String content) {
        File file = new File(name);
        File parentFile = file.getParentFile();
        if (!parentFile.exists())parentFile.mkdir();

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
