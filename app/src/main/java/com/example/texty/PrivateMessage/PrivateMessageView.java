package com.example.texty.PrivateMessage;

public interface PrivateMessageView {

    void notifyPrivateMessage(String message,String username);
    void runThread(Runnable thread);
    void addMyMessage(String message);
    void addOtherMessage(String message, String username);
}
