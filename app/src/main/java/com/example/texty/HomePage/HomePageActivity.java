package com.example.texty.HomePage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.texty.R;
import com.example.texty.SignIn.SignInActivity;
import com.example.texty.Utilities.Authenticator;


public class HomePageActivity extends AppCompatActivity implements HomePageView{


    private final String TAG = "HomePageActivity";
    private HomePagePresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mPresenter = new HomePagePresenter(this);

        if(!mPresenter.IsLoggedIn())
        {
            reSignIn();
        }
        else {
            mPresenter.initializeSocket();
            mPresenter.initializeChat();

            Toast.makeText(getApplicationContext(), "Hello " + Authenticator.getUsername(getApplicationContext()), Toast.LENGTH_LONG).show();
            final Button button = (Button) findViewById(R.id.signOutButton);

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Authenticator.setUsername(HomePageActivity.this, "");
                    Authenticator.setToken(HomePageActivity.this, "");

                    Intent signInIntent = new Intent(HomePageActivity.this, SignInActivity.class);
                    startActivity(signInIntent);
                    finish();
                }
            });
        }

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

    }

    @Override
    public void addOtherMessage(String message, String username) {

    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}
