package com.example.pocketbank;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.pocketbank.authentication.Login;
import com.example.pocketbank.Dialogs.getPhotoBottomSheet;
import com.example.pocketbank.databinding.ActivityMainBinding;
import com.example.pocketbank.fragments.homeFragment;
import com.example.pocketbank.fragments.investmentFragment;
import com.example.pocketbank.fragments.loanFragment;
import com.example.pocketbank.fragments.statisticsFragment;
import com.example.pocketbank.fragments.transactionFragment;
import com.example.pocketbank.others.seeProfileImage;
import com.example.pocketbank.others.utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG ="MainActivity";
    private utils utility;
    private MainActivity mainActivityContext;
    private MaterialTextView userName,userEmail;
    public ExecutorService executorServiceMainActivity;
    public SQLiteDatabase readableDatabaseMainActivity;
    public BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;
    private String email,name;
    public int userId;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private getPhotoBottomSheet getPhotoBottomSheet;
    private ShapeableImageView userProfileImage,addPhotoIcon;
    private ActivityResultLauncher<Intent> cropImage;
    private File userProfileImageFile,appImageDirectory,croppedImageFile;
    private Uri imageFileContentUri;
    private MenuItem previousItem;
    //private boolean firstTimeTransactionFragment = true , firstTimeLoanFragment = true , firstTimeInvestmentFragment = true , firstTimeStatisticsFragment = true , firstTimeHomeFragment = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer , homeFragment.class , null , "homeFragment").commit();

        ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        activityMainBinding.bottomNavigationView.setOnItemReselectedListener((item) ->
            Log.v(TAG , "onNavigationItemReselected"));

        activityMainBinding.bottomNavigationView.setOnItemSelectedListener( item ->
        {
            if(item.getItemId() == R.id.home)
            {
                doNotSelect(previousItem);
                toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_32);
                toolbar.setTitle(R.string.app_name);
                toolbar.setTag(getString(R.string.navigationDrawer));
                item.setIcon(R.drawable.ic_baseline_home_24);
                while(!Objects.equals(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).getTag(), "homeFragment"))
                    onBackPressed();
                previousItem = item;
            }
            else if (item.getItemId() == R.id.investment)
            {
                if(!Objects.equals(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).getTag(), "investmentFragment"))
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer , investmentFragment.class , null ,"investmentFragment").addToBackStack(null).commit();
                doNotSelect(previousItem);
                toolbar.setTitle(R.string.investments);
                toolbar.setNavigationIcon(R.drawable.arrow_back_24);
                toolbar.setTag(getString(R.string.backArrow));
                item.setIcon(R.drawable.ic_baseline_currency_rupee_24);
                previousItem = item;
            }
            else if (item.getItemId() == R.id.loan)
            {
                if(!Objects.equals(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).getTag(), "loanFragment"))
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, loanFragment.class, null, "loanFragment").addToBackStack(null).commit();
                doNotSelect(previousItem);
                toolbar.setTitle(R.string.loan);
                toolbar.setNavigationIcon(R.drawable.arrow_back_24);
                toolbar.setTag(getString(R.string.backArrow));
                item.setIcon(R.drawable.ic_baseline_event_available_24);
                previousItem = item;
            }
            else if(item.getItemId() == R.id.transactions)
            {
                if (!Objects.equals(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).getTag(), "transactionFragment"))
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, transactionFragment.class, null, "transactionFragment").addToBackStack(null).commit();
                doNotSelect(previousItem);
                toolbar.setTitle(R.string.transactions);
                toolbar.setNavigationIcon(R.drawable.arrow_back_24);
                toolbar.setTag(getString(R.string.backArrow));
                item.setIcon(R.drawable.ic_baseline_swap_vert_24);
                previousItem = item;
            }
            else
            {
                if (!Objects.equals(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).getTag(), "statisticsFragment"))
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, statisticsFragment.class, null, "statisticsFragment").addToBackStack(null).commit();
                doNotSelect(previousItem);
                toolbar.setTitle(R.string.statistics);
                toolbar.setNavigationIcon(R.drawable.arrow_back_24);
                toolbar.setTag(getString(R.string.backArrow));
                item.setIcon(R.drawable.ic_baseline_trending_up_24);
                previousItem = item;
            }
            return true;
        });

        setContentView(activityMainBinding.getRoot());

        startInitialisation(activityMainBinding);



    }

    @Override
    protected void onStart()
    {
        Log.v(TAG,"onStart() has been called..");
        super.onStart();
        executeAllDbOperations();
    }

    @Override
    protected void onResume()
    {
        Log.v(TAG , "onResume() has been called..");
        super.onResume();
        setValueToViews();

    }

    private void startInitialisation(ActivityMainBinding activityMainBinding)
    {

        mainActivityContext = this;

        executorServiceMainActivity = myApplication.getExecutorService();

        readableDatabaseMainActivity = myApplication.readableDatabase;

        utility = new utils(this);

        getPhotoBottomSheet = new getPhotoBottomSheet();

        drawerLayout = activityMainBinding.mainActivityDrawerLayout;

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        navigationView = activityMainBinding.navigationView;

        bottomNavigationView = activityMainBinding.bottomNavigationView;

        toolbar = activityMainBinding.toolBar;

        setSupportActionBar(activityMainBinding.toolBar);

        View headerView = navigationView.getHeaderView(0);

        userProfileImage = headerView.findViewById(R.id.yourPhoto);

        addPhotoIcon = headerView.findViewById(R.id.addPhoto);

        userName = headerView.findViewById(R.id.fullName);

        userEmail = headerView.findViewById(R.id.email);

        appImageDirectory = new File(getFilesDir() ,"appImages");

        userProfileImageFile = new File(appImageDirectory, "userProfileImage.jpg");

        croppedImageFile = new File(appImageDirectory,"croppedProfileImage.jpg");

        imageFileContentUri = FileProvider.getUriForFile(mainActivityContext, "com.example.pocketBank.fileProvider", userProfileImageFile);

        userId = utility.getUserFromSharedPreference().getUserId();

        registerActivityResults();

        setListeners();

    }

    private void executeAllDbOperations()
    {
        executorServiceMainActivity.execute(() ->
        {
            try {

                Cursor cursor = readableDatabaseMainActivity.query("user", new String[]{"email", "name"}, "userId=?", new String[]{String.valueOf(userId)}, null, null, null);

                if (cursor.moveToFirst())
                {
                    email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                }

                cursor.close();

                mainActivityContext.runOnUiThread(() ->
                {
                    userEmail.setText(email);
                    userName.setText(name);
                });

            }

            catch (Exception ee)
            {
                ee.printStackTrace();
                Log.e(TAG, "error has occurred");
            }

        });

    }

    private void setListeners()
    {
        toolbar.setNavigationOnClickListener( (v) ->
        {
            if(toolbar.getTag().equals(getString(R.string.backArrow)))
                onBackPressed();
            else
                drawerLayout.openDrawer(GravityCompat.START);

        });

        navigationView.setNavigationItemSelectedListener(item ->
        {
                if (item.getItemId() == R.id.signOut)
                    showDialog();
                else if (item.getItemId() == R.id.share)
                {
                    String githubLink = getString(R.string.sendingText) + "";
                    Intent implicitIntent = new Intent();
                    implicitIntent.putExtra(Intent.EXTRA_TEXT, githubLink);
                    implicitIntent.setAction(Intent.ACTION_SEND);
                    implicitIntent.setType("text/plain");
                    try {
                        startActivity(implicitIntent);
                    } catch (ActivityNotFoundException activityNotFoundException) {
                        Toast.makeText(mainActivityContext, getString(R.string.noAppFound), Toast.LENGTH_SHORT).show();
                        activityNotFoundException.printStackTrace();
                    }
                }

                return true;

        });

        userProfileImage.setOnClickListener(v ->
        {
                if(!croppedImageFile.exists())
                {
                    getPhotoBottomSheet.show(getSupportFragmentManager(), "Bottom Sheet");
                }
                else
                {
                    Intent intent = new Intent(mainActivityContext,seeProfileImage.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mainActivityContext, userProfileImage, "profilePic");
                    intent.putExtra("imageFile",FileProvider.getUriForFile(mainActivityContext, "com.example.pocketBank.fileProvider",croppedImageFile));
                    startActivity(intent,options.toBundle());
                }

        });

        addPhotoIcon.setOnClickListener( v -> getPhotoBottomSheet.show(getSupportFragmentManager(),"Bottom Sheet"));

    }

    private void doNotSelect(MenuItem previousItem)
    {

        if (previousItem != null)
        {
            if (previousItem.getItemId() == R.id.loan)
                previousItem.setIcon(R.drawable.ic_outline_event_available_24);
            else if (previousItem.getItemId() == R.id.transactions)
                previousItem.setIcon(R.drawable.ic_outline_swap_vert_24);
            else if (previousItem.getItemId() == R.id.investment)
                previousItem.setIcon(R.drawable.ic_outline_currency_rupee_24);
            else if (previousItem.getItemId() == R.id.home)
                previousItem.setIcon(R.drawable.ic_outline_home_24);
            else
                previousItem.setIcon(R.drawable.ic_outline_trending_up_24);
        }

    }

    private void showDialog()
    {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(mainActivityContext);
        materialAlertDialogBuilder.setTitle("Are You Sure Want To Log Out?.");
        materialAlertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(GoogleSignIn.getLastSignedInAccount(mainActivityContext) != null)
                {
                     myApplication.getGoogleSignInClient().signOut().addOnCompleteListener(mainActivityContext, task -> {
                         System.out.println("Inside This..");
                         signOut();
                     });
                }
                else
                    signOut();
            }

            private void signOut()
            {
                startActivity(new Intent(mainActivityContext, Login.class));
                utility.signOutUser();
                finish();
            }

        });

        materialAlertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        materialAlertDialogBuilder.show();

    }

    private void registerActivityResults()
    {
        cropImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            CropImage.ActivityResult croppedImage = CropImage.getActivityResult(result.getData());

            if (result.getResultCode() == RESULT_OK)
            {
                Uri resultUri = Objects.requireNonNull(croppedImage).getUri();

                userProfileImage.setImageURI(resultUri);

                if(!appImageDirectory.exists()) {
                    boolean created = appImageDirectory.mkdir();
                    Log.v(TAG, Boolean.toString(created));
                }

                Toast.makeText(mainActivityContext,R.string.profilePhotoUpdated,Toast.LENGTH_SHORT).show();

                File returnImageFile = new File(getCacheDir(),resultUri.getLastPathSegment());

                boolean renameValue = returnImageFile.renameTo(croppedImageFile);

                Log.v("MainActivity" , Boolean.toString(renameValue));

                if(userProfileImageFile.delete())
                    Log.v(TAG , "userProfileImageDeletedSuccessfully");
            }
            else if (result.getResultCode() == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = Objects.requireNonNull(croppedImage).getError();
                error.printStackTrace();
            }

        });

    }

    public Uri getUri()
    {
        if(userProfileImageFile.exists())
            return imageFileContentUri;
        else
        {
            try
            {
                boolean directoryCreated = appImageDirectory.mkdir();
                boolean fileCreated = userProfileImageFile.createNewFile();
                Log.v(TAG , Boolean.toString(directoryCreated));
                Log.v(TAG , Boolean.toString(fileCreated));
                return imageFileContentUri;
            }
            catch (Exception ee)
            {
                ee.printStackTrace();
                return null;
            }
        }

    }

    public ShapeableImageView getUserProfileImage()
    {
        return userProfileImage;
    }

    public ActivityResultLauncher<Intent> getCropImage()
    {
        return cropImage;
    }

    public Uri getImageFileContentUri()
    {
        return imageFileContentUri;
    }

    public File getUserProfileImageFile()
    {
        return croppedImageFile;
    }

    private void setValueToViews()
    {
        if (croppedImageFile.exists())
            userProfileImage.setImageURI(FileProvider.getUriForFile(mainActivityContext, "com.example.pocketBank.fileProvider", croppedImageFile));
    }

    public void deleteFile()
    {
        boolean deleted = croppedImageFile.delete();
        Log.v(TAG ,Boolean.toString(deleted));
    }

}