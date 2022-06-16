package com.example.pocketbank.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbank.R;
import com.example.pocketbank.databinding.TransactionslayoutBinding;
import com.example.pocketbank.model.Transaction;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;
import java.util.Locale;

public class transactionAdapter extends RecyclerView.Adapter<com.example.pocketbank.adapters.transactionAdapter.transactionViewHolder>
{
    private ArrayList<Transaction> transactions;

    static class transactionViewHolder extends RecyclerView.ViewHolder
    {
        public MaterialTextView amount,description,date,recipient;
        transactionViewHolder(View view)
        {
            super(view);
            amount = view.findViewById(R.id.amount);
            description = view.findViewById(R.id.description);
            date = view.findViewById(R.id.date);
            recipient = view.findViewById(R.id.recipient);
        }
    }

    @NonNull
    @Override
    public transactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        TransactionslayoutBinding transactionslayoutBinding = TransactionslayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new transactionViewHolder(transactionslayoutBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull transactionViewHolder holder, int position)
    {

        Transaction currentTransaction = transactions.get(position);

        if(currentTransaction.getAmount() < 0)
            holder.amount.setTextColor(Color.RED);
        else
            holder.amount.setTextColor(Color.GREEN);

        holder.amount.setText(String.format(Locale.getDefault() , "%.1f" ,currentTransaction.getAmount()));

        holder.date.setText(currentTransaction.getDate());

        holder.description.setText(currentTransaction.getDescription());

        holder.recipient.setText(currentTransaction.getRecipient());

    }

    @Override
    public int getItemCount()
    {
        return transactions.size();
    }

    @Override
    public long getItemId(int position)
    {
        return super.getItemId(position);
    }

    public void setData(ArrayList<Transaction> arrayList)
    {
        this.transactions = arrayList;
    }

}
