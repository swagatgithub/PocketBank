package com.example.pocketbank;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbank.Dialogs.fabInducedDialog;
import com.example.pocketbank.adapters.transactionAdapter;
import com.example.pocketbank.authentication.Login;
import com.example.pocketbank.bottomsheets.getPhotoBottomSheet;
import com.example.pocketbank.databinding.ActivityMainBinding;
import com.example.pocketbank.model.Shopping;
import com.example.pocketbank.model.Transaction;
import com.example.pocketbank.others.seeProfileImage;
import com.example.pocketbank.shopping.addShopping;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
public class MainActivity extends AppCompatActivity
{
    private static final String TAG ="MainActivity";
    private ActivityMainBinding activityMainBinding;
    private com.example.pocketbank.utils utility;
    private MainActivity mainActivity;
    private TextView currentMoney,userName,userEmail;
    private LineChart lineChart;
    private BarChart barChart;
    private RecyclerView recyclerView;
    private ExecutorService executorServiceMainActivity;
    private SQLiteDatabase readableDatabaseMainActivity;
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Transaction> transactionArrayList,profitableTransactions;
    private ArrayList<Shopping> shoppingArrayList;
    private transactionAdapter transactionAdapter;
    private int currentYear,userId;
    private Calendar calendar;
    private BarDataSet barDataSet;
    private BarData barData;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private getPhotoBottomSheet getPhotoBottomSheet;
    private ShapeableImageView userProfileImage,addPhotoIcon;
    private View headerView;
    private ActivityResultLauncher<Intent> cropImage;
    private File userProfileImageFile,appImageDirectory,croppedImageFile;
    private Uri imageFileContentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("onCreate...Main");
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new ChangeClipBounds());
        mainActivity = this;
        checkForUserExistence();
    }

    private void startInitialisation()
    {
        calendar = Calendar.getInstance();
        currentYear = myApplication.currentYear();
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        drawerLayout = activityMainBinding.mainActivityDrawerLayout;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        currentMoney = activityMainBinding.currentMoney;
        lineChart = activityMainBinding.profitChart;
        barChart = activityMainBinding.barChart;
        recyclerView = activityMainBinding.recyclerViewTrans;
        navigationView = activityMainBinding.navigationView;
        headerView = navigationView.getHeaderView(0);
        userProfileImage = headerView.findViewById(R.id.yourPhoto);
        addPhotoIcon = headerView.findViewById(R.id.addPhoto);
        userName = headerView.findViewById(R.id.fullName);
        userEmail = headerView.findViewById(R.id.email);
        bottomNavigationView = activityMainBinding.bottomNavigationView;
        toolbar = activityMainBinding.toolBar;
        floatingActionButton = activityMainBinding.floatingActionButton;
        getPhotoBottomSheet = new getPhotoBottomSheet();
        executorServiceMainActivity = myApplication.getExecutorService();
        readableDatabaseMainActivity = myApplication.readableDatabase;
        bottomNavigationView.setSelectedItemId(R.id.home);
        transactionArrayList = new ArrayList<>();
        profitableTransactions = new ArrayList<>();
        shoppingArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        appImageDirectory = new File(getFilesDir() ,"appImages");
        userProfileImageFile = new File(appImageDirectory, "userProfileImage.jpg");
        croppedImageFile = new File(appImageDirectory,"croppedProfileImage.jpg");
        imageFileContentUri = FileProvider.getUriForFile(mainActivity, "com.example.pocketbank.fileprovider", userProfileImageFile);
        userId = utility.getUserFromSharedPreference().getUserId();
        executeAllDbOperations();
        setSupportActionBar(toolbar);
        setListeners();
        createDialog();
        registerActivityResults();

    }

    private void checkForUserExistence()
    {
        utility = new com.example.pocketbank.utils(mainActivity);
        if(utility.getUserFromSharedPreference() != null)
        {
            Toast.makeText(mainActivity, "Welcome " + utility.getUserFromSharedPreference().getName(), Toast.LENGTH_SHORT).show();
            startInitialisation();
            setContentView( activityMainBinding.getRoot());
        }
        else
        {
            Intent intent = new Intent(mainActivity, Login.class);
            startActivity(intent);
            finish();
        }
    }

    private void executeAllDbOperations()
    {
        executorServiceMainActivity.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Transaction transaction;
                    Cursor cursor;
                    cursor = readableDatabaseMainActivity.query("user",new String[]{"remainedAmount","email","name"},"userId=?",new String[]{String.valueOf(userId)},null,null,null);
                    if(cursor.moveToFirst())
                    {
                        currentMoney.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("remainedAmount"))) + getString(R.string.rupees));
                        userEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                        userName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    }
                    cursor = readableDatabaseMainActivity.query("transactions",null,"userId=?",new String[]{String.valueOf(userId)},null,null,"date desc");
                    while(cursor.moveToNext())
                    {
                        transaction = new Transaction();
                        transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                        transaction.setTransactionId(cursor.getInt(cursor.getColumnIndexOrThrow("transactionId")));
                        transaction.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                        transaction.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                        transaction.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                        transaction.setRecipient(cursor.getString(cursor.getColumnIndexOrThrow("recipient")));
                        transaction.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                        transactionArrayList.add(transaction);
                    }
                    transactionAdapter = new transactionAdapter(transactionArrayList);
                    recyclerView.setAdapter(transactionAdapter);
                    cursor = readableDatabaseMainActivity.query("transactions",null,"userId=? AND type=?",new String[]{String.valueOf(userId),"profit"},null,null,null);
                    while(cursor.moveToNext())
                    {
                        transaction = new Transaction();
                        transaction.setTransactionId(cursor.getInt(cursor.getColumnIndexOrThrow("transactionId")));
                        transaction.setUserId(userId);
                        transaction.setType("profit");
                        transaction.setRecipient(cursor.getString(cursor.getColumnIndexOrThrow("recipient")));
                        transaction.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                        transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                        transaction.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                        profitableTransactions.add(transaction);
                    }
                    if(profitableTransactions.size() != 0)
                    {
                        ArrayList<Entry> entries = new ArrayList<>();
                        int transactionYear,month;
                        boolean monthPresent;
                        for(Transaction transaction1 : profitableTransactions)
                        {
                            calendar.setTime(myApplication.getSimpleDateFormat().parse(transaction1.getDate()));
                            transactionYear = calendar.get(Calendar.YEAR);
                            if (currentYear == transactionYear)
                            {
                                month = calendar.get(Calendar.MONTH) + 1;
                                monthPresent = false;
                                for(Entry entry : entries)
                                {
                                    if (entry.getX() == month)
                                    {
                                        entry.setY(entry.getY()+(float)transaction1.getAmount());
                                        monthPresent=true;
                                        break;
                                    }
                                }
                                if (!monthPresent)
                                {
                                    Entry entry = new Entry();
                                    entry.setX(month);
                                    entry.setY((float) transaction1.getAmount());
                                    entries.add(entry);
                                }
                            }
                        }
                        LineDataSet lineDataSet = new LineDataSet(entries,"Profit Chart");
                        lineDataSet.setColor(Color.parseColor("#B54269"));
                        lineDataSet.setCircleColor(Color.parseColor("#B54269"));
                        lineDataSet.setHighLightColor(Color.parseColor("#B54269"));
                        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        lineDataSet.setDrawFilled(true);
                        LineData lineData = new LineData(lineDataSet);
                        lineChart.setDescription(null);
                        //lineChart.getXAxis().setSpaceMin(1);
                        //lineChart.getXAxis().setSpaceMax(1);
                        lineChart.getXAxis().setAxisMinimum(1);
                        lineChart.getXAxis().setAxisMaximum(12);
                        lineChart.getXAxis().setEnabled(false);
                        lineChart.getAxisRight().setEnabled(false);
                        lineChart.getAxisLeft().setDrawGridLines(false);
                        lineChart.getAxisLeft().setAxisMinimum(1);
                        lineChart.getAxisLeft().setAxisMaximum(200000);
                        lineChart.getAxisLeft().setDrawZeroLine(true);
                       // lineChart.getAxisLeft().setLabelCount(10);
                       // lineChart.getAxisLeft().setGranularityEnabled(true);
                        //lineChart.getAxisLeft().setGranularity(10);

                        lineChart.setData(lineData);
                        lineChart.fitScreen();
                    }
                    cursor.close();
                }
                catch (Exception ee)
                {
                    ee.printStackTrace();
                }
            }
        });
    }

    private void setListeners()
    {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean value = false;
                if (item.getItemId() == R.id.loan) {
                    Toast.makeText(getBaseContext(), "Loan Clicked", Toast.LENGTH_SHORT).show();
                    value = true;
                } else if (item.getItemId() == R.id.investment) {
                    System.out.println("investment is called...");
                    value = true;
                } else if (item.getItemId() == R.id.home) {
                    value = true;
                } else if (item.getItemId() == R.id.transactions) {
                    value = true;
                } else if (item.getItemId() == R.id.statistics) {
                    value = true;
                }
                return value;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DialogFragment dialogFragment = new fabInducedDialog();
                dialogFragment.show(getSupportFragmentManager(), "Floating Dialog..");
            }

        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if (item.getItemId() == R.id.signOut) {
                    alertDialog.show();
                } else if (item.getItemId() == R.id.share)
                {
                    String githubLink = getString(R.string.sendingText) + "";
                    Intent implicitIntent = new Intent();
                    implicitIntent.putExtra(Intent.EXTRA_TEXT, githubLink);
                    implicitIntent.setAction(Intent.ACTION_SEND);
                    implicitIntent.setType("text/plain");
                    try {
                        startActivity(implicitIntent);
                    } catch (ActivityNotFoundException activityNotFoundException) {
                        Toast.makeText(mainActivity, getString(R.string.noAppFound), Toast.LENGTH_SHORT).show();
                        activityNotFoundException.printStackTrace();
                    }
                }
                return true;
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!croppedImageFile.exists())
                {
                    getPhotoBottomSheet.show(getSupportFragmentManager(), "Bottom Sheet");
                }
                else
                {
                    Intent intent = new Intent(mainActivity,seeProfileImage.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mainActivity, userProfileImage, "profilePic");
                    intent.putExtra("imageFile",FileProvider.getUriForFile(mainActivity, "com.example.pocketbank.fileprovider",croppedImageFile));
                    startActivity(intent,options.toBundle());
                }

            }

        });

        addPhotoIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getPhotoBottomSheet.show(getSupportFragmentManager(),"Bottom Sheet");
            }
        });

        activityMainBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mainActivity, addShopping.class));
            }
        });

    }

    private void createDialog()
    {
        builder = new AlertDialog.Builder(mainActivity);
        builder.setCustomTitle(getLayoutInflater().inflate(R.layout.customtitle,null));
        builder.setMessage("Are You Sure Want To Log Out?.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                startActivity(new Intent(MainActivity.this, Login.class));
                utility.signOutUser();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    private void registerActivityResults()
    {
        cropImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
        {
            @Override
            public void onActivityResult(ActivityResult result)
            {
                CropImage.ActivityResult croppedImage = CropImage.getActivityResult(result.getData());
                if (result.getResultCode() == RESULT_OK)
                {
                    Uri resultUri = croppedImage.getUri();
                    userProfileImage.setImageURI(resultUri);
                    Toast.makeText(mainActivity,R.string.profilePhotoUpdated,Toast.LENGTH_SHORT).show();
                    File returnImageFile = new File(getCacheDir(),resultUri.getLastPathSegment());
                    returnImageFile.renameTo(croppedImageFile);
                    userProfileImageFile.delete();
                }
                else if (result.getResultCode() == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    Exception error = croppedImage.getError();
                    error.printStackTrace();
                }
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
                appImageDirectory.mkdir();
                userProfileImageFile.createNewFile();
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

    @Override
    protected void onResume()
    {
        super.onResume();
        setValueToViews();
    }

    private void setValueToViews()
    {
        if(croppedImageFile.exists())
            userProfileImage.setImageURI(FileProvider.getUriForFile(mainActivity, "com.example.pocketbank.fileprovider",croppedImageFile));
    }

    public void deleteFile()
    {
        croppedImageFile.delete();
    }
}