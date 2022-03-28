package com.example.pocketbank.others;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;


import com.example.pocketbank.R;
import com.example.pocketbank.databinding.ActivityGiveYourEmailOrTelephoneBinding;
import com.example.pocketbank.myApplication;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

public class giveYourEmailOrTelephone extends AppCompatActivity
{

    private MaterialTextView getDataText;
    private MaterialButton sendDetailsButton;
    private TextInputLayout emailOrPhoneNumberLayout;
    private TextInputEditText emailOrPhoneNumberEditText;
    private String lostInfoValue;
    private ExecutorService giveYourEmailOrTelephoneExecutorService;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivityGiveYourEmailOrTelephoneBinding activityGiveYourEmailOrTelephoneBinding = ActivityGiveYourEmailOrTelephoneBinding.inflate(getLayoutInflater());
        setContentView(activityGiveYourEmailOrTelephoneBinding.getRoot());
        initializeViews(activityGiveYourEmailOrTelephoneBinding);
    }

    private void initializeViews(ActivityGiveYourEmailOrTelephoneBinding activityGiveYourEmailOrTelephoneBinding)
    {

        giveYourEmailOrTelephoneExecutorService = myApplication.getExecutorService();
        getDataText = activityGiveYourEmailOrTelephoneBinding.getData;
        sendDetailsButton = activityGiveYourEmailOrTelephoneBinding.sendDetails;
        emailOrPhoneNumberLayout = activityGiveYourEmailOrTelephoneBinding.giveEmailOrPhoneNumber;
        emailOrPhoneNumberEditText = activityGiveYourEmailOrTelephoneBinding.givenData;
        lostInfoValue = getIntent().getStringExtra(getString(R.string.extraInformation));
        setSupportActionBar(activityGiveYourEmailOrTelephoneBinding.toolBarGiveYourEmailOrTelephoneActivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setListeners();

    }

    private void setListeners()
    {
        sendDetailsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    processInput();
                }
                catch(Exception ee)
                {
                     ee.printStackTrace();
                }

            }
        });

        emailOrPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(s.length() == 0)
                    emailOrPhoneNumberLayout.setError(getString(R.string.enterValidEmailOrPhone));
                else
                    emailOrPhoneNumberLayout.setError(null);
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        setData();
    }

    private void setData()
    {
        getDataText.setText(getString(R.string.get)+" "+lostInfoValue);
    }

    private void processInput()
    {
        if(Pattern.matches("[0-9]+",emailOrPhoneNumberEditText.getText()))
        {
            System.out.println("sendSms value is "+sendSms());
        }
        else if(Pattern.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",emailOrPhoneNumberEditText.getText()))
        {
            sendMail();
        }
        else
            emailOrPhoneNumberLayout.setError(getString(R.string.enterValidEmailOrPhone));
    }

    public String sendSms()
    {
        final String[] returnValue = {null};
        giveYourEmailOrTelephoneExecutorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                String returnString;
                try {
                    // Construct data
                    String apiKey = "apikey=" + "NTA3MDZmNGE0YjUxNDI2ODc4NDI0ZDVhNjE1Mjc1NDg=";
                    String message = "&message=" + "Hello User";
                    String sender = "&sender=" + "Pocket Bank";
                    String numbers = "&numbers=" + "9078550419";

                    // Send data
                    HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
                    String data = apiKey + numbers + message + sender;
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                    conn.getOutputStream().write(data.getBytes("UTF-8"));
                    final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    final StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = rd.readLine()) != null)
                    {
                        stringBuffer.append(line);
                    }
                    rd.close();
                    returnValue[0] = stringBuffer.toString();
                    System.out.println("Inside this...."+returnValue[0]);
                }
                catch (Exception e) {
                    System.out.println("Error SMS "+e);
                    returnValue[0] =  "Error "+e;
                }
            }
        });
        return returnValue[0];
    }

    private void sendMail()
    {

    }

}