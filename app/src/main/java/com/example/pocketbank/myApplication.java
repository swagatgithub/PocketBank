package com.example.pocketbank;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import com.example.pocketbank.database.databaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
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
    private static SimpleDateFormat simpleDateFormat;
    private static GoogleSignInClient googleSignInClient;
    private static Context myApplicationContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        myApplicationContext = getApplicationContext();
        try
        {
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

    public static SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    public boolean checkEmail(ViewGroup viewGroup ,View view)
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
        if(Objects.requireNonNull(((TextInputEditText) view).getText()).length() == 0)
        {
            ((TextInputLayout) viewGroup).setError(getString(R.string.invalidPassword));
            strength = false;
        }
        else if(!Pattern.matches(".*[A-Z].*", Objects.requireNonNull(((TextInputEditText) view).getText())) )
        {
            ((TextInputLayout) viewGroup).setError(getString(R.string.atLeastUpperCase));
            strength = false;
        }
       else if( !Pattern.matches( ".*[a-z].*", Objects.requireNonNull(((TextInputEditText) view).getText())) )
       {
            ((TextInputLayout) viewGroup).setError(getString(R.string.atLeastLowerCase));
            strength = false;
       }
       else if( !Pattern.matches(".*\\d.*", Objects.requireNonNull(((TextInputEditText) view).getText())) )
       {
            ((TextInputLayout) viewGroup).setError(getString(R.string.atLeastOneDigit));
            strength = false;
       }
        else if(!Pattern.matches(".*[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?].*", Objects.requireNonNull(((TextInputEditText) view).getText())) ) {
            ((TextInputLayout) viewGroup).setError(getString(R.string.atLeastOneSpecialCharacter));
            strength = false;
        }
        else if( !Pattern.matches("(^.{8,})", Objects.requireNonNull(((TextInputEditText) view).getText())) )
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

    public static void dialogIt(int messageId, Context context)
    {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
        materialAlertDialogBuilder.setMessage(messageId);
        materialAlertDialogBuilder.setPositiveButton(R.string.okay, (dialog, which) -> dialog.dismiss());
        materialAlertDialogBuilder.show();
    }

    public static boolean hasIntentExtras(Intent intent)
    {
        return intent.getExtras() != null;
    }

    public static GoogleSignInClient getGoogleSignInClient()
    {
        if(googleSignInClient == null)
        {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(myApplicationContext,gso);
        }
        return googleSignInClient;
    }

}
