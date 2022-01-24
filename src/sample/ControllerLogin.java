package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ControllerLogin {

    @FXML
    private Button login_enterButton;

    @FXML
    private ImageView exit, login_loading;

    @FXML
    private TextField login_nameInput;

    @FXML
    private Label login_errorMsg;

    @FXML
    public void enterButtonClicked() throws IOException {

        String username = login_nameInput.getText();

        if (username.isBlank()) {
            login_errorMsg.setVisible(true);

        } else {

            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Ray\\Desktop\\CODING\\Java\\Diary\\src\\sample\\username.txt"));
            writer.write(username);
            writer.close();

            FXMLLoader viewFXMLloader = new FXMLLoader(getClass().getResource("view.fxml"));
            Scene scene = new Scene(viewFXMLloader.load());

            Stage stage = (Stage) login_enterButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        }
    }

    @FXML
    private void handleExitButton() {
        System.exit(0);
    }

}
