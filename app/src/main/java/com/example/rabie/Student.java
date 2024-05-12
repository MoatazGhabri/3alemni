package com.example.rabie;

public class Student {
    private String name;
    private int score;
    private String teacherName;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }
public  String getTeacherName(){
        return teacherName;
}
    public int getScore() {
        return score;
    }
}

