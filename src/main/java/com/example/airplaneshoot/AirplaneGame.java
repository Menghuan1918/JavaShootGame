package com.example.airplaneshoot;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AirplaneGame extends Application {
    // Set the window size-----------------------------------------------------------------------------------------------
    static int SetWidth = 1500;
    static int SetHeight = 844;
    private Stage primaryStage;
    private StartMenu startMenu;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.startMenu = new StartMenu(this::startGame);
        primaryStage.setTitle("Shooting Game");
        primaryStage.setScene(startMenu.getScene());
        primaryStage.show();
    }

    public static MediaPlayer backPlayer;
    public static Text MusicText = new Text();

    public void startGame() {
        AirplaneGame.musicPlay("/Music/世界万物皆可爱.mp3");
        StackPane root = new StackPane();
        // Add the background-------------------------------------------------------------------------------------------
        URL mediaUrl = getClass().getResource("/Pictures/Background2.mp4");
        Media media = new Media(Objects.requireNonNull(mediaUrl).toString());
        backPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(backPlayer);
        mediaView.setFitWidth(SetWidth);
        mediaView.setFitHeight(SetHeight);
        mediaView.setPreserveRatio(false);
        root.getChildren().add(mediaView);
        backPlayer.setVolume(0);
        backPlayer.setOnError(() -> System.out.println("Media Player Error: " + backPlayer.getError()));
        backPlayer.play();

        // Load the image-----------------------------------------------------------------------------------------------
        Map<String, Image> Images = new HashMap<>();
        Canvas canvas = new Canvas(SetWidth, SetHeight);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        URL PlayerUrl = getClass().getResource("/Pictures/Reimu.png");
        Image airplaneImage = new Image(Objects.requireNonNull(PlayerUrl).toString());
        Images.put("airplaneImage", airplaneImage);
        URL PlayerRightUrl = getClass().getResource("/Pictures/ReimuRight.png");
        Image airplaneRightImage = new Image(Objects.requireNonNull(PlayerRightUrl).toString());
        Images.put("airplaneRightImage", airplaneRightImage);
        URL PlayerLeftUrl = getClass().getResource("/Pictures/ReimuLeft.png");
        Image airplaneLeftImage = new Image(Objects.requireNonNull(PlayerLeftUrl).toString());
        Images.put("airplaneLeftImage", airplaneLeftImage);
        URL EnemyUrl = getClass().getResource("/Pictures/Maoyu.png");
        Image enemyImage = new Image(Objects.requireNonNull(EnemyUrl).toString());
        Images.put("enemyImage", enemyImage);
        URL SlowMode = getClass().getResource("/Pictures/SlowMode.png");
        Image slowModeImage = new Image(Objects.requireNonNull(SlowMode).toString());
        Images.put("slowModeImage", slowModeImage);
        URL ShootUrl = getClass().getResource("/Pictures/Shoot.png");
        Image shootImage = new Image(Objects.requireNonNull(ShootUrl).toString());
        Images.put("shootImage", shootImage);
        URL DanmuUrl = getClass().getResource("/Pictures/Danmu.png");
        Image danmuImage = new Image(Objects.requireNonNull(DanmuUrl).toString());
        Images.put("danmuImage", danmuImage);
        Scene scene = new Scene(root, SetWidth, SetHeight);
        // Text display--------------------------------------------------------------------------------------------------
        //HP
        Map<String, Text> Texts = new HashMap<>();
        Text PlayerText = new Text();
        PlayerText.setText("Player HP  ❤ × 3");
        PlayerText.setStyle("-fx-font-family: 'Microsoft YaHei'; -fx-font-weight: bold;" +
                " -fx-font-size: 30px;-fx-fill: red;");
        PlayerText.setTranslateX(-600);
        PlayerText.setTranslateY(400);
        root.getChildren().add(PlayerText);
        Texts.put("PlayerText", PlayerText);
        //Score
        Text ScoreText = new Text();
        ScoreText.setText("Score: 0");
        ScoreText.setStyle("-fx-font-family: 'Microsoft YaHei'; -fx-font-weight: bold;" +
                " -fx-font-size: 30px;-fx-fill: yellow;");
        ScoreText.setTranslateX(640);
        ScoreText.setTranslateY(-400);
        root.getChildren().add(ScoreText);
        Texts.put("ScoreText", ScoreText);
        //GetCard
        Text GetCardText = new Text();
        GetCardText.setText("GetCard: 90%");
        GetCardText.setStyle("-fx-font-family: 'Microsoft YaHei'; -fx-font-weight: bold;" +
                " -fx-font-size: 30px;-fx-fill: yellow;");
        GetCardText.setTranslateX(600);
        GetCardText.setTranslateY(400);
        root.getChildren().add(GetCardText);
        Texts.put("GetCardText", GetCardText);
        //Music
        MusicText.setText("Music: ");
        MusicText.setStyle("-fx-font-family: 'Microsoft YaHei';-fx-font-weight: bold;" +
                " -fx-font-size: 20px;-fx-fill: white;");
        MusicText.setTranslateX(-600);
        MusicText.setTranslateY(-400);
        //root.getChildren().add(MusicText);
        // Not show in the game, as it is not finished-------------------------------------------------------------------
        Texts.put("MusicText", MusicText);
        // Load the store------------------------------------------------------------------------------------------------
        Store store = new Store();
        // Key control---------------------------------------------------------------------------------------------------
        AirplaneControl airplaneControl = new AirplaneControl(gc, canvas, store, Images, Texts, this::gameOver);
        airplaneControl.start();
        scene.setOnKeyPressed(airplaneControl::handleKeyPressed);
        scene.setOnKeyReleased(airplaneControl::handleKeyReleased);
        primaryStage.setScene(scene);
    }

    public void gameOver() {
        GameOver gameOver = new GameOver(this::startGame, this::backToStartMenu);
        primaryStage.setScene(gameOver.getScene());
    }

    public void backToStartMenu() {
        primaryStage.setScene(startMenu.getScene());
    }

    public static MediaPlayer mediaPlayer;
    public static String NowPlay = "1";

    public static void musicPlay(String FileURL) {
        if (!NowPlay.equals(FileURL)) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            URL mediaUrl = AirplaneGame.class.getResource(FileURL);
            Media media = new Media(Objects.requireNonNull(mediaUrl).toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
            NowPlay = FileURL;
            MusicText.setText("Music: " + FileURL.substring(FileURL.lastIndexOf("/") + 1));
            System.out.println("Music: " + FileURL.substring(FileURL.lastIndexOf("/") + 1));
        }
    }

    private static final String SCORE_FILE_NAME = "Score.txt";

    private static Path getScoreFilePath() {
        return Paths.get(System.getProperty("user.home"), SCORE_FILE_NAME);
    }

    public static void saveHighScore(int highScore) {
        try (BufferedWriter writer = Files.newBufferedWriter(getScoreFilePath())) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.err.println("Error while writing to the high score file: " + e.getMessage());
        }
    }

    public static int loadHighScore() {
        try {
            String content = new String(Files.readAllBytes(getScoreFilePath()));
            return Integer.parseInt(content.trim());
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error while reading the high score file: " + e.getMessage());
            return 0; // If the file does not exist, return 0
        }
    }

    public static URL KONURL;
    static ExecutorService MusicPool = Executors.newVirtualThreadPerTaskExecutor();
    public static void KON(String FileURL){
        KONURL = AirplaneGame.class.getResource(FileURL);
        MusicPool.submit(Music);
    }
    static Runnable Music = () -> {
        Media media = new Media(Objects.requireNonNull(KONURL).toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    };

    public static void main(String[] args) {
        launch(args);
    }
}
