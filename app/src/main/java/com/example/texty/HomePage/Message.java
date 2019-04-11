package com.example.texty.HomePage;

public class Message {
    private String fromName, message;
    private Integer isSelf;

    public Message() {
    }

    public Message(String fromName, String message, Integer isSelf) {
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer isSelf() {
        return isSelf;
    }

    public void setSelf(Integer isSelf) {
        this.isSelf = isSelf;
    }

}