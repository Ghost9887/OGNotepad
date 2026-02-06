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
import javafx.scene.input.ScrollEvent;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.beans.value.ObservableValue;

import java.io.File;
import java.util.Optional;

import ghost.ognotepad.backend.*;


//TODO: FEATURES TO ADD 
/*
 *  1. Overall refactor
 *  2. Fix it so it starts in the middle
 *  3. Add to edit => {Select All, Print Time, Find, Paste, Copy, Cut}
 *  4. Add to view => {Toggle Line break horizontal}
 *  5. Open new Window 
 *  6. Proper undo/redo
 *  7. Settings page (Fonts, DarkMode, LightMode, Default Width, Default Height, Default font size)
 *  8. Dark/light mode switcher
 *  9. Fullscreen (doesn't work)
 *  10. Config file to store settings
 *  11. Print File
 *  ?. New Files open in a new Tab maybe????
 */

public class GUI {

    private final Stage primaryStage;
    private int defaultWidth = 1000;
    private int defaultHeight = 800;
    private int row = 0;
    private int column = 0;
    private int count = 0;
    private int fontSize = 12;

    private TextArea area = createTextArea();

    private Label rowLabel = new Label("Row: 1");
    private Label columnLabel = new Label("Label: 0");
    private Label countLabel = new Label("Count: 0");
    private Label fontSizeLabel = new Label("Font Size: 12");

    private Label fileLabel = new Label("");
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
        //this is for the zooming with scroll wheel
        scene.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                if (event.getDeltaY() > 0) {
                    zoomIn();
                } else if (event.getDeltaY() < 0) {
                    zoomOut();
                }
                event.consume();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createBottomBar() {
        HBox box = new HBox(60);
        box.setPadding(new Insets(5, 0, 0, 10));
        box.setPrefWidth(defaultWidth);
        box.getChildren().addAll(rowLabel, columnLabel, countLabel, fontSizeLabel);
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
        MenuItem newFile = new MenuItem("New File");
        newFile.setOnAction(event -> {
            newFile();   
        });
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        MenuItem newWindow = new MenuItem("New Window");

        MenuItem save = new MenuItem("Save");
        save.setOnAction(event -> {
            saveFile();   
        });
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        MenuItem open = new MenuItem("Open");
        open.setOnAction(event -> {
            loadFile();   
        });
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

        file.getItems().addAll(newFile, newWindow, save, open);

        Menu edit = new Menu("Edit");
        Menu view = new Menu("View");
        MenuItem zoom = new MenuItem("Zoom");
        zoom.setAccelerator(new KeyCodeCombination(KeyCode.PAGE_UP, KeyCombination.CONTROL_DOWN));
        zoom.setOnAction(event -> { 
            zoomIn(); 
        });
        MenuItem unzoom = new MenuItem("Zoom out");
        unzoom.setAccelerator(new KeyCodeCombination(KeyCode.PAGE_DOWN, KeyCombination.CONTROL_DOWN));
        unzoom.setOnAction(event -> {
            zoomOut();
        });
        
        view.getItems().addAll(zoom, unzoom);
        parent.getMenus().addAll(file, edit, view);

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

        rowLabel.setText("Row: " + row);
        columnLabel.setText("Column: " + column);
        countLabel.setText("Count: " + text.length());
    }

    private void newFile() {
        if (checkChange()) {
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
        return originalContent.equals(area.getText());
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
                a.setHeaderText("Couldn't save file: " + fullName);
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
                    a.setHeaderText("Couldn't load file: " + selectedFile.getAbsolutePath());
                    a.setContentText("Error: " + error);
                    a.show();
                }
                default -> {}
            }
        }
    }

    private void zoomIn() {
        if (fontSize < 150) {
            fontSize++;
            fontSizeLabel.setText("Font Size: " + fontSize);
            area.setFont(Font.font("Consolas", FontWeight.NORMAL, fontSize));
            refreshCaret();
        }
    }

    private void zoomOut() {
        if (fontSize > 1) {
            fontSize--;
            fontSizeLabel.setText("Font Size: " + fontSize);
            area.setFont(Font.font("Consolas", FontWeight.NORMAL, fontSize));
            refreshCaret();
        }
    }
    
    private void refreshCaret() {
        int pos = area.getCaretPosition();

        //add a text so it updates the curosr size (doesnt do this when updating the font)
        area.setText(area.getText() + "");
        area.positionCaret(pos);
    }
}
