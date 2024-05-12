package com.example.rabie;

public class AuthSingleton {
    private static AuthSingleton instance;
    private boolean isLoggedIn = false;
    private String teacherName;


    private AuthSingleton() {
    }

    public static synchronized AuthSingleton getInstance() {
        if (instance == null) {
            instance = new AuthSingleton();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    public String getTeacherName() {
        return teacherName;
    }
    public void setTeacherName(String name) {
        this.teacherName = name;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
