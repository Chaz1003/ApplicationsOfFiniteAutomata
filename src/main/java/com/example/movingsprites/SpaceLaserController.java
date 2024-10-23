package com.example.movingsprites;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SpaceLaserController implements Initializable {

    @FXML
    private AnchorPane scene;

    @FXML
    private ImageView starship;

    private MovementController movementController = new MovementController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Set bg img to space_background.png
        Image backgroundImage = new Image(getClass().getResource("/com/example/movingsprites/Images/space_background.png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        scene.setBackground(new Background(bgImage));

        movementController.makeMovable(starship, scene);
    }
}