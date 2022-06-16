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
import android.view.View;
import android.widget.Toast;

import com.example.pocketbank.R;
import com.example.pocketbank.databinding.ActivityAddLoanBinding;
import com.example.pocketbank.myApplication;
import com.example.pocketbank.workManagers.loanWorker;
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

public class addLoan extends AppCompatActivity
{

    private TextInputLayout loanTypeTextInputLayout , initialAmountTextInputLayout , monthlyROITextInputLayout , monthlyPaymentTextInputLayout , initialDateTextInputLayout , finishDateTextInputLayout , lenderNameTextInputLayout ;
    private TextInputEditText loanTypeTextInputEditText , initialAmountTextInputEditText , monthlyROITextInputEditText , monthlyPaymentTextInputEditText , initialDateTextInputEditText , finishDateTextInputEditText , lenderNameTextInputEditText ;
    private boolean reCreation;
    private addLoan addLoanContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addLoanContext = this ;
        ActivityAddLoanBinding activityAddLoanBinding = ActivityAddLoanBinding.inflate(getLayoutInflater());
        setSupportActionBar(activityAddLoanBinding.toolBarAddLoanActivity);
        setContentView(activityAddLoanBinding.getRoot());
        startInitialisation(activityAddLoanBinding);
    }

    private void startInitialisation(ActivityAddLoanBinding activityAddLoanBinding)
    {

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        loanTypeTextInputLayout = activityAddLoanBinding.loanTypeTextInputLayout;
        loanTypeTextInputEditText = activityAddLoanBinding.loanTypeEditText;
        lenderNameTextInputEditText = activityAddLoanBinding.lenderNameEditText;
        lenderNameTextInputLayout = activityAddLoanBinding.lenderNameTextInputLayout;
        initialAmountTextInputLayout = activityAddLoanBinding.initialAmountLayoutAddLoanActivity;
        initialAmountTextInputEditText = activityAddLoanBinding.initialAmountEditTextAddLoanActivity;
        monthlyROITextInputLayout = activityAddLoanBinding.monthlyROILayoutAddLoanActivity;
        monthlyROITextInputEditText = activityAddLoanBinding.monthlyROIEditTextAddLoanActivity;
        monthlyPaymentTextInputLayout = activityAddLoanBinding.monthlyPaymentLayout;
        monthlyPaymentTextInputEditText = activityAddLoanBinding.monthlyPaymentEditText;
        initialDateTextInputLayout = activityAddLoanBinding.initialDateLayoutAddLoanActivity;
        initialDateTextInputEditText = activityAddLoanBinding.initialDateEditTextAddLoanActivity;
        finishDateTextInputLayout = activityAddLoanBinding.finishDateLayoutAddLoanActivity;
        finishDateTextInputEditText = activityAddLoanBinding.finishDateEditTextAddLoanActivity;

        activityAddLoanBinding.addLoan.setOnClickListener( v ->
        {
            if(validateAllFields())
            {
                executeDatabaseOperations();
            }
        });

        initialDateTextInputEditText.setOnFocusChangeListener( (View v ,boolean hasFocus) ->
        {
            if(hasFocus)
                initialDateTextInputLayout.setHelperText(getString(R.string.dateFormat));
            else
                initialDateTextInputLayout.setHelperText(null);
        });

        finishDateTextInputEditText.setOnFocusChangeListener( (View v ,boolean hasFocus) ->
        {
            if(hasFocus)
                finishDateTextInputLayout.setHelperText(getString(R.string.dateFormat));
            else
                finishDateTextInputLayout.setHelperText(null);
        });

        initialDateTextInputLayout.setEndIconOnClickListener( v ->
        {
            MaterialDatePicker.Builder<Long> builder =MaterialDatePicker.Builder.datePicker();
            CalendarConstraints.Builder builder1 = new CalendarConstraints.Builder();
            CalendarConstraints calendarConstraints = builder1.setValidator(DateValidatorPointForward.now()).build();
            MaterialDatePicker<Long> materialDatePicker = builder.setTitleText(R.string.selectInitialDate).
                    setCalendarConstraints(calendarConstraints).
                    setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

            materialDatePicker.show(getSupportFragmentManager() , "datePicker");

            materialDatePicker.addOnPositiveButtonClickListener( selection ->
                initialDateTextInputEditText.setText(myApplication.getSimpleDateFormat().format(new Date(selection))));

            materialDatePicker.addOnNegativeButtonClickListener(p ->
            {
                if(Objects.requireNonNull(initialDateTextInputEditText.getText()).length() == 0)
                    initialDateTextInputEditText.requestFocus();

            });

        });

        finishDateTextInputLayout.setEndIconOnClickListener( v ->
        {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText(R.string.selectFinalDate);
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
            builder.setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build());
            MaterialDatePicker<Long> materialDatePicker = builder.build();
            materialDatePicker.show(getSupportFragmentManager() , "datePicker");

            materialDatePicker.addOnNegativeButtonClickListener( k ->
            {
                if (Objects.requireNonNull(finishDateTextInputEditText.getText()).length() == 0)
                    finishDateTextInputEditText.requestFocus();

            });

            materialDatePicker.addOnPositiveButtonClickListener(h ->
                finishDateTextInputEditText.setText(myApplication.getSimpleDateFormat().format(new Date(h))));

        });

    }

    private void executeDatabaseOperations()
    {

        ExecutorService executorService = myApplication.getExecutorService();

        SQLiteDatabase writeableDatabase = myApplication.writeableDatabase;

        SQLiteDatabase readableDatabase = myApplication.readableDatabase;

        executorService.execute(() ->
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("amount" , Double.parseDouble(Objects.requireNonNull(initialAmountTextInputEditText.getText()).toString()));
            contentValues.put("date", Objects.requireNonNull(initialDateTextInputEditText.getText()).toString());
            contentValues.put("type" , "loan");
            contentValues.put("userId" , new utils(this).getUserFromSharedPreference().getUserId());
            contentValues.put("recipient" , new utils(this).getUserFromSharedPreference().getName());
            contentValues.put("description" , Objects.requireNonNull(loanTypeTextInputEditText.getText()).toString());
            long insertValue = writeableDatabase.insert("transactions",null ,contentValues);
            if(insertValue != -1)
            {
                Cursor cursor = readableDatabase.query("user",new String[]{"remainedAmount"},null ,null , null ,null,null);
                cursor.moveToNext();

                double remainedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("remainedAmount"));

                cursor.close();

                remainedAmount += Double.parseDouble(initialAmountTextInputEditText.getText().toString());

                ContentValues contentValues1 = new ContentValues();

                contentValues1.put("remainedAmount" ,remainedAmount);

                int noOfRows = writeableDatabase.update("user",contentValues1 ,null,null);

                if(noOfRows != 0)
                {
                    ContentValues contentValues2 = new ContentValues();

                    contentValues2.put("initDate" ,initialDateTextInputEditText.getText().toString());

                    contentValues2.put("finishDate" , Objects.requireNonNull(finishDateTextInputEditText.getText()).toString());

                    contentValues2.put("initAmount" , Double.parseDouble(initialAmountTextInputEditText.getText().toString()));

                    contentValues2.put("monthlyPayment" ,Double.parseDouble(Objects.requireNonNull(monthlyPaymentTextInputEditText.getText()).toString()));

                    contentValues2.put("monthlyROI" ,Double.parseDouble(Objects.requireNonNull(monthlyROITextInputEditText.getText()).toString()));

                    contentValues2.put("loanType" , Objects.requireNonNull(loanTypeTextInputEditText.getText()).toString());

                    contentValues2.put("lenderName",Objects.requireNonNull(lenderNameTextInputEditText.getText()).toString());

                    contentValues2.put("loanRemainedAmount" ,Double.parseDouble(initialAmountTextInputEditText.getText().toString()));

                    long returnValue = writeableDatabase.insert("loans",null,contentValues2);

                    if(returnValue != -1)
                    {

                        Cursor cursor1 = readableDatabase.query("loans" , new String[]{"loanId"} ,null ,null ,null ,null ,null);

                        cursor1.moveToNext();

                        int loanId = cursor1.getInt(cursor1.getColumnIndexOrThrow("loanId"));

                        cursor1.close();

                        int monthGap = 0 , diffMonth = 0 ;

                        try
                        {
                            Calendar calendar = Calendar.getInstance();

                            Date initDate = myApplication.getSimpleDateFormat().parse(initialDateTextInputEditText.getText().toString());

                            calendar.setTime(Objects.requireNonNull(initDate));

                            Calendar calendar1 = Calendar.getInstance();

                            Date finishDate = myApplication.getSimpleDateFormat().parse(finishDateTextInputEditText.getText().toString());

                            calendar1.setTime(Objects.requireNonNull(finishDate));

                            int diffYear = calendar1.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);

                            diffMonth = diffYear * 12 + calendar1.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);

                        }

                        catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }

                        Constraints.Builder constraintBuilder = new Constraints.Builder();

                        constraintBuilder.setRequiresBatteryNotLow(true);

                        Data.Builder inputDataBuilder = new Data.Builder();

                        inputDataBuilder.putInt("loanId" , loanId);

                        inputDataBuilder.putDouble("amount" , -1*Double.parseDouble(monthlyPaymentTextInputEditText.getText().toString()));

                        inputDataBuilder.putString("type" ,"loanPayment");

                        inputDataBuilder.putString("recipient" , Objects.requireNonNull(lenderNameTextInputEditText.getText()).toString());

                        inputDataBuilder.putString("description" , Objects.requireNonNull(loanTypeTextInputEditText.getText()).toString());

                        inputDataBuilder.putInt("userId" ,new utils(addLoanContext).getUserFromSharedPreference().getUserId());

                        Data inputData = inputDataBuilder.build();

                        for(int i=0;i < diffMonth;i++)
                        {

                            monthGap +=30;

                            WorkRequest workRequest = new OneTimeWorkRequest.Builder(loanWorker.class)
                                    .setConstraints(constraintBuilder.build())
                                    .setInputData(inputData)
                                    .setInitialDelay(monthGap , TimeUnit.DAYS)
                                    .addTag("loan")
                                    .build();

                            WorkManager.getInstance(addLoanContext).enqueue(workRequest);

                        }

                        addLoanContext.runOnUiThread(() ->
                        {

                            Toast.makeText(addLoanContext, R.string.loanAddedSuccessfully,Toast.LENGTH_SHORT  ).show();
                            reCreation = true;
                            loanTypeTextInputEditText.setText(null);
                            lenderNameTextInputEditText.setText(null);
                            initialAmountTextInputEditText.setText(null);
                            monthlyROITextInputEditText.setText(null);
                            monthlyPaymentTextInputEditText.setText(null);
                            initialDateTextInputEditText.setText(null);
                            finishDateTextInputEditText.setText(null);
                            loanTypeTextInputEditText.clearFocus();
                            lenderNameTextInputEditText.clearFocus();
                            initialAmountTextInputEditText.clearFocus();
                            monthlyROITextInputEditText.clearFocus();
                            monthlyPaymentTextInputEditText.clearFocus();
                            initialDateTextInputEditText.clearFocus();
                            finishDateTextInputEditText.clearFocus();
                            loanTypeTextInputEditText.requestFocus();

                        });

                    }

                }

            }

        });

    }

    private boolean validateAllFields()
    {
        reCreation = false;
        if(Objects.requireNonNull(loanTypeTextInputEditText.getText()).length() == 0) {
            loanTypeTextInputLayout.setError(getString(R.string.pleaseEnterLoanName));
            loanTypeTextInputEditText.requestFocus();
            addTextWatcher(loanTypeTextInputEditText, getString(R.string.pleaseEnterLoanName));
            return false;
        }
        else if(Objects.requireNonNull(initialAmountTextInputEditText.getText()).length() == 0) {
            initialAmountTextInputLayout.setError(getString(R.string.initialAmountError));
            initialAmountTextInputEditText.requestFocus();
            addTextWatcher(initialAmountTextInputEditText, getString(R.string.initialAmountError));
            return false;
        }
        else if(Objects.requireNonNull(monthlyROITextInputEditText.getText()).length() == 0)
        {
            monthlyROITextInputLayout.setError(getString(R.string.monthlyROIError));
            monthlyROITextInputEditText.requestFocus();
            addTextWatcher(monthlyROITextInputEditText , getString(R.string.monthlyROIError));
            return false;
        }
        else if(Objects.requireNonNull(monthlyPaymentTextInputEditText.getText()).length() == 0)
        {
            monthlyPaymentTextInputLayout.setError(getString(R.string.monthlyPaymentError));
            monthlyPaymentTextInputEditText.requestFocus();
            addTextWatcher(monthlyPaymentTextInputEditText, getString(R.string.monthlyPaymentError));
            return false;
        }
        else if(Objects.requireNonNull(initialDateTextInputEditText.getText()).length() == 0) {
            initialDateTextInputLayout.setError(getString(R.string.initialDateError));
            initialDateTextInputEditText.requestFocus();
            addTextWatcher(initialDateTextInputEditText, getString(R.string.initialDateError));
            return false;
        }
        else if(Objects.requireNonNull(finishDateTextInputEditText.getText()).length() == 0)
        {
            finishDateTextInputLayout.setError(getString(R.string.finishDateError));
            finishDateTextInputEditText.requestFocus();
            addTextWatcher(finishDateTextInputEditText , getString(R.string.finishDateError));
            return false;
        }
        else if(Objects.requireNonNull(lenderNameTextInputEditText.getText()).length() == 0)
        {
            lenderNameTextInputLayout.setError(getString(R.string.lenderNameError));
            lenderNameTextInputEditText.requestFocus();
            addTextWatcher(lenderNameTextInputEditText , getString(R.string.lenderNameError));
            return false;
        }
        else
            return true;
    }

    private void addTextWatcher(TextInputEditText textInputEditText , String errorText)
    {
        textInputEditText.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if(!reCreation)
                {
                    if (s.length() > 0)
                        ((TextInputLayout) textInputEditText.getParent().getParent()).setError(null);
                    else
                        ((TextInputLayout) textInputEditText.getParent().getParent()).setError(errorText);
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }

        });

    }

}