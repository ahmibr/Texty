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

    HomePagePresenter(HomePageView view) {
        mView = view;
        usersList = new ArrayList<>();
        if (isLoggedIn()) {
            myUserName = Authenticator.getUsername(mView.getContext());
            initializeSocket();
            initializeChat();
            mView.greetUser(myUserName);
        } else {
            mView.reSignIn();
        }
    }

    /**
     * Initializes socket connection
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    private void initializeSocket() {
        //@TODO get server link from DB
        try {
            mSocket = IO.socket(Constants.CHAT_ROOM_API);


            //****************Initialize listeners**********************************//
            onNewMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    receiveMessage(args);
                }
            };

            onPrivateMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    receivePrivateMessage(args);
                }
            };

            onUserJoin = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    addJoinedUser(args);
                }
            };

            onRetrieveUserList = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    retrieveUsersList(args);
                }
            };

            onUserLeave = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    removeLeftUser(args);
                }
            };

            onIdentifyUser = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.emit("username", myUserName);
                }
            };

            onReconnect = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.emit("username", myUserName);
                }
            };

            onRetrieveHistory = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    retrieveHistory(args);
                }
            };
            //***************************************************************************//

            //****************Attach listeners******************************************//
            mSocket.on("group message", onNewMessage);
            mSocket.on("private message", onPrivateMessage);
            mSocket.on("user join", onUserJoin);
            mSocket.on("user leave", onUserLeave);
            mSocket.on("retrieve list", onRetrieveUserList);
            mSocket.on("identify username", onIdentifyUser);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on("get group message", onRetrieveHistory);
            if (!mSocket.connected()) {
                mSocket.connect();
                mSocket.emit("username", myUserName);
                mSocket.emit("get group message");
            }

            Log.d(TAG, "Started socket successfully");
            //***************************************************************************//

        } catch (URISyntaxException e) {
            e.printStackTrace();
            //Connection error, go out
            Log.e(TAG, "Socket Error");
        }
    }

    /**
     * Notifies that a user left room
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    private void removeLeftUser(final Object[] args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                String username = (String) args[0];
                usersList.remove(username);
                mView.removeUser(username);
            }
        };

        mView.runThread(mThread);

    }


    /**
     * Retrieves users list from server
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    private void retrieveUsersList(final Object[] args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {

                usersList.clear();

                JSONArray data = (JSONArray) args[0];

                Log.d(TAG, "Received users list");
                //loop over received users list, and it them to current users list
                for (int i = 0; i < data.length(); ++i) {
                    try {
                        String member = data.getString(i);
                        if (!member.equals(myUserName))
                            usersList.add(data.getString(i));
                        Log.d(TAG, data.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mView.addUsersList(usersList);
            }
        };

        mView.runThread(mThread);


    }

    /**
     * Returns users list to view
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    List<String> getUsersList() {
        return usersList;
    }

    /**
     * Notifies view that user has joined room
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    private void addJoinedUser(final Object[] args) {

        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                String username = (String) args[0];
                usersList.add(username);
                mView.addUser(username);
            }
        };

        mView.runThread(mThread);
    }

    /**
     * Notifies user he received a private message
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    private void receivePrivateMessage(final Object... args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                JSONObject data = (JSONObject) args[0];
                try {
                    String username = data.getString("username");
                    String message = data.getString("message");

                    Log.d(TAG, "I'm in home and received the message from: " + username);

                    //Notify private message
                    mView.notifyPrivateMessage(message, username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mView.runThread(mThread);
    }

    /**
     * Logs user out from application
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    void logOut() {

        Authenticator.logOut(mView.getContext());
        mSocket.disconnect();
    }

    /**
     * Call back when home page is paused
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    void onHomePagePause() {
        mSocket.off("private message");
    }

    /**
     * Call back when home page is resumed
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    void onHomePageResume() {
        if (!mSocket.connected()) {
            mSocket.connect();
            mSocket.emit("username", myUserName);
            mSocket.emit("get group message");
        }
        mSocket.on("private message", onPrivateMessage);
    }

    /**
     * Sends message to server
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    void sendMessage(String message) {
        message = message.trim();
        if (message.isEmpty())
            return;

        mSocket.emit("group message", message);
        mView.addMyMessage(message, myUserName);
    }

    void initializeChat() {

    }

    /**
     * closes socket connection
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    void closeSocket() {
        if (mSocket != null) {
            mSocket.close();
            mSocket.off("group message");
            mSocket.off("private message");
            mSocket.off("user join");
            mSocket.off("user leave");
            mSocket.off("retrieve list");
        }

    }

    /**
     * Received messages from server
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    void receiveMessage(final Object[] args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "I received a message, in thread running");
                JSONObject data = (JSONObject) args[0];
                try {
                    String username = data.getString("username");
                    String message = data.getString("message");

                    Log.d(TAG, "I received the message from: ");
                    mView.addOtherMessage(message, username, true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        mView.runThread(mThread);
    }

    /**
     * Retrieves chat history from server
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    private void retrieveHistory(final Object[] args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                JSONArray data = (JSONArray) args[0];
                try {
                    mView.clearChat();

                    int len = data.length();

                    for (int i = 0; i < len; ++i) {

                        String username = data.getJSONObject(i).getString("senderUserName");
                        String message = data.getJSONObject(i).getString("content");

                        //redirect message based on who sent it
                        if (username.equals(myUserName)) {
                            mView.addMyMessage(message, myUserName);
                        } else {
                            mView.addOtherMessage(message, username, false);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mView.runThread(mThread);
    }

    /**
     * Checks wheter user is logged in or not
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    public boolean isLoggedIn() {
        return Authenticator.isLoggedIn(mView.getContext());
    }

}
