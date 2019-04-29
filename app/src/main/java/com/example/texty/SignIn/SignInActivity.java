package com.example.texty.SignIn;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.example.texty.SignUp.SignUpPresenter;
import com.example.texty.Utilities.Authenticator;

public class SignInActivity extends AppCompatActivity implements SignInView{

    private SignInPresenter mPresenter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        mPresenter = new SignInPresenter(this);
    }

    public void SignUp(View v) {

        //String userName = ((EditText) findViewById(R.id.usernameText)).getText().toString();
        Intent signupIntent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(signupIntent);
        finish();
    }
    public void SignIn(View v) {
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

            progressDialog = ProgressDialog.show(SignInActivity.this, "Login",
                    "Please wait for a while.", true);

            mPresenter.signIn(userName,password);

        }
    }

    @Override
    public void onSuccess() {
        progressDialog.dismiss();
        Intent homepageIntent = new Intent(SignInActivity.this, HomePageActivity.class);
//        Toast.makeText(getApplicationContext(), "Hello " + userName, Toast.LENGTH_LONG).show();
        startActivity(homepageIntent);
        finish();
    }

    @Override
    public void onFail(String error) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(),error, Toast.LENGTH_LONG).show();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}


