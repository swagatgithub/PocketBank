package com.example.pocketbank.others;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;

import com.example.pocketbank.MainActivity;
import com.example.pocketbank.authentication.Login;

public class launcher extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        checkForUserExistence();
    }

    private void checkForUserExistence()
    {
        utils utility = new utils(this);
        if(utility.getUserFromSharedPreference() != null)
            startActivity(new Intent(this , MainActivity.class));
        else
            startActivity(new Intent(this, Login.class));
            finish();
    }

}