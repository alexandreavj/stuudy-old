/* stuudy - by alex j*/

package com.stuudy.stuudy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    // preferences (study duration, break duration and username)
    static final HashMap<String, Object> preferences = new HashMap<>();

    // write app data to file
    public static void writeAppDataFile(String folderpath, String filename) {
        // BufferedWriter uses a buffer (a temporary storage area) to accumulate a certain amount of data before actually writing it to the underlying output stream. this reduces the number of actual write operations to the output stream
        // the FileWriter is what actually writes the data to the file
        try (BufferedWriter fbufferedWriter = new BufferedWriter(new FileWriter(folderpath + File.separator + filename))) {    // File.separator adds the system specific filepath separator
            // write preferences
            fbufferedWriter.write(String.format("%s;%02d;%02d", preferences.get("username:") + " ".repeat(welcomeController.MAX_USERNAME_SIZE - preferences.get("username:").toString().length()), (int) preferences.get("study-duration:"), (int) preferences.get("pause-duration:")));

            // write kanban data
            if (homeController.toDos != null && homeController.inProgress != null && homeController.done != null) {    // check if the lists are not null (for the first time running, these lists are not linked to the UI yet)
                for (Object i : homeController.toDos) {
                    if (i instanceof TextField) {
                        // add white spaces to make the size of the data the same as the maximum size (easier to handle the reading chunks)
                        fbufferedWriter.write("#T#" + ((TextField) i).getText() + " ".repeat(homeController.MAX_KANBAN_TEXT_SIZE - ((TextField) i).getText().length()) + "#T#");
                    }
                }
                for (Object i : homeController.inProgress) {
                    if (i instanceof TextField) {
                        fbufferedWriter.write("#P#" + ((TextField) i).getText() + " ".repeat(homeController.MAX_KANBAN_TEXT_SIZE - ((TextField) i).getText().length()) + "#P#");
                    }
                }
                for (Object i : homeController.done) {
                    if (i instanceof TextField) {
                        fbufferedWriter.write("#D#" + ((TextField) i).getText() + " ".repeat(homeController.MAX_KANBAN_TEXT_SIZE - ((TextField) i).getText().length()) + "#D#");
                    }
                }
            }

            System.out.println("App data written to file successfully.");
        } catch (IOException e) {
            System.out.println("Error: could not write app data - " + e.getMessage());
            System.exit(3);
        }
    }

    // read app data file
    public static void readAppPreferences(String folderpath, String filename) {
        File appDataFile = new File(folderpath + File.separator + filename);
        if (appDataFile.exists()) {
            String temp = "";

            // read preferences (username, durations)
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(folderpath + File.separator + filename))) {
                int bufferSize = welcomeController.MAX_USERNAME_SIZE + 6;
                char[] buffer = new char[bufferSize];
                // reads the specified number of chars
                int nbrBytesRead = bufferedReader.read(buffer, 0, bufferSize);
                if (nbrBytesRead != -1)
                    // converts the char[] to a String
                    temp = new String(buffer, 0, nbrBytesRead);

                // regex to check the data
                String regexPreferences = "(.{30});(\\d{2});(\\d{2})";    // three capturing groups: 1. 30 characters; 2. two digits; 3. two digits
                Pattern patternPreferences = Pattern.compile(regexPreferences);
                Matcher matcherPreferences = patternPreferences.matcher(temp);
                if (matcherPreferences.matches()) {
                    // get groups from regex
                    preferences.put("username:", matcherPreferences.group(1).strip());    // remove white spaces
                    preferences.put("study-duration:", Integer.parseInt(matcherPreferences.group(2)));
                    preferences.put("pause-duration:", Integer.parseInt(matcherPreferences.group(3)));

                    System.out.println("App preferences successfully read.");
                } else {
                    System.out.println("Error: could not read app data - data does not match the parameters. Restoring to defaults...");
                }
            } catch (IOException e) {
                System.out.println("Error: could not read app data - " + e.getMessage());
                System.exit(2);
            }
        } else {
            System.out.println("Error: could not read app data - the file does not exist. Restoring to defaults...");
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("logo.png")));

        if (preferences.get("username:").equals("")) {    // first time running the app
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("welcome.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 350);
            stage.setTitle("stuudy: welcome");
            stage.setResizable(false);
            stage.setScene(scene);
        } else {    // not the first time running the app
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
            stage.setTitle("stuudy: home");
            stage.setResizable(true);
            stage.setScene(scene);
        }

        stage.getIcons().add(icon);
        stage.show();
    }

    public static void main(String[] args) {
        // default values - preferences
        preferences.put("username:", "");
        preferences.put("study-duration:", 50);
        preferences.put("pause-duration:", 15);

        // load app data
        readAppPreferences(System.getProperty("user.home"), "stuudy.txt");

        // set timer
        homeController.secondsRemaining = (int) preferences.get("study-duration:") * 60;

        launch();
    }
}