package ghost.ognotepad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ghost.ognotepad.frontend.Code;
import ghost.ognotepad.backend.*;

public class AppTest {
    
    @Test
    public void savesFileCorrectly() throws IOException {
        Path temp = Files.createTempFile("note", ".txt");
        Code code = FileLogic.save("Hello", temp.toString());

        assertEquals(new Code.Success(null), code);
    }

    @Test
    public void loadFile() throws IOException {
        Path temp = Files.createTempFile("note2", ".txt");
        FileLogic.save("Hello", temp.toString());
        Code code = FileLogic.load(temp.toString());

        assertEquals(new Code.Success("Hello"), code);
    }
}
