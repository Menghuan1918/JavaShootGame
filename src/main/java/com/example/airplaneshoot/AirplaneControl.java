package com.example.airplaneshoot;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AirplaneControl extends AnimationTimer {
    private final GraphicsContext gc;
    public double playerX, playerY;
    private Canvas canvas;
    private long lastUpdate = 0;
    private final Set<KeyCode> keysPressed = new HashSet<>();
    // Below are the images-------------------------------------------------------------------------------------------
    private final Image airplaneLeftImage, airplaneRightImage;
    private Image airplaneImage;
    private final Image enemyImage, slowModeImage, ShootImage, DanmuImage;
    // Below are the texts---------------------------------------------------------------------------------------------
    private Text PlayerText, ScoreText, GetCardText;
    public Store store;
    double TargetEnemyCount = 0;
    double TargetShootCount = 0;
    double TargetDanmuCount = 0;
    boolean SpecialEnemy;
    boolean ShootPress;
    // Some constants--------------------------------------------------------------------------------------------------
    int DanmuDamage = 1;
    int PlayerSpeed;
    boolean slowMode;
    // The GameOver Scene----------------------------------------------------------------------------------------------
    private final Runnable GameOver;

    public AirplaneControl(GraphicsContext gc, Canvas canvas, Store store, Map<String, Image> Images,
                           Map<String, Text> Texts, Runnable GameOver) {
        this.gc = gc;
        this.playerX = 500; // Set the initial position
        this.playerY = 500; // Set the initial position
        this.canvas = canvas;
        this.store = store;
        this.airplaneImage = Images.get("airplaneImage");
        this.enemyImage = Images.get("enemyImage");
        this.slowModeImage = Images.get("slowModeImage");
        this.ShootImage = Images.get("shootImage");
        this.DanmuImage = Images.get("danmuImage");
        this.airplaneLeftImage = Images.get("airplaneLeftImage");
        this.airplaneRightImage = Images.get("airplaneRightImage");
        this.PlayerText = Texts.get("PlayerText");
        this.ScoreText = Texts.get("ScoreText");
        this.GetCardText = Texts.get("GetCardText");
        this.GameOver = GameOver;
    }

    public void handleKeyPressed(KeyEvent event) {
        keysPressed.add(event.getCode());
    }

    public void handleKeyReleased(KeyEvent event) {
        keysPressed.remove(event.getCode());
    }

    private final Random random = new Random();

    // Create a thread pool to handle the tasks-------------------------------------------------------------------------
    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Object Lock_Enemy = new Object();
    private final Object Lock_Shoot = new Object();
    private final Object Lock_Danmu = new Object();

    public boolean checkCollision(double enemyX, double enemyY, double enemySizeX, double enemySizeY) {
        double playerCenterX = playerX + 40;
        double playerCenterY = playerY + 78;
        // Find the closest point to the circle within the rectangle
        double enemyCenterX = Math.max(enemyX, Math.min(playerCenterX, enemyX + enemySizeX));
        double enemyCenterY = Math.max(enemyY, Math.min(playerCenterY, enemyY + enemySizeY));
        double distance = Math.sqrt(Math.pow(playerCenterX - enemyCenterX, 2) + Math.pow(playerCenterY - enemyCenterY, 2));
        return distance <= 10;
    }

    Runnable DeleteEnemy = () -> {
        synchronized (Lock_Enemy) {
            store.EnemyList.removeIf(enemy -> enemy.EnemyY > canvas.getHeight() || enemy.EnemyX > canvas.getWidth() || enemy.EnemyX < 0);
        }
    };
    Runnable DeleteShoot = () -> {
        synchronized (Lock_Shoot) {
            store.ShootList.removeIf(shoot -> shoot.ShootY < 0);
        }
    };
    Runnable DeleteDanmu = () -> {
        synchronized (Lock_Danmu) {
            store.DanmuList.removeIf(danmu -> danmu.DanmuY > canvas.getHeight() || danmu.DanmuX > canvas.getWidth() || danmu.DanmuX < 0);
        }
    };
    Runnable KeePress = () -> {
        // Key control---------------------------------------------------------------------------------------------------
        slowMode = keysPressed.contains(KeyCode.SHIFT);
        if (slowMode)
            PlayerSpeed = Store.PlayerSpeed / 2;
        else
            PlayerSpeed = Store.PlayerSpeed;
        if (keysPressed.contains(KeyCode.UP) && playerY > 0) {
            playerY -= PlayerSpeed * 0.75;
        }
        if (keysPressed.contains(KeyCode.DOWN) && playerY < canvas.getHeight() - airplaneImage.getHeight() + 40) {
            playerY += PlayerSpeed * 0.75;
        }
        if (keysPressed.contains(KeyCode.LEFT) && playerX > 0) {
            playerX -= PlayerSpeed;
        }
        if (keysPressed.contains(KeyCode.RIGHT) && playerX < canvas.getWidth() - airplaneImage.getWidth()) {
            playerX += PlayerSpeed;
        }
        ShootPress = keysPressed.contains(KeyCode.Z);
    };
    Runnable RefreshEnemy = () -> {
        // Refresh the enemy-------------------------------------------------------------------------------------------------
        synchronized (Lock_Enemy) {
            for (Enemy enemy : store.EnemyList) {
                enemy.EnemyX += enemy.EnemySpeedX;
                enemy.EnemyY += enemy.EnemySpeedY;
                if (TargetDanmuCount >= 1) {
                    int DanmuSpeedX;
                    // Refresh the danmu and add it to the list---------------------------------------------------------------------
                    if (enemy.EnemyX > playerX)
                        DanmuSpeedX = (random.nextInt(10) - 5) - 2;
                    else if (enemy.EnemyX < playerX)
                        DanmuSpeedX = (random.nextInt(10) - 5) + 2;
                    else
                        DanmuSpeedX = (random.nextInt(10) - 5);
                    int DanmuSpeedY = (random.nextInt(10) + 5);
                    synchronized (Lock_Danmu) {
                        store.DanmuList.add(new Danmu(enemy.EnemyX + 50, enemy.EnemyY, DanmuSpeedX, DanmuSpeedY, DanmuDamage));
                    }
                }
            }
        }
        if (TargetDanmuCount >= 1) TargetDanmuCount -= 1;
    };
    Runnable RefreshShoot = () -> {
        // Refresh the shoot-------------------------------------------------------------------------------------------------
        synchronized (Lock_Shoot) {
            for (Shoot shoot : store.ShootList) {
                shoot.ShootY -= shoot.ShootSpeed;
            }
        }
    };
    Runnable RefreshDanmu = () -> {
        // Refresh the danmu-------------------------------------------------------------------------------------------------
        synchronized (Lock_Danmu) {
            for (Danmu danmu : store.DanmuList) {
                danmu.DanmuX += danmu.DanmuSpeedX;
                danmu.DanmuY += danmu.DanmuSpeedY;
            }
        }
    };
    Runnable EnemyDetect = () -> {
        // Do the collision detection for the enemy, the size of the enemy is 64x58, the size of the player is 80x132,
        List<Enemy> toRemove = new ArrayList<>();
        synchronized (Lock_Enemy) {
            for (Enemy enemy : store.EnemyList) {
                if (checkCollision(enemy.EnemyX, enemy.EnemyY, 64, 58)) {
                    toRemove.add(enemy);
                    AirplaneGame.KON("/Music/PlayerHP.wav");
                    Store.PlayerHP -= 1;
                }
            }
        }
        PlayerText.setText("Player HP  ❤ × " + Store.PlayerHP);
        try {
            store.EnemyList.removeAll(toRemove);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
    Runnable DanmuDetect = () -> {
        // Do the collision detection for the danmu, the size of the danmu is 20x20, the size of the player is 80x132,
        List<Danmu> toRemove = new ArrayList<>();
        synchronized (Lock_Danmu) {
            for (Danmu danmu : store.DanmuList) {
                if (checkCollision(danmu.DanmuX, danmu.DanmuY, 20, 20)) {
                    toRemove.add(danmu);
                    AirplaneGame.KON("/Music/PlayerHP.wav");
                    Store.PlayerHP -= danmu.DanmuDamage;
                }
            }
        }
        PlayerText.setText("Player HP  ❤ × " + Store.PlayerHP);
        try {
            store.DanmuList.removeAll(toRemove);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
    Runnable ShootDetect = () -> {
        // Do the collision detection for the shoot, the size of the shoot is 18x90, the size of the enemy is 64x58,
        List<Shoot> toRemove = new ArrayList<>();
        List<Enemy> toRemoveEnemy = new ArrayList<>();
        synchronized (Lock_Shoot) {
            synchronized (Lock_Enemy) {
                for (Shoot shoot : store.ShootList) {
                    for (Enemy enemy : store.EnemyList) {
                        if (shoot.ShootX + 18 >= enemy.EnemyX && shoot.ShootX <= enemy.EnemyX + 63
                                && shoot.ShootY + 90 >= enemy.EnemyY && shoot.ShootY <= enemy.EnemyY + 58) {
                            toRemove.add(shoot);
                            enemy.EnemyHP -= shoot.ShootDamage;
                            if (enemy.EnemyHP <= 0) {
                                toRemoveEnemy.add(enemy);
                                Store.Score += 100;
                                Store.GetCard += 0.15f;
                                if (enemy.SpecialEnemy)
                                    Store.GetCard += 0.2f;
                                // Refresh the text-------------------------------------------------------------------------------------
                                ScoreText.setText("Score: " + Store.Score);
                                GetCardText.setText("GetCard: " + Math.round(Store.GetCard / Store.Level * 100) + "%");
                            }
                        }
                    }
                }
            }
        }
        try {
            store.ShootList.removeAll(toRemove);
            store.EnemyList.removeAll(toRemoveEnemy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public void handle(long currentNanoTime) {
        // UP to 60 FPS-------------------------------------------------------------------------------------------------
        long interval = 1_000_000_000 / 58;
        if (currentNanoTime - lastUpdate >= interval) {
            lastUpdate = currentNanoTime;
            TargetEnemyCount += (double) Store.PlayerLevel / 60.0;
            TargetShootCount += (double) Store.ShootFrequency / 60.0;
            TargetDanmuCount += 0.0333333333333;
            // Card select-------------------------------------------------------------------------------------------------
            if ((Store.GetCard / Store.Level >= 1)) {
                this.stop();
                AirplaneGame.backPlayer.pause();
                keysPressed.clear();
                Platform.runLater(() -> {
                    CardSelect.ShowCard();
                    Store.GetCard = 0;
                    AirplaneGame.backPlayer.play();
                    GetCardText.setText("GetCard: " + Math.round(Store.GetCard / Store.Level * 100) + "%");
                    PlayerText.setText("Player HP  ❤ × " + Store.PlayerHP);
                    this.start(); // Restart the timer
                });
            }
            // GAME OVER--------------------------------------------------------------------------------------------------
            if (Store.PlayerHP <= 0) {
                this.stop();
                keysPressed.clear();
                Platform.runLater(() -> {
                    this.stop();
                    GameOver.run();
                });
            }
            // Add enemy--------------------------------------------------------------------------------------------------
            if (TargetEnemyCount >= 1) {
                int speedX = Math.round(Store.EnemySpeedLevel * (random.nextInt(20) - 10));
                int speedY = Math.round(Store.EnemySpeedLevel * (random.nextInt(10) + 5));
                SpecialEnemy = random.nextInt(1000) <= Store.EnemySpecialUP;
                store.EnemyList.add(new Enemy(50 + random.nextInt(1400), -10,
                        speedX, speedY, Store.EnemyHP, SpecialEnemy));
                TargetEnemyCount -= 1;
            }
            if (TargetShootCount >= 1 && ShootPress) {
                store.ShootList.add(new Shoot((int) playerX + 30, (int) playerY, Store.ShootSpeed, Store.ShootDamage));
                TargetShootCount = 0;
            }
            // Add the tasks to the thread pool----------------------------------------------------------------------------
            Future<?> futureDeleteEnemy = executor.submit(DeleteEnemy);
            Future<?> futureDeleteShoot = executor.submit(DeleteShoot);
            Future<?> futureDeleteDanmu = executor.submit(DeleteDanmu);
            Future<?> futureKey = executor.submit(KeePress);
            Future<?> futureRefreshEnemy = executor.submit(RefreshEnemy);
            Future<?> futureRefreshShoot = executor.submit(RefreshShoot);
            Future<?> futureRefreshDanmu = executor.submit(RefreshDanmu);
            // Wait for the tasks to finish--------------------------------------------------------------------------------
            try {
                futureDeleteEnemy.get();
                futureDeleteShoot.get();
                futureDeleteDanmu.get();
                futureKey.get();
                futureRefreshEnemy.get();
                futureRefreshShoot.get();
                futureRefreshDanmu.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            // Add the tasks to the thread pool----------------------------------------------------------------------------
            Future<?> futureEnemyDetect = executor.submit(EnemyDetect);
            Future<?> futureDanmuDetect = executor.submit(DanmuDetect);
            Future<?> futureShootDetect = executor.submit(ShootDetect);
            try {
                futureEnemyDetect.get();
                futureDanmuDetect.get();
                futureShootDetect.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            // Clear-----------------------------------------------------------------------------------------------------
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            // Draw plane-------------------------------------------------------------------------------------------------
            if (keysPressed.contains(KeyCode.LEFT))
                gc.drawImage(airplaneLeftImage, playerX, playerY);
            else if (keysPressed.contains(KeyCode.RIGHT))
                gc.drawImage(airplaneRightImage, playerX, playerY);
            else
                gc.drawImage(airplaneImage, playerX, playerY);
            if (slowMode)
                gc.drawImage(slowModeImage, playerX - 25, playerY + 15);
            // Draw shoot-------------------------------------------------------------------------------------------------
            for (Shoot shoot : store.ShootList)
                gc.drawImage(ShootImage, shoot.ShootX, shoot.ShootY);
            // Draw Enemy-------------------------------------------------------------------------------------------------
            for (Enemy enemy : store.EnemyList)
                gc.drawImage(enemyImage, enemy.EnemyX, enemy.EnemyY);
            // Draw Danmu-------------------------------------------------------------------------------------------------
            for (Danmu danmu : store.DanmuList)
                gc.drawImage(DanmuImage, danmu.DanmuX, danmu.DanmuY);
        }
    }
}
