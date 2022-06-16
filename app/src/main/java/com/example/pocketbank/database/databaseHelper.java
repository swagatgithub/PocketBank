package com.example.pocketbank.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.NonNull;

public class databaseHelper extends SQLiteOpenHelper
{
    private static final String TAG = "DatabaseHelper";
    private static final String databaseName = "MeiBank_DB";
    private static final int databaseVersion = 1;

    public databaseHelper(@NonNull Context context)
    {
        super(context,databaseName,null,databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {

        Log.d(TAG,"onCreate() : called");

        String createUserTable = "create table user("+"userId integer PRIMARY KEY AUTOINCREMENT,"+"password Text NOT NULL,"+
                "email Text NOT NULL,"+"name text not null,address text,remainedAmount double)";

        String createShoppingTable = "create table shopping("+"shoppingId integer primary key autoincrement,"+"itemId integer,"+
                "userId integer,"+"transactionId integer,"+"price double,"+"date text,"+"description text)";

        String createInvestmentTable = "create table investments("+"investmentId integer primary key autoincrement,"+"amount double,"+
                "monthlyRoi double,"+"name text,"+"initDate text,finishDate text,userId integer,transactionId integer)";

        String createLoanTable = "create table loans(loanId integer primary key autoincrement,initDate text,finishDate text,"
                +"initAmount double,monthlyPayment double,monthlyROI double, loanType text , lenderName text , loanRemainedAmount double)";

        String createTransactionTable = "create table transactions(transactionId integer primary key autoincrement,amount double,date text,"+ "type text,userId integer,recipient text,description text)";

        String createItemsTable = "create table items(itemId integer primary key autoincrement,itemNameResourceId integer , itemImageResourceId integer)";

        sqLiteDatabase.execSQL(createUserTable);

        sqLiteDatabase.execSQL(createShoppingTable);

        sqLiteDatabase.execSQL(createInvestmentTable);

        sqLiteDatabase.execSQL(createLoanTable);

        sqLiteDatabase.execSQL(createTransactionTable);

        sqLiteDatabase.execSQL(createItemsTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }
}
