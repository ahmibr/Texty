package com.example.texty.PrivateMessage;

import android.content.Context;
import android.view.View;

public interface PrivateMessageView {

    void notifyPrivateMessage(String message,String username);
    void runThread(Runnable thread);
    Context getContext();
    void addMyMessage(String message,String username);
    void onSendClick(View v);
    void onMoreClick(View v);
    void addOtherMessage(String message, String username);
}
