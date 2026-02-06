package ghost.ognotepad.backend;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileLogic {
    
    public static void save(String content, String path) {
        byte[] bytes = content.getBytes();
        try (FileOutputStream stream = new FileOutputStream(path)) {
            stream.write(bytes);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String load(String path) {
        try {
            String content = Files.readString(Path.of(path));
            return content;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
