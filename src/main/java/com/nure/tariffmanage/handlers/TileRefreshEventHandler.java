package com.nure.tariffmanage.handlers;

import com.nure.tariffmanage.MainController;
import com.nure.tariffmanage.TariffManageApp;
import com.nure.tariffmanage.utill.DBConnection;
import eu.hansolo.tilesfx.Tile;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TileRefreshEventHandler implements EventHandler<MouseEvent> {
    @Override
    public void handle(MouseEvent event) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        String sql = "Select * from options";
        String addOns = "Select Client_id, option_id, paid_until, value_left from client_option where client_id=? and option_id=?";
        Map<String, Integer> map = new HashMap<>();
        try {
            Statement statement = DBConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(addOns);
                preparedStatement.setInt(1, TariffManageApp.USER);
                preparedStatement.setInt(2, resultSet.getInt("id"));
                ResultSet addOnesSet = preparedStatement.executeQuery();
                if (!addOnesSet.next()) {
                    String data = String.format("Add %s to your tariff for 28 days, %dUAH", resultSet.getString("OPTION_NAME"),
                            Math.round(resultSet.getDouble("OPTION_COST")));
                    dialog.getItems().add(data);
                    map.put(data, resultSet.getInt("id"));
                }
            }
            try{
                dialog.setSelectedItem(dialog.getItems().get(0));
            }
            catch (IndexOutOfBoundsException ignored){
            }
            dialog.initStyle(StageStyle.UTILITY);
            dialog.setTitle("Add-ons");
            dialog.setHeaderText("Choose Add-Ons to add");
            dialog.setContentText("Available Add-Ons:");
            Optional<String> result = dialog.showAndWait();
            int id;
            if (result.isPresent()) {
                id = map.get(result.get());
            } else {
                return;
            }
            resultSet.beforeFirst();
            do resultSet.next();
            while (resultSet.getInt("ID") != id);
            if(resultSet.getDouble("OPTION_COST")>Double.parseDouble(MainController.mainController.userData.get(1))){
                Alert noMoneyAlert = new Alert(Alert.AlertType.ERROR);
                noMoneyAlert.setTitle("Error");
                noMoneyAlert.setHeaderText("Not enough money to apply option, you should have " + resultSet.getDouble("OPTION_COST") + "UAH on balance to proceed");
                noMoneyAlert.initStyle(StageStyle.UTILITY);
                noMoneyAlert.show();
            }
            else {
                String updateSql = "select balance from clients where id=?";
                PreparedStatement balanceStatement = DBConnection.getConnection().prepareStatement(updateSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                balanceStatement.setInt(1, TariffManageApp.USER);
                ResultSet balanceSet = balanceStatement.executeQuery();
                balanceSet.next();
                balanceSet.updateDouble("BALANCE", balanceSet.getDouble("BALANCE")-resultSet.getDouble("OPTION_COST"));
                balanceSet.updateRow();
                MainController.mainController.userData = MainController.mainController.getUserData();
                String alterSql = "INSERT INTO CLIENT_OPTION(CLIENT_ID,OPTION_ID,PAID_UNTIL,VALUE_LEFT) VALUES(?,?,?,?)";
                PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(alterSql);
                preparedStatement.setInt(1, TariffManageApp.USER);
                preparedStatement.setInt(2, id);
                preparedStatement.setDate(3, new Date(System.currentTimeMillis() + (28 * 24 * 60 * 60 * 1000L)));
                preparedStatement.setDouble(4, resultSet.getDouble("OPTION_VALUE"));
                preparedStatement.executeUpdate();
                Tile newTile = MainController.mainController.initAddonsTile();
                newTile.setStyle(MainController.mainController.shadowStyleString);
                newTile.setOnMouseClicked(this);
                MainController.mainController.gridPaneDown.getChildren().remove(1);
                MainController.mainController.gridPaneDown.add(newTile, 1, 0);
                Tile tile = (Tile)MainController.mainController.gridPaneDown.getChildren().get(0);
                tile.setValue(Double.parseDouble(MainController.mainController.userData.get(1)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
