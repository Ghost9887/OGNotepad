package ghost.chess.gui;

import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
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
        primaryStage.setTitle("Hello World!");

        final GridPane grid = createGrid();

        Text sceneTitle = new Text("Welcome");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userName = new Label("username:");
        grid.add(userName, 0, 1);
        
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                actionTarget.setFill(Color.FIREBRICK);
                actionTarget.setText("Sign in button pressed");
            }
        });

        final Scene scene = new Scene(grid, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
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
