package ghost.chess.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class GUI {

    private final Stage primaryStage;
    private final int width = 1000;
    private final int height = 800;

    public GUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void createWindow() {
        primaryStage.setTitle("Hello World!");
        StackPane root = new StackPane();

        root.setStyle("-fx-background-color: coral;");

        Button btn = createButton(); 

        root.getChildren().addAll(btn);

        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();
    }

    private Button createButton() {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        return btn;
    }

}
