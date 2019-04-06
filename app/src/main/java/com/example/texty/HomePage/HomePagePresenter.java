package com.example.texty.HomePage;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.example.texty.Utilities.Authenticator;
import com.example.texty.Utilities.Constants;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentNavigableMap;

public class HomePagePresenter {

    private Socket mSocket;
//    private ;
    private HomePageView mView;
    private final String TAG = "HomePageActivity";

    HomePagePresenter(HomePageView view){
        mView = view;
    }

    void initializeSocket(){
        //@TODO get server link from DB
        try {
            mSocket = IO.socket(Constants.CHAT_ROOM_API);

            Emitter.Listener onNewMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {

                   Runnable mThread = new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG,"I'm in thread running");
//                            JSONObject data = (JSONObject) args[0];
//                            String username;
                            String message = (String)args[0];
//                            mView.printToast(message);
//                            String messageType;
//                            try {
//                                username = data.getString("username");
//                                message = data.getString("message");
//                                messageType = data.getString("messageType");
//                                mView.printToast(message);
//                            } catch (JSONException e) {
//                                Log.e(TAG,"An error in parsing JSON");
//                                return;
//                            }


                            //@TODO add the message to view
//                    addMessage(username, message);
                        }
                    };

                   mView.runThread(mThread);
                }
            };
            mSocket.on("chat message",onNewMessage);
            mSocket.connect();

            Log.d(TAG,"Started socket successfully");

        } catch (URISyntaxException e) {
            e.printStackTrace();
            //Connection error, go out
            Log.e(TAG,"Socket Error");
        }
    }

    void sendMessage(String message){
        //@TODO Add message to list
        //@TODO Remove text from textview
        mSocket.emit("chat message", message);
    }

    void initializeChat(){

    }

    void closeSocket(){
        if(mSocket != null)
        {
            mSocket.close();
            mSocket.off("new message");
        }

    }

    public boolean IsLoggedIn() {
        return Authenticator.isLoggedIn(mView.getContext());
    }
}
