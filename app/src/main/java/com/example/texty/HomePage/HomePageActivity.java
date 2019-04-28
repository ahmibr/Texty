package com.example.texty.HomePage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.texty.PrivateMessage.PrivateMessageActivity;
import com.example.texty.R;
import com.example.texty.SignIn.SignInActivity;
import com.example.texty.Utilities.Authenticator;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;


public class HomePageActivity extends AppCompatActivity implements HomePageView, PopupMenu.OnMenuItemClickListener {

    private MessagesListAdapter messageadapter;
    private List<Message> arrayList;
    private final String TAG = "HomePageActivity";
    private HomePagePresenter mPresenter;
    private MediaPlayer notificationSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        arrayList = new ArrayList<Message>();
        messageadapter =new MessagesListAdapter(this, arrayList);
        ListView list = (ListView)findViewById(R.id.messages_view);
        list.setAdapter(messageadapter);
        mPresenter = new HomePagePresenter(this);
        notificationSound = MediaPlayer.create(this, R.raw.notification);
    }



    public void reSignIn(){
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
        finish();
    }

    @Override
    public void greetUser(String username) {
        Toast.makeText(getApplicationContext(), "Hello " + username, Toast.LENGTH_LONG).show();
    }


    void printToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"Socket closed");
        super.onDestroy();
        mPresenter.closeSocket();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"Main paused");
        mPresenter.onHomePagePause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"Main resumed");
        mPresenter.onHomePageResume();
    }

    @Override
    public void runThread(Runnable thread) {
        Log.d(TAG,"Runned in main");
        runOnUiThread(thread);
    }

    @Override
    public void addMyMessage(String message,String myUsername) {
        Message m = new Message(myUsername,message,1);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView)findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);
    }

    @Override
    public void addOtherMessage(String message, String username) {
        Message m = new Message(username,message,2);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView)findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);
        notificationSound.start();
    }

    @Override
    public void notifyPrivateMessage(String message, String username) {
        Intent privateMessage = new Intent(this, PrivateMessageActivity.class);
        privateMessage.putExtra("to",username);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),username);
        notification.setContentTitle("New message from "+username);
        notification.setContentText(message);
        notification.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        notification.setWhen(System.currentTimeMillis());
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.circle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, username.hashCode(), privateMessage, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(username.hashCode(),notification.build());
    }

    @Override
    public void addUsersList(List<String> usersList) {

    }

    @Override
    public void addUser(String username) {
        Message m = new Message(username,username+" joined",3);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView)findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);

    }
    @Override
    public void removeUser(String username) {
        Message m = new Message(username,username+" quited",3);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView)findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);
    }



    @Override
    public void onSendClick(View v) {
        String message = ((EditText)findViewById(R.id.Message)).getText().toString();
        ((EditText)findViewById(R.id.Message)).getText().clear();
        mPresenter.sendMessage(message);

    }

    public void onMoreClick(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        Menu menu = popup.getMenu();
        SubMenu Menu = menu.addSubMenu(1,0,1,"Members");
        List<String> users = mPresenter.getUsersList();
        for (int i=0;i<users.size();i++){
            Menu.add(1,i+1,1,users.get(i));
        }
        popup.show();

    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        List<String> users = mPresenter.getUsersList();
        switch (menuItem.getItemId()) {
            case R.id.Log_Out:
                mPresenter.logOut();
                Intent sign_in = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(sign_in);
                return true;
            case 0:
                return false;
            default:
                Intent private_message = new Intent(getApplicationContext(), PrivateMessageActivity.class);
                private_message.putExtra("to",menuItem.getTitle().toString());
                startActivity(private_message);
                return true;

        }

    }
}
