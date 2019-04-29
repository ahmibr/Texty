package com.example.texty.HomePage;

import android.util.Log;

import com.example.texty.Utilities.Authenticator;
import com.example.texty.Utilities.Constants;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class HomePagePresenter {

    private Socket mSocket = null;
    private HomePageView mView;
    private final String TAG = "HomePageActivity";
    private ArrayList<String> usersList;
    private String myUserName;

    //Events listeners
    private Emitter.Listener onNewMessage = null;
    private Emitter.Listener onPrivateMessage = null;
    private Emitter.Listener onUserJoin = null;
    private Emitter.Listener onRetrieveUserList = null;
    private Emitter.Listener onUserLeave = null;
    private Emitter.Listener onIdentifyUser = null;
    private Emitter.Listener onReconnect = null;
    private Emitter.Listener onRetrieveHistory = null;

    HomePagePresenter(HomePageView view){
        mView = view;
        usersList = new ArrayList<>();
        if(isLoggedIn()) {
            myUserName = Authenticator.getUsername(mView.getContext());
            initializeSocket();
            initializeChat();
            mView.greetUser(myUserName);
        }
        else{
            mView.reSignIn();
        }
    }

    /**
     * Initializes socket connection
     *
     * @author  Ahmed Ibrahim
     * @version 1.0
     * @since   2019-03-31
     */
    private void initializeSocket(){
        //@TODO get server link from DB
        try {
            mSocket = IO.socket(Constants.CHAT_ROOM_API);

            onNewMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    receiveMessage(args);
                }
            };

            onPrivateMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) { receivePrivateMessage(args); }};

            onUserJoin = new Emitter.Listener() {
                @Override
                public void call(final Object... args) { addJoinedUser(args); }};

            onRetrieveUserList = new Emitter.Listener() {
                @Override
                public void call(final Object... args) { retrieveUsersList(args);
                }};

            onUserLeave = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    removeLeftUser(args);
                }};

            onIdentifyUser = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.emit("username",myUserName);
                }
            };

            onReconnect = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.emit("username",myUserName);
                }
            };

            onRetrieveHistory = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    retrieveHistory(args);
                }
            };

            mSocket.on("group message",onNewMessage);
            mSocket.on("private message",onPrivateMessage);
            mSocket.on("user join",onUserJoin);
            mSocket.on("user leave",onUserLeave);
            mSocket.on("retrieve list",onRetrieveUserList);
            mSocket.on("identify username",onIdentifyUser);
            mSocket.on(Socket.EVENT_RECONNECT,onReconnect);
            mSocket.on("get group message",onRetrieveHistory);
            if(!mSocket.connected())
            {
                mSocket.connect();
                mSocket.emit("username",myUserName);
                mSocket.emit("get group message");
            }

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
                usersList.remove(username);
                mView.removeUser(username);
            }
        };

        mView.runThread(mThread);

    }


    private void retrieveUsersList(final Object[] args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {

                usersList.clear();

                JSONArray data = (JSONArray)args[0];

                Log.d(TAG,"Received users list");
                for(int i=0;i<data.length();++i){
                    try {
                        String member = data.getString(i);
                        if (!member.equals(myUserName))
                            usersList.add(data.getString(i));
                        Log.d(TAG,data.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mView.addUsersList(usersList);
            }
        };

        mView.runThread(mThread);


    }


    List<String> getUsersList(){
        return usersList;
    }

    private void addJoinedUser(final Object[] args) {

        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                String username = (String)args[0];
                usersList.add(username);
                mView.addUser(username);
            }
        };

        mView.runThread(mThread);
    }

    private void receivePrivateMessage(final Object... args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                JSONObject data = (JSONObject) args[0];
                try {
                    String username = data.getString("username");
                    String message = data.getString("message");

                    Log.d(TAG, "I'm in home and received the message from: "+username);
                    mView.notifyPrivateMessage(message, username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mView.runThread(mThread);
    }

    void logOut(){

        Authenticator.logOut(mView.getContext());
        mSocket.disconnect();
    }

    void onHomePagePause(){
        mSocket.off("private message");
    }

    void onHomePageResume(){
        if(!mSocket.connected())
        {
            mSocket.connect();
            mSocket.emit("username",myUserName);
            mSocket.emit("get group message");
        }
        mSocket.on("private message",onPrivateMessage);
    }

    void sendMessage(String message){
        message = message.trim();
        if(message.isEmpty())
            return;

        mSocket.emit("group message", message);
        mView.addMyMessage(message,myUserName);
    }

    void initializeChat(){

    }

    void closeSocket(){
        if(mSocket != null)
        {
            mSocket.close();
            mSocket.off("group message");
            mSocket.off("private message");
            mSocket.off("user join");
            mSocket.off("user leave");
            mSocket.off("retrieve list");
        }

    }

    void receiveMessage(final Object[] args){
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"I received a message, in thread running");
                JSONObject data = (JSONObject) args[0];
                try {
                    String username = data.getString("username");
                    String message = data.getString("message");

                    Log.d(TAG, "I received the message from: ");
                    mView.addOtherMessage(message, username,true);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mView.runThread(mThread);
    }

    private void retrieveHistory(final Object[] args){
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                JSONArray data = (JSONArray) args[0];
                try {
                    mView.clearChat();
                   int len = data.length();
                   for(int i=0;i<len;++i){
                       String username = data.getJSONObject(i).getString("senderUserName");
                       String message = data.getJSONObject(i).getString("content");
                       if(username.equals(myUserName)) {
                           mView.addMyMessage(message,myUserName);
                       }
                       else{
                           mView.addOtherMessage(message,username,false);
                       }
                   }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mView.runThread(mThread);
    }

    public boolean isLoggedIn() {
        return Authenticator.isLoggedIn(mView.getContext());
    }

}
