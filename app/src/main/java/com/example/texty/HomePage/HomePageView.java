package com.example.texty.HomePage;

import android.content.Context;

import java.util.List;

public interface HomePageView {

    void runThread(Runnable thread);
    void addMyMessage(String message);
    void addOtherMessage(String message,String username);
    void notifyPrivateMessage(String message,String username);
    void addUsersList(List<String> usersList);
    void addUser(String username);
    Context getContext();
}
