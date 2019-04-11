package com.example.texty.HomePage;

public interface PrivateMessageView {

    void notifyPrivateMessage(String message,String username);
    void runThread(Runnable thread);
    void addMyMessage(String message);
    void addOtherMessage(String message, String username);
}
