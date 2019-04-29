package com.example.texty.SignUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.texty.HomePage.HomePageActivity;
import com.example.texty.R;
import com.example.texty.SignIn.SignInActivity;
import com.example.texty.Utilities.Authenticator;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity implements SignUpView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        //Toast.makeText(getApplicationContext(), "Hello " + Authenticator.getUserName(getApplicationContext()), Toast.LENGTH_LONG).show();
    }

    public void SignUp(View v){

        boolean valid = true;
        EditText userNameEditText = (EditText) findViewById(R.id.usernameText);
        String userName = userNameEditText.getText().toString();
        EditText passwordEditText = (EditText) findViewById(R.id.passwordText);
        String password = passwordEditText.getText().toString();

        if(userName.length()==0)
        {
            userNameEditText.requestFocus();
            userNameEditText.setError("FIELD CANNOT BE EMPTY");
            valid = false ;
        }

        else if(!userName.matches("[a-zA-Z ]+"))
        {
            userNameEditText.requestFocus();
            userNameEditText.setError("ENTER ONLY ALPHABETICAL CHARACTER");
            valid = false ;
        }

        if (password.length()== 0)
        {
            passwordEditText.requestFocus();
            passwordEditText.setError("FIELD CANNOT BE EMPTY");
            valid = false ;
        }
        else if (password.length()< 8)
        {
            passwordEditText.requestFocus();
            passwordEditText.setError("PASSWORD MUST AT LEAST 8 CHARACTER");
            valid = false ;

        }
        if (valid){
            // TODO ahmed call signup in presenter and pass data to it
        }
    }
    public void SignIn(View v) {
        Intent signinIntent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(signinIntent);
        finish();
    }

    @Override
    public void onSuccess() {
        Intent homePageIntent = new Intent(SignUpActivity.this, HomePageActivity.class);
        startActivity(homePageIntent);
        finish();
    }

    @Override
    public void onFail(String error) {

        Toast.makeText(getApplicationContext(),"ERROR"+ error, Toast.LENGTH_LONG).show();
    }

    @Override
    public Context getContext() {
        return null;
    }
}

