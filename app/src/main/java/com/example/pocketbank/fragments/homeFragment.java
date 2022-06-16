package com.example.pocketbank.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.pocketbank.Dialogs.fabInducedDialog;
import com.example.pocketbank.MainActivity;
import com.example.pocketbank.R;
import com.example.pocketbank.adapters.transactionAdapter;
import com.example.pocketbank.databinding.FragmentHomeBinding;
import com.example.pocketbank.model.Transaction;
import com.example.pocketbank.myApplication;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class homeFragment extends Fragment
{
    private MaterialTextView currentMoney;
    private RecyclerView transactionRecyclerView;
    private com.example.pocketbank.adapters.transactionAdapter transactionAdapter;
    private final String homeFragmentTag = "homeFragment";
    private double remainedAmount;
    private ArrayList<Transaction> transactionArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        Log.v(homeFragmentTag , "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(homeFragmentTag , "onCreateView");
        return FragmentHomeBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        Log.v(homeFragmentTag , "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        FragmentHomeBinding fragmentHomeBinding = FragmentHomeBinding.bind(view);

        fragmentHomeBinding.extendedFloatingActionButton.setOnClickListener( v ->
        {
            DialogFragment dialogFragment = new fabInducedDialog();
            dialogFragment.show(getChildFragmentManager(),"FabInducedDialog");
        });

        transactionAdapter = new transactionAdapter();

        currentMoney = fragmentHomeBinding.currentMoney;

        transactionRecyclerView = fragmentHomeBinding.recyclerViewTrans;

        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        transactionRecyclerView.setHasFixedSize(true);

    }

    @Override
    public void onStart()
    {
        Log.v(homeFragmentTag, "onStart() has been called..");
        super.onStart();
        executeAllDatabaseOperations();
    }

    private void executeAllDatabaseOperations()
    {
        ExecutorService executorService = myApplication.getExecutorService();

        SQLiteDatabase readableDatabase = myApplication.readableDatabase;

        executorService.execute(() ->
        {
            try {
                Cursor cursor = readableDatabase.query("user", new String[]{"remainedAmount"}, null, null, null, null, null, null);
                cursor.moveToNext();
                remainedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("remainedAmount"));
                cursor.close();
                cursor = readableDatabase.query("transactions", null, null, null, null, null, "date desc");
                transactionArrayList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    Transaction transaction = new Transaction();
                    transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                    transaction.setTransactionId(cursor.getInt(cursor.getColumnIndexOrThrow("transactionId")));
                    transaction.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                    transaction.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                    transaction.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    transaction.setRecipient(cursor.getString(cursor.getColumnIndexOrThrow("recipient")));
                    transaction.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                    transactionArrayList.add(transaction);
                }

                cursor.close();

                ((MainActivity) requireHost()).runOnUiThread(() ->
                {
                    currentMoney.setText(getString(R.string.rupees, remainedAmount));
                    transactionAdapter.setData(transactionArrayList);
                    transactionRecyclerView.setAdapter(transactionAdapter);
                });

            } catch (Exception ee) {
                ee.printStackTrace();
                Log.e(homeFragmentTag, "error has occurred");
            }

        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("homeFragment" , "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(homeFragmentTag , "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(homeFragmentTag , "onDestroyView");
    }

}