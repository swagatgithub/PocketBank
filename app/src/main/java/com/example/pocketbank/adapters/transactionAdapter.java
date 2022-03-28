package com.example.pocketbank.adapters;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.TransactionslayoutBinding;
import com.example.pocketbank.model.Transaction;
import java.util.ArrayList;
public class transactionAdapter extends RecyclerView.Adapter
{
    private TransactionslayoutBinding transactionslayoutBinding;
    private ArrayList<Transaction> transactions;
    private transactionViewHolder transactionViewHolder;
    private Transaction currentTransaction;
    private transactionAdapter transactionAdapter;

    class transactionViewHolder extends RecyclerView.ViewHolder
    {
        private TextView amount,description,date,transactionId,sender;
        transactionViewHolder(View view)
        {
            super(view);
            initializeViews();
        }
        private void initializeViews()
        {
            amount = transactionslayoutBinding.amount;
            description = transactionslayoutBinding.description;
            date = transactionslayoutBinding.date;
            transactionId = transactionslayoutBinding.transactionId;
            sender = transactionslayoutBinding.sender;
        }
    }

    public transactionAdapter(ArrayList<Transaction> transactions)
    {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        transactionslayoutBinding = TransactionslayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new transactionViewHolder(transactionslayoutBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        transactionViewHolder = (transactionViewHolder)holder;
        currentTransaction = transactions.get(position);
        if(currentTransaction.getAmount() > 0)
        {
            transactionViewHolder.amount.setText("+ "+String.valueOf(currentTransaction.getAmount()));
            transactionViewHolder.amount.setTextColor(Color.GREEN);
        }
        else
        {
            transactionViewHolder.amount.setText("- " + String.valueOf(currentTransaction.getAmount()));
            transactionViewHolder.amount.setTextColor(Color.RED);
        }
        transactionViewHolder.date.setText(currentTransaction.getDate());
        transactionViewHolder.description.setText(currentTransaction.getDescription());
        transactionViewHolder.transactionId.setText(String.valueOf(currentTransaction.getTransactionId()));
        transactionViewHolder.sender.setText(currentTransaction.getRecipient());
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
}
