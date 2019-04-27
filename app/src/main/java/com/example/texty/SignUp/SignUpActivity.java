package com.example.texty.SignUp;

import android.content.Intent;
import android.os.Bundle;

import com.example.texty.HomePage.HomePageActivity;
import com.example.texty.R;
import com.example.texty.SignIn.SignInActivity;
import com.example.texty.Utilities.Authenticator;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        //Toast.makeText(getApplicationContext(), "Hello " + Authenticator.getUserName(getApplicationContext()), Toast.LENGTH_LONG).show();
    }

    public void SignIn(View v) {
        //TODO add values then goto homepage m4 sign (make sure meen AHMED)
        Intent signinIntent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(signinIntent);
        finish();
    }
}

