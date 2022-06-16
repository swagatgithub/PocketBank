package com.example.pocketbank.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.pocketbank.MainActivity;
import com.example.pocketbank.adapters.loansAdapter;
import com.example.pocketbank.databinding.FragmentLoanBinding;
import com.example.pocketbank.model.loan;
import com.example.pocketbank.myApplication;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class loanFragment extends Fragment
{

    private RecyclerView recyclerView;
    private com.example.pocketbank.adapters.loansAdapter loansAdapter ;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        loansAdapter = new loansAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentLoanBinding fragmentLoanBinding = FragmentLoanBinding.inflate(getLayoutInflater(), container, false);
        recyclerView = fragmentLoanBinding.loanRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return fragmentLoanBinding.getRoot();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        executeAllDbOperations();
    }

    private void executeAllDbOperations()
    {
        ExecutorService executorService = myApplication.getExecutorService();

        executorService.execute(() ->
        {
            SQLiteDatabase writeableDatabase = myApplication.writeableDatabase;

            Cursor cursor = writeableDatabase.query("loans" , null , null , null ,null , null , "initDate desc" );

            ArrayList<loan> loansArrayList = new ArrayList<>();

            while(cursor.moveToNext())
            {

                loan loan = new loan();
                loan.setLoanType(cursor.getString(cursor.getColumnIndexOrThrow("loanType")));
                loan.setLenderName(cursor.getString(cursor.getColumnIndexOrThrow("lenderName")));
                loan.setInitialAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("initAmount")));
                loan.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("initDate")));
                loan.setFinishDate(cursor.getString(cursor.getColumnIndexOrThrow("finishDate")));
                loan.setMonthlyPayment(cursor.getDouble(cursor.getColumnIndexOrThrow("monthlyPayment")));
                loan.setMonthlyROI(cursor.getDouble(cursor.getColumnIndexOrThrow("monthlyROI")));
                loansArrayList.add(loan);

            }

            cursor.close();

            ((MainActivity) requireContext()).runOnUiThread(() ->
            {

                loansAdapter.setLoans(loansArrayList);
                Log.v("loanFragment" , Integer.toString(loansArrayList.size()));
                recyclerView.setAdapter(loansAdapter);

            });

        });

    }

}