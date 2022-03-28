package com.example.pocketbank.others;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.view.View;
import android.view.Window;

import com.example.pocketbank.R;
import com.example.pocketbank.databinding.ActivitySeeProfileImageBinding;
import com.example.pocketbank.myApplication;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;

public class seeProfileImage extends AppCompatActivity
{
    private ActivitySeeProfileImageBinding activitySeeProfileImageBinding;
    private ShapeableImageView shapeableImageView;
    private MaterialToolbar materialToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new ChangeClipBounds());
        getWindow().setAllowEnterTransitionOverlap(true);
        activitySeeProfileImageBinding = ActivitySeeProfileImageBinding.inflate(getLayoutInflater());
        shapeableImageView = activitySeeProfileImageBinding.profilePicture;
        materialToolbar = activitySeeProfileImageBinding.seeProfileImageToolbar;
        setContentView(activitySeeProfileImageBinding.getRoot());

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        setListeners();
        setValueToViews();
    }

    private void setValueToViews()
    {
        shapeableImageView.setImageURI((Uri)getIntent().getParcelableExtra("imageFile"));
    }

    private void setListeners()
    {
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finishAfterTransition();
            }
        });
    }
}