package com.example.pocketbank.database;

import static java.text.DateFormat.getDateInstance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import java.sql.Date;
//import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
//import java.util.Date;

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
        String createShoppingTable = "create table shoppings("+"shoppingId integer primary key autoincrement,"+"itemId integer,"+
                "userId integer,"+"transactionId integer,"+"price double,"+"date date,"+"description text)";
        String createInvestmentTable = "create table investments("+"investmentId integer primary key autoincrement,"+"amount double,"+
                "monthlyRoi double,"+"name text,"+"initDate date,finishDate date,userId integer,transactionId integer)";

        String createLoanTable = "create table loans(loanId integer primary key autoincrement,initDate date,finishDate date,"
                +"initAmount double,remainedAmount double,monthlyPayment double,monthlyRoi double,name text,userId integer)";

        String createTransactionTable = "create table transactions(transactionId integer primary key autoincrement,amount double,date date,"+
                "type text,userId integer,recipient text,description text)";

        String createItemsTable = "create table items(itemId integer primary key autoincrement,itemName text,imageUrl text,itemDescription text)";
        sqLiteDatabase.execSQL(createUserTable);
        sqLiteDatabase.execSQL(createShoppingTable);
        sqLiteDatabase.execSQL(createInvestmentTable);
        sqLiteDatabase.execSQL(createLoanTable);
        sqLiteDatabase.execSQL(createTransactionTable);
        sqLiteDatabase.execSQL(createItemsTable);
        populateWithValues(sqLiteDatabase);
        populateShoppingTables(sqLiteDatabase);
    }
    private void populateShoppingTables(SQLiteDatabase sqLiteDatabase)
    {
        Calendar calendar = Calendar.getInstance();
        //Date actualDate = calendar.getTime();
        Date actualDate = new Date(System.currentTimeMillis());
        calendar.set(2022,Calendar.JANUARY,16);
        //calendar.get
        ContentValues contentValues = new ContentValues();
        ContentValues contentValues1 = new ContentValues();
        contentValues.put("userId",1);
        contentValues.put("transactionId",3);
        contentValues.put("itemId",5);
        contentValues.put( "price",500);
        contentValues.put("date",actualDate.toString());
        contentValues.put("description","Shopping");

        contentValues1.put("userId",1);
        contentValues1.put("transactionId",5);
        contentValues1.put("itemId",6);
        contentValues1.put( "price",1000000);
        contentValues1.put("date",new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        contentValues1.put("description","Medical");

        long insertValue = sqLiteDatabase.insert("shoppings",null,contentValues);
        long insertValue_1 = sqLiteDatabase.insert("shoppings",null,contentValues1);
        System.out.println("insertValue is "+insertValue);
        System.out.println(" another insertValue is "+insertValue_1);
    }

    private void populateWithValues(SQLiteDatabase sqLiteDatabase)
    {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2022,Calendar.MAY,12);
        Calendar calendar_1 = Calendar.getInstance();
        calendar_1.clear();
        calendar_1.set(2022,Calendar.NOVEMBER,23);
        System.out.println("inside db date is "+calendar.getTime());
        ContentValues record = new ContentValues();
        ContentValues record_1 = new ContentValues();
        ContentValues record_2 = new ContentValues();
        ContentValues record_3 = new ContentValues();
        ContentValues record_4 = new ContentValues();
        record.put("amount",2000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            record.put("date",LocalDate.now().toString());
        }
        record.put("type","profit");
        record.put("userId",1);
        record.put("recipient","walmart");
        record.put("description","Shopping");
        /*record.put("name","Bike");
        record.put("image_url","https://best-bike-store-demo.myshopify.com/");
        record.put("description","Perfect Mountain Bike");*/
        long row_id = sqLiteDatabase.insert("transactions",null,record);
        System.out.println("row_id is "+row_id);

        record_1.put("amount",2000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            record_1.put("date", LocalDate.now().toString());
        }

        record_1.put("type","profit");
        record_1.put("userId",1);
        record_1.put("recipient","Sbi");
        record_1.put("description","Shopping");
        /*record.put("name","Bike");
        record.put("image_url","https://best-bike-store-demo.myshopify.com/");
        record.put("description","Perfect Mountain Bike");*/
        long row_id_1 = sqLiteDatabase.insert("transactions",null,record_1);
        System.out.println("row_id is "+row_id_1);

        record_2.put("amount",200000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            record_2.put("date", LocalDate.now().toString());
        }
        record_2.put("type","profit");
        record_2.put("userId",1);
        record_2.put("recipient","Central");
        record_2.put("description","Shopping");
        long row_id_2 = sqLiteDatabase.insert("transactions",null,record_2);
        System.out.println("row_id is "+row_id_2);

        record_3.put("amount",1500);
        //System.out.println("Date Is "+((Integer)calendar.get(Calendar.YEAR)).toString()+"-"+((Integer)calendar.get(Calendar.MONTH)).toString()+"-"+((Integer)calendar.get(Calendar.DATE)).toString());
        String month,date;
        if(calendar.get(Calendar.MONTH) < 10)
            month="-0"+calendar.get(Calendar.MONTH);
        else
            month="-"+calendar.get(Calendar.MONTH);
        if(calendar.get(Calendar.DATE) < 10)
            date="-0"+calendar.get(Calendar.DATE);
        else
            date="-"+calendar.get(Calendar.DATE);
        record_3.put("date", calendar.get(Calendar.YEAR)+month+date);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            record_2.put("date", LocalDate.now().toString());
        }*/
        record_3.put("type","profit");
        record_3.put("userId",1);
        record_3.put("recipient","Central");
        record_3.put("description","Shopping");
        /*record.put("name","Bike");
        record.put("image_url","https://best-bike-store-demo.myshopify.com/");
        record.put("description","Perfect Mountain Bike");*/
        long row_id_3 = sqLiteDatabase.insert("transactions",null,record_3);
        System.out.println("row_id is "+row_id_3);

        String month_1,date_1;
        record_4.put("amount",75);
        //System.out.println("Date Is "+((Integer)calendar.get(Calendar.YEAR)).toString()+"-"+((Integer)calendar.get(Calendar.MONTH)).toString()+"-"+((Integer)calendar.get(Calendar.DATE)).toString());
        if(calendar_1.get(Calendar.MONTH) < 10)
            month_1="-0"+calendar_1.get(Calendar.MONTH);
        else
            month_1="-"+calendar_1.get(Calendar.MONTH);
        if(calendar_1.get(Calendar.DATE) < 10)
            date_1="-0"+calendar_1.get(Calendar.DATE);
        else
            date_1="-"+calendar_1.get(Calendar.DATE);

        record_4.put("date", calendar_1.get(Calendar.YEAR)+month_1+date_1);
        record_4.put("type","profit");
        record_4.put("userId",1);
        record_4.put("recipient","Central");
        record_4.put("description","Shopping");
        /*record.put("name","Bike");
        record.put("image_url","https://best-bike-store-demo.myshopify.com/");
        record.put("description","Perfect Mountain Bike");*/
        long row_id_4 = sqLiteDatabase.insert("transactions",null,record_4);
        System.out.println("row_id is "+row_id_4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

}
