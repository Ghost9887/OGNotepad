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
import javafx.scene.control.TextInputDialog;
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
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import ghost.ognotepad.backend.*;


//TODO: FEATURES TO ADD 
/*
 *  Settings page (Fonts, DarkMode, LightMode, Default Width, Default Height, Default font size)
 *  Dark/light mode switcher
 *  Config file to store settings
 *  Print File
 *  New Files open in a new Tab maybe????
 *  when deleting with control the count doesnt get updated 
 */

public class GUI {

    private final int MIN_FONT_SIZE = 1;
    private final int MAX_FONT_SIZE = 150;
    private final Stage primaryStage;
    private int defaultWidth = 800;
    private int defaultHeight = 600;
    private int row = 1;
    private int column = 0;
    private int count = 0;
    private int fontSize = 12;
    private TextArea area = createTextArea();
    private Label rowLabel = new Label("Row: 1");
    private Label columnLabel = new Label("Column: 0");
    private Label countLabel = new Label("Count: 0");
    private Label fontSizeLabel = new Label("Font Size: 12");
    private Label breakLabel = new Label("'break'");
    private Label fileLabel = new Label("");
    private String originalContent = "";

    public GUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void createWindow() {
        primaryStage.setTitle("OGNotepad");

        VBox root = new VBox();

        fileLabel.setPadding(new Insets(0, 0, 0, 10));
        breakLabel.setVisible(false);

        VBox topBar = createTopBar();
        HBox bottomBar = createBottomBar();
        VBox.setVgrow(area, Priority.ALWAYS);
        root.getChildren().addAll(topBar, fileLabel, area, bottomBar);

        Scene scene = new Scene(root, defaultWidth, defaultHeight);
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
        box.getChildren().addAll(rowLabel, columnLabel, countLabel, fontSizeLabel, breakLabel);
        return box;
    }

    private TextArea createTextArea() {
        TextArea area = new TextArea();
        area.setPrefWidth(defaultWidth);
        area.setPadding(new Insets(0, 10, 5, 0));
        area.setStyle(
            "-fx-focus-color: -fx-control-inner-background; -fx-faint-focus-color: -fx-control-inner-background;"
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
        MenuItem selectAll = new MenuItem("Select all");
        selectAll.setOnAction(event -> {
            area.selectAll();
        });
        selectAll.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHIFT_DOWN));

        MenuItem printTime = new MenuItem("Time"); 
        printTime.setOnAction(event-> {
            area.insertText(area.getCaretPosition(), LocalTime.now().toString().substring(0, 8));
        });
        printTime.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));

        MenuItem printDate = new MenuItem("Date"); 
        printDate.setOnAction(event-> {
            area.insertText(area.getCaretPosition(), LocalDate.now().toString());
        });
        printDate.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        
        MenuItem printDateTime = new MenuItem("DateTime"); 
        printDateTime.setOnAction(event-> {
            String dateTime = LocalDate.now().toString() + LocalTime.now().toString().substring(0, 8);
            area.insertText(area.getCaretPosition(), dateTime);
        });
        printDateTime.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));

        MenuItem find = new MenuItem("Find");
        find.setOnAction(event -> {
            find(0, "");
        });
        find.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        
        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(event -> {
            area.paste();
        });
        paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(event -> {
            area.copy();
        });
        copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));

        MenuItem cut = new MenuItem("Cut");
        cut.setOnAction(event -> {
            area.cut();
        });
        cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));

        MenuItem undo = new MenuItem("Undo");
        undo.setOnAction(event -> {
            area.undo();
        });
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));

        MenuItem redo = new MenuItem("Redo");
        redo.setOnAction(event -> {
            area.redo();
        });
        redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));

        edit.getItems().addAll(selectAll, printTime, printDate, printDateTime, find, paste, copy, cut, undo, redo);


        Menu view = new Menu("View");
        MenuItem zoom = new MenuItem("Zoom");
        zoom.setOnAction(event -> { 
            zoomIn(); 
        });
        zoom.setAccelerator(new KeyCodeCombination(KeyCode.PAGE_UP, KeyCombination.CONTROL_DOWN));

        MenuItem unzoom = new MenuItem("Zoom out");
        unzoom.setOnAction(event -> {
            zoomOut();
        });
        unzoom.setAccelerator(new KeyCodeCombination(KeyCode.PAGE_DOWN, KeyCombination.CONTROL_DOWN));
        
        MenuItem toggleBreak = new MenuItem("Toggle break");
        toggleBreak.setOnAction(event -> {
            area.setWrapText(true);
            breakLabel.setVisible(true);
        });

        MenuItem toggleNoBreak = new MenuItem("Toggle no break");
        toggleNoBreak.setOnAction(event -> {
            area.setWrapText(false);
            breakLabel.setVisible(false);
        });
        view.getItems().addAll(zoom, unzoom, toggleBreak, toggleNoBreak);


        Menu settings = new Menu("Settings");

        parent.getMenus().addAll(file, edit, view, settings);

        VBox box = new VBox(20);
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
        if (fontSize < MAX_FONT_SIZE) {
            fontSize++;
            fontSizeLabel.setText("Font Size: " + fontSize);
            area.setFont(Font.font("Consolas", FontWeight.NORMAL, fontSize));
            refreshCaret();
        }
    }

    private void zoomOut() {
        if (fontSize > MIN_FONT_SIZE) {
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

    private void find(int index, String lastWord) {
        TextInputDialog td = new TextInputDialog(lastWord);
        td.setHeaderText("Find sequence");
        Optional<String> res = td.showAndWait();
        if (res.isPresent()) {
            String word = res.get();
            if (!word.equals(lastWord)) index = 0;
            if (area.getText().contains(word)) {

                int pos = area.getText().indexOf(word, index);

                if (pos < 0) {
                    index = 0;
                    pos = area.getText().indexOf(word, index);
                }
                area.selectRange(pos, pos + word.length());
                find(pos + word.length(), word);

            }else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No matches");
                alert.showAndWait();
            }
        }
    }
}
