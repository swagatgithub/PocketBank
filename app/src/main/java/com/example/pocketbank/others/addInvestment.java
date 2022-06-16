package com.example.pocketbank.others;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.ActivityAddInvestmentBinding;
import com.example.pocketbank.myApplication;
import com.example.pocketbank.workManagers.investmentWorker;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class addInvestment extends AppCompatActivity
{

    private TextInputEditText investmentName , initialAmount, monthlyROI , initialDate , finishDate;
    private TextInputLayout investmentNameLayout , initialAmountLayout , monthlyROILayout , initialDateLayout , finishDateLayout;
    private String initialDateInString , finalDateInString;
    private Double initialAmountInDouble ,rateOfInterestInDouble;
    private boolean reCreation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        System.out.println("Base Context Is "+getBaseContext());
        ActivityAddInvestmentBinding activityAddInvestmentBinding = ActivityAddInvestmentBinding.inflate(getLayoutInflater());
        setContentView(activityAddInvestmentBinding.getRoot());
        startInitialization(activityAddInvestmentBinding);
    }

    private void startInitialization(ActivityAddInvestmentBinding activityAddInvestmentBinding)
    {
        setSupportActionBar(activityAddInvestmentBinding.toolBarInvestmentActivity);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Log.v("investmentFragment" , "startInitialization");
        investmentName = activityAddInvestmentBinding.investmentNameEditText;
        initialAmount = activityAddInvestmentBinding.initialAmountEditText;
        monthlyROI = activityAddInvestmentBinding.monthlyROIEditText;
        initialDate = activityAddInvestmentBinding.initialDateEditText;
        finishDate = activityAddInvestmentBinding.finishDateEditText;
        investmentNameLayout = activityAddInvestmentBinding.investmentNameLayout;
        initialAmountLayout = activityAddInvestmentBinding.initialAmountLayout;
        monthlyROILayout = activityAddInvestmentBinding.monthlyROILayout;
        initialDateLayout = activityAddInvestmentBinding.initialDateLayout;
        finishDateLayout = activityAddInvestmentBinding.finishDateLayout;
        activityAddInvestmentBinding.addInvestment.setOnClickListener(this::onClick);
        CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now());

        initialDate.setOnFocusChangeListener( (View v, boolean hasFocus) ->
        {
            if(hasFocus)
                initialDateLayout.setHelperText(getString(R.string.dateFormat));
            else
                initialDateLayout.setHelperText(null);
        });

        finishDate.setOnFocusChangeListener((View v, boolean hasFocus) ->
        {
            if(hasFocus)
                finishDateLayout.setHelperText(getString(R.string.dateFormat));
            else
                finishDateLayout.setHelperText(null);
        });

        initialDateLayout.setEndIconOnClickListener(v ->
        {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setCalendarConstraints(calendarConstraints.build());
            builder.setTitleText(R.string.selectInitialDate);
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
            MaterialDatePicker<Long> materialDatePicker = builder.build();


            materialDatePicker.addOnNegativeButtonClickListener( negative ->
            {

                if(Objects.requireNonNull(initialDate.getText()).length() > 0)
                    initialDate.clearFocus();
                else
                    initialDate.requestFocus();

            });

            materialDatePicker.addOnPositiveButtonClickListener( positive ->
            {

                initialDate.setText(myApplication.getSimpleDateFormat().format( new Date( positive)));
                initialDate.clearFocus();

            });

            materialDatePicker.show(getSupportFragmentManager() , "Select Initial Date");

        });

        finishDateLayout.setEndIconOnClickListener(v->
        {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setCalendarConstraints(calendarConstraints.build());
            builder.setTitleText(R.string.selectFinalDate);
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
            MaterialDatePicker<Long> materialDatePicker = builder.build();
            materialDatePicker.addOnNegativeButtonClickListener( negative ->
            {
                if(Objects.requireNonNull(finishDate.getText()).length() > 0)
                    finishDate.clearFocus();
                else
                    finishDate.requestFocus();
            });

            materialDatePicker.addOnPositiveButtonClickListener( positive ->
            {
                finishDate.setText(myApplication.getSimpleDateFormat().format( new Date(positive)));
                finishDate.clearFocus();
            });

            materialDatePicker.show(getSupportFragmentManager() , "Select Finish Date");

        });

    }

    private void executeDbOperations()
    {
        SQLiteDatabase writeableDatabaseInvestmentFragment = myApplication.writeableDatabase;
        SQLiteDatabase readableDatabaseInvestmentFragment = myApplication.readableDatabase;
        ExecutorService executorService = myApplication.getExecutorService();

        executorService.execute( () ->
        {
            Cursor cursor = readableDatabaseInvestmentFragment.query("user", new String[]{"remainedAmount","userId","name"}, null , null , null , null , null);
            cursor.moveToNext();
            double remainedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("remainedAmount"));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
            String userName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            cursor.close();
            ContentValues contentValues = new ContentValues();
            contentValues.put("amount" , -1*Double.parseDouble(Objects.requireNonNull(initialAmount.getText()).toString()));
            contentValues.put("date" , Objects.requireNonNull(initialDate.getText()).toString());
            contentValues.put("type" , getString(R.string.investment));
            contentValues.put("userId" , userId);
            contentValues.put("recipient" , Objects.requireNonNull(investmentName.getText()).toString());
            contentValues.put("description" ,"Investment");

            long returnValue = writeableDatabaseInvestmentFragment.insert("transactions" , null , contentValues);

            if(returnValue != -1)
            {
                remainedAmount -= Double.parseDouble(initialAmount.getText().toString());
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put("remainedAmount" ,remainedAmount);
                writeableDatabaseInvestmentFragment.update("user" , contentValues1 , null ,null);
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("amount", Double.parseDouble(Objects.requireNonNull(initialAmount.getText()).toString()));
                contentValues2.put("monthlyRoi", Double.parseDouble(Objects.requireNonNull(monthlyROI.getText()).toString()));
                contentValues2.put("name", investmentName.getText().toString() );
                contentValues2.put("initDate" , initialDate.getText().toString());
                contentValues2.put("finishDate" , Objects.requireNonNull(finishDate.getText()).toString());
                contentValues2.put("userId" , userId );
                contentValues2.put("transactionId", returnValue);

                long insertValue = writeableDatabaseInvestmentFragment.insert("investments" , null ,contentValues2);

                if(insertValue != -1)
                {

                    this.runOnUiThread(() ->
                    {
                        Toast.makeText(this, R.string.investmentAddedSuccessfully, Toast.LENGTH_SHORT).show();
                        reCreation = true;
                        investmentName.setText(null);
                        investmentName.requestFocus();
                        initialAmount.setText(null);
                        initialAmount.clearFocus();
                        monthlyROI.setText(null);
                        monthlyROI.clearFocus();
                        initialDate.setText(null);
                        initialDate.clearFocus();
                        finishDate.setText(null);
                        finishDate.clearFocus();

                    });

                    try
                    {

                        Calendar calendar = Calendar.getInstance();
                        Date initDate = myApplication.getSimpleDateFormat().parse(initialDateInString);
                        calendar.setTime(Objects.requireNonNull(initDate));

                        Calendar calendar1 = Calendar.getInstance();
                        Date finalDate = myApplication.getSimpleDateFormat().parse(finalDateInString);
                        calendar1.setTime(Objects.requireNonNull(finalDate));

                        int diffYear = calendar1.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
                        int diffMonth = diffYear * 12 + calendar1.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);

                        Data inputData = new Data.Builder().putInt("userId" , userId)
                                .putString("type" , getString(R.string.profit))
                                .putString("description" , getString(R.string.investmentProfit))
                                .putString("recipient",userName)
                                .putDouble("amount" ,initialAmountInDouble*rateOfInterestInDouble/100)
                                .build();

                        Constraints constraints =new Constraints.Builder().setRequiresBatteryNotLow(true).build();

                        int monthGap = 0;
                        for(int i=0;i<diffMonth;i++)
                        {
                            monthGap+=30;
                            WorkRequest workRequest = new OneTimeWorkRequest.Builder(investmentWorker.class)
                                    .setConstraints(constraints)
                                    .setInputData(inputData)
                                    .setInitialDelay(monthGap, TimeUnit.DAYS)
                                    .addTag("investment Profit").build();
                            WorkManager.getInstance(this).enqueue(workRequest);
                        }
                    }
                    catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                }

            }

        });

    }

    private boolean checkDetails()
    {
        boolean returnValue = true;
        reCreation = false ;
        if (Objects.requireNonNull(investmentName.getText()).length() == 0) {
            returnValue = false;
            investmentNameLayout.setError(getString(R.string.investmentNameError));
            investmentNameLayout.requestFocus();
            addTextWatcher(investmentName , getString(R.string.investmentNameError));
        } else if (Objects.requireNonNull(initialAmount.getText()).length() == 0) {
            returnValue = false;
            initialAmountLayout.setError(getString(R.string.initialAmountError));
            initialAmountLayout.requestFocus();
            addTextWatcher(initialAmount , getString(R.string.initialAmountError));

        } else if (Objects.requireNonNull(monthlyROI.getText()).length() == 0) {
            returnValue = false;
            monthlyROILayout.setError(getString(R.string.monthlyROIError));
            monthlyROILayout.requestFocus();
            addTextWatcher(monthlyROI , getString(R.string.monthlyROIError));
        } else if (Objects.requireNonNull(initialDate.getText()).length() == 0) {
            returnValue = false;
            initialDateLayout.setError(getString(R.string.initialDateError));
            initialDateLayout.requestFocus();
            addTextWatcher(initialDate , getString(R.string.initialDateError ) );
        } else if (Objects.requireNonNull(finishDate.getText()).length() == 0) {
            returnValue = false;
            finishDateLayout.setError(getString(R.string.finishDateError));
            finishDateLayout.requestFocus();
            addTextWatcher(finishDate , getString(R.string.finishDateError));
        }
        return returnValue;
    }

    private void addTextWatcher(TextInputEditText textInputEditText , String errorMessage)
    {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (!reCreation)
                {
                    if (s.length() > 0)
                        ((TextInputLayout) textInputEditText.getParent().getParent()).setError(null);
                    else
                        ((TextInputLayout) textInputEditText.getParent().getParent()).setError(errorMessage);
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    private void onClick(View v) {

        if (checkDetails()) {
            initialDateInString = Objects.requireNonNull(initialDate.getText()).toString();
            finalDateInString = Objects.requireNonNull(finishDate.getText()).toString();
            rateOfInterestInDouble = Double.parseDouble(Objects.requireNonNull(monthlyROI.getText()).toString());
            initialAmountInDouble = Double.parseDouble(Objects.requireNonNull(initialAmount.getText()).toString());
            executeDbOperations();

        }

    }
}