package com.mart.models;

public class User {
    private String username;
    private int score;
    private boolean visibility;

    public User() {}

    public User(String username, int score, boolean visibility) {
        this.username = username;
        this.score = score;
        this.visibility = visibility;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
}
