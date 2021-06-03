package com.example.chatapplication.model;

public class User {


    private String email;
    private String userName;
    private String password;
    private String imageUrl;


    public User(String email, String userName, String imageUrl) {
        this.email = email;
        this.userName = userName;
        this.imageUrl = imageUrl;
    }

    public User() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
