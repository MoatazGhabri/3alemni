package com.example.rabie;

public class User {
    public String fullName, email, cin, phone ;

    public User() {
    }

    public User(String fullName, String email, String cin, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.cin = cin;
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
