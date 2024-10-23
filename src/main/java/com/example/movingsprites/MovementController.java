package com.example.movingsprites;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MovementController {

    private final BooleanProperty wPressed = new SimpleBooleanProperty();
    private final BooleanProperty aPressed = new SimpleBooleanProperty();
    private final BooleanProperty sPressed = new SimpleBooleanProperty();
    private final BooleanProperty dPressed = new SimpleBooleanProperty();
    private final BooleanProperty spacePressed = new SimpleBooleanProperty();
    private final BooleanProperty shiftPressed = new SimpleBooleanProperty();

    private int movementVariable = 2;
    private double sceneWidth;
    private double sceneHeight;
    private int score = 0;

    @FXML
    private ImageView sprite;

    @FXML
    private AnchorPane scene;

    private Label scoreLabel;
    private Label healthLabel;
    private Label timerLabel;

    private final ArrayList<Rectangle> lasers = new ArrayList<>();

    private ImageView enemy;
    private Rectangle healthBar;
    // Initial health
    private double enemyHealth = 100;
    private double maxEnemyHealth = 100;

    private double enemyDirectionX = 1;
    private double enemyDirectionY = 1;

    private long lastLaser = 0;
    private static final long LASER_INTERVAL = 300000000;

    private long startTime;
    private final long duration = 60 * 1000;


    public void makeMovable(ImageView sprite, AnchorPane scene) {
        this.sprite = sprite;
        this.scene = scene;

        this.sceneWidth = scene.getPrefWidth();
        this.sceneHeight = scene.getPrefHeight();

        addLabels();

        startTimer();
        addEnemy();

        movementSetup();
        timer.start();
    }

    private void addLabels() {
        // Score label
        scoreLabel = new Label("Score: 0");
        scoreLabel.setLayoutX(10);
        scoreLabel.setLayoutY(10);
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        // Health label
        healthLabel = new Label("Enemy Health: 100");
        healthLabel.setLayoutX(200);
        healthLabel.setLayoutY(10);
        healthLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        // Timer label
        timerLabel = new Label("Time: 60");
        timerLabel.setLayoutX(450);
        timerLabel.setLayoutY(10);
        timerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        scene.getChildren().addAll(scoreLabel, healthLabel, timerLabel);
    }

    // Add an enemy
    private void addEnemy() {
        // Set the enemy's properties
        enemy = new ImageView(new Image(getClass().getResource("/com/example/movingsprites/Images/meteor.png").toExternalForm()));
        enemy.setFitWidth(50);
        enemy.setFitHeight(50);
        enemy.setLayoutX(Math.random() * (sceneWidth - 50));
        enemy.setLayoutY(Math.random() * (sceneHeight - 50));

        // Enemy's health bar
        healthBar = new Rectangle(100, 10);
        healthBar.setFill(Color.GREEN);
        healthBar.setLayoutX(enemy.getLayoutX());
        healthBar.setLayoutY(enemy.getLayoutY() - 15);

        enemyHealth = maxEnemyHealth;

        scene.getChildren().addAll(enemy, healthBar);

        updateEnemyHealth();
    }

    // Update enemy health bar when hit by a laser
    private void updateEnemyHealth() {
        double healthPercentage = enemyHealth / maxEnemyHealth;
        healthBar.setWidth(healthPercentage * 100);

        // Change health bar color to yellow when HP is half
        if (healthPercentage < 0.5) {
            healthBar.setFill(Color.YELLOW);
        }
        // Change health bar color to yellow when HP is < 20%
        if (healthPercentage < 0.2) {
            healthBar.setFill(Color.RED);
        }

        updateHealthLabel();

        // Spawn another enemy with +100 health
        if (enemyHealth <= 0) {
            scene.getChildren().removeAll(enemy, healthBar);
            maxEnemyHealth += 100;
            addEnemy();
        }
    }

    // Update the health label
    private void updateHealthLabel() {
        healthLabel.setText("Enemy Health: " + (int) enemyHealth);
    }

    // Update the score label
    private void updateScoreLabel() {
        score += 10;
        scoreLabel.setText("Score: " + score);
    }

    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long timestamp) {
            double newX = sprite.getLayoutX();
            double newY = sprite.getLayoutY();

            // Handle user movement
            if (wPressed.get()) {
                newY -= movementVariable;
            }

            if (sPressed.get()) {
                newY += movementVariable;
            }

            if (aPressed.get()) {
                newX -= movementVariable;
            }

            if (dPressed.get()) {
                newX += movementVariable;
            }

            // Speed boost when shift is pressed
            if (shiftPressed.get()) {
                movementVariable = 4;
            } else {
                movementVariable = 2;
            }

            // Restrict player boundaries
            if (newX >= 0 && newX <= sceneWidth - sprite.getFitWidth()) {
                sprite.setLayoutX(newX);
            }
            if (newY >= 0 && newY <= sceneHeight - sprite.getFitHeight()) {
                sprite.setLayoutY(newY);
            }

            if (spacePressed.get() && (timestamp - lastLaser) >= LASER_INTERVAL) {
                shootLaser();
                lastLaser = timestamp;
            }
            // Handle laser movement and collision check
            Iterator<Rectangle> iterator = lasers.iterator();
            while (iterator.hasNext()) {
                Rectangle laser = iterator.next();
                laser.setLayoutX(laser.getLayoutX() + 5);

                // Check for collision with enemy
                if (laser.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                    enemyHealth -= 10;
                    updateEnemyHealth();
                    updateScoreLabel();
                    // Remove laser from scene to avoid hitting the enemy multiple times
                    iterator.remove();
                    scene.getChildren().remove(laser);
                }
            }

            moveEnemy();
        }
    };

    private void movementSetup() {

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> wPressed.set(true);
                case A -> aPressed.set(true);
                case S -> sPressed.set(true);
                case D -> dPressed.set(true);
                case SPACE -> {
                    spacePressed.set(true);
                    shootLaser();
                }
                // Set sprite to sprite with boosters
                case SHIFT -> {
                    sprite.setImage(new Image(getClass().getResource("/com/example/movingsprites/sprites/starship_boost.png").toExternalForm()));
                    shiftPressed.set(true);
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> wPressed.set(false);
                case A -> aPressed.set(false);
                case S -> sPressed.set(false);
                case D -> dPressed.set(false);
                case SPACE -> spacePressed.set(false);
                // Set the sprite image to the default one (no booster)
                case SHIFT -> {
                    sprite.setImage(new Image(getClass().getResource("/com/example/movingsprites/sprites/starship.png").toExternalForm()));
                    shiftPressed.set(false);
                }
            }
        });
    }

    private void shootLaser() {
        // Violet rectangle representing the laser
        Rectangle laser = new Rectangle(10, 5, Color.VIOLET);

        // Make the laser shoot out from the front of the sprite
        laser.setLayoutY(sprite.getLayoutY() + sprite.getFitHeight() / 2 - 2.5);
        laser.setLayoutX(sprite.getLayoutX() + sprite.getFitWidth());

        lasers.add(laser);
        scene.getChildren().add(laser);
    }

    private void moveEnemy() {
        double enemySpeed = 1.5;
        double newX = enemy.getLayoutX() + enemySpeed * enemyDirectionX;
        double newY = enemy.getLayoutY() + enemySpeed * enemyDirectionY;

        if (newX <= 0 || newX >= sceneWidth - enemy.getFitWidth()) {
            enemyDirectionX *= -1;
        }
        if (newY <= 0 || newY >= sceneHeight - enemy.getFitHeight()) {
            enemyDirectionY *= -1;
        }

        enemy.setLayoutX(newX);
        enemy.setLayoutY(newY);

        healthBar.setLayoutX(enemy.getLayoutX());
        healthBar.setLayoutY(enemy.getLayoutY() - 15);
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();

        AnimationTimer gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long remainingTime = (duration - elapsedTime) / 1000;

                timerLabel.setText("Time: " + remainingTime);

                if (elapsedTime >= duration) {
                    stopGame();
                    this.stop();
                }
            }
        };
        gameTimer.start();
    }

    private void stopGame() {
        timer.stop();


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movingsprites/end-view.fxml"));
            Parent endView = loader.load();

            // Pass the score
            EndViewController endController = loader.getController();
            endController.setScore(score);

            // Set end-view scene
            Stage stage = (Stage) scene.getScene().getWindow(); // Get current stage
            Scene endScene = new Scene(endView);
            stage.setScene(endScene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
