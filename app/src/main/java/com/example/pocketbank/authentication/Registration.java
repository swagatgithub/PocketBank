package com.example.pocketbank.authentication;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.animation.LayoutTransition;
import android.app.Activity;
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
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.pocketbank.MainActivity;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.ActivityRegistrationBinding;
import com.example.pocketbank.model.user;
import com.example.pocketbank.myApplication;
import com.example.pocketbank.others.utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity
{
    private MaterialTextView loginTextView, orTextView , notYouTextView;
    private Registration registrationActivityContext;
    private MaterialButton registerButton, googleSignInButton;
    private ActivityRegistrationBinding binder;
    private TextInputLayout nameTextInputLayout,emailTextInputLayout, passwordTextInputLayout, addressTextInputLayout;
    private TextInputEditText editTextNameRegistrationActivity, editTextPasswordRegistrationActivity,editTextEmailRegistrationActivity,editTextAddressRegistrationActivity;
    private MaterialDivider leftMaterialDivider, rightMaterialDivider;
    private ExecutorService applicationExecutorService;
    private SQLiteDatabase readableDatabaseRegistrationActivity , writeableDatabaseRegistrationActivity;
    private String currentEmail,currentPassword,currentName,currentAddress;
    private boolean userExists;
    private ActivityResultLauncher<Intent> signInWithGoogleRegistrationActivity;
    private GoogleSignInClient googleSignInClientRegistrationActivity;
    private user existedUserOfApp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        registrationActivityContext = this;
        applicationExecutorService = myApplication.getExecutorService();
        writeableDatabaseRegistrationActivity = myApplication.writeableDatabase;
        readableDatabaseRegistrationActivity = myApplication.readableDatabase;
        binder = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binder.getRoot());
        initViews();

        googleSignInClientRegistrationActivity = myApplication.getGoogleSignInClient();

        signInWithGoogleRegistrationActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
        {
            @Override
            public void onActivityResult(ActivityResult result)
            {
                if (result.getResultCode() == Activity.RESULT_OK)
                {
                    try {
                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult(ApiException.class);
                        handleGoogleSignInButtonClick(account);
                    } catch (ApiException ee) {
                        Toast.makeText(registrationActivityContext, GoogleSignInStatusCodes.getStatusCodeString(ee.getStatusCode()), Toast.LENGTH_SHORT).show();
                        ee.printStackTrace();
                    }
                }
            }

            private void handleGoogleSignInButtonClick(GoogleSignInAccount account)
            {
                if(existedUserOfApp != null)
                {
                    if(existedUserOfApp.getEmail().equals(account.getEmail()))
                    {
                        utils utility = new utils(registrationActivityContext);
                        utility.addUserToSharedPreferences(existedUserOfApp);
                        startActivity(new Intent(registrationActivityContext, MainActivity.class));
                        finish();
                        Login.finishLogin();
                    }

                }
                else
                {
                    editTextEmailRegistrationActivity.setText(account.getEmail());
                    notYouTextView.setVisibility(VISIBLE);
                    passwordTextInputLayout.setVisibility(VISIBLE);
                    passwordTextInputLayout.requestFocus();
                    ((ConstraintLayout.LayoutParams) registerButton.getLayoutParams()).topToBottom = R.id.passwordInputLayoutRegistration;
                    googleSignInButton.setVisibility(GONE);
                    rightMaterialDivider.setVisibility(GONE);
                    leftMaterialDivider.setVisibility(GONE);
                    orTextView.setVisibility(GONE);
                    currentName = account.getDisplayName();
                }
            }

        });
        
    }

    private void initViews()
    {
        ViewGroup viewGroup = binder.parent;
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
        notYouTextView = binder.notYou;
        registerButton = binder.register;
        googleSignInButton = binder.googleSignInButton;
        leftMaterialDivider = binder.leftDivider;
        rightMaterialDivider = binder.rightDivider;
        orTextView = binder.or;
        setListeners();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (myApplication.hasIntentExtras(getIntent()))
        {
            if(getIntent().getExtras().getBoolean("signInWithGoogle"))
            {
                editTextEmailRegistrationActivity.setText(getIntent().getExtras().getString("email"));
                currentName = getIntent().getExtras().getString("name");
                leftMaterialDivider.setVisibility(GONE);
                orTextView.setVisibility(GONE);
                rightMaterialDivider.setVisibility(GONE);
                googleSignInButton.setVisibility(GONE);
            }
            else
            {
                Gson gson = new Gson();
                Class<user> userObject=null;
                try
                {
                    userObject = (Class<user>) Class.forName("com.example.pocketbank.model.user");
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
                userExists = getIntent().getExtras().getBoolean("oneUserExists");
                existedUserOfApp = gson.fromJson(getIntent().getExtras().getString("thatParticularUser"),userObject);
                editTextEmailRegistrationActivity.requestFocus();
            }
        }
        else
            editTextEmailRegistrationActivity.requestFocus();
        setTextAndItsColor(loginTextView, getString(R.string.alternativeSignIn));
    }

    private void startUserRegistration()
    {
        applicationExecutorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                if (userExists)
                {
                    registrationActivityContext.runOnUiThread(() -> {
                        myApplication.dialogIt(R.string.oneUserAllowed, registrationActivityContext);
                        if(GoogleSignIn.getLastSignedInAccount(registrationActivityContext) != null)
                            myApplication.getGoogleSignInClient().signOut();
                    });
                } else
                    insertRecordIntoDb();
            }

            private void insertRecordIntoDb()
            {
                ContentValues record = new ContentValues();
                record.put("name", Objects.requireNonNull(editTextNameRegistrationActivity.getText()).toString());
                record.put("password", Objects.requireNonNull(editTextPasswordRegistrationActivity.getText()).toString());
                record.put("email", Objects.requireNonNull(editTextEmailRegistrationActivity.getText()).toString());
                record.put("address", Objects.requireNonNull(editTextAddressRegistrationActivity.getText()).toString());
                record.put("remainedAmount", 0);

                long insertValue = writeableDatabaseRegistrationActivity.insert("user", null, record);
                if (insertValue == -1) {
                    registrationActivityContext.runOnUiThread(() -> Toast.makeText(getApplicationContext(), R.string.registrationUnSuccessful, Toast.LENGTH_SHORT).show());
                } else
                {
                    registrationActivityContext.runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), R.string.registrationSuccessful, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(registrationActivityContext, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    });
                    saveInSharedPreference(insertValue);
                }
            }

            private void saveInSharedPreference(long rowId) {
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
        registerButton.setOnClickListener(view -> {
            if (registerButton.getText().equals(getString(R.string.Continue)))
                loadViewsIntoLayout();
            else {
                if (checkName())
                    startUserRegistration();
                else
                    addTextChanger(editTextNameRegistrationActivity);
            }
        });

        loginTextView.setOnClickListener(view -> onBackPressed());

        editTextPasswordRegistrationActivity.setOnFocusChangeListener((v, hasFocus) -> {

            if (!hasFocus && Objects.requireNonNull(((TextInputEditText) v).getText()).length() != 0) {
                ((myApplication) getApplicationContext()).checkPassword(passwordTextInputLayout, editTextPasswordRegistrationActivity);
                addTextChanger((TextInputEditText) v);
            }
        });

        editTextEmailRegistrationActivity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && Objects.requireNonNull(((TextInputEditText) v).getText()).length() != 0) {
                ((myApplication) getApplicationContext()).checkEmail(emailTextInputLayout, editTextEmailRegistrationActivity);
                addTextChanger((TextInputEditText) v);
            }
        });

        editTextNameRegistrationActivity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                checkName();
                addTextChanger(editTextNameRegistrationActivity);
            }
        });

        googleSignInButton.setOnClickListener(v -> signInWithGoogleRegistrationActivity.launch(googleSignInClientRegistrationActivity.getSignInIntent()));

        notYouTextView.setOnClickListener(v -> {
            notYouTextView.setVisibility(GONE);
            myApplication.getGoogleSignInClient().signOut();
            currentName =null;
            currentAddress = null;
            nameTextInputLayout.setVisibility(GONE);
            addressTextInputLayout.setVisibility(GONE);
            googleSignInButton.setVisibility(VISIBLE);
            orTextView.setVisibility(VISIBLE);
            leftMaterialDivider.setVisibility(VISIBLE);
            rightMaterialDivider.setVisibility(VISIBLE);
            emailTextInputLayout.setVisibility(VISIBLE);
            passwordTextInputLayout.setVisibility(GONE);
            editTextPasswordRegistrationActivity.setText(null);
            ((TextInputLayout)editTextEmailRegistrationActivity.getParent().getParent()).setError(null);
            registerButton.setText(R.string.Continue);
            ((ConstraintLayout.LayoutParams)registerButton.getLayoutParams()).topToBottom = R.id.emailInputLayoutRegistration;
            editTextEmailRegistrationActivity.setText(null);
            editTextEmailRegistrationActivity.requestFocus();
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
                  registerButton.setText(R.string.Join);
                  ((ConstraintLayout.LayoutParams)registerButton.getLayoutParams()).topToBottom = R.id.addressInputLayoutRegistrationActivity;
                  currentEmail = Objects.requireNonNull(editTextEmailRegistrationActivity.getText()).toString();
                  currentPassword = editTextPasswordRegistrationActivity.getText().toString();
                  editTextNameRegistrationActivity.setText(currentName);
                  editTextAddressRegistrationActivity.setText(currentAddress);
                  emailTextInputLayout.setVisibility(GONE);
                  passwordTextInputLayout.setVisibility(GONE);
                  if(Objects.requireNonNull(editTextNameRegistrationActivity.getText()).length() == 0)
                      nameTextInputLayout.requestFocus();
                  else
                  {
                      if(Objects.requireNonNull(editTextAddressRegistrationActivity.getText()).length() == 0)
                          addressTextInputLayout.requestFocus();
                  }
                  nameTextInputLayout.setVisibility(VISIBLE);
                  addressTextInputLayout.setVisibility(VISIBLE);
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
                    checkName();
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
            currentName = Objects.requireNonNull(editTextNameRegistrationActivity.getText()).toString();
            currentAddress = Objects.requireNonNull(editTextAddressRegistrationActivity.getText()).toString();
            nameTextInputLayout.setVisibility(GONE);
            addressTextInputLayout.setVisibility(GONE);
            registerButton.setText(R.string.Continue);
            emailTextInputLayout.setVisibility(VISIBLE);
            passwordTextInputLayout.setVisibility(VISIBLE);
            editTextEmailRegistrationActivity.setText(currentEmail);
            editTextPasswordRegistrationActivity.setText(currentPassword);
            ((ConstraintLayout.LayoutParams)registerButton.getLayoutParams()).topToBottom = R.id.passwordInputLayoutRegistration;
        }
        else
        {
            if(GoogleSignIn.getLastSignedInAccount(registrationActivityContext) != null)
                myApplication.getGoogleSignInClient().signOut();
            super.onBackPressed();
        }

    }

    private boolean checkName()
    {
        boolean returnValue = true;
        if(Objects.requireNonNull(editTextNameRegistrationActivity.getText()).length() == 0)
        {
            nameTextInputLayout.setError(getString(R.string.emptyName));
            returnValue = false;
        }
        else
            nameTextInputLayout.setError(null);
       return returnValue;

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(GoogleSignIn.getLastSignedInAccount(registrationActivityContext) != null)
            myApplication.getGoogleSignInClient().signOut();
    }

}
