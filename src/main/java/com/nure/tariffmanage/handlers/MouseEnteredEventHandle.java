package com.nure.tariffmanage.handlers;

import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MouseEnteredEventHandle implements EventHandler<MouseEvent> {
    VBox vBox;
    public MouseEnteredEventHandle(VBox vBox){
        this.vBox = vBox;
    }
    @Override
    public void handle(MouseEvent event) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.3), vBox);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);
        scaleTransition.play();
    }
}
