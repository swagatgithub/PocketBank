package com.example.pocketbank.shopping;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pocketbank.R;
//import com.example.pocketbank.databinding.ActivityAddShoppingBinding;
import com.example.pocketbank.myApplication;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Date;
public class addShopping extends AppCompatActivity
{
   /* private ActivityAddShoppingBinding activityAddShoppingBinding;
    private Button addItem,selectDate,pickItem;
    private EditText date,store,description;
    private MaterialDatePicker<Long> materialDatePicker;*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getphotobottomsheet);
        //startInitialisation();
    }
    private void startInitialisation()
    {
        /*myApplication.setStatusBarWiddgetsColor(getWindow().getDecorView());
        activityAddShoppingBinding = ActivityAddShoppingBinding.inflate(getLayoutInflater());
        setContentView(activityAddShoppingBinding.getRoot());
        addItem = activityAddShoppingBinding.addButton;
        selectDate = activityAddShoppingBinding.selectDateButton;
        pickItem = activityAddShoppingBinding.pickItemButton;
        date = activityAddShoppingBinding.dateEditText;
        store = activityAddShoppingBinding.storeInfo;
        description = activityAddShoppingBinding.descriptionShopping;
        setListeners();
        createMaterialDatePicker();*/
    }
    /*private void setListeners()
    {
        addItem.setOnClickListener(v ->
        {

        });
        selectDate.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(),getString(R.string.dateDialog)));
        pickItem.setOnClickListener(
                v ->
                {
                    pickItem.setText(R.string.changeItem);
                }
        );
    }
    private void createMaterialDatePicker()
    {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText(R.string.selectShoppingDate);
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        materialDatePicker = builder.build();
        materialDatePicker.addOnNegativeButtonClickListener(v ->
        {
            callFocus(date);
        });
        materialDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            date.setText(myApplication.getSimpleDateFormat().format(new Date(materialDatePicker.getSelection())));
        });
        materialDatePicker.addOnCancelListener(dialog ->
        {
            callFocus(date);
        });
    }
    private void callFocus(EditText editText)
    {
        if(editText.getText().length() == 0)
            date.requestFocusFromTouch();
    }*/
}