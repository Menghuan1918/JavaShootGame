package com.example.airplaneshoot;

public class Enemy {
    int EnemyX;
    int EnemyY;
    int EnemySpeedX;
    int EnemySpeedY;
    float EnemyHP;
    boolean SpecialEnemy;

    public Enemy(int EnemyX, int EnemyY, int EnemySpeedX, int EnemySpeedY, float EnemyHP, boolean SpecialEnemy) {
        this.EnemyX = EnemyX;
        this.EnemyY = EnemyY;
        this.EnemySpeedX = EnemySpeedX;
        this.EnemySpeedY = EnemySpeedY;
        this.EnemyHP = EnemyHP;
        this.SpecialEnemy = SpecialEnemy;
    }
}
