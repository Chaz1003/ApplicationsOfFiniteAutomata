package com.example.movingsprites;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class EndViewController {

    @FXML
    public AnchorPane end_scene;
    public Button btnExit;

    @FXML
    private Label scoreLabel;


    public void setScore(int score) {
        Image backgroundImage = new Image(getClass().getResource("/com/example/movingsprites/Images/space_background.png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        end_scene.setBackground(new Background(bgImage));

        scoreLabel.setText(String.valueOf(score));
    }

    public void exitGame(MouseEvent mouseEvent) {
        System.exit(0);
    }
}
