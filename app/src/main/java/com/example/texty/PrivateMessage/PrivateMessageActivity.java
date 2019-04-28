package com.example.texty.PrivateMessage;
import com.example.texty.HomePage.Message;
import com.example.texty.HomePage.MessagesListAdapter;
import com.example.texty.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PrivateMessageActivity extends AppCompatActivity implements PrivateMessageView {

    private MessagesListAdapter messageadapter;
    private List<Message> arrayList;
    private PrivateMessagePresenter mPresenter;
    private MediaPlayer notificationSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        String to = getIntent().getStringExtra("to");


        arrayList = new ArrayList<Message>();
        messageadapter = new MessagesListAdapter(this, arrayList);
        ListView list = (ListView) findViewById(R.id.messages_view);
        list.setAdapter(messageadapter);

        mPresenter = new PrivateMessagePresenter(this, to);
        TextView header = (TextView) findViewById(R.id.header);
        header.setText(to);
        Button More = (Button) findViewById(R.id.more);
        More.setVisibility(View.GONE);
        notificationSound = MediaPlayer.create(this, R.raw.notificationprivate);

//        to delete messageadapter
        //arrayList.removeAll(arrayList);
       // messageadapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void notifyPrivateMessage(String message, String username) {
        Intent privateMessage = new Intent(this, PrivateMessageActivity.class);
        privateMessage.putExtra("to", username);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), username);
        notification.setContentTitle("New message from " + username);
        notification.setContentText(message);
        notification.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        notification.setWhen(System.currentTimeMillis());
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.circle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, username.hashCode(), privateMessage, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(username.hashCode(), notification.build());
    }

    @Override
    public void runThread(Runnable thread) {
        runOnUiThread(thread);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void addMyMessage(String message, String username) {
        Message m = new Message(username, message, 1);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView) findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);
    }

    @Override
    public void onSendClick(View v) {
        String message = ((EditText) findViewById(R.id.Message)).getText().toString();
        ((EditText) findViewById(R.id.Message)).getText().clear();
        mPresenter.sendMessage(message);
    }


    @Override
    public void addOtherMessage(String message, String username) {
        Message m = new Message(username, message, 2);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView) findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);
        notificationSound.start();
    }

}
