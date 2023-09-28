package com.tkisor.uwtweaker.forge.event;

import java.util.List;

public class Difficulty {
    private Difficulty() {}
    private static String gameDifficulty = "normal";
    private static double gameDifficultSize = 0;
    private static final List<String> gameDifficultyList = List.of("easy", "normal", "hard", "inferno");

    public static double getGameDifficultSize() {
        return gameDifficultSize;
    }

    public static void setGameDifficultSize(double gameDifficultSize) {
        Difficulty.gameDifficultSize = gameDifficultSize;
    }

    public static void addGameDifficultSize(double gameDifficultSize) {
        Difficulty.gameDifficultSize += gameDifficultSize;
    }

    public static List<String> getGameDifficultyList() {
        return gameDifficultyList;
    }

    public static String getGameDifficulty() {
        return gameDifficulty;
    }

    public static void setGameDifficulty(String gameDifficulty) {
        Difficulty.gameDifficulty = gameDifficulty;
    }

    public static double difficultMultiplier() {
        switch (gameDifficulty) {
            case "easy" -> {
                return 0.5;
            }
            case "normal" -> {
                return 1;
            }
            case "hard" -> {
                return 2.5;
            }
            case "inferno" -> {
                return 8;
            }
            default -> throw new IllegalStateException("Unexpected value: " + gameDifficulty);
        }
    }
}
