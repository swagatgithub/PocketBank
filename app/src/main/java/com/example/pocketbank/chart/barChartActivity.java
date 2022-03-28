package com.example.pocketbank.chart;
import static com.example.pocketbank.myApplication.currentMonth;
import static com.example.pocketbank.myApplication.getSimpleDateFormat;


import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import com.example.pocketbank.databinding.ActivityBarChartBinding;
import com.example.pocketbank.model.Shopping;
import com.example.pocketbank.myApplication;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
public class barChartActivity extends AppCompatActivity
{
    private ActivityBarChartBinding activityBarChartBinding;
    private BarChart barChart;
    private ExecutorService executorServiceBarChartActivity;
    private SQLiteDatabase readableDatabaseBarChartActivity;
    private int userId;
    private ArrayList<Shopping> shoppingArrayList;
    private Calendar calendarBarChartActivity;
    private BarDataSet barDataSet;
    private BarData barData;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("userId",0);
        activityBarChartBinding = ActivityBarChartBinding.inflate(getLayoutInflater());
        setContentView(activityBarChartBinding.getRoot());
        startInitialization();
    }
    private void startInitialization()
    {

        barChart = activityBarChartBinding.barChartNew;
        executorServiceBarChartActivity = myApplication.getExecutorService();
        readableDatabaseBarChartActivity = myApplication.readableDatabase;
        calendarBarChartActivity = Calendar.getInstance();
        shoppingArrayList = new ArrayList<>();
        fetchRecordFromDatabase();
    }
    private void fetchRecordFromDatabase()
    {
        executorServiceBarChartActivity.execute(() -> {
            try
            {
                Cursor cursor = readableDatabaseBarChartActivity.query("shopping",new String[]{"price","date"},"user_id=?",new String[]{String.valueOf(userId)},null,null,null);
                Shopping shopping;
                while(cursor.moveToNext())
                {
                    shopping = new Shopping();
                    shopping.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                    shopping.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                    shoppingArrayList.add(shopping);
                }
                cursor.close();
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                boolean dayPresent;
                for(Shopping shopping1 : shoppingArrayList ) {
                    calendarBarChartActivity.setTime(getSimpleDateFormat().parse(shopping1.getDate()));
                    if(currentMonth() == calendarBarChartActivity.get(Calendar.MONTH))
                    {
                        int dayOfTheMonth = calendarBarChartActivity.get(Calendar.DAY_OF_MONTH);
                        dayPresent = false;
                        for (BarEntry barEntry : barEntries) {
                            if (barEntry.getX() == dayOfTheMonth) {
                                dayPresent = true;
                                barEntry.setY(barEntry.getY() + (float) shopping1.getPrice());
                                break;
                            }
                        }
                        if (!dayPresent)
                            barEntries.add(new BarEntry((float) dayOfTheMonth, (float) shopping1.getPrice()));
                    }
                }
                barDataSet = new BarDataSet(barEntries,"");
                barDataSet.setColor(Color.parseColor("#D599AE"));
                barDataSet.setHighlightEnabled(true);
                barDataSet.setHighLightColor(Color.parseColor("#E80C57"));
                barData = new BarData(barDataSet);
                barData.setBarWidth(0.15f);
                barData.setDrawValues(false);
                barChart.setData(barData);
                barChart.setDoubleTapToZoomEnabled(false);
                barChart.setPinchZoom(false);
                barChart.setDescription(null);
                barChart.getAxisRight().setEnabled(false);
                barChart.getXAxis().setCenterAxisLabels(true);
                barChart.getXAxis().setDrawGridLines(true);
                barChart.getXAxis().setDrawAxisLine(false);
               // barChart.getXAxis().setDrawGridLinesBehindData(true);
                barChart.getAxisLeft().setAxisMinimum(1);
                barChart.getAxisLeft().setAxisMaximum(1000);
                barChart.getAxisLeft().setEnabled(false);
                barChart.setDrawValueAboveBar(false);
                barChart.getLegend().setEnabled(false);
                barChart.setScaleEnabled(false);
                barChart.enableScroll();
                //barChart.canScrollVertically(-1);
            }
            catch(Exception ee)
            {
                ee.printStackTrace();
            }
        });
    }
}