package com.example.texty.HomePage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.texty.R;
import com.example.texty.SignIn.SignInActivity;
import com.example.texty.Utilities.Authenticator;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        if(!Authenticator.isLoggedIn(this))
        {
            Intent signInIntent = new Intent(this, SignInActivity.class);
            startActivity(signInIntent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Hello " + Authenticator.getUserName(getApplicationContext()), Toast.LENGTH_LONG).show();
            final Button button = (Button) findViewById(R.id.signOutButton);

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Authenticator.setUserName(HomePageActivity.this, "");

                    Intent signInIntent = new Intent(HomePageActivity.this, SignInActivity.class);
                    startActivity(signInIntent);
                    finish();
                }
            });
        }

    }
}
