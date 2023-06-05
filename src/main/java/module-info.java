module com.nure.tariffmanage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires ojdbc10;
    requires eu.hansolo.tilesfx;
    requires eu.hansolo.toolboxfx;
    requires eu.hansolo.fx.heatmap;
    requires eu.hansolo.fx.countries;
    requires eu.hansolo.toolbox;
    requires javafx.media;
    requires javafx.web;
    requires javafx.swing;
    requires AnimateFX;
    requires org.apache.commons.codec;


    opens com.nure.tariffmanage to javafx.fxml;
    exports com.nure.tariffmanage;
    exports com.nure.tariffmanage.utill;
    opens com.nure.tariffmanage.utill to javafx.fxml;
    exports com.nure.tariffmanage.handlers;
    opens com.nure.tariffmanage.handlers to javafx.fxml;
    exports com.nure.tariffmanage.historyObjects;
    opens com.nure.tariffmanage.historyObjects to javafx.fxml;
}