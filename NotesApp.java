import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Date;


public class NotesApp extends Application {

    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private int masterEncryptionKey;

    private ListView<Note> noteListView;
    private TextArea noteContentView;
    private MenuItem saveNoteMenuItem; // Made an instance field

    @Override
    public void init() throws Exception {
        super.init(); // Good practice to call super.init()

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Master Password");
        dialog.setHeaderText("Set Master Password for Encryption");
        dialog.setContentText("Please enter your master password:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String password = result.get();
            masterEncryptionKey = password.hashCode(); // Simple key derivation
            System.out.println("Master encryption key set.");
        } else {
            System.err.println("No master password provided or dialog cancelled. Application will exit.");
            // Platform.exit() might not be immediate or work correctly here as JavaFX toolkit may not be fully initialized.
            // Throwing an exception is a more reliable way to stop initialization.
            Platform.runLater(() -> { // Ensure exit is on FX thread if toolkit is running
                Alert alert = new Alert(Alert.AlertType.ERROR, "Master password is required to use the application. Exiting.");
                alert.setHeaderText("Startup Error");
                alert.showAndWait();
                Platform.exit();
            });
            throw new Exception("Master password not provided. Application cannot start.");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Secure Notes");

        // Root layout
        BorderPane rootLayout = new BorderPane();
        rootLayout.getStyleClass().add("root"); // Apply root style for -fx-base etc.

        // MenuBar
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newNoteMenuItem = new MenuItem("New Note");
        newNoteMenuItem.setOnAction(e -> handleNewNote());
        saveNoteMenuItem = new MenuItem("Save Note"); // Assign to instance field
        saveNoteMenuItem.setOnAction(e -> handleSaveNote());
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(e -> Platform.exit());
        fileMenu.getItems().addAll(newNoteMenuItem, saveNoteMenuItem, new SeparatorMenuItem(), exitMenuItem);

        // Edit Menu
        Menu editMenu = new Menu("Edit");
        MenuItem deleteNoteMenuItem = new MenuItem("Delete Note");
        deleteNoteMenuItem.setOnAction(e -> handleDeleteNote());
        editMenu.getItems().add(deleteNoteMenuItem);

        menuBar.getMenus().addAll(fileMenu, editMenu);

        // ToolBar / Button Bar
        Button newNoteButton = new Button("New Note");
        newNoteButton.setOnAction(e -> handleNewNote());
        Button deleteNoteButton = new Button("Delete Note");
        deleteNoteButton.setOnAction(e -> handleDeleteNote());
        ToolBar toolBar = new ToolBar(newNoteButton, deleteNoteButton);

        // Top VBox for MenuBar and ToolBar
        VBox topContainer = new VBox(menuBar, toolBar);
        rootLayout.setTop(topContainer);

        // ListView for note titles
        noteListView = new ListView<>();
        noteListView.setCellFactory(lv -> new ListCell<Note>() {
            @Override
            protected void updateItem(Note item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String displayText = item.getTitle();
                    long revealTime = item.getRevealDateMillis();
                    if (revealTime > 0) {
                        if (System.currentTimeMillis() < revealTime) {
                            displayText += " (Locked ⏳)";
                        } else {
                            displayText += " (Opened 🔓)";
                        }
                    }
                    setText(displayText);
                }
            }
        });
        noteListView.setItems(notes);
        noteListView.setPrefWidth(250); // Set a preferred width for the list
        rootLayout.setLeft(noteListView);
        BorderPane.setMargin(noteListView, new Insets(5));

        // Add listener for selection changes
        noteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                long revealTime = newSelection.getRevealDateMillis();
                if (revealTime > 0 && System.currentTimeMillis() < revealTime) {
                    noteContentView.setText("This note is time-locked until: " + new Date(revealTime).toLocaleDateString() + " " + new Date(revealTime).toLocaleTimeString());
                    noteContentView.setEditable(false);
                    if (saveNoteMenuItem != null) saveNoteMenuItem.setDisable(true);
                } else {
                    noteContentView.setText(newSelection.decryptContent());
                    noteContentView.setEditable(true);
                    if (saveNoteMenuItem != null) saveNoteMenuItem.setDisable(false);
                }
            } else {
                noteContentView.clear();
                noteContentView.setEditable(true); // Default state
                if (saveNoteMenuItem != null) saveNoteMenuItem.setDisable(false); // Default state
            }
        });

        // TextArea for note content
        noteContentView = new TextArea();
        noteContentView.setPromptText("Select a note to view/edit its content, or create a new one.");
        noteContentView.setEditable(true);
        rootLayout.setCenter(noteContentView);
        BorderPane.setMargin(noteContentView, new Insets(5, 5, 5, 0));

        // Initial Data (Optional for testing)
        // Ensure this runs after masterEncryptionKey is set in init()
        notes.add(new Note("First Note", "This is the content of the first note.", masterEncryptionKey));
        notes.add(new Note("Second Note", "Some text for the second note.", masterEncryptionKey));
        notes.add(new Note("Shopping List", "Milk\nEggs\nBread\nJavaFX books", masterEncryptionKey));


        // Scene and Stage
        Scene scene = new Scene(rootLayout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleNewNote() {
        Dialog<Pair<String, LocalDate>> dialog = new Dialog<>();
        dialog.setTitle("Create New Note");
        dialog.setHeaderText("Enter Note Title and Optional Reveal Date");

        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the content for the dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Note Title");
        DatePicker revealDatePicker = new DatePicker();
        revealDatePicker.setPromptText("Optional Reveal Date");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Reveal Date (Optional):"), 0, 1);
        grid.add(revealDatePicker, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable create button depending on whether a title was entered.
        javafx.scene.Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            createButton.setDisable(newValue.trim().isEmpty());
        });

        // Convert the result to a pair of title and date when the create button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Pair<>(titleField.getText(), revealDatePicker.getValue());
            }
            return null;
        });

        Optional<Pair<String, LocalDate>> result = dialog.showAndWait();

        result.ifPresent(titleAndDate -> {
            String title = titleAndDate.getKey();
            LocalDate selectedDate = titleAndDate.getValue();
            long revealMillis = 0;

            if (selectedDate != null) {
                revealMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                // Ensure reveal date is in the future
                if (revealMillis <= System.currentTimeMillis()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setHeaderText("Reveal Date Must Be in the Future");
                    alert.setContentText("Please select a date after today for the reveal.");
                    alert.showAndWait();
                    return; // Stop note creation
                }
            }

            Note newNote;
            if (revealMillis > 0) {
                newNote = new Note(title, "", masterEncryptionKey, revealMillis);
            } else {
                newNote = new Note(title, "", masterEncryptionKey);
            }
            notes.add(newNote);
            noteListView.getSelectionModel().select(newNote);
            noteContentView.requestFocus();
        });
    }


    private void handleSaveNote() {
        Note selectedNote = noteListView.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            String content = noteContentView.getText();
            selectedNote.encryptContent(content); // This also updates the 'updatedAt' timestamp
            // Optional: Refresh the list view if title could change or for consistent display
            // noteListView.refresh(); // Not strictly necessary here as only content changes
            System.out.println("Note '" + selectedNote.getTitle() + "' saved.");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Note Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a note to save.");
            alert.showAndWait();
        }
    }

    private void handleDeleteNote() {
        Note selectedNote = noteListView.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Delete Note");
            confirmDialog.setHeaderText("Delete '" + selectedNote.getTitle() + "'?");
            confirmDialog.setContentText("Are you sure you want to delete this note? This action cannot be undone.");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                notes.remove(selectedNote);
                noteContentView.clear();
                System.out.println("Note '" + selectedNote.getTitle() + "' deleted.");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Note Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a note to delete.");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
