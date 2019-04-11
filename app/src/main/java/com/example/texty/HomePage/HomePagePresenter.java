package com.example.texty.HomePage;

import android.util.Log;

import com.example.texty.Utilities.Authenticator;
import com.example.texty.Utilities.Constants;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;

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
                public void call(final Object... args) { receivePrivateMessage(args); }};

            Emitter.Listener onUserJoin = new Emitter.Listener() {
                @Override
                public void call(final Object... args) { addJoinedUser(args); }};

            Emitter.Listener onRetrieveUserList = new Emitter.Listener() {
                @Override
                public void call(final Object... args) { retrieveUsersList(args);
                }};

            Emitter.Listener onUserLeave = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    removeLeftUser(args);
                }};

            mSocket.on("chat message",onNewMessage);
            mSocket.on("private message",onPrivateMessage);
            mSocket.on("user join",onUserJoin);
            mSocket.on("user leave",onUserLeave);
            mSocket.on("retrieve list",onRetrieveUserList);
            mSocket.connect();
            mSocket.emit("username",Authenticator.getUsername(mView.getContext()));

            Log.d(TAG,"Started socket successfully");

        } catch (URISyntaxException e) {
            e.printStackTrace();
            //Connection error, go out
            Log.e(TAG,"Socket Error");
        }
    }

    private void removeLeftUser(final Object[] args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                String username = (String)args[0];

                mView.removeUser(username);
            }
        };

        mView.runThread(mThread);
    }


    private void retrieveUsersList(final Object[] args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {

                Log.d(TAG,(String)args[0]);
            }
        };

        mView.runThread(mThread);
    }

    private void addJoinedUser(final Object[] args) {

        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                String username = (String)args[0];

                mView.addUser(username);
            }
        };

        mView.runThread(mThread);
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
