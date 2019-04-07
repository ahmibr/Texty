package com.example.texty.SignIn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.texty.HomePage.HomePageActivity;
import com.example.texty.R;
import com.example.texty.SignUp.SignUpActivity;
import com.example.texty.Utilities.Authenticator;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

    }

    public void SignUp(View v) {

        String userName = ((EditText) findViewById(R.id.usernameText)).getText().toString();
//        Authenticator.setUserName(SignInActivity.this,userName);

        Intent signupIntent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(signupIntent);
        finish();
    }
    public void SignIn(View v) {

        String userName = ((EditText) findViewById(R.id.usernameText)).getText().toString();
        Authenticator.setUsername(SignInActivity.this,userName);

        Intent homepageIntent = new Intent(SignInActivity.this, HomePageActivity.class);
        startActivity(homepageIntent);
        finish();
    }
}


