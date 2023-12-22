package com.example.airplaneshoot;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Objects;

public class StartMenu {
    private final Scene scene;
    private final ImageView startButton;
    private final ImageView exitButton;
    private final URL startURL = getClass().getResource("/Pictures/Start.png");
    private final URL exitURL = getClass().getResource("/Pictures/Exit.png");
    private final URL startSelectURL = getClass().getResource("/Pictures/StartSelect.png");
    private final URL exitSelectURL = getClass().getResource("/Pictures/ExitSelect.png");
    private boolean isStartSelected = true;

    public StartMenu(Runnable onStart) {
        AirplaneGame.musicPlay("/Music/兽之智慧.mp3");
        StackPane root = new StackPane();
        // Draw background--------------------------------------------------------------------------
        URL mediaUrl = getClass().getResource("/Pictures/Background1.png");
        Image mediaImage = new Image(Objects.requireNonNull(mediaUrl).toString());
        ImageView mediaImageView = new ImageView(mediaImage);
        mediaImageView.setFitWidth(AirplaneGame.SetWidth);
        mediaImageView.setFitHeight(AirplaneGame.SetHeight);
        mediaImageView.setPreserveRatio(false);
        root.getChildren().add(mediaImageView);
        // Show high score---------------------------------------------------------------------------
        int highScore;
        highScore = AirplaneGame.loadHighScore();
        Text highScoreText = new Text("High Score: " + highScore);
        highScoreText.setTranslateX(400);
        highScoreText.setTranslateY(200);
        highScoreText.setStyle("-fx-font-family: 'Microsoft YaHei'; -fx-font-weight: bold;" +
                " -fx-font-size: 70px;-fx-fill: red;");
        root.getChildren().add(highScoreText);


        startButton = new ImageView(new Image(Objects.requireNonNull(startURL).toString()));
        exitButton = new ImageView(new Image(Objects.requireNonNull(exitURL).toString()));
        startButton.setTranslateY(0);
        startButton.setTranslateX(500);
        exitButton.setTranslateX(550);
        exitButton.setTranslateY(100);

        root.getChildren().addAll(startButton, exitButton);

        scene = new Scene(root, AirplaneGame.SetWidth, AirplaneGame.SetHeight);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    AirplaneGame.KON("/Music/SelectOK.wav");
                case DOWN:
                    AirplaneGame.KON("/Music/SelectOK.wav");
                    isStartSelected = !isStartSelected;
                    updateButtonImages();
                    break;
                case Z:
                    if (isStartSelected) {
                        AirplaneGame.KON("/Music/Start.wav");
                        onStart.run(); // 运行传入的 onStart Runnable
                    } else {
                        System.exit(0);
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
        if (isStartSelected) {
            startButton.setImage(new Image(Objects.requireNonNull(startSelectURL).toString()));
            exitButton.setImage(new Image(Objects.requireNonNull(exitURL).toString()));
        } else {
            startButton.setImage(new Image(Objects.requireNonNull(startURL).toString()));
            exitButton.setImage(new Image(Objects.requireNonNull(exitSelectURL).toString()));
        }
    }
}
