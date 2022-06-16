package com.example.pocketbank.workManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.pocketbank.myApplication;

import java.util.Date;

public class investmentWorker extends Worker
{
    private static final String TAG = "investmentWorker";
    public investmentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork()
    {

        SQLiteDatabase writeableDatabase = myApplication.writeableDatabase;
        SQLiteDatabase readableDatabaseInvestmentWorker = myApplication.readableDatabase;
        Data data = getInputData();
        String type = data.getString("type");
        String recipient = data.getString("recipient");
        String description = data.getString("description");
        int userId = data.getInt("userId" ,0);
        double amount = data.getDouble("amount",0);

        try
        {
            Date date = new Date();
            ContentValues contentValues = new ContentValues();
            contentValues.put("amount" ,amount);
            contentValues.put("date" ,myApplication.getSimpleDateFormat().format(date));
            contentValues.put("type" , type);
            contentValues.put("userId" , userId);
            contentValues.put("recipient" , recipient);
            contentValues.put("description" , description);
            long returnValue = writeableDatabase.insert("transactions" , null,contentValues);

            if( returnValue != -1 )
            {
                Log.v(TAG, "transaction inserted..");
                Cursor cursor = readableDatabaseInvestmentWorker.query("user", new String[]{"remainedAmount"},null , null , null,null,null);
                cursor.moveToNext();
                double remainedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("remainedAmount"));
                cursor.close();

                remainedAmount +=amount;

                ContentValues contentValues1 = new ContentValues();

                contentValues1.put("remainedAmount",remainedAmount);

                int noOfRecordUpdated = writeableDatabase.update("user" , contentValues1 , null , null);
                if(noOfRecordUpdated > 0)
                    Log.v(TAG , "Record Updated");
            }

        }

        catch(Exception ee)
        {
            ee.printStackTrace();
            return Result.failure();
        }
        return Result.success();
    }

}
