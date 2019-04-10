package com.example.texty.HomePage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.texty.R;
import com.example.texty.SignIn.SignInActivity;
import com.example.texty.Utilities.Authenticator;


public class HomePageActivity extends AppCompatActivity implements HomePageView{


    private final String TAG = "HomePageActivity";
    private HomePagePresenter mPresenter;
    private MediaPlayer notificationSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        mPresenter = new HomePagePresenter(this);

        notificationSound = MediaPlayer.create(this, R.raw.notification);

        if(!mPresenter.IsLoggedIn())
        {
            reSignIn();
        }
        else {
            mPresenter.initializeSocket();
            mPresenter.initializeChat();

            Toast.makeText(getApplicationContext(), "Hello " + Authenticator.getUsername(getApplicationContext()), Toast.LENGTH_LONG).show();

        }
    }

    public void send(View v){


        String message = ((EditText)findViewById(R.id.Message)).getText().toString();
        ((EditText)findViewById(R.id.Message)).getText().clear();
        mPresenter.sendMessage(message);


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
        runOnUiThread(thread);
    }

    @Override
    public void addMyMessage(String message) {
        TextView view = (TextView)findViewById(R.id.temp);
        view.setText(message);
//        ListView list = (ListView)findViewById(R.id.messages_view);
//        final ArrayList<String> list = new ArrayList<String>();
//        list.add(message);
        notificationSound.start();
    }

    @Override
    public void addOtherMessage(String message, String username) {

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
    public Context getContext() {
        return getApplicationContext();
    }
}
