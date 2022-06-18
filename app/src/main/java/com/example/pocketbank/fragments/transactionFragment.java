package com.example.pocketbank.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import com.example.pocketbank.MainActivity;
import com.example.pocketbank.R;
import com.example.pocketbank.adapters.transactionAdapter;
import com.example.pocketbank.databinding.FragmentTransactionBinding;
import com.example.pocketbank.model.Transaction;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class transactionFragment extends Fragment {
    private RecyclerView recyclerView;
    private transactionAdapter transactionAdapter;
    private SQLiteDatabase readableDatabase;
    private ExecutorService executorServiceTransactionFragment;
    private MaterialTextView noTransactionsToShowTextView;
    private static final String Tag = "transactionFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(Tag, "onCreate");
        transactionAdapter = new transactionAdapter();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v(Tag, "onAttach");
        readableDatabase = ((MainActivity) Objects.requireNonNull(requireHost())).readableDatabaseMainActivity;
        executorServiceTransactionFragment = ((MainActivity) Objects.requireNonNull(requireHost())).executorServiceMainActivity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(Tag, "onCreateView");
        ((MainActivity) Objects.requireNonNull(requireActivity())).bottomNavigationView.setSelectedItemId(R.id.transactions);
        FragmentTransactionBinding fragmentTransactionBinding = FragmentTransactionBinding.inflate(inflater, container, false);
        startInitialization(fragmentTransactionBinding);
        return fragmentTransactionBinding.getRoot();
    }

    private void startInitialization(FragmentTransactionBinding fragmentTransactionBinding) {
        noTransactionsToShowTextView = fragmentTransactionBinding.nothingToShow;

        recyclerView = fragmentTransactionBinding.transactionsRecyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fragmentTransactionBinding.transactionRadioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) ->
        {
            switch (checkedId) {
                case R.id.allRadioButton:
                    getTransactionsFromDatabase("all");
                    break;
                case R.id.profitRadioButton:
                    getTransactionsFromDatabase("Profit");
                    break;
                case R.id.loanRadioButton:
                    getTransactionsFromDatabase("loan");
                    break;
                case R.id.loanPaymentRadioButton:
                    getTransactionsFromDatabase("loanPayment");
                    break;
                case R.id.sendRadioButton:
                    getTransactionsFromDatabase("send");
                    break;
                case R.id.receiveRadioButton:
                    getTransactionsFromDatabase("receive");
                    break;
                case R.id.shoppingRadioButton:
                    getTransactionsFromDatabase("shopping");
                    break;
                case R.id.investmentRadioButton:
                    getTransactionsFromDatabase("Investment");
                    break;
            }

        });

        fragmentTransactionBinding.show.setOnClickListener((v) ->
        {
            if (Objects.requireNonNull(fragmentTransactionBinding.amountTextInputEditText.getText()).length() == 0) {
                fragmentTransactionBinding.amountTextInputLayout.setError(getString(R.string.pleaseEnterAmount));
                fragmentTransactionBinding.amountTextInputEditText.requestFocus();
                fragmentTransactionBinding.amountTextInputEditText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() != 0)
                            fragmentTransactionBinding.amountTextInputLayout.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }

                });
            } else
                fetchTransactionsMoreThanGivenAmount(Double.parseDouble(fragmentTransactionBinding.amountTextInputEditText.getText().toString()));
        });

        fragmentTransactionBinding.transactionRadioGroup.check(R.id.allRadioButton);

    }

    private void fetchTransactionsMoreThanGivenAmount(double givenAmount) {

        executorServiceTransactionFragment.execute(() ->
        {

            ArrayList<Transaction> transactionArrayList = new ArrayList<>();

            Cursor cursor = readableDatabase.query("transactions", null, "amount >= ?", new String[]{Double.toString(givenAmount)}, null, null, "date desc");

            while (cursor.moveToNext()) {
                Transaction transaction = new Transaction();
                transaction.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                transaction.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                transaction.setRecipient(cursor.getString(cursor.getColumnIndexOrThrow("recipient")));
                transactionArrayList.add(transaction);
            }

            cursor.close();

            ((MainActivity) Objects.requireNonNull(requireHost())).runOnUiThread(() -> showResults(transactionArrayList));

        });

    }

    private void getTransactionsFromDatabase(String transactionType)
    {

        executorServiceTransactionFragment.execute(() ->
        {
            Cursor cursor;
            ArrayList<Transaction> transactions = new ArrayList<>();

            if (!transactionType.equals("all"))
                cursor = readableDatabase.query("transactions", null, "type = ?", new String[]{transactionType}, null, null, "date desc");
            else
                cursor = readableDatabase.query("transactions", null, null, null, null, null, "date desc");

            while (cursor.moveToNext()) {
                Transaction transaction = new Transaction();
                transaction.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                transaction.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                transaction.setRecipient(cursor.getString(cursor.getColumnIndexOrThrow("recipient")));
                transactions.add(transaction);
            }

            cursor.close();

            ((MainActivity) Objects.requireNonNull(requireHost())).runOnUiThread(() -> showResults(transactions));

        });

    }

    private void showResults(ArrayList<Transaction> transactionArrayList)
    {
        if (transactionArrayList.size() > 0) {
            noTransactionsToShowTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            transactionAdapter.setData(transactionArrayList);
            recyclerView.setAdapter(transactionAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            noTransactionsToShowTextView.setVisibility(View.VISIBLE);
        }
    }

}