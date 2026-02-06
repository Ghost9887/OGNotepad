package ghost.ognotepad.backend;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileOutputStream;
import java.io.IOException;

import ghost.ognotepad.frontend.Code;

public class FileLogic {
    
    public static Code save(String content, String path) {
        byte[] bytes = content.getBytes();
        try (FileOutputStream stream = new FileOutputStream(path)) {
            stream.write(bytes);
            return new Code.Success(null);
        }catch (IOException e) {
            return new Code.Error(e.toString());
        }
    }

    public static Code load(String path) {
        try {
            String content = Files.readString(Path.of(path));
            return new Code.Success(content);
        }catch (IOException e){
            return new Code.Error(e.toString());
        }
    }

}
