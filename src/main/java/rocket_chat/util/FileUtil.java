package rocket_chat.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {

    public static void writeFile(String path, String fileName, String content) {
        try {
            File createPath = new File(path);
            File createFile = new File(createPath, fileName);
            if (!createFile.exists()) {
                createPath.mkdirs();
                createFile.createNewFile();
            }
            if (isFilled(path, fileName)) {
                removeFirsLine(path, fileName);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(createFile, true))) {
                writer.write(content);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readFile(String path, String fileName) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path, fileName)))) {
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static List<String> getAllFilesName(String path) {
        List<String> files = new ArrayList<>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            for (File f : list) {
                files.add(f.getName().replaceFirst(".txt", ""));
            }
        }
        return files;
    }

    private static boolean isFilled(String path, String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path, fileName)))) {
            return reader.lines().count() >= 100;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void removeFirsLine(String path, String fileName) {
        File file = new File(path, fileName);
        List<String> list = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            boolean firstLine = true;
            while (reader.ready()) {
                if (firstLine) {
                    reader.readLine();
                    firstLine = false;
                    continue;
                }
                list.add(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.delete();
        list.forEach(line -> writeFile(path, fileName, line.concat("\n")));
    }
}
