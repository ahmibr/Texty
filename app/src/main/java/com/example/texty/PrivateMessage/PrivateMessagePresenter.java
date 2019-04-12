package com.example.texty.PrivateMessage;

import android.util.Log;

import com.example.texty.Utilities.Constants;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class PrivateMessagePresenter {

    private PrivateMessageView mView;
    private String to;
    private Socket mSocket;
    private final String TAG = "PrivateMessagePresenter";
    
    PrivateMessagePresenter(PrivateMessageView view,String to){
        mView = view;
        this.to = to;
        initializeSocket();
    }

    void initializeSocket(){
        //@TODO get server link from DB
        try {
            mSocket = IO.socket(Constants.CHAT_ROOM_API);

            Emitter.Listener onPrivateMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) { receivePrivateMessage(args); }};

            mSocket.on("private message",onPrivateMessage);
            mSocket.connect();

            Log.d(TAG,"Started socket successfully");

        } catch (URISyntaxException e) {
            e.printStackTrace();
            //Connection error, go out
            Log.e(TAG,"Socket Error");
        }
    }

    void sendMessage(String message){
        message = message.trim();
        if(message.isEmpty())
            return;

        JSONObject dataSent = new JSONObject();


        try {
            dataSent.put("to",to);
            dataSent.put("message",message);
            mSocket.emit("private message", dataSent);
            mView.addMyMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void receivePrivateMessage(final Object[] args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                JSONObject data = (JSONObject) args[0];
                try {
                    String username = data.getString("username");
                    String message = data.getString("message");

                    Log.d(TAG, "I received the message from: "+username);

                    if(username.equals(to)) {
                        mView.addOtherMessage(message, username);
                    }
                    else {
                        mView.notifyPrivateMessage(message, username);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mView.runThread(mThread);
    }
}
