package com.example.texty.HomePage;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.example.texty.Utilities.Constants;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class HomePagePresenter {

    private Socket mSocket;
    private Emitter.Listener onNewMessage;
    private HomePageActivity mView;
    private final String TAG = "HomePageActivity";

    HomePagePresenter(HomePageActivity view){
        mView = view;
    }

    void initializeSocket(){
        //@TODO get server link from DB
        try {
            mSocket = IO.socket(Constants.CHAT_ROOM_API);

            onNewMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {

                   Runnable mThread = new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            String username;
                            String message;
                            String messageType;
                            try {
                                username = data.getString("username");
                                message = data.getString("message");
                                messageType = data.getString("messageType");
                                mView.printToast(message);
                            } catch (JSONException e) {
                                Log.e(TAG,"An error in parsing JSON");
                                return;
                            }

                            //@TODO add the message to view
//                    addMessage(username, message);
                        }
                    };

                   mView.runOnUiThread(mThread);
                }
            };
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //Connection error, go out
        }
    }

    void sendMessage(String message){
        //@TODO Add message to list
        //@TODO Remove text from textview
        mSocket.emit("group message", message);
    }

    void initializeChat(){

    }
}
