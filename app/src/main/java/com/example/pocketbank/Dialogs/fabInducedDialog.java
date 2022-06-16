package com.example.pocketbank.Dialogs;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.CustomdialogBinding;
import com.example.pocketbank.others.addInvestment;
import com.example.pocketbank.others.addLoan;
import com.example.pocketbank.others.addTransfer;
import com.example.pocketbank.others.addShopping;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class fabInducedDialog extends DialogFragment
{
    private AlertDialog transactionAlertDialog;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        CustomdialogBinding customdialogBinding = CustomdialogBinding.inflate(getLayoutInflater());
        startInitialisation(customdialogBinding);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setView(customdialogBinding.getRoot());
        builder.setNegativeButton(R.string.dismiss, (dialogInterface, i) ->
                dialogInterface.cancel());
        transactionAlertDialog = builder.create();
        return  transactionAlertDialog;
    }

    private void startInitialisation(CustomdialogBinding customdialogBinding)
    {
        customdialogBinding.shopping.setOnClickListener( v ->
        {

            Intent intent = new Intent(getContext(), addShopping.class);
            startActivity(intent);
            transactionAlertDialog.dismiss();

        });

        customdialogBinding.investment.setOnClickListener( v ->
        {

            Intent intent = new Intent(getContext() , addInvestment.class);
            startActivity(intent);
            transactionAlertDialog.dismiss();

        });

        customdialogBinding.loans.setOnClickListener( v ->
        {
            Intent intent = new Intent(getContext() , addLoan.class);
            startActivity(intent);
            transactionAlertDialog.dismiss();

        });

        customdialogBinding.sendReceive.setOnClickListener( v ->
        {
            Intent intent = new Intent(getContext() , addTransfer.class);
            startActivity(intent);
            transactionAlertDialog.dismiss();
        });

    }

}