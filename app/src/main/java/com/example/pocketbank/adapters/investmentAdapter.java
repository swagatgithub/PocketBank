package com.example.pocketbank.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.InvestmentLayoutBinding;
import com.example.pocketbank.model.investment;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Locale;

public class investmentAdapter extends RecyclerView.Adapter<investmentAdapter.investmentViewHolder>
{

    private ArrayList<investment> investments;
    private static final String TAG = "investmentAdapter";

    static class investmentViewHolder extends RecyclerView.ViewHolder
    {
        private final MaterialTextView investmentNameData;
        private final MaterialTextView investmentAmountData;
        private final MaterialTextView startDateData;
        private final MaterialTextView endDateData;
        private final MaterialTextView monthlyROIData;
        private final MaterialTextView profitData;
        public investmentViewHolder(@NonNull View itemView)
        {
            super(itemView);
            investmentNameData = itemView.findViewById(R.id.investmentNameData);
            investmentAmountData = itemView.findViewById(R.id.amountInvestedData);
            startDateData = itemView.findViewById(R.id.investmentStartData);
            endDateData = itemView.findViewById(R.id.investmentClosingDateData);
            monthlyROIData = itemView.findViewById(R.id.monthlyRateOfInterestData);
            profitData = itemView.findViewById(R.id.monthlyProfitData);
        }

    }

    @NonNull
    @Override
    public investmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Log.v(TAG , "onCreateViewHolder..");
        InvestmentLayoutBinding binding = InvestmentLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent ,false);
        return new investmentViewHolder(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(@NonNull investmentViewHolder holder, int position)
    {
        Log.v(TAG , "onBindViewHolder..");
        investment investment = investments.get(position);
        holder.profitData.setText(String.format(Locale.getDefault(),"%.1f",investment.getProfit()));
        holder.monthlyROIData.setText(String.format(Locale.getDefault(),"%.1f",investment.getMonthlyRateOfInterest()));
        holder.endDateData.setText(investment.getFinishDate());
        holder.startDateData.setText(investment.getStartDate());
        holder.investmentAmountData.setText(String.format(Locale.getDefault(),"%.1f",investment.getInvestmentAmount()));
        holder.investmentNameData.setText(investment.getInvestmentName());
    }

    @Override
    public int getItemCount()
    {
        Log.v(TAG , "getItemCount..");
        return investments.size();
    }

    public void setInvestments(ArrayList<investment> investments)
    {
        Log.v(TAG ,investments.toString());
        this.investments = investments;
    }

}
