package com.nure.tariffmanage.handlers;

import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MouseExitedEventHandle implements EventHandler<MouseEvent> {
    VBox vBox;
    public MouseExitedEventHandle(VBox vBox){
        this.vBox = vBox;
    }
    @Override
    public void handle(MouseEvent event) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.3), vBox);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();
    }
}
