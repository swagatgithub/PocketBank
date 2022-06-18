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
import com.example.pocketbank.R;
import com.example.pocketbank.adapters.investmentAdapter;
import com.example.pocketbank.databinding.FragmentInvestmentBinding;
import com.example.pocketbank.model.investment;
import com.example.pocketbank.myApplication;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class investmentFragment extends Fragment
{
    private static final String TAG = "investmentFragment";
    private RecyclerView investmentRecyclerView;
    private com.example.pocketbank.adapters.investmentAdapter investmentAdapter;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(TAG , "onCreateView Called..");
        ((MainActivity) Objects.requireNonNull(requireActivity())).bottomNavigationView.setSelectedItemId(R.id.investment);
        FragmentInvestmentBinding fragmentInvestmentBinding = FragmentInvestmentBinding.inflate(inflater, container, false);
        investmentRecyclerView = fragmentInvestmentBinding.investmentsRecyclerView;
        investmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        investmentAdapter = new investmentAdapter();
        return fragmentInvestmentBinding.getRoot();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.v(TAG , "onStart");
        executeDatabaseOperations();
    }

    private double calculateNoOfMonths(String startDate ,String finishDate)
    {
        try
        {
            Calendar calendar = Calendar.getInstance();
            Date initDate = myApplication.getSimpleDateFormat().parse(startDate);
            calendar.setTime(Objects.requireNonNull(initDate));

            Calendar calendar1 = Calendar.getInstance();
            Date finalDate = myApplication.getSimpleDateFormat().parse(finishDate);
            calendar1.setTime(Objects.requireNonNull(finalDate));

            int diffYear = calendar1.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            return diffYear * 12 + calendar1.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
            return 0;
        }

    }

    private void executeDatabaseOperations()
    {
        ExecutorService executorServiceInvestmentFragment = myApplication.getExecutorService();

        executorServiceInvestmentFragment.execute( () ->
        {
            ArrayList<investment> investments = new ArrayList<>();
            SQLiteDatabase readableDatabaseInvestmentFragment = myApplication.readableDatabase;

            Cursor cursor = readableDatabaseInvestmentFragment.query("investments" , null , null , null ,null,null ,null);

            while (cursor.moveToNext())
            {
                investment investment = new investment();
                investment.setInvestmentName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                investment.setInvestmentAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                investment.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("initDate")));
                investment.setFinishDate(cursor.getString(cursor.getColumnIndexOrThrow("finishDate")));
                investment.setMonthlyRateOfInterest(cursor.getDouble(cursor.getColumnIndexOrThrow("monthlyRoi")));
                investment.setProfit(calculateNoOfMonths(investment.getStartDate(),investment.getFinishDate())*(investment.getInvestmentAmount()*investment.getMonthlyRateOfInterest()/100));
                investments.add(investment);
            }

            cursor.close();

            ((MainActivity)requireContext()).runOnUiThread(() ->
            {
                investmentAdapter.setInvestments(investments);
                investmentRecyclerView.setAdapter(investmentAdapter);

            });

        });

    }

}