package com.example.texty.SignIn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        Intent signupIntent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(signupIntent);
        finish();
    }
    public void SignIn(View v) {
        boolean valid = true;
        EditText userNameEditText = (EditText) findViewById(R.id.usernameText);
        String userName = userNameEditText.getText().toString();

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

        //TODO Validation For Password too
        if (valid){
            Authenticator.setUsername(SignInActivity.this,userName);
            Intent homepageIntent = new Intent(SignInActivity.this, HomePageActivity.class);
            Toast.makeText(getApplicationContext(), "Hello " + userName, Toast.LENGTH_LONG).show();
            startActivity(homepageIntent);
            finish();
        }
    }
}


