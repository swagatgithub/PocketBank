package com.example.pocketbank.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.LoanAdapterLayoutBinding;
import com.example.pocketbank.model.loan;
import com.example.pocketbank.myApplication;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class loansAdapter extends RecyclerView.Adapter<loansAdapter.loanViewHolder>
{

    private ArrayList<loan> loans;

    static class loanViewHolder extends RecyclerView.ViewHolder
    {
        private final MaterialTextView dataForLenderName , dataForLoanType  , dataForLoanAmount , dataForStartDate , dataForFinishDate , dataForMonthlyROI , dataForMonthlyPayment , dataForCalculatedLoss ;
        public loanViewHolder(@NonNull View itemView)
        {
            super(itemView);
            dataForLenderName = itemView.findViewById(R.id.dataForLenderName);
            dataForLoanType = itemView.findViewById(R.id.loanType);
            dataForLoanAmount = itemView.findViewById(R.id.dataForLoanAmount);
            dataForStartDate = itemView.findViewById(R.id.dataForStartDate);
            dataForFinishDate = itemView.findViewById(R.id.dataForFinishDate);
            dataForMonthlyPayment = itemView.findViewById(R.id.dataForMonthlyPayment);
            dataForMonthlyROI = itemView.findViewById(R.id.dataForMonthlyROI);
            dataForCalculatedLoss =itemView.findViewById(R.id.dataForCalculatedLoss);
        }
    }

    public void setLoans(ArrayList<loan> loans) {
        this.loans = loans;
    }

    @NonNull
    @Override
    public loanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LoanAdapterLayoutBinding loanAdapterLayoutBinding = LoanAdapterLayoutBinding.inflate(LayoutInflater.from(parent.getContext()) , parent , false);
        return new loanViewHolder(loanAdapterLayoutBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull loanViewHolder holder, int position)
    {

        loan loan = loans.get(position);
        holder.dataForMonthlyROI.setText(String.format(Locale.getDefault(),"%.1f",loan.getMonthlyROI() ) );
        holder.dataForMonthlyPayment.setText(String.format(Locale.getDefault(),"%.1f" ,loan.getMonthlyPayment()));
        holder.dataForFinishDate.setText(loan.getFinishDate());
        holder.dataForStartDate.setText(loan.getStartDate());
        holder.dataForLoanAmount.setText(String.format(Locale.getDefault(),"%.1f",loan.getInitialAmount()));
        holder.dataForLoanType.setText(loan.getLoanType());
        holder.dataForLenderName.setText(loan.getLenderName());
        holder.dataForCalculatedLoss.setText( String.format(Locale.getDefault() , "%.1f" , getCalculatedLoss(loan)) );

    }

    private double getCalculatedLoss(loan loan)
    {

        try
        {

            Calendar calendar = Calendar.getInstance();
            Date initDate = myApplication.getSimpleDateFormat().parse(loan.getStartDate());
            calendar.setTime(Objects.requireNonNull(initDate));

            Calendar calendar1 = Calendar.getInstance();
            Date finalDate = myApplication.getSimpleDateFormat().parse(loan.getFinishDate());
            calendar1.setTime(Objects.requireNonNull(finalDate));

            int diffYear = calendar1.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);

            return (diffYear * 12 + calendar1.get(Calendar.MONTH) - calendar.get(Calendar.MONTH))*(loan.getInitialAmount() * loan.getMonthlyROI()/100);

        }

        catch (Exception ee)
        {
            ee.printStackTrace();
            return 0;
        }

    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

}
