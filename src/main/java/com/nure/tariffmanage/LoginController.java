package com.nure.tariffmanage;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.text.PlainDocument;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML
    public TextField loginUsernameTextField;
    @FXML
    public PasswordField loginPasswordPasswordField;
    @FXML
    public Label invalidLoginCredentials;
    @FXML
    public Button LoginButton;
    @FXML
    public CheckBox rememberBox;
    @FXML
    public void onLoginButtonClick() throws IOException {
        if (!validateInput()) {
            return;
        }
        TariffManageApp.USER = validateUser();
        if (TariffManageApp.USER != 0) {
            UserSessionWriter.saveDataToFile(TariffManageApp.USER);
            TariffManageApp.REMEMBER = rememberBox.isSelected();
            Stage stage = (Stage)rememberBox.getScene().getWindow();
            stage.close();
            TariffManageApp.initMainView(stage);
        } else {
            invalidLoginCredentials.setText("Invalid credentials!");
        }
    }

    @FXML
    public void forgotPasswordPressed() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password recovery");
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText("Temporarily password has been sent via SMS");
        alert.showAndWait();
    }

    private boolean validateInput() {
        if (loginUsernameTextField.getText().isBlank()) {
            loginUsernameTextField.setStyle("-fx-border-color:red;");
            new animatefx.animation.Shake(loginUsernameTextField).play();
            if (loginPasswordPasswordField.getText().isBlank()) {
                loginPasswordPasswordField.setStyle("-fx-border-color:red");
                new animatefx.animation.Shake(loginPasswordPasswordField).play();
            }
            return false;
        }
        if (loginPasswordPasswordField.getText().isBlank()) {
            loginPasswordPasswordField.setStyle("-fx-border-color:red");
            new animatefx.animation.Shake(loginPasswordPasswordField).play();
            return false;
        }
        return true;
    }

    @FXML
    public void passwordAndUsernameInput() {
        invalidLoginCredentials.setText("");
        loginPasswordPasswordField.setStyle("");
        loginUsernameTextField.setStyle("");
    }

    private int validateUser() {
        try {
            String sql = "select id from Clients Where login =? and user_password =?";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, loginUsernameTextField.getText());
            String hashed = DigestUtils.md5Hex(loginPasswordPasswordField.getText());
            preparedStatement.setString(2, hashed);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            return 0;
        } catch (SQLException exception) {
            System.err.println("Error getting data from db!");
        }
        return 0;
    }
    @FXML
    public void closePressed(ActionEvent actionEvent) {
        Platform.exit();
    }
}