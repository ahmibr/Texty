package com.example.texty.HomePage;

import android.util.Log;

import com.example.texty.Utilities.Authenticator;
import com.example.texty.Utilities.Constants;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class HomePagePresenter {

    private Socket mSocket;
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
                    receiveMessage(args);
                }
            };

            Emitter.Listener onPrivateMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    receivePrivateMessage(args);
                }
            };

            mSocket.on("chat message",onNewMessage);
            mSocket.on("private message",onPrivateMessage);
            mSocket.connect();
            mSocket.emit("username",Authenticator.getUsername(mView.getContext()));

            Log.d(TAG,"Started socket successfully");

        } catch (URISyntaxException e) {
            e.printStackTrace();
            //Connection error, go out
            Log.e(TAG,"Socket Error");
        }
    }

    private void receivePrivateMessage(final Object... args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"I'm in thread running");
                String message = (String)args[0];
                int idx = message.indexOf(":");
                String username = message.substring(0,idx);
                message = message.substring(idx+1);

                //@TODO add the message to view
                mView.notifyPrivateMessage(message,username);
            }
        };

        mView.runThread(mThread);
    }

    void sendMessage(String message){
        message = message.trim();
        if(message.isEmpty())
            return;

        mSocket.emit("chat message", Authenticator.getUsername(mView.getContext()) + ": " +message);
        mView.addMyMessage(message);
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

    void receiveMessage(final Object... args){
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"I'm in thread running");
//                            JSONObject data = (JSONObject) args[0];
                String message = (String)args[0];
                int idx = message.indexOf(":");
                String username = message.substring(0,idx);
                message = message.substring(idx+1);
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

                    Log.d(TAG,"I received the message");
                    mView.addOtherMessage(message,username);
            }
        };

        mView.runThread(mThread);
    }
    public boolean IsLoggedIn() {
        return Authenticator.isLoggedIn(mView.getContext());
    }

}
