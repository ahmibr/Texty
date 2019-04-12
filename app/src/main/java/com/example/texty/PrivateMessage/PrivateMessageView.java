package com.example.texty.PrivateMessage;

import android.view.View;

public interface PrivateMessageView {

    void notifyPrivateMessage(String message,String username);
    void runThread(Runnable thread);
    void addMyMessage(String message);
    void onSendClick(View v);
    void onMoreClick(View v);
    void addOtherMessage(String message, String username);
}
