package com.example.pocketbank.others;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.ActivityAddTransferBinding;
import com.example.pocketbank.myApplication;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class addTransfer extends AppCompatActivity
{

    private TextInputLayout amountTextInputLayout , recipientTextInputLayout , dateTextInputLayout , descriptionTextInputLayout ;
    private TextInputEditText amountEditText , recipientEditText , dateEditText , descriptionEditText ;
    private RadioButton send;
    private addTransfer addTransferContext;
    private static final String Tag = "addTransfer";
    private boolean reCreation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.v(Tag , "onCreate() has been called..");
        super.onCreate(savedInstanceState);
        addTransferContext = this;
        ActivityAddTransferBinding activityAddTransferBinding = ActivityAddTransferBinding.inflate(getLayoutInflater());
        startInitialization(activityAddTransferBinding);
        setContentView(activityAddTransferBinding.getRoot());
    }

    private void startInitialization(ActivityAddTransferBinding activityAddTransferBinding)
    {
        setSupportActionBar(activityAddTransferBinding.toolbarAddTransferActivity);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        amountTextInputLayout = activityAddTransferBinding.amountLayoutAddTransferActivity;
        amountEditText = activityAddTransferBinding.amountEditTextAddTransferActivity;
        recipientTextInputLayout = activityAddTransferBinding.recipientLayoutAddTransferActivity;
        recipientEditText = activityAddTransferBinding.recipientEditTextAddTransferActivity;
        dateTextInputLayout = activityAddTransferBinding.dateLayoutAddTransferActivity;
        dateEditText = activityAddTransferBinding.dateEditTextAddTransferActivity;
        descriptionTextInputLayout = activityAddTransferBinding.descriptionLayoutAddTransferActivity;
        descriptionEditText = activityAddTransferBinding.descriptionEditTextAddTransferActivity;
        send = activityAddTransferBinding.send;
        activityAddTransferBinding.dateLayoutAddTransferActivity.setEndIconOnClickListener((v) ->
        {

            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTitleText(R.string.selectTransferDate)
                    .setCalendarConstraints(new CalendarConstraints.Builder()
                            .setValidator(DateValidatorPointBackward.now() ).build());

            MaterialDatePicker<Long> materialDatePicker = builder.build();

            materialDatePicker.addOnPositiveButtonClickListener((Long selection) ->
            {

                    dateEditText.setText(myApplication.getSimpleDateFormat().format( new Date(selection)));
                    dateEditText.clearFocus();

            });

            materialDatePicker.addOnNegativeButtonClickListener((k) ->
            {
                if(Objects.requireNonNull(dateEditText.getText()).length() == 0)
                    dateEditText.requestFocus();
            });
            materialDatePicker.show(getSupportFragmentManager() , "DatePickerDialog");
        });

        dateEditText.setOnFocusChangeListener((View v, boolean hasFocus) ->
        {
            if (hasFocus)
                dateTextInputLayout.setHelperText(getString(R.string.dateFormat));
            else
                dateTextInputLayout.setHelperText(null);
        });

        activityAddTransferBinding.addTransfer.setOnClickListener((v) ->
        {
            if (validateAllFields())
                insertRecordIntoDatabase();
        });

    }

    private void insertRecordIntoDatabase()
    {

        ExecutorService executorService = myApplication.getExecutorService();
        executorService.execute(() ->
        {

            SQLiteDatabase readableDatabase = myApplication.readableDatabase;

            SQLiteDatabase writeableDatabase = myApplication.writeableDatabase;

            String type;

            utils utils = new utils(this);

            if(send.isChecked())
                type = "send";
            else
                type = "receive";

            ContentValues contentValues = new ContentValues();
            if (type.equals("send"))
                contentValues.put("amount" , -1*Double.parseDouble(Objects.requireNonNull(amountEditText.getText()).toString()));
            else
                contentValues.put("amount" , Double.parseDouble(Objects.requireNonNull(amountEditText.getText()).toString()));
            contentValues.put("type" ,type);
            contentValues.put("date" , Objects.requireNonNull(dateEditText.getText()).toString());
            contentValues.put("recipient" , Objects.requireNonNull(recipientEditText.getText()).toString());
            contentValues.put("description" , Objects.requireNonNull(descriptionEditText.getText()).toString());
            contentValues.put("userId" , utils.getUserFromSharedPreference().getUserId());
            long returnValue = writeableDatabase.insert("transactions", null, contentValues);

            if (returnValue != -1) {
                Cursor cursor = readableDatabase.query("user", new String[]{"remainedAmount"}, null, null, null, null, null);
                cursor.moveToNext();
                double remainedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("remainedAmount"));
                cursor.close();
                if (type.equals("send"))
                    remainedAmount -= Double.parseDouble(amountEditText.getText().toString());
                else
                    remainedAmount += Double.parseDouble(amountEditText.getText().toString());
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put("remainedAmount", remainedAmount);
                int affectedRows = writeableDatabase.update("user", contentValues1, null, null);
                if(affectedRows > 0)
                {
                    addTransferContext.runOnUiThread(() ->
                    {
                        reCreation = true;
                        amountEditText.setText(null);
                        recipientEditText.setText(null);
                        dateEditText.setText(null);
                        descriptionEditText.setText(null);
                        descriptionEditText.clearFocus();
                        amountEditText.clearFocus();
                        recipientEditText.clearFocus();
                        dateEditText.clearFocus();
                        send.setChecked(true);

                    });
                }
            }
        });
    }

    private boolean validateAllFields()
    {
        reCreation = false;
        if(Objects.requireNonNull(amountEditText.getText()).length() == 0)
        {
            amountEditText.requestFocus();
            addTextChanger(amountEditText,getString(R.string.invalidAmount));
            amountTextInputLayout.setError(getString(R.string.invalidAmount));
            return false;
        }
        else if(Objects.requireNonNull(recipientEditText.getText()).length() == 0)
        {
            recipientEditText.requestFocus();
            addTextChanger(recipientEditText,getString(R.string.invalidRecipient));
            recipientTextInputLayout.setError(getString(R.string.invalidRecipient));
            return false;
        }
        else if(Objects.requireNonNull(dateEditText.getText()).length() == 0)
        {
            dateEditText.requestFocus();
            addTextChanger(dateEditText,getString(R.string.pleaseSelectADate));
            dateTextInputLayout.setError(getString(R.string.pleaseSelectADate));
            return false;
        }
        else if(Objects.requireNonNull(descriptionEditText.getText()).length() == 0)
        {
            descriptionEditText.requestFocus();
            addTextChanger(descriptionEditText,getString(R.string.pleaseHaveADescription));
            descriptionTextInputLayout.setError(getString(R.string.pleaseHaveADescription));
            return false;
        }
        else
            return true;
    }

    private void addTextChanger(TextInputEditText textInputEditText ,String errorText)
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

                if (!reCreation)
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