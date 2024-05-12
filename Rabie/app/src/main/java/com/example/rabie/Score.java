package com.example.rabie;

public class Score {
    private String userName;
    private int score;
    private String teacherName;

    public Score() {
    }

    public Score(String userName,String teacherName, int score) {
        this.userName = userName;
        this.score = score;
        this.teacherName = teacherName;
    }

    public String getUserName() {
        return userName;
    }
    public String getTeacherName() {
        return teacherName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

