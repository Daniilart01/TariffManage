package com.nure.tariffmanage.handlers;

import com.nure.tariffmanage.MainController;
import com.nure.tariffmanage.TariffManageApp;
import com.nure.tariffmanage.utill.DBConnection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ChangeTariffEventHandle implements EventHandler<ActionEvent> {
    String tariffName;

    public ChangeTariffEventHandle(String tariffName) {
        this.tariffName = tariffName;
    }

    @Override
    public void handle(ActionEvent event) {
        String client = "Select Tariff_id, Tariff_start_date from clients where id=?";
        String tariff = "Select id,tariff_cost from tariffs where tariff_name =?";
        try {
            PreparedStatement tariffStatement = DBConnection.getConnection().prepareStatement(tariff);
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(client, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setInt(1, TariffManageApp.USER);
            tariffStatement.setString(1,tariffName);
            ResultSet tariffSet = tariffStatement.executeQuery();
            ResultSet resultSet = preparedStatement.executeQuery();
            tariffSet.next(); resultSet.next();
            if(Double.parseDouble(MainController.mainController.userData.get(1))>=tariffSet.getDouble(2)){
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Tariff changing");
                confirmation.setHeaderText("Change your tariff to "+tariffName+"? \n(will be write-offed "+Math.round(tariffSet.getDouble(2))+" UAH from your account)");
                confirmation.initStyle(StageStyle.UTILITY);
                Optional<ButtonType> buttonType = confirmation.showAndWait();
                if(buttonType.get()!=ButtonType.OK){
                    return;
                }
                resultSet.updateDouble(1, tariffSet.getDouble(1));
                resultSet.updateDate(2, new Date(System.currentTimeMillis()));
                resultSet.updateRow();
                MainController.mainController.reorderTariff();
                MainController.mainController.initTariffs();
                String nullSql = "Select end_date from usage_history where end_date IS NULL AND CLIENT_ID =?";
                PreparedStatement nullSqlStatement = DBConnection.getConnection().prepareStatement(nullSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                nullSqlStatement.setInt(1, TariffManageApp.USER);
                ResultSet nullSqlSet = nullSqlStatement.executeQuery();
                nullSqlSet.next();
                nullSqlSet.updateDate(1,new Date(System.currentTimeMillis()));
                nullSqlSet.updateRow();
                String alterSql = "INSERT INTO USAGE_HISTORY(CLIENT_ID,TARIFF_ID,START_DATE) VALUES (?,?,?)";
                PreparedStatement alterStatement = DBConnection.getConnection().prepareStatement(alterSql);
                alterStatement.setInt(1,TariffManageApp.USER);
                alterStatement.setDouble(2, tariffSet.getDouble(1));
                alterStatement.setDate(3, new Date(System.currentTimeMillis()));
                alterStatement.executeUpdate();
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Tariff was successfully changed to "+tariffName+"!");
                success.initStyle(StageStyle.UTILITY);
                success.show();
            }
            else{
                Alert noMoneyAlert = new Alert(Alert.AlertType.ERROR);
                noMoneyAlert.setTitle("Error");
                noMoneyAlert.setHeaderText("Not enough money to change tariff, you should have " + Math.round(tariffSet.getDouble(2)) + "UAH on balance to proceed");
                noMoneyAlert.initStyle(StageStyle.UTILITY);
                noMoneyAlert.show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
