package com.nure.tariffmanage;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.skins.BarChartItem;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.sql.Date;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import oracle.jdbc.proxy.annotation.Pre;
import oracle.jdbc.replay.driver.TxnReplayableArray;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.*;


public class MainController {


    @FXML
    private Button exitButton;
    @FXML
    private GridPane gridPaneUP;
    @FXML
    private Label helloLabel;
    @FXML
    private Label numberField;
    @FXML
    private GridPane gridPaneDown;

    public ArrayList<ArrayList<String>> addons;
    public ArrayList<String> tariffs;
    public ArrayList<String> userData;
    private Date tariffPaidUntil;
    @FXML
    private Hyperlink tariffField;
    private boolean isTariffActive = true;
    @FXML
    private Label unpaidLabel;
    @FXML
    private DatePicker beginningDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ListView<String> statisticsListView;
    @FXML
    private Label invalidDatesLabel;

    public void initialize() throws SQLException {
        //PHONE_NUMBER(0),BALANCE(1),TARIFF_NAME(2),TARIFF_COST(3),GIGABYTES(4),MINUTES_OUT(5),MINUTES_ABROAD(6),
        //GIGABYTES_LEFT(7),MINUTES_OUT_LEFT(8),MINUTES_ABROAD_LEFT(9),LOGIN(10)
        checkAddons();
        userData = getUserData();
        helloLabel.setText(userData.get(10) + "!");
        numberField.setText(userData.get(0));
        tariffField.setTooltip(new Tooltip("Reorder the tariff" + "(" + userData.get(3) + "UAH)"));
        if (tariffPaidUntil.before(new Date(System.currentTimeMillis()))) {
            if (!(Double.parseDouble(userData.get(1)) >= Double.parseDouble(userData.get(3)))) {
                unpaidLabel.setVisible(true);
                unpaidLabel.setText("Unpaid" + " (" + userData.get(3) + "UAH)");
                isTariffActive = false;
                clearTariffBalances();
            }
            reorderTariff();
        } else {
            initTiles();
        }
    }

