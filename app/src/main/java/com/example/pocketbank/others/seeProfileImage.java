package com.example.pocketbank.others;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.pocketbank.databinding.ActivitySeeProfileImageBinding;
import com.google.android.material.imageview.ShapeableImageView;

public class seeProfileImage extends AppCompatActivity
{

    private ShapeableImageView shapeableImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivitySeeProfileImageBinding activitySeeProfileImageBinding = ActivitySeeProfileImageBinding.inflate(getLayoutInflater());
        shapeableImageView = activitySeeProfileImageBinding.profilePicture;
        setContentView(activitySeeProfileImageBinding.getRoot());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        shapeableImageView.setImageURI(getIntent().getParcelableExtra("imageFile"));
    }

}