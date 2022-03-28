package com.example.pocketbank.authentication;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.animation.LayoutTransition;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.pocketbank.MainActivity;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.ActivityRegistrationBinding;
import com.example.pocketbank.model.user;
import com.example.pocketbank.myApplication;
import com.example.pocketbank.utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity
{
    private final String TAG ="RegistrationActivity";
    private MaterialTextView loginTextView, orTextView , nameFromGoogle, emailFromGoogle , notYouText;
    private Registration registrationActivityContext;
    private MaterialButton registerButton, googleSignInButton;
    private ActivityRegistrationBinding binder;
    private TextInputLayout nameTextInputLayout,emailTextInputLayout, passwordTextInputLayout, addressTextInputLayout;
    private TextInputEditText editTextNameRegistrationActivity, editTextPasswordRegistrationActivity,editTextEmailRegistrationActivity,editTextAddressRegistrationActivity;
    private MaterialDivider leftMaterialDivider, rightMaterialDivider;
    private ExecutorService applicationExecutorService;
    private SQLiteDatabase readableDatabaseRegistrationActivity , writeableDatabaseRegistrationActivity;
    private ViewGroup viewGroup, allGoogleData;
    private String currentEmail,currentPassword,currentName,currentAddress;
    private Bundle gotBundle;
    private ShapeableImageView profilePhotoFromGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gotBundle = getIntent().getBundleExtra("signInWithGoogle");
        registrationActivityContext = this;
        applicationExecutorService = myApplication.getExecutorService();
        writeableDatabaseRegistrationActivity = myApplication.writeableDatabase;
        readableDatabaseRegistrationActivity = myApplication.readableDatabase;
        binder = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binder.getRoot());
        initViews();
    }

    private void initViews() {
        viewGroup = binder.parent;
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(LayoutTransition.APPEARING, 1000);
        layoutTransition.setDuration(LayoutTransition.DISAPPEARING, 500);
        layoutTransition.setDuration(LayoutTransition.CHANGE_APPEARING, 1000);
        viewGroup.setLayoutTransition(layoutTransition);
        loginTextView = binder.loginFromRegistrationActivity;
        nameTextInputLayout = binder.nameInputLayoutRegistration;
        emailTextInputLayout = binder.emailInputLayoutRegistration;
        editTextEmailRegistrationActivity = binder.editTextEmailRegistration;
        passwordTextInputLayout = binder.passwordInputLayoutRegistration;
        addressTextInputLayout = binder.addressInputLayoutRegistrationActivity;
        editTextPasswordRegistrationActivity = binder.editTextPasswordRegistration;
        editTextNameRegistrationActivity = binder.editTextNameRegistration;
        editTextAddressRegistrationActivity = binder.editTextAddressRegistration;
        profilePhotoFromGoogle = binder.profilePhotoFromGoogle;
        notYouText = binder.notYou;
        nameFromGoogle = binder.googleDisplayName;
        emailFromGoogle = binder.emailFromGoogle;
        allGoogleData = binder.allGoogleData;
        registerButton = binder.register;
        googleSignInButton = binder.googleSignInButton;
        leftMaterialDivider = binder.leftDivider;
        rightMaterialDivider = binder.rightDivider;
        orTextView = binder.or;
        setListeners();
        if (gotBundle != null)
            createSignInWithGoogleLayout();
    }

    private void createSignInWithGoogleLayout()
    {
        emailTextInputLayout.setVisibility(GONE);
        registerButton.setText(R.string.Register);
        allGoogleData.setVisibility(VISIBLE);
        ((ConstraintLayout.LayoutParams) passwordTextInputLayout.getLayoutParams()).topToBottom = R.id.allGoogleData;
        passwordTextInputLayout.setVisibility(VISIBLE);
        passwordTextInputLayout.requestFocus();
        ((ConstraintLayout.LayoutParams) registerButton.getLayoutParams()).topToBottom = R.id.passwordInputLayoutRegistration;
        googleSignInButton.setVisibility(GONE);
        leftMaterialDivider.setVisibility(GONE);
        rightMaterialDivider.setVisibility(GONE);
        orTextView.setVisibility(GONE);
        nameFromGoogle.setText(gotBundle.getString("name"));
        emailFromGoogle.setText(gotBundle.getString("email"));
        Glide.with(registrationActivityContext).load(gotBundle.getString("photoUri")).into(profilePhotoFromGoogle);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        editTextEmailRegistrationActivity.requestFocus();
        setTextAndItsColor(loginTextView,getString(R.string.alternativeSignIn));
    }

    private void startUserRegistration()
    {
        applicationExecutorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {

                    Cursor cursor = readableDatabaseRegistrationActivity.query("user", new String[]{"userId", "email"}, "email=?", new String[]{editTextEmailRegistrationActivity.getText().toString()}, null, null, null);
                    if(cursor.moveToFirst())
                        emailTextInputLayout.setError(getString(R.string.usedEmail));
                    else
                        insertRecordIntoDb();
                    cursor.close();

                }
                catch (Exception ee)
                {
                    ee.printStackTrace();
                }
            }
            private void insertRecordIntoDb()
            {
                ContentValues record = new ContentValues();
                record.put("name", editTextNameRegistrationActivity.getText().toString());
                record.put("password", editTextPasswordRegistrationActivity.getText().toString());
                record.put("email", editTextEmailRegistrationActivity.getText().toString());
                record.put("address",  Objects.requireNonNull(editTextAddressRegistrationActivity.getText()).toString());
                record.put("remainedAmount", 0.0);

                long insertValue = writeableDatabaseRegistrationActivity.insert("user", null, record);
                if (insertValue == -1) {
                    registrationActivityContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                                Toast.makeText(getApplicationContext(), R.string.registrationUnSuccessful, Toast.LENGTH_SHORT).show();
                        }
                        });
                    } else {
                        registrationActivityContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.registrationSuccessful, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(registrationActivityContext, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        saveInSharedPreference(insertValue);
                    }
                }
                private void saveInSharedPreference(long rowId)
                {
                    Cursor cursor_1 = readableDatabaseRegistrationActivity.query("user", null, "userId=?", new String[]{String.valueOf(rowId)}, null, null, null);
                    cursor_1.moveToFirst();
                    user currentUser = new user();
                    currentUser.setUserId(cursor_1.getInt(cursor_1.getColumnIndexOrThrow("userId")));
                    currentUser.setName(cursor_1.getString(cursor_1.getColumnIndexOrThrow("name")));
                    currentUser.setAddress(cursor_1.getString(cursor_1.getColumnIndexOrThrow("address")));
                    currentUser.setRemainedAmount(cursor_1.getDouble(cursor_1.getColumnIndexOrThrow("remainedAmount")));
                    currentUser.setEmail(cursor_1.getString(cursor_1.getColumnIndexOrThrow("email")));
                    currentUser.setPassword(cursor_1.getString(cursor_1.getColumnIndexOrThrow("password")));
                    cursor_1.close();
                    utils utility = new utils(registrationActivityContext);
                    utility.addUserToSharedPreferences(currentUser);
                }

            });
    }


    private void setListeners()
    {

        registerButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                if(registerButton.getText().equals(getString(R.string.Continue)))
                    loadViewsIntoLayout();
                else
                    startUserRegistration();
            }

        });

        loginTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }

        });

        editTextPasswordRegistrationActivity.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus && Objects.requireNonNull(((TextInputEditText) v).getText()).length() != 0)
                {
                    ((myApplication)getApplicationContext()).checkPassword(passwordTextInputLayout,editTextPasswordRegistrationActivity);
                    addTextChanger((TextInputEditText)v);
                }
            }
        });

        notYouText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

    }

    private void loadViewsIntoLayout()
    {
        if(Objects.requireNonNull(editTextEmailRegistrationActivity.getText()).length() == 0)
        {
            addTextChanger(editTextEmailRegistrationActivity);
            emailTextInputLayout.setError(getString(R.string.invalidEmail));
        }
        else if (!Pattern.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", editTextEmailRegistrationActivity.getText()))
        {
            addTextChanger(editTextEmailRegistrationActivity);
            emailTextInputLayout.setError(getString(R.string.wrongEmail));
        }
        else
        {
            if(passwordTextInputLayout.getVisibility() == GONE)
            {
                passwordTextInputLayout.setVisibility(VISIBLE);
                editTextPasswordRegistrationActivity.requestFocus();
                ((ConstraintLayout.LayoutParams)registerButton.getLayoutParams()).topToBottom = R.id.passwordInputLayoutRegistration;
                leftMaterialDivider.setVisibility(GONE);
                rightMaterialDivider.setVisibility(GONE);
                orTextView.setVisibility(GONE);
                googleSignInButton.setVisibility(GONE);
            }
            else
                checkPasswordValue();
        }
    }

    private void checkPasswordValue()
    {
          if(Objects.requireNonNull(editTextPasswordRegistrationActivity.getText()).length() == 0)
          {
              addTextChanger(editTextPasswordRegistrationActivity);
              passwordTextInputLayout.requestFocus();
              passwordTextInputLayout.setError(getString(R.string.invalidPassword));
          }
          else
          {
              if(((myApplication)getApplicationContext()).checkPassword(passwordTextInputLayout,editTextPasswordRegistrationActivity))
              {
                  currentEmail = Objects.requireNonNull(editTextEmailRegistrationActivity.getText()).toString();
                  currentPassword = editTextPasswordRegistrationActivity.getText().toString();
                  emailTextInputLayout.setVisibility(GONE);

                  passwordTextInputLayout.setVisibility(GONE);

                  nameTextInputLayout.setVisibility(VISIBLE);
                  nameTextInputLayout.requestFocus();
                  addressTextInputLayout.setVisibility(VISIBLE);
                  editTextNameRegistrationActivity.setText(currentName);
                  editTextAddressRegistrationActivity.setText(currentAddress);
                  registerButton.setText(R.string.agreeAndJoin);
                  ((ConstraintLayout.LayoutParams)registerButton.getLayoutParams()).topToBottom = R.id.addressInputLayoutRegistrationActivity;
              }
              else
                  addTextChanger(editTextPasswordRegistrationActivity);
          }
    }
    private void addTextChanger(@NonNull TextInputEditText view)
    {
        view.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(view.getId() == R.id.editTextEmailRegistration)
                    ((myApplication)getApplicationContext()).checkEmail(emailTextInputLayout,view);
                else if(view.getId() == R.id.editTextPasswordRegistration)
                    ((myApplication)getApplicationContext()).checkPassword(passwordTextInputLayout,view);
                else
                {
                    ((TextInputLayout)view.getParent().getParent()).setError(null);
                }
            }

        });

    }

    private void setTextAndItsColor(TextView view, String text)
    {
        SpannableString signInString = new SpannableString(text);
        signInString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        view.setText(signInString);
    }

    @Override
    public void onBackPressed()
    {
        if (nameTextInputLayout.getVisibility() == VISIBLE)
        {
            currentName = editTextNameRegistrationActivity.getText().toString();
            currentAddress = editTextAddressRegistrationActivity.getText().toString();
            nameTextInputLayout.setVisibility(GONE);
            addressTextInputLayout.setVisibility(GONE);
            registerButton.setText(R.string.Continue);
            emailTextInputLayout.setVisibility(VISIBLE);
            passwordTextInputLayout.setVisibility(VISIBLE);
            editTextEmailRegistrationActivity.setText(currentEmail);
            editTextPasswordRegistrationActivity.setText(currentPassword);
            ((ConstraintLayout.LayoutParams)registerButton.getLayoutParams()).topToBottom = R.id.passwordInputLayoutRegistration;
            leftMaterialDivider.setVisibility(VISIBLE);
            rightMaterialDivider.setVisibility(VISIBLE);
            orTextView.setVisibility(VISIBLE);
            googleSignInButton.setVisibility(VISIBLE);
        }
        else
            super.onBackPressed();
    }

}