    private void checkAddons() throws SQLException {
        String sql = "select PAID_UNTIL from CLIENT_OPTION WHERE client_id=?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        preparedStatement.setInt(1, TariffManageApp.USER);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            if ((new Date(System.currentTimeMillis())).after(resultSet.getDate("PAID_UNTIL"))) {
                resultSet.deleteRow();
            }
        }
    }

    private void clearTariffBalances() throws SQLException {
        String sqlClient = "select GIGABYTES_LEFT,MINUTES_OUT_LEFT,MINUTES_ABROAD_LEFT from clients where id=?";
        PreparedStatement statementClient = DBConnection.getConnection().prepareStatement(sqlClient, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        statementClient.setInt(1, TariffManageApp.USER);
        ResultSet resultSetClient = statementClient.executeQuery();
        if (!resultSetClient.next()) {
            System.err.println("Error tariff changing!");
            return;
        }
        resultSetClient.updateString("GIGABYTES_LEFT", "0");
        resultSetClient.updateString("MINUTES_OUT_LEFT", "0");
        resultSetClient.updateString("MINUTES_ABROAD_LEFT", "0");
        resultSetClient.updateRow();
        userData = getUserData();
    }

    private void initTiles() throws SQLException {
        String shadowStyleString = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 0);";

        tariffField.setText(userData.get(2));

        Tile tile;

        if (userData.get(7).equals("UNLIMITED")) {
            tile = infinityTileBuilder();
        } else if (isTariffActive) {
            tile = tileBuilder();
            tile.setValue(Double.parseDouble(userData.get(7)));
            tile.setMaxValue(Double.parseDouble(userData.get(4)));
            tile.setTitle(userData.get(7) + " out of " + userData.get(4) + " GB");
        } else {
            tile = tileBuilder();
            tile.setValue(0);
            tile.setMaxValue(0);
            tile.setTitle("0 out of 0 GB");
        }
        tile.setText("GB Left");
        tile.setStyle(shadowStyleString);

        gridPaneUP.add(tile, 0, 0);

        if (isTariffActive) {
            tile = infinityTileBuilder();
        } else {
            tile = tileBuilder();
            tile.setValue(0);
            tile.setMaxValue(0);
            tile.setTitle("0 out of 0 Min");
        }
        tile.setText("Minutes in network");
        tile.setStyle(shadowStyleString);

        gridPaneUP.add(tile, 1, 0);

        tile = tileBuilder();
        tile.setValue(Double.parseDouble(userData.get(8)));
        tile.setMaxValue(Double.parseDouble(userData.get(5)));
        tile.setTitle(userData.get(8) + " out of " + userData.get(5) + " Min");
        tile.setText("Minutes to other operators");
        tile.setStyle(shadowStyleString);

        gridPaneUP.add(tile, 2, 0);

        tile = tileBuilder();
        tile.setValue(Double.parseDouble(userData.get(9)));
        tile.setMaxValue(Double.parseDouble(userData.get(6)));
        tile.setTitle(userData.get(9) + " out of " + userData.get(6) + " Min");
        tile.setText("Minutes abroad");
        tile.setStyle(shadowStyleString);

        gridPaneUP.add(tile, 3, 0);

        tile = initAddonsTile();
        tile.setStyle(shadowStyleString);
        gridPaneDown.add(tile, 1, 0);

        tile = initBalanceTile();
        tile.setStyle(shadowStyleString);
        if (isTariffActive) {
            if (Double.parseDouble(userData.get(1)) < Double.parseDouble(userData.get(3))) {
                tile.setTextColor(Color.valueOf("D95757"));
                tile.setText("Next payment: " + tariffPaidUntil + ". You should have at least " + userData.get(3) + "UAH on your balance");
            } else {
                tile.setTextColor(Color.GREEN);
                tile.setText("Next payment: " + tariffPaidUntil + ", " + userData.get(3) + "UAH");
            }
        } else {
            tile.setTextColor(Color.valueOf("D95757"));
            tile.setText("Unpaid payment on: " + tariffPaidUntil + ", please Top-Up your balance");
        }
        tile.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Payment");
            alert.setHeaderText("Choose amount to Top-Up");
            alert.initStyle(StageStyle.UTILITY);

            ButtonType buttonTypeTwenty = new ButtonType("20UAH");
            ButtonType buttonTypeFifty = new ButtonType("50UAH");
            ButtonType buttonTypeHundred = new ButtonType("100UAH");
            ButtonType buttonTypeTwoHundred = new ButtonType("200UAH");
            ButtonType buttonTypeFiveHundred = new ButtonType("500UAH");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeTwenty, buttonTypeFifty, buttonTypeHundred,
                    buttonTypeTwoHundred, buttonTypeFiveHundred, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            try {
                if (result.get() == buttonTypeTwenty) {
                    String url20 = "https://www.portmone.com.ua/r3/perekaz-za-zapitom/6zfixBP";
                    Desktop.getDesktop().browse(new URI(url20));
                    topUp(20);
                } else if (result.get() == buttonTypeFifty) {
                    String url50 = "https://www.portmone.com.ua/r3/perekaz-za-zapitom/6zfixLP";
                    Desktop.getDesktop().browse(new URI(url50));
                    topUp(50);
                } else if (result.get() == buttonTypeHundred) {
                    String url100 = "https://www.portmone.com.ua/r3/perekaz-za-zapitom/6zfixQh";
                    Desktop.getDesktop().browse(new URI(url100));
                    topUp(100);
                } else if (result.get() == buttonTypeTwoHundred) {
                    String url200 = "https://www.portmone.com.ua/r3/perekaz-za-zapitom/6zfixWY";
                    Desktop.getDesktop().browse(new URI(url200));
                    topUp(200);
                } else if (result.get() == buttonTypeFiveHundred) {
                    String url500 = "https://www.portmone.com.ua/r3/perekaz-za-zapitom/6zfix-s";
                    Desktop.getDesktop().browse(new URI(url500));
                    topUp(500);
                }
            } catch (IOException | URISyntaxException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
        gridPaneDown.add(tile, 0, 0);
    }

    private void topUp(int val) throws SQLException {
        String clientSQL = "select Balance from clients where id =?";
        String costSQL = "select Tariff_Cost from Clients c inner join tariffs t on c.tariff_id = t.id where c.id =?";
        PreparedStatement clientPreparedStatement = DBConnection.getConnection().prepareStatement(clientSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        clientPreparedStatement.setInt(1, TariffManageApp.USER);
        PreparedStatement costPreparedStatement = DBConnection.getConnection().prepareStatement(costSQL);
        costPreparedStatement.setInt(1, TariffManageApp.USER);
        ResultSet costSet = costPreparedStatement.executeQuery();
        ResultSet resultSet = clientPreparedStatement.executeQuery();
        if (!resultSet.next()) {
            System.err.println("Error top-up!");
        } else {
            if (!isTariffActive) {
                costSet.next();
                if ((resultSet.getDouble("BALANCE") + val) >= costSet.getDouble("TARIFF_COST")) {
                    resultSet.updateDouble("BALANCE", (resultSet.getDouble("BALANCE") + val) - costSet.getDouble("TARIFF_COST"));
                    isTariffActive = true;
                    unpaidLabel.setVisible(false);
                    reorderTariff();
                }
                else{
                    resultSet.updateDouble("BALANCE", resultSet.getDouble("BALANCE") + val);
                }
            }
            else {
                resultSet.updateDouble("BALANCE", resultSet.getDouble("BALANCE") + val);
            }
            resultSet.updateRow();
            userData = getUserData();
            Tile tile = (Tile) gridPaneDown.getChildren().get(1);
            tile.setValue(resultSet.getDouble("BALANCE"));
            if (isTariffActive) {
                if (tile.getValue() > Double.parseDouble(userData.get(3))) {
                    tile.setTextColor(Color.GREEN);
                    tile.setText("Next payment: " + tariffPaidUntil + ", " + userData.get(3) + "UAH");
                } else {
                    tile.setTextColor(Color.valueOf("D95757"));
                    tile.setText("Next payment: " + tariffPaidUntil + ". You should have at least " + userData.get(3) + "UAH on your balance");
                }
            } else {
                tile.setTextColor(Color.valueOf("D95757"));
                tile.setText("Unpaid payment on: " + tariffPaidUntil + ", please Top-Up your balance");
            }
        }
        String sql = "INSERT INTO TOP_UPS(client_id, top_up_date, top_up_amount) values(?,?,?)";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);
        preparedStatement.setInt(1, TariffManageApp.USER);
        preparedStatement.setDate(2, new Date(System.currentTimeMillis()));
        preparedStatement.setDouble(3, val);
        preparedStatement.executeUpdate();
    }

    private Tile initBalanceTile() {
        return TileBuilder.create().skinType(Tile.SkinType.NUMBER).value(Double.parseDouble(userData.get(1)))
                .textColor(Color.valueOf("D95757"))
                .valueColor(Color.valueOf("03a4f5"))
                .description("UAH")
                .title("Balance (Click here to Top-Up)")
                .textSize(Tile.TextSize.BIGGER)
                .titleColor(Color.BLACK)
                .descriptionColor(Color.BLACK)
                .backgroundColor(Color.WHITE)
                .build();
    }

    private Tile initAddonsTile() throws SQLException {
        List<BarChartItem> barChartItems = new ArrayList<>();
        HashMap<ArrayList<String>, Date> map = getUserAddons();
        for (Map.Entry<ArrayList<String>, Date> dateArrayListEntry : map.entrySet()) {
            BarChartItem barChartItem = new BarChartItem();
            double maxValue = Double.parseDouble(dateArrayListEntry.getKey().get(2));
            double value = Double.parseDouble(dateArrayListEntry.getKey().get(1));
            barChartItem.setValue(100 * value / maxValue);
            double id = Double.parseDouble(dateArrayListEntry.getKey().get(0));
            if (id == 1 || id == 2) {
                barChartItem.setFormatString(maxValue + " GB");
                barChartItem.setName("  " + value + " GB Left, until: " + dateArrayListEntry.getValue());
            } else if (id == 3 || id == 4) {
                barChartItem.setFormatString(Math.round(maxValue) + " Min");
                barChartItem.setName("  " + Math.round(value) + " Min Left, until: " + dateArrayListEntry.getValue());
            } else if (id == 5) {
                barChartItem.setFormatString(Math.round(maxValue) + "Min Abroad");
                barChartItem.setName("  " + Math.round(value) + " Min Abroad Left, until: " + dateArrayListEntry.getValue());
            }
            barChartItem.setBarColor(Color.valueOf("1edbb6"));
            barChartItem.setBarBackgroundColor(Color.valueOf("b1f2e5"));
            barChartItems.add(barChartItem);
        }
        return TileBuilder.create().skinType(Tile.SkinType.BAR_CHART).barChartItems(barChartItems)
                .title("Add-ons")
                .titleColor(Color.BLACK)
                .valueColor(Color.BLACK)
                .textColor(Color.BLACK)
                .titleAlignment(TextAlignment.CENTER)
                .textSize(Tile.TextSize.BIGGER)
                .backgroundColor(Color.WHITE)
                .build();
    }

    @FXML
    public void logOutPressed() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("");
        alert.setContentText("Log out?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            UserSessionWriter.saveDataToFile(0);
            stage.close();
            TariffManageApp.initLoginView(stage);
        }
    }

    @FXML
    public void exitPressed() {
        if (TariffManageApp.REMEMBER) {
            UserSessionWriter.saveDataToFile(TariffManageApp.USER);
        } else {
            UserSessionWriter.saveDataToFile(0);
        }
        Platform.exit();
    }

    private ArrayList<String> getUserData() throws SQLException {
        String sql = "select * from clients C INNER JOIN tariffs T ON C.TARIFF_ID = T.ID WHERE C.ID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);
        preparedStatement.setInt(1, TariffManageApp.USER);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        tariffPaidUntil = resultSet.getDate("TARIFF_PAID_UNTIL");
        return new ArrayList<>(List.of(resultSet.getString("PHONE_NUMBER"), resultSet.getString("BALANCE"), resultSet.getString("TARIFF_NAME"),
                resultSet.getString("TARIFF_COST"), resultSet.getString("GIGABYTES"),
                resultSet.getString("MINUTES_OUT"), resultSet.getString("MINUTES_ABROAD"),
                resultSet.getString("GIGABYTES_LEFT"), resultSet.getString("MINUTES_OUT_LEFT"),
                resultSet.getString("MINUTES_ABROAD_LEFT"), resultSet.getString("LOGIN")));
    }

    private HashMap<ArrayList<String>, Date> getUserAddons() throws SQLException {
        String sql = "select * from CLIENT_OPTION CO INNER JOIN OPTIONS O ON CO.OPTION_ID = O.ID WHERE CO.CLIENT_ID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);
        preparedStatement.setInt(1, TariffManageApp.USER);
        ResultSet resultSet = preparedStatement.executeQuery();
        HashMap<ArrayList<String>, Date> map = new HashMap<>();
        while (resultSet.next()) {
            map.put(new ArrayList<>(List.of(resultSet.getString("OPTION_ID"), resultSet.getString("VALUE_LEFT"),
                    resultSet.getString("OPTION_VALUE"))), resultSet.getDate("PAID_UNTIL"));
        }
        return map;
    }

    private Tile tileBuilder() {
        return TileBuilder.create().skinType(Tile.SkinType.CIRCULAR_PROGRESS)
                .backgroundColor(Color.WHITE)
                .barBackgroundColor(Color.valueOf("b1f2e5"))
                .barColor(Color.valueOf("1edbb6"))
                .textSize(Tile.TextSize.BIGGER)
                .textColor(Color.BLACK)
                .textAlignment(TextAlignment.CENTER)
                .valueColor(Color.BLACK)
                .titleAlignment(TextAlignment.CENTER)
                .titleColor(Color.BLACK)
                .unitColor(Color.BLACK)
                .valueVisible(false)
                .build();
    }

    private Tile infinityTileBuilder() {
        return TileBuilder.create().skinType(Tile.SkinType.IMAGE)
                .backgroundColor(Color.WHITE)
                .textSize(Tile.TextSize.BIGGER)
                .textColor(Color.BLACK).textAlignment(TextAlignment.CENTER)
                .image(new Image("C:\\JavaProjects\\TariffManage\\src\\main\\resources\\com\\nure\\tariffmanage\\infinity.png"))
                .build();
    }

    @FXML
    public void reorderTariffButton() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Reorder your tariff?");
        alert.initStyle(StageStyle.UTILITY);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (Double.parseDouble(userData.get(1)) >= Double.parseDouble(userData.get(3))) {
                Date temp = new Date(System.currentTimeMillis() + 24 * 27 * 60 * 60 * 1000L);
                if (temp.after(tariffPaidUntil)) {
                    reorderTariff();
                } else {
                    Alert alreadyReorderedTariff = new Alert(Alert.AlertType.ERROR);
                    alreadyReorderedTariff.setTitle("Error");
                    alreadyReorderedTariff.setHeaderText("Tariff already reordered, try tomorrow");
                    alreadyReorderedTariff.initStyle(StageStyle.UTILITY);
                    alreadyReorderedTariff.show();
                }
            } else {
                Alert noMoneyAlert = new Alert(Alert.AlertType.ERROR);
                noMoneyAlert.setTitle("Error");
                noMoneyAlert.setHeaderText("Not enough money to reorder tariff, you should have " + userData.get(3) + "UAH on balance to proceed");
                noMoneyAlert.initStyle(StageStyle.UTILITY);
                noMoneyAlert.show();
            }
        }
    }

    private void reorderTariff() throws SQLException {
        gridPaneUP.getChildren().clear();
        gridPaneDown.getChildren().clear();
        if (isTariffActive) {
            updateUserTariffData();
        }
        initTiles();
    }

    private void updateUserTariffData() throws SQLException {
        String sqlClient = "select Balance, Tariff_paid_until, GIGABYTES_LEFT,MINUTES_OUT_LEFT,MINUTES_ABROAD_LEFT from clients where id=?";
        String sqlTariff = "select * from Tariffs T INNER JOIN CLIENTS C ON C.tariff_id = T.id where C.id=?";
        PreparedStatement statementClient = DBConnection.getConnection().prepareStatement(sqlClient, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        PreparedStatement statementTariff = DBConnection.getConnection().prepareStatement(sqlTariff);
        statementClient.setInt(1, TariffManageApp.USER);
        statementTariff.setInt(1, TariffManageApp.USER);
        ResultSet resultSetClient = statementClient.executeQuery();
        ResultSet resultSetTariff = statementTariff.executeQuery();
        if (!resultSetClient.next() & !resultSetTariff.next()) {
            System.err.println("Error tariff changing!");
            return;
        }
        Date date = new Date(System.currentTimeMillis() + (28 * 24 * 60 * 60 * 1000L));
        resultSetClient.updateDate("TARIFF_PAID_UNTIL", date);
        resultSetClient.updateDouble("BALANCE", resultSetClient.getDouble("BALANCE") - resultSetTariff.getDouble("TARIFF_COST"));
        resultSetClient.updateString("GIGABYTES_LEFT", resultSetTariff.getString("GIGABYTES"));
        resultSetClient.updateString("MINUTES_OUT_LEFT", resultSetTariff.getString("MINUTES_OUT"));
        resultSetClient.updateString("MINUTES_ABROAD_LEFT", resultSetTariff.getString("MINUTES_ABROAD"));
        resultSetClient.updateRow();
        userData = getUserData();
        tariffField.setTooltip(new Tooltip("Reorder the tariff" + "(" + userData.get(3) + "UAH)"));
    }

    @FXML
    public void dateChanging() {
        beginningDatePicker.setStyle("");
        endDatePicker.setStyle("");
        invalidDatesLabel.setText("");
    }

    public void showTariffs(ActionEvent actionEvent) throws SQLException {
        if (!checkDates()) {
            return;
        }
        statisticsListView.getItems().clear();
        String sql = "select t.TARIFF_NAME, u.START_DATE, u.END_DATE from usage_history u inner join tariffs t on u.tariff_id=t.id where u.client_id=?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);
        preparedStatement.setInt(1, TariffManageApp.USER);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<TariffUsageHistory> list = new ArrayList<>();
        while (resultSet.next()) {
            if (resultSet.getDate(3) == null || (resultSet.getDate(2).toLocalDate().plusDays(1).isAfter(beginningDatePicker.getValue()) &&
                    resultSet.getDate(2).toLocalDate().minusDays(1).isBefore(endDatePicker.getValue())) ||
                    (resultSet.getDate(3).toLocalDate().plusDays(1).isAfter(beginningDatePicker.getValue()) &&
                            resultSet.getDate(3).toLocalDate().minusDays(1).isBefore(endDatePicker.getValue())) ||
                    (resultSet.getDate(2).toLocalDate().plusDays(1).isAfter(beginningDatePicker.getValue()) &&
                            resultSet.getDate(2).toLocalDate().minusDays(1).isBefore(endDatePicker.getValue())) &&
                            (resultSet.getDate(3).toLocalDate().plusDays(1).isAfter(beginningDatePicker.getValue()) &&
                                    resultSet.getDate(3).toLocalDate().minusDays(1).isBefore(endDatePicker.getValue())) ||
                    (resultSet.getDate(2).toLocalDate().minusDays(1).isBefore(beginningDatePicker.getValue()) &&
                            resultSet.getDate(3).toLocalDate().plusDays(1).isAfter(endDatePicker.getValue()))) {
                if (resultSet.getDate(3) == null) {
                    if ((resultSet.getDate(2).toLocalDate().plusDays(1).isAfter(beginningDatePicker.getValue()) &&
                            resultSet.getDate(2).toLocalDate().minusDays(1).isBefore(endDatePicker.getValue())) ||
                            (LocalDate.now().plusDays(1).isAfter(beginningDatePicker.getValue()) &&
                                    LocalDate.now().minusDays(1).isBefore(endDatePicker.getValue())) ||
                            (resultSet.getDate(2).toLocalDate().plusDays(1).isAfter(beginningDatePicker.getValue()) &&
                                    resultSet.getDate(2).toLocalDate().minusDays(1).isBefore(endDatePicker.getValue())) &&
                                    (LocalDate.now().plusDays(1).isAfter(beginningDatePicker.getValue()) &&
                                            LocalDate.now().minusDays(1).isBefore(endDatePicker.getValue())) ||
                            (resultSet.getDate(2).toLocalDate().minusDays(1).isBefore(beginningDatePicker.getValue()) &&
                                    LocalDate.now().plusDays(1).isAfter(endDatePicker.getValue()))) {
                        list.add(new TariffUsageHistory(resultSet.getString(1), resultSet.getDate(2),null));
                    }
                    continue;
                }
                list.add(new TariffUsageHistory(resultSet.getString(1), resultSet.getDate(2),
                        resultSet.getDate(3)));
            }
        }
        List<String> stringList = new ArrayList<>();
        list = list.stream().sorted().toList();
        for (TariffUsageHistory tariffUsageHistory : list) {
            if(tariffUsageHistory.endDate() == null) {
                stringList.add(String.format("Tariff: %s, Current tariff from  %s-%s-%s", tariffUsageHistory.tariffName(),
                        tariffUsageHistory.beginingDate().toLocalDate().getDayOfMonth(),
                        tariffUsageHistory.beginingDate().toLocalDate().getMonth(),
                        tariffUsageHistory.beginingDate().toLocalDate().getYear()));
            }
            else{
                stringList.add(String.format("Tariff: %s, From %s-%s-%s to %s-%s-%s", tariffUsageHistory.tariffName(),
                        tariffUsageHistory.beginingDate().toLocalDate().getDayOfMonth(),
                        tariffUsageHistory.beginingDate().toLocalDate().getMonth(),
                        tariffUsageHistory.beginingDate().toLocalDate().getYear(),
                        tariffUsageHistory.endDate().toLocalDate().getDayOfMonth(),
                        tariffUsageHistory.endDate().toLocalDate().getMonth(),
                        tariffUsageHistory.endDate().toLocalDate().getYear()));
            }
        }
        statisticsListView.getItems().addAll(stringList);
    }

    public void showTopUps(ActionEvent actionEvent) throws SQLException {
        if (!checkDates()) {
            return;
        }
        statisticsListView.getItems().clear();
        String sql = "select top_up_amount, top_up_date from TOP_UPS Where client_id=?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);
        preparedStatement.setInt(1, TariffManageApp.USER);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<TopUpsHistory> list = new ArrayList<>();
        while (resultSet.next()) {
            if (resultSet.getDate(2).toLocalDate().plusDays(1).isAfter(beginningDatePicker.getValue()) &&
                    resultSet.getDate(2).toLocalDate().minusDays(1).isBefore(endDatePicker.getValue())) {
                list.add(new TopUpsHistory(resultSet.getDouble(1),resultSet.getDate(2)));
            }
        }
        List<String> stringList = new ArrayList<>();
        list = list.stream().sorted().toList();
        for (TopUpsHistory topUpsHistory : list) {
            stringList.add(String.format("Date: %s-%s-%s : +%sUAH", topUpsHistory.date().toLocalDate().getDayOfMonth(),
                    topUpsHistory.date().toLocalDate().getMonth(),
                    topUpsHistory.date().toLocalDate().getYear(),
                    topUpsHistory.amount()));
        }
        statisticsListView.getItems().addAll(stringList);
    }

    private boolean checkDates() {
        if (beginningDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            invalidDatesLabel.setText("Fill both dates");
            return false;
        }
        if (beginningDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            invalidDatesLabel.setText("End date can not be after beginning date");
            return false;
        }
        return true;
    }
}
