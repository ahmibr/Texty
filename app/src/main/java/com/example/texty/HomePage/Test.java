package com.example.texty.HomePage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.texty.R;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toast.makeText(getApplicationContext(),getIntent().getExtras().getString("username"),Toast.LENGTH_LONG).show();
    }
}
