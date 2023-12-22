package com.example.airplaneshoot;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Store the variables
public class Store {
    // Below is var-------------------------------------------------------------------------------------------------------
    public static int PlayerSpeed;
    public static int PlayerHP;
    public static int PlayerLevel;
    public static float EnemySpeedLevel;
    public static float EnemyHP;
    public static int EnemySpecialUP;
    public static int ShootSpeed;
    public static float ShootDamage;
    public static int ShootFrequency;
    public static float GetCard;
    // Below is about Level---------------------------------------------------------------------------------------------------
    public static float Level = 1;
    public static int Score = 0;
    // Below is the list-----------------------------------------------------------------------------------------------
    public ArrayList<Enemy> EnemyList = new ArrayList<>();
    public ArrayList<Shoot> ShootList = new ArrayList<>();
    public ArrayList<Danmu> DanmuList = new ArrayList<>();
    public static List<String> CardList = new ArrayList<>();
    // Above is the variables-------------------------------------------------------------------------------------------
    public Store() {
        PlayerSpeed = 20;
        PlayerLevel = 2;//The number of the enemy
        PlayerHP = 3;
        EnemyHP = 3.0f;
        EnemySpeedLevel = 0.8F;
        EnemySpecialUP = 100;//Means 10%
        ShootFrequency = 10;
        ShootSpeed = 40;
        ShootDamage = 1;
        GetCard = 0.6F;
        // Below is the card list-------------------------------------------------------------------------------------------
        URL Card1 = getClass().getResource("/Pictures/Card1.png");
        CardList.add(Objects.requireNonNull(Card1).toString());
        URL Card2 = getClass().getResource("/Pictures/Card2.png");
        CardList.add(Objects.requireNonNull(Card2).toString());
        URL Card3 = getClass().getResource("/Pictures/Card3.png");
        CardList.add(Objects.requireNonNull(Card3).toString());
        URL Card4 = getClass().getResource("/Pictures/Card4.png");
        CardList.add(Objects.requireNonNull(Card4).toString());
        URL Card5 = getClass().getResource("/Pictures/Card5.png");
        CardList.add(Objects.requireNonNull(Card5).toString());
        URL Card6 = getClass().getResource("/Pictures/Card6.png");
        CardList.add(Objects.requireNonNull(Card6).toString());
        URL Card7 = getClass().getResource("/Pictures/Card7.png");
        CardList.add(Objects.requireNonNull(Card7).toString());
        URL Card8 = getClass().getResource("/Pictures/Card8.png");
        CardList.add(Objects.requireNonNull(Card8).toString());
        URL Card9 = getClass().getResource("/Pictures/Card9.png");
        CardList.add(Objects.requireNonNull(Card9).toString());
    }

}

