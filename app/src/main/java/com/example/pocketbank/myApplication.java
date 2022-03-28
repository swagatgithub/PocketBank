package com.example.pocketbank;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.view.View;
import android.view.ViewGroup;


import com.example.pocketbank.database.databaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class myApplication extends Application
{
    private static ExecutorService executorService;
    public static SQLiteDatabase readableDatabase,writeableDatabase;
    private static Calendar calendar;
    private static SimpleDateFormat simpleDateFormat;

    @Override
    public void onCreate()
    {
        super.onCreate();
        try
        {
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",new Locale("en","IN"));
            executorService = Executors.newFixedThreadPool(4);
            databaseHelper dbHelper = new databaseHelper(getApplicationContext());
            readableDatabase = dbHelper.getReadableDatabase();
            writeableDatabase = dbHelper.getWritableDatabase();
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    public static ExecutorService getExecutorService()
    {
        return executorService;
    }

    public static void shutDownExecutorService()
    {
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        }
        catch (InterruptedException ie)
        {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static int getCurrentDate()
    {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int currentMonth()
    {
        return calendar.get(Calendar.MONTH);
    }

    public static int currentYear()
    {
        return calendar.get(Calendar.YEAR);
    }

    public static SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    public boolean checkEmail(ViewGroup viewGroup ,View view  )
    {
        boolean checkEmailReturnValue = true;
        if( !Pattern.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", Objects.requireNonNull( ( (TextInputEditText)view ).getText() ) ) )
        {
            ((TextInputLayout) viewGroup).setError(getString(R.string.invalidEmail));
            checkEmailReturnValue = false;
        }
        else
            ((TextInputLayout) viewGroup).setError(null);
        return checkEmailReturnValue;
    }

    public boolean checkPassword(ViewGroup viewGroup,View view)
    {
        boolean strength = true;
        if(((TextInputEditText)view) .getText().length() == 0)
        {
            ((TextInputLayout) viewGroup).setError(getString(R.string.invalidPassword));
            strength = false;
        }
        else if(!Pattern.matches(".*[A-Z].*", ( (TextInputEditText)view ) .getText() ) )
        {
            ((TextInputLayout) viewGroup).setError(getString(R.string.atLeastUpperCase));
            strength = false;
        }
       else if( !Pattern.matches( ".*[a-z].*",( (TextInputEditText)view ).getText() ) )
       {
            ((TextInputLayout) viewGroup).setError(getString(R.string.atLeastLowerCase));
            strength = false;
       }
       else if( !Pattern.matches(".*\\d.*",( (TextInputEditText)view ).getText() ) )
       {
            ((TextInputLayout) viewGroup).setError(getString(R.string.atLeastOneDigit));
            strength = false;
       }
        else if(!Pattern.matches(".*[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?].*",( (TextInputEditText)view ).getText() ) ) {
            ((TextInputLayout) viewGroup).setError(getString(R.string.atLeastOneSpecialCharacter));
            strength = false;
        }
        else if( !Pattern.matches("(^.{8,})",( (TextInputEditText)view ).getText() ) )
        {
            ((TextInputLayout) viewGroup).setError(getString(R.string.atLeastEightCharacterLong));
            strength = false;
        }
        /*else if(!Pattern.matches("(^./s.$)" , ( (TextInputEditText)view ).getText()) )
        {
            ((TextInputLayout) viewGroup).setError(getString(R.string.removeWhiteSpace));
            strength = false;
        }*/
        else
            ((TextInputLayout) viewGroup).setError(null);
        return strength;
    }

}
