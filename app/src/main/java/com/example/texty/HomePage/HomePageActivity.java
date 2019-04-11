package com.example.texty.HomePage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.texty.R;
import com.example.texty.SignIn.SignInActivity;
import com.example.texty.Utilities.Authenticator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToLongBiFunction;

public class HomePageActivity extends AppCompatActivity implements HomePageView, PopupMenu.OnMenuItemClickListener {

    private MessagesListAdapter messageadapter;
    private List<Message> arrayList;

    //private List<String>menu;
    //private ArrayAdapter<String> menuadapter;
    private String my_name ="reem ";
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

        if(!mPresenter.IsLoggedIn())
        {
            reSignIn();
        }
        else {
            mPresenter.initializeSocket();
            mPresenter.initializeChat();
            my_name = Authenticator.getUsername(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Hello " + Authenticator.getUsername(getApplicationContext()), Toast.LENGTH_LONG).show();

        }
    }

    public void OnSendClick(View v){
        String message = ((EditText)findViewById(R.id.Message)).getText().toString();
        ((EditText)findViewById(R.id.Message)).getText().clear();
        mPresenter.sendMessage(message);
    }
    public void onMoreClick(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }


    void reSignIn(){
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
        finish();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"Main resumed");
    }

    @Override
    public void runThread(Runnable thread) {
        Log.d(TAG,"Runned in main");
        runOnUiThread(thread);
    }

    @Override
    public void addMyMessage(String message) {
        Message m = new Message(my_name,message,1);
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
        Intent i = new Intent(getApplicationContext(), Test.class);
        i.putExtra("username",username);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),username);
        notification.setContentTitle("New message from "+username);
        notification.setContentText(message);
        notification.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        notification.setWhen(System.currentTimeMillis());
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.circle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        notification.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1,notification.build());
    }

    @Override
    public void addUsersList(List<String> usersList) {

    }

    @Override
    public void addUser(String username) {
        Message m = new Message(username,"Joined",3);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView)findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);
    }

    @Override
    public void removeUser(String username) {
        Message m = new Message(username,"quited",3);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView)findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.Log_Out:
                printToast("item 2 is clicked");
                return true;
            case R.id.Show_Friends:
                printToast("item 1 is clicked");
                return true;
            default:
                return false;

        }

    }
}
