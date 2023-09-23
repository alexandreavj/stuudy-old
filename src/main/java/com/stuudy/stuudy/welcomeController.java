package com.stuudy.stuudy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class welcomeController implements Initializable {
    public static int MAX_USERNAME_SIZE = 30;

    @FXML
    private TextField nameField;

    @FXML
    protected void onStartButtonClick(ActionEvent event) throws IOException {
        HashMap<String, Object> preferences = Main.preferences;

        String username = nameField.getText();
        // check if name field is empty
        if (!username.isEmpty()) {
            System.out.println("new username: " + username);
            preferences.put("username:", nameField.getText());
            Main.writeAppDataFile(System.getProperty("user.home"), "stuudy.txt");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
            stage.setTitle("stuudy: home");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // adds a listener to each text field: each time the text is changed, it is checked whether the max char number was exceeded
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > MAX_USERNAME_SIZE)
                nameField.setText(oldValue);
        });
    }
}