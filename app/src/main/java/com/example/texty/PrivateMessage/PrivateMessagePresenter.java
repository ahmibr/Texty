package com.example.texty.PrivateMessage;

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

public class PrivateMessagePresenter {

    private PrivateMessageView mView;
    private String to; //Who I'm chatting with
    private Socket mSocket;
    private String myUsername;
    private final String TAG = "PrivateMessagePresenter";

    //Listeners
    private Emitter.Listener onReconnect = null;
    private Emitter.Listener onRetrieveHistory = null;

    PrivateMessagePresenter(PrivateMessageView view, String to) {
        mView = view;
        this.to = to;
        myUsername = Authenticator.getUsername(mView.getContext());
        initializeSocket();
    }

    /**
     * Initializes socket connection
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    void initializeSocket() {
        try {
            mSocket = IO.socket(Constants.CHAT_ROOM_API);

            Emitter.Listener onPrivateMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    receivePrivateMessage(args);
                }
            };

            onReconnect = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.emit("username", myUsername);
                }
            };

            onRetrieveHistory = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    retrieveHistory(args);
                }
            };

            mSocket.on("private message", onPrivateMessage);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on("get private message", onRetrieveHistory);

            if (!mSocket.connected()) {
                mSocket.connect();
                mSocket.emit("username", myUsername);
            }
            mSocket.emit("get private message", to);
            Log.d(TAG, "Started socket successfully");

        } catch (URISyntaxException e) {
            e.printStackTrace();
            //Connection error, go out
            Log.e(TAG, "Socket Error");
        }
    }

    /**
     * Send a message to server
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    void sendMessage(String message) {
        message = message.trim();
        if (message.isEmpty())
            return;

        JSONObject dataSent = new JSONObject();


        try {
            Log.d(TAG, "I sent message");
            //Put message into json package
            dataSent.put("to", to);
            dataSent.put("message", message);

            Log.d(TAG, dataSent.toString());

            mSocket.emit("private message", dataSent);
            mView.addMyMessage(message, myUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Call back when private message is received
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    private void receivePrivateMessage(final Object[] args) {
        Runnable mThread = new Runnable() {
            @Override
            public void run() {
                JSONObject data = (JSONObject) args[0];
                try {
                    String username = data.getString("username");
                    String message = data.getString("message");

                    Log.d(TAG, "I received the message from: " + username);

                    if (username.equals(to)) {
                        mView.addOtherMessage(message, username, true);
                    } else {
                        mView.notifyPrivateMessage(message, username);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Log.d(TAG, "I recieved private message");
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
                        if (username.equals(myUsername)) {
                            mView.addMyMessage(message, myUsername);
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
}
