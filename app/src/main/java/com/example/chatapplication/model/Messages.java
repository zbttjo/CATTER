package com.example.chatapplication.model;

public class Messages {

    String message;
    String from;

    public Messages() {

    }

    public Messages(String message, String from) {
        this.message = message;
        this.from = from;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String sender) {
        this.from = sender;
    }
}
