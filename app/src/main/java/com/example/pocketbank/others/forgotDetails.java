package com.example.pocketbank.others;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pocketbank.R;
import com.example.pocketbank.databinding.ActivityForgotDetailsBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class forgotDetails extends AppCompatActivity
{

    private ActivityForgotDetailsBinding activityForgotDetailsBinding;
    private MaterialTextView forgotAllDetails,forgotEmail,forgotPassword;
    private forgotDetails forgotDetailsContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        forgotDetailsContext = this;
        activityForgotDetailsBinding = ActivityForgotDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityForgotDetailsBinding.getRoot());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        startInitialization();
    }

    private void startInitialization()
    {
        forgotAllDetails = activityForgotDetailsBinding.forgotAllDetails;
        forgotEmail = activityForgotDetailsBinding.forgotEmail;
        forgotPassword = activityForgotDetailsBinding.forgotPassword;
        MaterialToolbar toolbarForgotDetailsActivity = activityForgotDetailsBinding.toolBarForgotDetailsActivity;
        setSupportActionBar(toolbarForgotDetailsActivity);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setListeners();
    }

    private void setListeners()
    {

        forgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(getString(R.string.smallPassword));
            }
        });

        forgotEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(getString(R.string.yourEmailAddress));
            }

        });

        forgotAllDetails.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(getString(R.string.details));
            }

        });

    }

    private void startActivity(String extraInformation)
    {
        Intent intent = new Intent(forgotDetailsContext,giveYourEmailOrTelephone.class);
        intent.putExtra(getString(R.string.extraInformation),extraInformation);
        startActivity(intent);
    }
    
}