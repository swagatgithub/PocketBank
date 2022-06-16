package com.example.pocketbank.others;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.pocketbank.R;
import com.example.pocketbank.database.databaseHelper;
import com.example.pocketbank.databinding.ActivityAddShoppingBinding;
import com.example.pocketbank.myApplication;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class addShopping extends AppCompatActivity
{

    private MaterialDatePicker<Long> materialDatePicker;
    private ActivityResultLauncher<Intent> launcher;
    private ExecutorService executorServiceAddShoppingActivity;
    private SQLiteDatabase writeableDatabaseAddShoppingActivity;
    private SQLiteDatabase readableDatabaseAddShoppingActivity;
    private int nameResourceId, imageResourceId, itemId;
    private ActivityAddShoppingBinding activityAddShoppingBinding;
    private addShopping addShoppingContext;
    private boolean reCreation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        addShoppingContext = this;

        activityAddShoppingBinding = ActivityAddShoppingBinding.inflate(getLayoutInflater());

        setContentView(activityAddShoppingBinding.getRoot());

        setSupportActionBar(activityAddShoppingBinding.toolBarAddShoppingActivity);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        databaseHelper databaseHelper = new databaseHelper(this);

        executorServiceAddShoppingActivity = myApplication.getExecutorService();

        writeableDatabaseAddShoppingActivity = databaseHelper.getWritableDatabase();

        readableDatabaseAddShoppingActivity = databaseHelper.getReadableDatabase();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (Objects.requireNonNull(activityAddShoppingBinding.selectDateEditText.getText()).length() == 0)
                    activityAddShoppingBinding.selectDateEditText.requestFocus();
                executorServiceAddShoppingActivity.execute(() -> {
                    Cursor cursor = readableDatabaseAddShoppingActivity.query("items", null, null, null, null, null, null);
                    cursor.moveToLast();

                    nameResourceId = cursor.getInt(cursor.getColumnIndexOrThrow("itemNameResourceId"));

                    imageResourceId = cursor.getInt(cursor.getColumnIndexOrThrow("itemImageResourceId"));

                    itemId = cursor.getInt(cursor.getColumnIndexOrThrow("itemId"));

                    addShoppingContext.runOnUiThread(() ->
                    {

                            activityAddShoppingBinding.imageAddShoppingActivity.setImageResource(imageResourceId);
                            activityAddShoppingBinding.nameAddShoppingActivity.setText(nameResourceId);
                            activityAddShoppingBinding.itemCardView.setVisibility(View.VISIBLE);
                            activityAddShoppingBinding.add.setText(R.string.addItem);

                    });

                    cursor.close();

                });

            }

        });

        startInitialisation();

    }

    private void startInitialisation()
    {
        activityAddShoppingBinding.selectDateEditText.setOnFocusChangeListener((v, hasFocus) ->
        {
            if(hasFocus)
                activityAddShoppingBinding.selectDateInputLayout.setHelperText(getString(R.string.dateFormat));
            else
                activityAddShoppingBinding.selectDateInputLayout.setHelperText(null);
        });

        activityAddShoppingBinding.add.setOnClickListener(v ->
        {

            if (((MaterialButton) v).getText().equals(getString(R.string.pickItem)))
                launcher.launch(new Intent(this, chooseItem.class));
            else
            {
                if (checkAllFields())
                    insertRecordIntoDatabase();
            }

        });

        activityAddShoppingBinding.selectDateInputLayout.setEndIconOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "DatePickerDialog"));

        CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now());

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(calendarConstraints.build());

        builder.setTitleText(R.string.selectShoppingDate);

        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());

        materialDatePicker = builder.build();

        materialDatePicker.addOnNegativeButtonClickListener(v ->
        {
            if (Objects.requireNonNull(activityAddShoppingBinding.selectDateEditText.getText()).length() == 0)
                activityAddShoppingBinding.selectDateEditText.requestFocusFromTouch();
        });

        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {

            activityAddShoppingBinding.selectDateEditText.setText(myApplication.getSimpleDateFormat().format(new Date(selection)));

            if(activityAddShoppingBinding.selectDateEditText.hasFocus())
                activityAddShoppingBinding.selectDateEditText.clearFocus();

        });

    }

    private void setListeners(TextInputEditText textInputEditText)
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
                    if (Objects.requireNonNull(textInputEditText.getText()).length() != 0)
                        ((TextInputLayout) textInputEditText.getParent().getParent()).setError(null);
                    else
                        check_A_Field(textInputEditText);
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }

        });

    }

    private void check_A_Field(TextInputEditText textInputEditText)
    {
        if(textInputEditText.getId() == R.id.selectDateEditText)
            ((TextInputLayout)textInputEditText.getParent().getParent()).setError(getString(R.string.pleaseSelectADate));
        else if(textInputEditText.getId() == R.id.storeInfo)
            ((TextInputLayout)textInputEditText.getParent().getParent()).setError(getString(R.string.pleaseEnterStoreInfo));
        else if(textInputEditText.getId() == R.id.description)
            ((TextInputLayout)textInputEditText.getParent().getParent()).setError(getString(R.string.pleaseHaveADescription));
        else
            ((TextInputLayout)textInputEditText.getParent().getParent()).setError(getString(R.string.pleaseEnterPrice));
        textInputEditText.requestFocus();
    }

    private void insertRecordIntoDatabase()
    {
        executorServiceAddShoppingActivity.execute(() ->
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("amount", -1*Double.parseDouble(Objects.requireNonNull(activityAddShoppingBinding.priceEditText.getText()).toString()));
            contentValues.put("date", Objects.requireNonNull(activityAddShoppingBinding.selectDateEditText.getText()).toString());
            contentValues.put("type", "shopping");
            contentValues.put("userId", new utils(addShoppingContext).getUserFromSharedPreference().getUserId());
            contentValues.put("recipient", Objects.requireNonNull(activityAddShoppingBinding.storeInfo.getText()).toString());
            contentValues.put("description", Objects.requireNonNull(activityAddShoppingBinding.description.getText()).toString());
            long transactionId = writeableDatabaseAddShoppingActivity.insert("transactions", null, contentValues);
            if (transactionId != -1)
            {
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put("itemId", itemId);
                contentValues1.put("userId", new utils(addShoppingContext).getUserFromSharedPreference().getUserId());
                contentValues1.put("transactionId", transactionId);
                contentValues1.put("price", Double.parseDouble(activityAddShoppingBinding.priceEditText.getText().toString()));
                contentValues1.put("date", activityAddShoppingBinding.selectDateEditText.getText().toString());
                contentValues1.put("description", activityAddShoppingBinding.description.getText().toString());

                long shoppingId = writeableDatabaseAddShoppingActivity.insert("shopping", null, contentValues1);

                if (shoppingId != -1)
                {

                    addShoppingContext.runOnUiThread(() ->
                    {

                        Toast.makeText(addShoppingContext, R.string.itemAdded, Toast.LENGTH_SHORT).show();

                        reCreation = true;

                        activityAddShoppingBinding.add.setText(R.string.pickItem);

                        activityAddShoppingBinding.itemCardView.setVisibility(View.GONE);

                        activityAddShoppingBinding.selectDateEditText.setText(null);

                        activityAddShoppingBinding.selectDateEditText.requestFocus();

                        activityAddShoppingBinding.storeInfo.setText(null);

                        activityAddShoppingBinding.storeInfo.clearFocus();

                        activityAddShoppingBinding.description.setText(null);

                        activityAddShoppingBinding.description.clearFocus();

                        activityAddShoppingBinding.priceEditText.setText(null);

                        activityAddShoppingBinding.priceEditText.clearFocus();

                    });

                    Cursor cursor = readableDatabaseAddShoppingActivity.query("user", new String[]{"remainedAmount"}, "userId = ?", new String[]{Integer.toString(new utils(addShoppingContext).getUserFromSharedPreference().getUserId())}, null, null, null);
                    cursor.moveToNext();
                    double remainedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("remainedAmount"));
                    cursor.close();
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put("remainedAmount", remainedAmount - Double.parseDouble(Objects.requireNonNull(activityAddShoppingBinding.priceEditText.getText()).toString()));
                    int count = writeableDatabaseAddShoppingActivity.update("user", contentValues2, "userId = ?", new String[]{Integer.toString(new utils(addShoppingContext).getUserFromSharedPreference().getUserId())});
                    cursor.close();
                    if(count == 1)
                        Log.v("addShopping" ,"Record Updated Successfully");
                }

            }

        });

    }

    private boolean checkAllFields()
    {
        reCreation = false;
        if (Objects.requireNonNull(activityAddShoppingBinding.selectDateEditText.getText()).length() == 0)
        {
            activityAddShoppingBinding.selectDateInputLayout.setError(getString(R.string.pleaseSelectADate));
            activityAddShoppingBinding.selectDateEditText.requestFocus();
            setListeners(activityAddShoppingBinding.selectDateEditText);
            return false;
        }
        else if(Objects.requireNonNull(activityAddShoppingBinding.storeInfo.getText()).length() == 0) {
            activityAddShoppingBinding.storeInputLayout.setError(getString(R.string.pleaseEnterStoreInfo));
            activityAddShoppingBinding.storeInfo.requestFocus();
            setListeners(activityAddShoppingBinding.storeInfo);
            return false;
        }
        else if(Objects.requireNonNull(activityAddShoppingBinding.description.getText()).length() == 0)
        {
            activityAddShoppingBinding.descriptionLayout.setError(getString(R.string.pleaseHaveADescription));
            activityAddShoppingBinding.description.requestFocus();
            setListeners(activityAddShoppingBinding.description);
            return false;
        }
        else if(Objects.requireNonNull(activityAddShoppingBinding.priceEditText.getText()).length() == 0)
        {
            activityAddShoppingBinding.priceLayout.setError(getString(R.string.pleaseEnterPrice));
            activityAddShoppingBinding.priceEditText.requestFocus();
            setListeners(activityAddShoppingBinding.priceEditText);
            return false;
        }
        else
            return true;
    }

}