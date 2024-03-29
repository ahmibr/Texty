package com.example.texty.HomePage;

import android.content.Context;
import android.view.View;

import java.util.List;

interface HomePageView {

    void runThread(Runnable thread);
    void addMyMessage(String message,String myUsername);
    void addOtherMessage(String message,String username, boolean notify);
    void clearChat();
    void notifyPrivateMessage(String message,String username);
    void addUsersList(List<String> usersList);
    void addUser(String username);
    void removeUser(String username);
    void reSignIn();
    void greetUser(String username);
    void onSendClick(View v);
    Context getContext();
}
