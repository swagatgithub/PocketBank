package com.example.pocketbank.authentication;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.example.pocketbank.utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class Login extends AppCompatActivity
{
    private static final String tag = "LoginActivity";
    private TextInputEditText emailInput,passwordInput;
    private MaterialButton loginButton, googleLoginButton;
    private ActivityLoginBinding activityLoginBinding;
    private ExecutorService loginExecutorService;
    private SQLiteDatabase loginReadableDatabase;
    private MaterialTextView register,helperText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private Login loginContext;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> signInWithGoogle;
    private user user;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        Log.v(tag,getString(R.string.onCreate));
        super.onCreate(savedInstanceState);

        loginContext = this;

        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(activityLoginBinding.getRoot());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestId()
                .requestIdToken("null")
                .build();

        googleSignInClient = GoogleSignIn.getClient(loginContext,gso);

        signInWithGoogle = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
        {
            @Override
            public void onActivityResult(ActivityResult result)
            {
                if(result.getResultCode() == Activity.RESULT_OK)
                {
                    try
                    {
                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult(ApiException.class);
                        //handleGoogleSignInButtonClick(account);
                        System.out.println("getId is "+account.getId());
                        System.out.println("getIdToken is "+account.getIdToken());
                        System.out.println("photUri is "+account.getPhotoUrl());
                    }
                    catch (ApiException ee)
                    {
                        System.out.println("Inside Exception..");
                        Toast.makeText(loginContext,GoogleSignInStatusCodes.getStatusCodeString(ee.getStatusCode()),Toast.LENGTH_SHORT).show();
                        ee.printStackTrace();
                    }
                }
            }

            private void handleGoogleSignInButtonClick(GoogleSignInAccount account)
            {
                if(helperText.getVisibility() == View.GONE)
                {
                    Intent intent = new Intent(loginContext, Registration.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name",account.getDisplayName());
                    bundle.putString("email",account.getEmail());
                    bundle.putString("photoUri", Objects.requireNonNull(account.getPhotoUrl()).toString());
                    intent.putExtra("signInWithGoogle",bundle);
                    startActivity(intent);
                }
                else
                {
                    if(user.getEmail().equals(account.getEmail()))
                        rightCredentialFollowUp();
                    else
                        dialogIt(R.string.oneUserAllowed);
                }
            }

        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.v(tag,"onStart() called");
        startInitialisation();

    }

    private void startInitialisation()
    {

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

    private void setListeners()
    {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginContext, Registration.class);
                startActivity(intent);
            }

        });

        helperText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(loginContext, forgotDetails.class);
                startActivity(intent);
            }

        });

        emailInput.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus && Objects.requireNonNull(emailInput.getText()).length() != 0) {
                    ((myApplication)getApplicationContext()).checkEmail(emailTextInputLayout,emailInput);
                    addTextWatcher(emailInput);
                }
            }
        });

        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus && Objects.requireNonNull(passwordInput.getText()).length() != 0) {
                    ((myApplication)getApplicationContext()).checkPassword(passwordTextInputLayout,passwordInput);
                    addTextWatcher(passwordInput);
                }
            }

        });

        googleLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signInWithGoogle.launch(googleSignInClient.getSignInIntent());
            }

        });

    }

    private void loginUser()
    {
        if(!((myApplication)getApplicationContext()).checkEmail(emailTextInputLayout,emailInput))
        {
            if(!((myApplication)getApplicationContext()).checkPassword(passwordTextInputLayout,passwordInput))
                addTextWatcher(passwordInput);
            addTextWatcher(emailInput);
            emailTextInputLayout.requestFocus();
        }
        else if(!((myApplication)getApplicationContext()).checkPassword(passwordTextInputLayout,passwordInput))
        {
            addTextWatcher(passwordInput);
            passwordTextInputLayout.requestFocus();
        }
        else
        {
            if(user == null)
                dialogIt(R.string.invalidUser);
            else
            {
                if(Objects.requireNonNull(emailInput.getText()).toString().equals(user.getEmail()) && Objects.requireNonNull(passwordInput.getText()).toString().equals(user.getPassword()))
                    rightCredentialFollowUp();
                else if(emailInput.getText().toString().equals(user.getEmail()))
                    dialogIt(R.string.incorrectPassword);
                else if(Objects.requireNonNull(passwordInput.getText()).toString().equals(user.getPassword()))
                    dialogIt(R.string.incorrectEmail);
                else
                    dialogIt(R.string.incorrectLoginDetails);
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

    private void addTextWatcher(TextInputEditText textInputEditText)
    {
        textInputEditText.addTextChangedListener(new TextWatcher()
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
                if(textInputEditText.getId() == R.id.emailInput)
                    ((myApplication) getApplicationContext()).checkEmail(emailTextInputLayout, emailInput);
                else
                    ((myApplication)getApplicationContext()).checkPassword(passwordTextInputLayout,passwordInput);
                checkErrorText(textInputEditText.getParent().getParent());
            }

        });

    }

    private void checkUserExistsOrNot()
    {
        loginExecutorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                Cursor cursor = loginReadableDatabase.query("user",null,null,null,null,null,null);
                if(cursor.getCount() > 0)
                {
                    cursor.moveToNext();
                    user = new user();
                    user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                    user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                    user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                    user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                    user.setRemainedAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("remainedAmount")));
                    user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    loginContext.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            helperText.setVisibility(View.VISIBLE);
                            helperText.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    startActivity(new Intent(loginContext, forgotDetails.class));
                                }

                            });
                        }
                    });
                }
                cursor.close();
            }
        });

    }

    private void dialogIt(int messageId)
    {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(loginContext);
        materialAlertDialogBuilder.setMessage(messageId);
        materialAlertDialogBuilder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        materialAlertDialogBuilder.show();
    }

    private void checkErrorText(ViewParent viewParent)
    {
        if(((TextInputLayout)viewParent).getError() == null)
            ((TextInputLayout)viewParent).setErrorEnabled(false);
    }

}