package com.example.texty.HomePage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.texty.R;
import com.example.texty.SignIn.SignInActivity;
import com.example.texty.Utilities.Authenticator;

import java.util.ArrayList;


public class HomePageActivity extends AppCompatActivity implements HomePageView{


    private final String TAG = "HomePageActivity";
    private HomePagePresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        mPresenter = new HomePagePresenter(this);

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
    }

    @Override
    public void addOtherMessage(String message, String username) {

    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}
