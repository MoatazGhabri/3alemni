package com.example.rabie;

public class ParticipationItem {
    private String username;
    private int score;

    public ParticipationItem(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

//    public boolean hasParticipated() {
//        return participated;
//    }

    public int getScore() {
        return score;
    }
}

