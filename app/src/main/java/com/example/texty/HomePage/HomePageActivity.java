package com.example.texty.HomePage;

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

import com.example.texty.Utilities.Constants;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;



public class HomePageActivity extends AppCompatActivity {


    private final String TAG = "HomePageActivity";
    private HomePagePresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mPresenter = new HomePagePresenter(this);

        if(!Authenticator.isLoggedIn(this))
        {
            reSignIn();
        }
        else {
            mPresenter.initializeSocket();
            mPresenter.initializeChat();
        }

    }

    void reSignIn(){
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
        finish();
    }


    void printToast(String message){

    }
}
