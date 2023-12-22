package com.example.airplaneshoot;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Objects;

public class GameOver {
    private final Scene scene;
    private final ImageView RestartButton;
    private final ImageView ReturnMainButton;
    private final ImageView exitButton;
    private final URL RestartURL = getClass().getResource("/Pictures/Restart.png");
    private final URL ReturnMainURL = getClass().getResource("/Pictures/ReturnMain.png");
    private final URL exitURL = getClass().getResource("/Pictures/Exit.png");
    private final URL RestartSelectURL = getClass().getResource("/Pictures/RestartSelect.png");
    private final URL ReturnMainSelectURL = getClass().getResource("/Pictures/ReturnMainSelect.png");
    private final URL exitSelectURL = getClass().getResource("/Pictures/ExitSelect.png");
    private String SelectButton = "Restart";

    public GameOver(Runnable onRestart, Runnable onReturnMain) {
        // Unset all the static variables-----------------------------------------------------------
        Store.Level = 1;
        Store.Score = 0;
        // Save the high score-----------------------------------------------------------------------
        int highScore = AirplaneGame.loadHighScore();
        if (Store.Score > highScore)
            AirplaneGame.saveHighScore(Store.Score);
        AirplaneGame.musicPlay("/Music/Player's Score.mp3");
        StackPane root = new StackPane();
        RestartButton = new ImageView(RestartURL.toString());
        ReturnMainButton = new ImageView(ReturnMainURL.toString());
        exitButton = new ImageView(exitURL.toString());
        RestartButton.setTranslateY(-200);
        ReturnMainButton.setTranslateY(-100);
        exitButton.setTranslateY(0);
        // Add background----------------------------------------------------------------------------
        URL Back = getClass().getResource("/Pictures/GameOver.png");
        ImageView BackView = new ImageView(Objects.requireNonNull(Back).toString());
        BackView.setFitWidth(AirplaneGame.SetWidth);
        BackView.setFitHeight(AirplaneGame.SetHeight);
        BackView.setPreserveRatio(false);
        root.getChildren().add(BackView);
        root.getChildren().addAll(RestartButton, ReturnMainButton, exitButton);
        scene = new Scene(root, AirplaneGame.SetWidth, AirplaneGame.SetHeight);
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                    AirplaneGame.KON("/Music/SelectOK.wav");
                    switch (SelectButton) {
                        case "Exit" -> SelectButton = "ReturnMain";
                        case "ReturnMain" -> SelectButton = "Restart";
                        case "Restart" -> SelectButton = "Exit";
                    }
                    updateButtonImages();
                    break;
                case DOWN:
                    AirplaneGame.KON("/Music/SelectOK.wav");
                    switch (SelectButton) {
                        case "Exit" -> SelectButton = "Restart";
                        case "ReturnMain" -> SelectButton = "Exit";
                        case "Restart" -> SelectButton = "ReturnMain";
                    }
                    updateButtonImages();
                    break;
                case Z:
                    AirplaneGame.KON("/Music/Start.wav");
                    switch (SelectButton) {
                        case "Restart" -> onRestart.run();
                        case "ReturnMain" -> onReturnMain.run();
                        case "Exit" -> System.exit(0);
                    }
                    break;
            }
        });
        updateButtonImages();
    }

    public Scene getScene() {
        return scene;
    }

    private void updateButtonImages() {
        switch (SelectButton) {
            case "Restart":
                RestartButton.setImage(new javafx.scene.image.Image(Objects.requireNonNull(RestartSelectURL).toString()));
                ReturnMainButton.setImage(new javafx.scene.image.Image(Objects.requireNonNull(ReturnMainURL).toString()));
                exitButton.setImage(new javafx.scene.image.Image(Objects.requireNonNull(exitURL).toString()));
                break;
            case "ReturnMain":
                RestartButton.setImage(new javafx.scene.image.Image(Objects.requireNonNull(RestartURL).toString()));
                ReturnMainButton.setImage(new javafx.scene.image.Image(Objects.requireNonNull(ReturnMainSelectURL).toString()));
                exitButton.setImage(new javafx.scene.image.Image(Objects.requireNonNull(exitURL).toString()));
                break;
            case "Exit":
                RestartButton.setImage(new javafx.scene.image.Image(Objects.requireNonNull(RestartURL).toString()));
                ReturnMainButton.setImage(new javafx.scene.image.Image(Objects.requireNonNull(ReturnMainURL).toString()));
                exitButton.setImage(new javafx.scene.image.Image(Objects.requireNonNull(exitSelectURL).toString()));
                break;
        }
    }

}
