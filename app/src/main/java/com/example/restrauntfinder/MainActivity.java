package com.example.restrauntfinder;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Go to next activitiy
    public void LogIn(View view) {
        //Check to see if the password is good then
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
        //Else some fail message
    }
}
