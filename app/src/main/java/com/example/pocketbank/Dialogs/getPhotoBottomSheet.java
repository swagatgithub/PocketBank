package com.example.pocketbank.Dialogs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.pocketbank.MainActivity;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.GetphotobottomsheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.Objects;

public class getPhotoBottomSheet extends BottomSheetDialogFragment
{

    private ShapeableImageView gallery,camera,delete;
    private ActivityResultLauncher<Uri> activityResultLauncher;
    private ActivityResultLauncher<Intent> openGallery;
    private MaterialTextView removePhoto;
    private BottomSheetDialogFragment bottomSheetDialogFragment;
    private MainActivity mainActivity;
    private AlertDialog removeDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        mainActivity =(MainActivity)getActivity();

        createRemoveDialog();

        bottomSheetDialogFragment = this;

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), (result)->
        {
                if(result)
                {
                    bottomSheetDialogFragment.dismiss();
                    mainActivity.getCropImage().launch(CropImage.activity(mainActivity.getImageFileContentUri()).getIntent(requireContext()));
                }
                else
                {
                    bottomSheetDialogFragment.dismiss();
                }

        });

        openGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {

            if(result.getResultCode() == Activity.RESULT_OK)
            {
                bottomSheetDialogFragment.dismiss();
                mainActivity.getCropImage().launch(CropImage.activity(Objects.requireNonNull(result.getData()).getData()).getIntent(requireContext()));
            }

        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        GetphotobottomsheetBinding getphotobottomsheetBinding = GetphotobottomsheetBinding.inflate(getLayoutInflater(),container,false);
        gallery = getphotobottomsheetBinding.gallery;
        camera = getphotobottomsheetBinding.camera;
        delete = getphotobottomsheetBinding.delete;
        removePhoto = getphotobottomsheetBinding.deleteText;
        return getphotobottomsheetBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if(!mainActivity.getUserProfileImageFile().exists())
        {
            delete.setVisibility(View.GONE);
            removePhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setListeners();
    }

    private void setListeners()
    {

        camera.setOnClickListener(v -> launchCamera());

        gallery.setOnClickListener(v -> openGallery());

        delete.setOnClickListener(v -> removePhoto());

    }

    private void launchCamera()
    {
        activityResultLauncher.launch(mainActivity.getUri());
    }

    private void openGallery()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        openGallery.launch(intent);
    }

    private void removePhoto()
    {
        bottomSheetDialogFragment.dismiss();
        removeDialog.show();
    }

    private void createRemoveDialog()
    {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        builder.setTitle(R.string.removeProfilePhoto);

        builder.setPositiveButton(R.string.yes, (dialog, which) ->
        {

            mainActivity.getUserProfileImage().setImageResource(R.drawable.blankprofile);

            Toast.makeText(mainActivity.getBaseContext(), R.string.photoRemoved ,Toast.LENGTH_SHORT).show();

            mainActivity.deleteFile();

        });

        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

        builder.create();

        removeDialog = builder.create();

    }

}
