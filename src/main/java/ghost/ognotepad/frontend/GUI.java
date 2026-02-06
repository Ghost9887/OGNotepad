package ghost.ognotepad.frontend;

import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.beans.value.ObservableValue;

import java.io.File;
import java.util.Optional;

import ghost.ognotepad.backend.*;

public class GUI {

    private final Stage primaryStage;
    private final int defaultWidth = 1000;
    private final int defaultHeight = 800;
    private final int fontSize = 16;
    private int row = 0;
    private int column = 0;
    private int count = 0;
    private int size = 12;

    private final TextArea area = createTextArea();

    private final Label rowLabel = new Label("Row: 1    ");
    private final Label columnLabel = new Label("Label: 0    ");
    private final Label countLabel = new Label("Count: 0    ");
    private final Label sizeLabel = new Label("Size: 12");

    private final Label fileLabel = new Label("");
    private String originalContent = "";

    public GUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void createWindow() {
        primaryStage.setTitle("OGNotepad");

        VBox root = new VBox();
        VBox topBar = createTopBar();
        HBox bottomBar = createBottomBar();
        VBox.setVgrow(area, Priority.ALWAYS);
        root.getChildren().addAll(topBar, fileLabel, area, bottomBar);
        Scene scene = new Scene(root, defaultWidth, defaultHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createBottomBar() {
        HBox box = new HBox();
        box.setPadding(new Insets(5, 0, 0, 10));
        box.setPrefWidth(defaultWidth);

        Label encoding = new Label("    UTF-8   ");
        box.getChildren().addAll(rowLabel, columnLabel, countLabel, encoding, sizeLabel);

        return box;
    }

    private TextArea createTextArea() {
        TextArea area = new TextArea();
        area.setPrefWidth(defaultWidth);
        area.setPadding(new Insets(0, 10, 5, 0));
        area.setStyle(
            "-fx-focus-color: -fx-control-inner-background ; -fx-faint-focus-color: -fx-control-inner-background ;"
        );
        area.setFont(Font.font("Consolas", FontWeight.NORMAL, fontSize));
        area.caretPositionProperty().addListener(this::updateBottomBar);
        return area;
    }

    private VBox createTopBar() {
        MenuBar parent = new MenuBar();
        Menu file = new Menu("File");

        MenuItem newFile = new MenuItem("New File   ctrl+n");
        newFile.setOnAction(event -> {
            newFile();   
        });
        MenuItem newWindow = new MenuItem("New Window   ctrl+shift+n");
        MenuItem save = new MenuItem("Save  ctrl+s");
        save.setOnAction(event -> {
            saveFile();   
        });
        MenuItem open = new MenuItem("Open  ctrl+o");
        open.setOnAction(event -> {
            loadFile();   
        });
        file.getItems().addAll(newFile, newWindow, save, open);

        Menu edit = new Menu("Edit");
        Menu view = new Menu("View");
        Menu help = new Menu("Help");

        parent.getMenus().addAll(file, edit, view, help);

        VBox box = new VBox();
        box.setSpacing(10);
        box.setPadding(new Insets(0, 5, 5, 0));
        box.setPrefWidth(defaultWidth);
        box.getChildren().add(parent);
        return box;
    }


    //ACTIONS
    private void updateBottomBar(ObservableValue<? extends Number> obs, Number oldPos, Number newPos) {
        int caret = newPos.intValue();
        String text = area.getText();

        row = text.substring(0, caret).split("\n", -1).length;
        int lastNewline = text.lastIndexOf('\n', caret - 1);
        column = (lastNewline == -1) ? caret : caret - lastNewline - 1;

        rowLabel.setText("Row: " + row + "    ");
        columnLabel.setText("Column: " + column + "    ");
        countLabel.setText("Count: " + text.length());
    }

    private void newFile() {
        if (!checkChange()) {
            fileLabel.setText("");
            area.setText("");
        }else {
            Alert a = new Alert(AlertType.WARNING);           
            a.setTitle("Unsaved changes");
            a.setHeaderText("Your file has unsaved changes");
            a.setContentText("Do you want to save?");
            ButtonType cancel = new ButtonType("Don't save");
            ButtonType save = new ButtonType("Save");

            a.getButtonTypes().setAll(save, cancel);
            Optional<ButtonType> result = a.showAndWait();
            if (result.isPresent()) {
                //TODO: CHECK IF THE FILE HAS ACTUALLY BEEN SAVED
                if (result.get() == save) {
                    saveFile();
                }
                fileLabel.setText("");
                area.setText("");
            }
        }
    }

    private boolean checkChange() {
        if (originalContent.equals(area.getText())) {
            return false;
        }
        return true;
    }

    private void saveFile() {
        if (fileLabel.getText().length() < 1) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save File");
            File selectedFile = chooser.showSaveDialog(primaryStage);

            if (selectedFile != null) {
                StringBuilder fullName = new StringBuilder(); 
                fullName.append(selectedFile.getAbsolutePath());
                if (!selectedFile.getName().contains(".")) {
                    fullName.append(".txt");
                }
                FileLogic.save(area.getText(), fullName.toString());
                fileLabel.setText(fullName.toString());
            }
        } else {
            FileLogic.save(area.getText(), fileLabel.getText());
        }
    }

    private void loadFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save File");
        File selectedFile = chooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getAbsolutePath());
            area.setText(FileLogic.load(fileLabel.getText()));
            originalContent = area.getText();
        }
    }
}
