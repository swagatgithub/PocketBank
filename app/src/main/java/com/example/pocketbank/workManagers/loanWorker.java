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

public class loanWorker extends Worker
{

    public loanWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork()
    {

        SQLiteDatabase sqLiteDatabase = myApplication.writeableDatabase;

        Data inputData = getInputData();

        int loanId = inputData.getInt("loanId" , 0);
        double amount = inputData.getDouble("amount", 0.0);
        int userId = inputData.getInt("userId", 0);
        String type = inputData.getString("type");
        String description = inputData.getString("description");
        String recipient = inputData.getString("recipient");

        ContentValues contentValues = new ContentValues();

        Date date = new Date();
        contentValues.put("amount" , amount);
        contentValues.put("type" , type);
        contentValues.put("description" , description);
        contentValues.put("recipient" , recipient);
        contentValues.put("userId" , userId);
        contentValues.put("date" , myApplication.getSimpleDateFormat().format(date));

        long returnValue = sqLiteDatabase.insert("transactions", null, contentValues);

        if(returnValue != -1)
        {

            Cursor cursor = sqLiteDatabase.query("user", new String[]{"remainedAmount"}, null, null, null, null, null);

            cursor.moveToNext();

            double remainedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("remainedAmount"));

            cursor.close();

            Cursor cursor1 = sqLiteDatabase.query("loans" , new String[]{"loanRemainedAmount"} ,"loanId=?" , new String[]{Integer.toString(loanId)},null ,null ,null);

            cursor1.moveToNext();

            double loanRemainedAmount = cursor1.getDouble(cursor1.getColumnIndexOrThrow("loanRemainedAmount"));

            cursor1.close();

            loanRemainedAmount += amount;

            remainedAmount += amount;

            ContentValues contentValues1 = new ContentValues();

            contentValues1.put("remainedAmount", remainedAmount);

            int noOfRowsUpdated = sqLiteDatabase.update("user", contentValues1, null, null);

            ContentValues contentValues2 = new ContentValues();

            contentValues2.put("loanRemainedAmount" , loanRemainedAmount);

            int loanRemainedValueUpdated = sqLiteDatabase.update("loans" , contentValues2 , "loanId = ?", new String[]{Integer.toString(loanId)});

            if(noOfRowsUpdated != 0 && loanRemainedValueUpdated != 0)
            {
                Log.v("loanWorker", "");
                return Result.success();
            }

        }

        return Result.failure();

    }

}
