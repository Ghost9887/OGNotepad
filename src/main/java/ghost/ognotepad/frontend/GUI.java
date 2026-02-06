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
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCode;
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
        fileLabel.setPadding(new Insets(0, 0, 0, 10));
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
        area.setFont(Font.font("Consolas", FontWeight.NORMAL, size));
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
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        MenuItem newWindow = new MenuItem("New Window   ctrl+shift+n");
        MenuItem save = new MenuItem("Save  ctrl+s");
        save.setOnAction(event -> {
            saveFile();   
        });
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        MenuItem open = new MenuItem("Open  ctrl+o");
        open.setOnAction(event -> {
            loadFile();   
        });
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

        file.getItems().addAll(newFile, newWindow, save, open);
        Menu edit = new Menu("Edit");

        Menu view = new Menu("View");
        MenuItem zoom = new MenuItem("Zoom    ctrl+scrlUp");
        zoom.setOnAction(event -> { 
            zoomIn(); 
        });
        MenuItem unzoom = new MenuItem("Zoom out    ctrl+scrlDown");
        unzoom.setOnAction(event -> {
            zoomOut();
        });
        
        view.getItems().addAll(zoom, unzoom);
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
                if (result.get() == save) {
                    if (saveFile()) {
                        fileLabel.setText("");
                        area.setText("");
                    }
                }else if (result.get() == cancel) {
                    fileLabel.setText("");
                    area.setText("");
                }
            }
        }
    }

    private boolean checkChange() {
        if (originalContent.equals(area.getText())) {
            return false;
        }
        return true;
    }

    private boolean saveFile() {
        StringBuilder fullName = new StringBuilder();

        if (fileLabel.getText().length() < 1) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save File");
            File selectedFile = chooser.showSaveDialog(primaryStage);

            if (selectedFile != null) {
                fullName.append(selectedFile.getAbsolutePath());
                if (!selectedFile.getName().contains(".")) {
                    fullName.append(".txt");
                }
            }else {
                return false;
            }
        } else {
            fullName.append(fileLabel.getText());
        }

        Code code = FileLogic.save(area.getText(), fullName.toString());
        switch (code) {
            case Code.Success(String paylaod) -> {
                fileLabel.setText(fullName.toString());
                originalContent = area.getText();
                return true;
            }
            case Code.Error(String error) -> {
                Alert a = new Alert(AlertType.ERROR);           
                a.setTitle("Failed to save file");
                a.setHeaderText("Could'nt save file: " + fullName);
                a.setContentText("Error: " + error);
                a.show();   
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    private void loadFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File selectedFile = chooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            Code code = FileLogic.load(selectedFile.getAbsolutePath());
            switch (code) {
                case Code.Success(String payload) -> {
                    area.setText(payload);
                    fileLabel.setText(selectedFile.getAbsolutePath());
                    originalContent = area.getText();
                }
                case Code.Error(String error) -> {
                    Alert a = new Alert(AlertType.ERROR);           
                    a.setTitle("Failed to load file");
                    a.setHeaderText("Could'nt load file: " + selectedFile.getAbsolutePath());
                    a.setContentText("Error: " + error);
                    a.show();
                }
                default -> {
                }
            }
        }
    }

    private void zoomIn() {
        if (size < 150) {
            size++;
            sizeLabel.setText("Size: " + size);
            area.setFont(Font.font("Consolas", FontWeight.NORMAL, size));
        }
    }

    private void zoomOut() {
        if (size > 1) {
            size--;
            sizeLabel.setText("Size: " + size);
            area.setFont(Font.font("Consolas", FontWeight.NORMAL, size));
        }
    }
}
