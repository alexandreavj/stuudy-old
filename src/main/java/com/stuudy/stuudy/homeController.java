package com.stuudy.stuudy;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class homeController implements Initializable {
    public static int MAX_KANBAN_TEXT_SIZE = 200;

    private final String[] fortunePhrases = {"a journey of a thousand miles begins with a single step.",
            "fortune favors the bold.",
            "your efforts will lead to success.",
            "embrace change for it will bring you growth.",
            "a surprise is waiting just around the corner.",
            "your creativity knows no bounds.",
            "good things come to those who wait.",
            "seize every opportunity that comes your way.",
            "a smile can brighten any day.",
            "the best is yet to come.",
            "trust your intuition; it will guide you well.",
            "your hard work will pay off in unexpected ways.",
            "kindness will open doors you never knew existed.",
            "success is the sum of small efforts.",
            "you have the power to make a difference.",
            "believe in yourself and others will too.",
            "your potential is limitless.",
            "adversity will lead you to greater strength.",
            "luck is on your side.",
            "a generous heart attracts abundant blessings.",
            "cherish your loved ones; they bring you joy.",
            "opportunities for advancement are on the horizon.",
            "take time to relax and recharge.",
            "your determination will conquer any challenge.",
            "unexpected friendships will enrich your life.",
            "wisdom is the key to unlocking new doors.",
            "your positive attitude is contagious.",
            "the universe is conspiring in your favor.",
            "learning from the past will shape your future.",
            "celebrate your achievements, no matter how small.",
            "love and laughter will surround you."};

    @FXML private Button buttonLoadBGFile;
    @FXML private Button buttonStudyTimer;
    @FXML private Button buttonPauseTimer;

    @FXML private ImageView iconButtonSettingsTimer;
    @FXML private ImageView iconButtonSettingsTickTimer;
    @FXML private ImageView iconStartTimer;
    @FXML private ImageView iconPauseTimer;
    @FXML private ImageView imageFloppyDiskKanban;
    @FXML private ImageView imageKanban;

    @FXML private Pane paneSettingsTimer;
    @FXML private Pane kanbanPane;

    @FXML private Slider sliderDurationStudy;
    @FXML private Slider sliderDurationPause;

    @FXML private Label fortuneText;
    @FXML private Label timerLabel;
    @FXML private Label labelSliderStudy;
    @FXML private Label labelSliderBreak;

    @FXML private MediaView videoPlayerUI;

    @FXML private Pane paneToDo;
    @FXML private Pane paneInProgress;
    @FXML private Pane paneDone;

    @FXML public static ObservableList<Node> toDos = null;
    @FXML public static ObservableList<Node> inProgress = null;
    @FXML public static ObservableList<Node> done = null;

    private final DataFormat dataFormatDragNDropKanban = new DataFormat("TODO");

    private MediaPlayer mediaPlayer;

    static Timer timer;

    static int secondsRemaining;

    static boolean isRunning = false;

    public static void startTimer(Label timerUI, ImageView iconStart, ImageView iconPause) {
        if (!isRunning) {
            timer = new Timer();
            isRunning = true;
            int initialDuration = secondsRemaining;
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        secondsRemaining--;

                        updateTimer(timerUI);

                        if (secondsRemaining <= 0) {
                            resetTimer(timerUI, initialDuration, iconStart, iconPause);
                            System.out.println("Timer finished.");
                        }
                    });
                }
            }, 0, 1000);
        }
    }

    public static void pauseTimer() {
        if (isRunning) {
            timer.cancel();
            isRunning = false;
        }
    }

    public static void resetTimer(Label timerUI, int duration, ImageView iconStart, ImageView iconPause) {
        pauseTimer();
        secondsRemaining = duration;  // reset to initial time
        iconStart.setVisible(true);
        iconPause.setVisible(false);

        updateTimer(timerUI);
    }

    public static void updateTimer(Label timerUI) {
        timerUI.setText(secondsRemaining / 60 + ":" + String.format("%02d", secondsRemaining % 60));
    }

    private static void updateAppPreferences(String folderpath, String filename) {
        try {
            RandomAccessFile preferencesFile = new RandomAccessFile(folderpath + File.separator + filename, "rw");
            preferencesFile.writeBytes(String.format("%s;%02d;%02d", Main.preferences.get("username:") + " ".repeat(welcomeController.MAX_USERNAME_SIZE - Main.preferences.get("username:").toString().length()), (int) Main.preferences.get("study-duration:"), (int) Main.preferences.get("pause-duration:")));
            preferencesFile.close();
        } catch (Exception e) {
            System.out.println("Could not write preferences to file.");
        }
    }

    @FXML
    private void setFortuneText(String[] phrases) {
        fortuneText.setText(phrases[Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1]);
    }

    @FXML
    private void clickButtonSettingsTimer() {
        if (paneSettingsTimer.isVisible()) {
            paneSettingsTimer.setVisible(false);
            iconButtonSettingsTickTimer.setVisible(false);
            iconButtonSettingsTimer.setVisible(true);

            // edit preferences
            Main.preferences.put("study-duration:", (int) sliderDurationStudy.getValue());
            Main.preferences.put("pause-duration:", (int) sliderDurationPause.getValue());

            // write to DB
            updateAppPreferences(System.getProperty("user.home"), "stuudy.txt");
            System.out.println("STUDY: " + Main.preferences.get("study-duration:") + "; PAUSE: " + Main.preferences.get("pause-duration:"));

            // change timer
            if (buttonStudyTimer.getStyle().equals("-fx-background-color: rgba(0, 0, 0, 0.1); -fx-border-color: #eeeeee; -fx-border-radius: 5;")) {
                resetTimer(timerLabel, (int) Main.preferences.get("study-duration:") * 60, iconStartTimer, iconPauseTimer);
            } else {
                resetTimer(timerLabel, (int) Main.preferences.get("pause-duration:") * 60, iconStartTimer, iconPauseTimer);
            }

        } else {
            paneSettingsTimer.setVisible(true);
            iconButtonSettingsTickTimer.setVisible(true);
            iconButtonSettingsTimer.setVisible(false);

        }
    }

    @FXML
    private void clickButtonLoadBGFile() {
        Stage stage = (Stage) buttonLoadBGFile.getScene().getWindow();

        FileChooser fileChooserBG = new FileChooser();
        fileChooserBG.setTitle("choose your video wallpaper");
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("video files (.mp4)", "*.mp4");
        fileChooserBG.getExtensionFilters().add(fileExtensions);
        fileChooserBG.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedFile = fileChooserBG.showOpenDialog(stage);
        if (selectedFile != null) {
            String videoFilepath = selectedFile.getAbsolutePath();
            System.out.println("Selected file: " + videoFilepath);

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            Media videoBG = new Media(new File(videoFilepath).toURI().toString());
            mediaPlayer = new MediaPlayer(videoBG);

            videoPlayerUI.setPreserveRatio(false);    // change aspect ratio to fill the video player

            // bind video player dimensions to window size
            videoPlayerUI.fitWidthProperty().bind(stage.widthProperty());
            videoPlayerUI.fitHeightProperty().bind(stage.heightProperty());

            videoPlayerUI.setMediaPlayer(mediaPlayer);
            mediaPlayer.play();
        } else {
            System.out.println("No selected files!");
        }
    }

    @FXML
    private void clickButtonStudyTimer() {
        if (buttonStudyTimer.getStyle().equals("-fx-background-color: rgba(0, 0, 0, 0); -fx-border-color: #eeeeee; -fx-border-radius: 5;")) {
            buttonStudyTimer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.1); -fx-border-color: #eeeeee; -fx-border-radius: 5;");
            buttonPauseTimer.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-border-color: #eeeeee; -fx-border-radius: 5;");

            HashMap<String, Object> preferences = Main.preferences;
            resetTimer(timerLabel, (int) preferences.get("study-duration:") * 60, iconStartTimer, iconPauseTimer);
        }
    }

    @FXML
    private void clickButtonPauseTimer() {
        if (buttonPauseTimer.getStyle().equals("-fx-background-color: rgba(0, 0, 0, 0); -fx-border-color: #eeeeee; -fx-border-radius: 5;")) {
            buttonPauseTimer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.1); -fx-border-color: #eeeeee; -fx-border-radius: 5;");
            buttonStudyTimer.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-border-color: #eeeeee; -fx-border-radius: 5;");

            HashMap<String, Object> preferences = Main.preferences;
            resetTimer(timerLabel, (int) preferences.get("pause-duration:") * 60, iconStartTimer, iconPauseTimer);
        }
    }

    @FXML
    private void clickButtonStartPauseTimer() {
        if (iconStartTimer.isVisible()) {
            iconStartTimer.setVisible(false);
            iconPauseTimer.setVisible(true);

            startTimer(timerLabel, iconStartTimer, iconPauseTimer);
        } else {
            iconStartTimer.setVisible(true);
            iconPauseTimer.setVisible(false);

            pauseTimer();
        }
    }

    @FXML
    private void removeBlankSlotKanban(ObservableList<Node> column) {
        // cannot remove while iterating; auxiliary list to store the ones that need to me removed
        ArrayList<TextField> remove = new ArrayList<>();
        for (int i = 0; i < column.size(); i++) {
            // if the node is a TextField and it's empty
            if ((column.get(i) instanceof TextField current) && (current.getText().isEmpty())) {
                if (i + 1 < column.size())
                    column.get(i + 1).setLayoutY(current.getLayoutY());
                remove.add(current);
            }
        }
        if (!remove.isEmpty()) {
            ((AnchorPane) remove.get(0).getParent()).setPrefHeight(((AnchorPane) remove.get(0).getParent()).getHeight() - remove.size() * 30);
            column.removeAll(remove);
        }
    }

    @FXML
    private void clickButtonKanban() {
        // change pane/icon visibility
        kanbanPane.setVisible(!kanbanPane.isVisible());
        imageKanban.setVisible(!imageKanban.isVisible());
        imageFloppyDiskKanban.setVisible(!imageFloppyDiskKanban.isVisible());

        // if floppy disk is not visible here it's because when it was clicked it was visible
        if (!imageFloppyDiskKanban.isVisible()) {
            removeBlankSlotKanban(toDos);
            removeBlankSlotKanban(inProgress);
            removeBlankSlotKanban(done);

            Main.writeAppDataFile(System.getProperty("user.home"), "stuudy.txt");
        }
    }

    @FXML
    private void clickNewToDo(ActionEvent event) {
        // make sure that the event was triggered by a button
        if (event.getSource() instanceof Button buttonClicked) {
            // crate a new TextField
            TextField newToDo = new TextField();
            // set some preferences
            newToDo.setPrefWidth(280);
            newToDo.setPrefHeight(25);
            newToDo.setLayoutX(5);
            newToDo.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-border-color: BLACK; -fx-border-radius: 5");
            newToDo.setFont(new Font("Andale Mono", 12));
            // adds a listener to the TextFields: each time the value is changed, it is checked whether it exceeds the max length or not
            newToDo.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() > MAX_KANBAN_TEXT_SIZE)
                    newToDo.setText(oldValue);
            });
            // set drag and drop for the new TextField
            newToDo.setOnDragDetected(e -> {
                Dragboard dragboard= newToDo.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.put(dataFormatDragNDropKanban, newToDo.getText());
                dragboard.setContent(content);
                e.consume();
            });
            // enlarge the AnchorPane
            ((AnchorPane) buttonClicked.getParent()).setPrefHeight(((AnchorPane) buttonClicked.getParent()).getPrefHeight() + 30);
            // this method will work for the three buttons on the kanban: the 'if' block checks which button was it by getting its ID
            if (buttonClicked.getId().equals("buttonNewToDo")) {
                // if the ObservableList does not end with a TextFields, place the new one below the add button
                if (!(toDos.get(toDos.size() - 1) instanceof TextField)) {
                    newToDo.setLayoutY(buttonClicked.getLayoutY() + 40);
                } else {    // if it is not empty, place it below the last TextField
                    newToDo.setLayoutY(toDos.get(toDos.size() - 1).getLayoutY() + 30);
                }
                // add the new to-do to the ObservableList (and to the UI)
                toDos.add(newToDo);
            } else if (buttonClicked.getId().equals("buttonNewInProgress")) {
                if (!(inProgress.get(inProgress.size() - 1) instanceof TextField)) {
                    newToDo.setLayoutY(buttonClicked.getLayoutY() + 40);
                } else {
                    newToDo.setLayoutY(inProgress.get(inProgress.size() - 1).getLayoutY() + 30);
                }
                inProgress.add(newToDo);
            } else {
                if (!(done.get(done.size() - 1) instanceof TextField)) {
                    newToDo.setLayoutY(buttonClicked.getLayoutY() + 40);
                } else {
                    newToDo.setLayoutY(done.get(done.size() - 1).getLayoutY() + 30);
                }
                done.add(newToDo);
            }
        }
    }

    private void setPaneDragAndDrop(Pane pane) {
        pane.setOnDragOver(dragEvent -> {
            // accept if the DataFormat matches and if the origin is not the same as the target
            if (dragEvent.getGestureSource() != pane && dragEvent.getDragboard().hasContent(dataFormatDragNDropKanban)) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
            dragEvent.consume();
        });

        pane.setOnDragDropped(dragEvent -> {
            Dragboard dragboard = dragEvent.getDragboard();
            boolean success = false;
            // add a new TextField to the target
            if (dragboard.hasContent(dataFormatDragNDropKanban)) {
                TextField draggedTextField = new TextField(dragboard.getContent(dataFormatDragNDropKanban).toString());

                // set some preferences
                draggedTextField.setPrefWidth(280);
                draggedTextField.setPrefHeight(25);
                draggedTextField.setLayoutX(5);
                draggedTextField.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-border-color: BLACK; -fx-border-radius: 5");
                draggedTextField.setFont(new Font("Andale Mono", 12));

                // adds a listener to the TextFields: each time the value is changed, it is checked whether it exceeds the max length or not
                draggedTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.length() > MAX_KANBAN_TEXT_SIZE)
                        draggedTextField.setText(oldValue);
                });

                // set drag and drop for the new TextField
                draggedTextField.setOnDragDetected(e -> {
                    Dragboard newDragboard= draggedTextField.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.put(dataFormatDragNDropKanban, draggedTextField.getText());
                    newDragboard.setContent(content);
                });

                // enlarge the AnchorPane
                pane.setPrefHeight(pane.getPrefHeight() + 30);

                if (pane.getId().equals("paneToDo")) {
                    // if the ObservableList does not end with a TextFields, place the new one below the add button (index 1)
                    if (!(toDos.get(toDos.size() - 1) instanceof TextField)) {
                        draggedTextField.setLayoutY(pane.getChildren().get(1).getLayoutY() + 40);
                    } else {    // if it is not empty, place it below the last TextField
                        draggedTextField.setLayoutY(toDos.get(toDos.size() - 1).getLayoutY() + 30);
                    }
                    // add the new to-do to the ObservableList (and to the UI)
                    toDos.add(draggedTextField);
                } else if (pane.getId().equals("paneInProgress")) {
                    if (!(inProgress.get(inProgress.size() - 1) instanceof TextField)) {
                        draggedTextField.setLayoutY(pane.getChildren().get(1).getLayoutY() + 40);
                    } else {
                        draggedTextField.setLayoutY(inProgress.get(inProgress.size() - 1).getLayoutY() + 30);
                    }
                    inProgress.add(draggedTextField);
                } else {
                    if (!(done.get(done.size() - 1) instanceof TextField)) {
                        draggedTextField.setLayoutY(pane.getChildren().get(1).getLayoutY() + 40);
                    } else {
                        draggedTextField.setLayoutY(done.get(done.size() - 1).getLayoutY() + 30);
                    }
                    done.add(draggedTextField);
                }

                // get original TextField
                TextField textFieldToRemove = (TextField) dragEvent.getGestureSource();
                // update subsequent TextFields (positions)
                for (int i = ((AnchorPane) textFieldToRemove.getParent()).getChildren().indexOf(textFieldToRemove) + 1; i < ((AnchorPane) textFieldToRemove.getParent()).getChildren().size(); i++) {
                    ((AnchorPane) textFieldToRemove.getParent()).getChildren().get(i).setLayoutY(((AnchorPane) textFieldToRemove.getParent()).getChildren().get(i).getLayoutY() - 30);
                }
                // update parent AnchorPane height
                ((AnchorPane) textFieldToRemove.getParent()).setPrefHeight(((AnchorPane) textFieldToRemove.getParent()).getPrefHeight() - 30);
                // remove original TextField
                ((AnchorPane) textFieldToRemove.getParent()).getChildren().remove(textFieldToRemove);

                success = true;
            }
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });
    }

    private void initializeKanban(String folderpath, String filename) {
        int bufferSize = homeController.MAX_KANBAN_TEXT_SIZE + 6;
        char [] buffer = new char[bufferSize];

        // regexes to check the kanban slots
        String regexKanban = "#(.)#(.{200})#(.)#";
        Pattern patternKanban = Pattern.compile(regexKanban);

        // reads the specified number of chars in a loop until EOF
        int charsRead;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(folderpath + File.separator + filename))) {
            String temp;
            bufferedReader.skip(welcomeController.MAX_USERNAME_SIZE + 6);   // skip the preferences
            while ((charsRead = bufferedReader.read(buffer, 0, bufferSize)) != -1) {
                temp = new String(buffer, 0, charsRead);
                Matcher matcherToDo = patternKanban.matcher(temp);
                if (matcherToDo.matches()) {
                    // crate a new TextField
                    TextField newToDo = new TextField();
                    // set some preferences
                    newToDo.setPrefWidth(280);
                    newToDo.setPrefHeight(25);
                    newToDo.setLayoutX(5);
                    newToDo.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-border-color: BLACK; -fx-border-radius: 5");
                    newToDo.setFont(new Font("Andale Mono", 12));
                    // adds a listener to the TextFields: each time the value is changed, it is checked whether it exceeds the max length or not
                    newToDo.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.length() > MAX_KANBAN_TEXT_SIZE)
                            newToDo.setText(oldValue);
                    });
                    // set drag and drop for the new TextField
                    newToDo.setOnDragDetected(e -> {
                        Dragboard dragboard= newToDo.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        content.put(dataFormatDragNDropKanban, newToDo.getText());
                        dragboard.setContent(content);
                        e.consume();
                    });
                    if (matcherToDo.group(1).equals(matcherToDo.group(3)) && matcherToDo.group(1).equals("T")) {
                        // TO-DO KANBAN
                        temp = temp.replace("#T#", "");
                        temp = temp.strip();
                        newToDo.setText(temp);
                        // enlarge the AnchorPane
                        paneToDo.setPrefHeight(paneToDo.getPrefHeight() + 30);
                        if (!(toDos.get(toDos.size() - 1) instanceof TextField)) {
                            newToDo.setLayoutY(toDos.get(1).getLayoutY() + 40);    // button is on index 1
                        } else {    // if it is not empty, place it below the last TextField
                            newToDo.setLayoutY(toDos.get(toDos.size() - 1).getLayoutY() + 30);
                        }
                        // add the new to-do to the ObservableList (and to the UI)
                        toDos.add(newToDo);
                    } else if (matcherToDo.group(1).equals(matcherToDo.group(3)) && matcherToDo.group(1).equals("P")) {
                        // IN PROGRESS KANBAN
                        temp = temp.replace("#P#", "");
                        temp = temp.strip();
                        newToDo.setText(temp);
                        paneInProgress.setPrefHeight(paneInProgress.getPrefHeight() + 30);
                        if (!(inProgress.get(inProgress.size() - 1) instanceof TextField)) {
                            newToDo.setLayoutY(inProgress.get(1).getLayoutY() + 40);
                        } else {
                            newToDo.setLayoutY(inProgress.get(inProgress.size() - 1).getLayoutY() + 30);
                        }
                        inProgress.add(newToDo);
                    } else if (matcherToDo.group(1).equals(matcherToDo.group(3)) && matcherToDo.group(1).equals("D")) {
                        // DONE KANBAN
                        temp = temp.replace("#D#", "");
                        temp = temp.strip();
                        newToDo.setText(temp);
                        paneDone.setPrefHeight(paneDone.getPrefHeight() + 30);
                        if (!(done.get(done.size() - 1) instanceof TextField)) {
                            newToDo.setLayoutY(done.get(1).getLayoutY() + 40);
                        } else {
                            newToDo.setLayoutY(done.get(done.size() - 1).getLayoutY() + 30);
                        }
                        done.add(newToDo);
                    } else {
                        // INVALID
                        System.out.println("Error: could not read the whole app data - data does not match the parameters. Some data will be ignored.");
                        break;
                    }
                } else {
                    // INVALID
                    System.out.println("Error: could not read the whole app data - data does not match the parameters. Some data will be ignored.");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error: could not read app data - " + e.getMessage());
            System.exit(2);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setFortuneText(fortunePhrases);

        // SET INITIAL STATE - adjust sliders and labels from timer settings to the values from the preferences
        sliderDurationStudy.setValue((int) Main.preferences.get("study-duration:"));
        sliderDurationPause.setValue((int) Main.preferences.get("pause-duration:"));
        labelSliderStudy.setText(String.valueOf((int) Main.preferences.get("study-duration:")));
        labelSliderBreak.setText(String.valueOf((int) Main.preferences.get("pause-duration:")));

        // adds a listener to the sliders: each time the value is changed, it is rounded to the closest multiple of 5
        sliderDurationStudy.valueProperty().addListener((observable, oldValue, newValue) -> {
            int roundedValueStudy = (int) (Math.round((double) newValue / 5.0) * 5.0);
            labelSliderStudy.setText(String.valueOf(roundedValueStudy));
            sliderDurationStudy.setValue(roundedValueStudy);
        });
        sliderDurationPause.valueProperty().addListener((observable, oldValue, newValue) -> {
            int roundedValuePause = (int) (Math.round((double) newValue / 5.0) * 5.0);
            labelSliderBreak.setText(String.valueOf(roundedValuePause));
            sliderDurationPause.setValue(roundedValuePause);
        });

        // get reference to children list - kanban
        toDos = paneToDo.getChildren();
        inProgress = paneInProgress.getChildren();
        done = paneDone.getChildren();

        initializeKanban(System.getProperty("user.home"), "stuudy.txt");

        // set kanban AnchorPanes as drag-and-drop targets
        setPaneDragAndDrop(paneToDo);
        setPaneDragAndDrop(paneInProgress);
        setPaneDragAndDrop(paneDone);

        // SET INITIAL STATE - timer
        resetTimer(timerLabel, (int) Main.preferences.get("study-duration:") * 60, iconStartTimer, iconPauseTimer);
    }
}