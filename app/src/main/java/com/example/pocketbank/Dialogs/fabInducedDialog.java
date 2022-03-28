package com.example.pocketbank.Dialogs;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.CustomdialogBinding;
public class fabInducedDialog extends DialogFragment
{
    private ImageView shopping,investment,loans,sendAndReceive;
    private TextView shoppingText,investmentText,loansText,sendAndReceiveText;
    private CustomdialogBinding customdialogBinding;
    private View view;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        customdialogBinding = CustomdialogBinding.inflate(getLayoutInflater());
        View view = customdialogBinding.getRoot();
        initializeViews();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.customAlertTheme);
        builder.setView(view);
        builder.setTitle(R.string.addTransactions);
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fabInducedDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    private void initializeViews()
    {
        shopping = customdialogBinding.imageView1;
        investment = customdialogBinding.imageView2;;
        loans = customdialogBinding.imageView3;
        sendAndReceive = customdialogBinding.imageView4;
        shoppingText = customdialogBinding.textview1;
        investmentText = customdialogBinding.textview2;
        loansText = customdialogBinding.textview3;
        sendAndReceiveText = customdialogBinding.textview4;
        setListenerForAllViews();
    }
    private void setListenerForAllViews()
    {
        setOnCLickListeners(shopping);
        setOnCLickListeners(shoppingText);
        setOnCLickListeners(investment);
        setOnCLickListeners(investmentText);
        setOnCLickListeners(sendAndReceive);
        setOnCLickListeners(sendAndReceiveText);
        setOnCLickListeners(loans);
        setOnCLickListeners(loansText);
    }
    private void setOnCLickListeners(View view)
    {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                switch (view.getId())
                {
                    case R.id.imageView_1:
                        Toast.makeText(getContext(),"Shop It",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.imageView_2:
                        break;
                    case R.id.textview_3:
                        break;
                    case R.id.textview_4:
                        break;
                    case R.id.textview_1:
                        break;
                    case R.id.textview_2:
                        break;
                    case R.id.imageView_3:
                        break;
                    case R.id.imageView_4:
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        customdialogBinding = null;
    }
}
