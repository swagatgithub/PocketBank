package com.example.pocketbank.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.pocketbank.MainActivity;
import com.example.pocketbank.databinding.FragmentStatisticsBinding;
import com.example.pocketbank.model.Transaction;
import com.example.pocketbank.model.loan;
import com.example.pocketbank.myApplication;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class statisticsFragment extends Fragment
{

    private static final String Tag = "statisticsFragment";
    private BarChart barChart;
    private MainActivity hostActivity;
    private PieChart pieChart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        hostActivity = ((MainActivity)requireHost());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentStatisticsBinding fragmentStatisticsBinding = FragmentStatisticsBinding.inflate(inflater, container, false);
        barChart = fragmentStatisticsBinding.barChartStatisticsFragment;
        pieChart = fragmentStatisticsBinding.pieChartStatisticsFragment;
        return fragmentStatisticsBinding.getRoot();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        executeDatabaseOperations();
    }

    private void executeDatabaseOperations()
    {
        hostActivity.executorServiceMainActivity.execute( () ->
        {
             SQLiteDatabase readableDatabaseMainActivity = hostActivity.readableDatabaseMainActivity;

             ArrayList<Transaction> transactionArrayList = new ArrayList<>();

             Cursor cursor = readableDatabaseMainActivity.query("transactions" , new String[]{"date" , "amount"} ,null ,null ,null ,null ,null );

             while (cursor.moveToNext())
             {
                Transaction transaction = new Transaction();
                transaction.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                transactionArrayList.add(transaction);
            }

            cursor.close();

            ArrayList<loan> loanArrayList = new ArrayList<>();

            Cursor cursor1 = readableDatabaseMainActivity.query("loans" , new String[]{"initAmount","loanRemainedAmount"} , null ,null ,null ,null ,null);

            while(cursor1.moveToNext())
            {
                loan loan = new loan();
                loan.setInitialAmount(cursor1.getDouble(cursor1.getColumnIndexOrThrow("initAmount")));
                loan.setLoanRemainedAmount(cursor1.getDouble(cursor1.getColumnIndexOrThrow("loanRemainedAmount")));
                loanArrayList.add(loan);
            }

            cursor1.close();

            if(transactionArrayList.size() > 0)
            {
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                for (Transaction transaction : transactionArrayList)
                {
                    try
                    {

                        calendar.setTime(Objects.requireNonNull(myApplication.getSimpleDateFormat().parse(transaction.getDate())));

                        int month = calendar.get(Calendar.MONTH);

                        int year = calendar.get(Calendar.YEAR);

                        int dayOfTheMonth = calendar.get(Calendar.DAY_OF_MONTH)+1;

                        if(currentMonth == month && currentYear == year)
                        {
                            boolean broken = false;
                            for (BarEntry barEntry : barEntries)
                            {
                                if(barEntry.getX() == dayOfTheMonth)
                                {
                                    barEntry.setY(barEntry.getY() + (float)transaction.getAmount());
                                    broken = true;
                                    break;
                                }
                            }

                            if(broken)
                                Log.v(Tag ,"if executed");
                            else
                            {
                                BarEntry barEntry = new BarEntry(dayOfTheMonth , (float) transaction.getAmount());
                                barEntries.add(barEntry);
                            }

                        }

                    }

                    catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                }

                BarDataSet barDataSet = new BarDataSet(barEntries , "Transaction Chart");

                barDataSet.setHighlightEnabled(false);

                barDataSet.setColor(Color.parseColor("#FF9800"));

                BarData barData = new BarData(barDataSet);
                hostActivity.runOnUiThread(() ->
                {
                    XAxis xAxis = barChart.getXAxis();

                    xAxis.setAxisMinimum(1);

                    xAxis.setAxisMaximum(31);

                    YAxis yAxis = barChart.getAxisLeft();

                    yAxis.setLabelCount(10);

                    yAxis.setAxisMinimum(1000);

                    yAxis.setAxisMaximum(10000);

                    barChart.getAxisRight().setEnabled(false);

                    barChart.setDescription(null);

                    barChart.setPinchZoom(false);

                    barChart.setDoubleTapToZoomEnabled(false);

                    barChart.invalidate();

                    barChart.setData(barData);

                });
            }

            if(loanArrayList.size() > 0)
            {
                double totalLoanAmount = 0 , totalRemainedAmount = 0.0;

                for(loan loan : loanArrayList)
                {
                    totalLoanAmount +=loan.getInitialAmount();
                    totalRemainedAmount += loan.getLoanRemainedAmount();
                }

                ArrayList<PieEntry> pieEntries = new ArrayList<>();

                pieEntries.add(new PieEntry((float) totalLoanAmount));

                pieEntries.add(new PieEntry((float) totalRemainedAmount));

                PieDataSet pieDataSet = new PieDataSet(pieEntries , "Loan Chart");

                pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

                pieDataSet.setSliceSpace(5);

                PieData pieData = new PieData(pieDataSet);

                hostActivity.runOnUiThread(() ->
                {
                    pieChart.setDrawHoleEnabled(false);
                    pieChart.animateY(2000 , Easing.EaseInOutCubic);
                    pieChart.setDescription(null);
                    pieChart.setData(pieData);
                    pieChart.invalidate();

                });

            }

        });

    }

}