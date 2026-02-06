package ghost.ognotepad.frontend;

import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;

public class GUI {

    private final Stage primaryStage;
    private final int width = 1000;
    private final int height = 800;

    public GUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void createWindow() {
        primaryStage.setTitle("OGNotepad");

        final GridPane grid = createGrid();
        
        final Scene scene = new Scene(grid, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();       
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        return grid;
    }
}
