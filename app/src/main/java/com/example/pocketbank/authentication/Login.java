package com.example.pocketbank.authentication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pocketbank.MainActivity;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.ActivityLoginBinding;
import com.example.pocketbank.model.user;
import com.example.pocketbank.myApplication;
import com.example.pocketbank.others.forgotDetails;
import com.example.pocketbank.others.utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class Login extends AppCompatActivity
{

    private static final String tag = "LoginActivity";
    private TextInputEditText emailInput, passwordInput;
    private MaterialButton loginButton, googleLoginButton;
    private ActivityLoginBinding activityLoginBinding;
    private ExecutorService loginExecutorService;
    private SQLiteDatabase loginReadableDatabase;
    private MaterialTextView register, helperText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private static Login loginContext;
    private GoogleSignInClient googleSignInClientLoginActivity;
    private ActivityResultLauncher<Intent> signInWithGoogle;
    private user user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.v(tag, getString(R.string.onCreate));
        super.onCreate(savedInstanceState);
        loginContext = this;
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());
        googleSignInClientLoginActivity = myApplication.getGoogleSignInClient();

        signInWithGoogle = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
        {

            @Override
            public void onActivityResult(ActivityResult result)
            {
                    try
                    {
                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult(ApiException.class);
                        handleGoogleSignInButtonClick(account);
                    }
                    catch (ApiException ee)
                    {
                        Toast.makeText(loginContext, GoogleSignInStatusCodes.getStatusCodeString(ee.getStatusCode()), Toast.LENGTH_SHORT).show();
                        ee.printStackTrace();
                    }
               // }
               // else
                    System.out.println("inside else part of Result..");
            }
            private void handleGoogleSignInButtonClick(GoogleSignInAccount account)
            {
                System.out.println("handleGoogleSignInButtonClick..");
                if (helperText.getVisibility() == View.GONE)
                {
                    Intent intent = new Intent(loginContext, Registration.class);
                    intent.putExtra("name", account.getDisplayName());
                    intent.putExtra("email", account.getEmail());
                    intent.putExtra("signInWithGoogle", true);
                    startActivity(intent);
                }
                else
                {
                    if (user.getEmail().equals(account.getEmail()))
                        rightCredentialFollowUp();
                    else
                    {
                        myApplication.dialogIt(R.string.oneUserAllowed, loginContext);
                        googleSignInClientLoginActivity.signOut();
                    }
                }
            }

        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.v(tag, "onStart() called");
        startInitialisation();
    }

    private void startInitialisation() {

        loginExecutorService = myApplication.getExecutorService();
        emailTextInputLayout = activityLoginBinding.emailInputLayout;
        passwordTextInputLayout = activityLoginBinding.passwordInputLayout;
        emailInput = activityLoginBinding.emailInput;
        passwordInput = activityLoginBinding.passwordInput;
        loginButton = activityLoginBinding.loginButton;
        register = activityLoginBinding.registerLink;
        helperText = activityLoginBinding.helpText;
        googleLoginButton = activityLoginBinding.googleSignInButton;
        loginReadableDatabase = myApplication.readableDatabase;
        checkUserExistsOrNot();
        setListeners();

    }

    private void setListeners() {
        loginButton.setOnClickListener(view -> loginUser());

        register.setOnClickListener(view -> {
            Intent intent = new Intent(loginContext, Registration.class);
            if (helperText.getVisibility() == View.VISIBLE)
            {
                Gson gson = new Gson();
                intent.putExtra("oneUserExists", true);
                intent.putExtra("thatParticularUser", gson.toJson(user));
            }
            startActivity(intent);
        });

        helperText.setOnClickListener(v -> {
            Intent intent = new Intent(loginContext, forgotDetails.class);
            startActivity(intent);
        });

        emailInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && Objects.requireNonNull(emailInput.getText()).length() != 0) {
                ((myApplication) getApplicationContext()).checkEmail(emailTextInputLayout, emailInput);
                addTextWatcher(emailInput);
            }
        });

        passwordInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && Objects.requireNonNull(passwordInput.getText()).length() != 0) {
                ((myApplication) getApplicationContext()).checkPassword(passwordTextInputLayout, passwordInput);
                addTextWatcher(passwordInput);
            }
        });

        googleLoginButton.setOnClickListener(v -> signInWithGoogle.launch(googleSignInClientLoginActivity.getSignInIntent()));

    }

    private void loginUser() {

        if (!((myApplication) getApplicationContext()).checkEmail(emailTextInputLayout, emailInput)) {
            if (!((myApplication) getApplicationContext()).checkPassword(passwordTextInputLayout, passwordInput))
                addTextWatcher(passwordInput);
            addTextWatcher(emailInput);
            emailTextInputLayout.requestFocus();
        } else if (!((myApplication) getApplicationContext()).checkPassword(passwordTextInputLayout, passwordInput)) {
            addTextWatcher(passwordInput);
            passwordTextInputLayout.requestFocus();
        } else {
            if (user == null)
                myApplication.dialogIt(R.string.invalidUser, loginContext);
            else {
                if (Objects.requireNonNull(emailInput.getText()).toString().equals(user.getEmail()) && Objects.requireNonNull(passwordInput.getText()).toString().equals(user.getPassword()))
                    rightCredentialFollowUp();
                else if (emailInput.getText().toString().equals(user.getEmail()))
                    myApplication.dialogIt(R.string.incorrectPassword, loginContext);
                else if (Objects.requireNonNull(passwordInput.getText()).toString().equals(user.getPassword()))
                    myApplication.dialogIt(R.string.incorrectEmail, loginContext);
                else
                    myApplication.dialogIt(R.string.incorrectLoginDetails, loginContext);
            }
        }
    }

    private void rightCredentialFollowUp()
    {
        utils utility = new utils(loginContext);
        utility.addUserToSharedPreferences(user);
        Toast.makeText(loginContext, R.string.loginSuccessful, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(loginContext, MainActivity.class));
        finish();
    }

    private void addTextWatcher(TextInputEditText textInputEditText) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (textInputEditText.getId() == R.id.emailInput)
                    ((myApplication) getApplicationContext()).checkEmail(emailTextInputLayout, emailInput);
                else
                    ((myApplication) getApplicationContext()).checkPassword(passwordTextInputLayout, passwordInput);
                checkErrorText(textInputEditText.getParent().getParent());
            }

        });
    }

    private void checkUserExistsOrNot()
    {
        loginExecutorService.execute(() ->
        {
            Cursor cursor = loginReadableDatabase.query("user", null, null, null, null, null, null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToNext();
                user = new user();
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                user.setRemainedAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("remainedAmount")));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                loginContext.runOnUiThread(() -> {
                    helperText.setVisibility(View.VISIBLE);
                    helperText.setOnClickListener(v -> startActivity(new Intent(loginContext, forgotDetails.class)));
                });
            }

            cursor.close();

        });

    }

    private void checkErrorText(ViewParent viewParent)
    {
        if (((TextInputLayout) viewParent).getError() == null)
            ((TextInputLayout) viewParent).setErrorEnabled(false);
    }

    public static void finishLogin()
    {
        loginContext.finish();
    }

}