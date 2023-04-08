package net.laboulangerie.laboulangeriemmo.utils;

import java.io.*;

public class FileUtils {

    public static String read(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void save(File file, String content) {
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) parentFile.mkdir();

        try {
            if (!file.exists()) file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
