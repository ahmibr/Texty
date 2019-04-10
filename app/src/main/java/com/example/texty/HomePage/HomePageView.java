package com.example.texty.HomePage;

import android.content.Context;

public interface HomePageView {

    void runThread(Runnable thread);
    void addMyMessage(String message);
    void addOtherMessage(String message,String username);
    void notifyPrivateMessage(String message,String username);
    Context getContext();
}
