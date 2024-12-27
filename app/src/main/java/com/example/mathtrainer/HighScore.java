package com.example.mathtrainer;

public class HighScore {
    private String userId;
    private String email;
    private int score;

    public HighScore() {
        // Default constructor required for Firebase
    }

    public HighScore(String userId, String email, int score) {
        this.userId = userId;
        this.email = email;
        this.score = score;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public int getScore() {
        return score;
    }
}