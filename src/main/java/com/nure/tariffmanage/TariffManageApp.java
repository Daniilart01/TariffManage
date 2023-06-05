package com.nure.tariffmanage;

import com.nure.tariffmanage.utill.UserSessionWriter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class TariffManageApp extends Application {

    public static int USER = 0;
    public static boolean REMEMBER;
    @Override
    public void start(Stage stage) throws IOException {
        stage.initStyle(StageStyle.UNDECORATED);
        USER = UserSessionWriter.loadDataFromFile();
        if(USER == 0){
            initLoginView(stage);
        }else{
            REMEMBER = true;
            initMainView(stage);
        }
    }

    public static void initMainView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TariffManageApp.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Tariff Management");
        stage.setScene(scene);
        stage.show();
    }
    public static void initLoginView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TariffManageApp.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}