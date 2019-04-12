package com.example.texty.HomePage;

import android.content.Context;
import android.view.View;

import java.util.List;

public interface HomePageView {

    void runThread(Runnable thread);
    void addMyMessage(String message);
    void addOtherMessage(String message,String username);
    void notifyPrivateMessage(String message,String username);
    void addUsersList(List<String> usersList);
    void addUser(String username);
    void removeUser(String username);
    void onSendClick(View v);
    void onMoreClick(View v);
    Context getContext();
}
