package com.ko.efarming.login;

import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.company_info.CompanyInfoActivity;
import com.ko.efarming.home.activities.HomeActivity;
import com.ko.efarming.login.fingerprint.FingerPrintUtils;
import com.ko.efarming.login.fingerprint.FingerprintHandler;
import com.ko.efarming.model.User;
import com.ko.efarming.util.Constants;
import com.ko.efarming.util.DeviceUtils;

import javax.crypto.Cipher;

import static com.ko.efarming.util.DeviceUtils.hideSoftKeyboard;
import static com.ko.efarming.util.TextUtils.isValidEmail;

public class LoginActivity extends BaseActivity {
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private TextView txtSignUp;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private boolean isCompanyProfileUpdated = false;
    private ValueEventListener valueEventListener = null;
    private static final String FINGERPRINT_KEY = "key_name";
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerPrintUtils fingerPrintUtils;
    private FingerprintHandler fingerprintHandler;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkWhetherUserLoggedIn();
        init();
        setupDefault();
        setupEvent();
    }

    private void checkWhetherUserLoggedIn() {
        if (getApp().getFireBaseAuth().getCurrentUser() != null) {
            if (getWhetherUserCompletedCompanyProfile()) {
                getApp().getAppPreference().setCheckRedirect(1);
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                startActivity(new Intent(LoginActivity.this, CompanyInfoActivity.class));
                finish();
            }
        }
    }

    private void init() {

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        txtSignUp = findViewById(R.id.txt_sign_up);
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerPrintUtils = new FingerPrintUtils(this,this,fingerprintManager,keyguardManager);
            fingerprintHandler = new FingerprintHandler(this);
            cipher = fingerPrintUtils.instantiateCipher();
            if(cipher != null){
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
            }
        }

    }

    private void setupDefault() {
        setupSpannableForSignUp();
    }

    private void setupSpannableForSignUp() {
        SpannableString signUpString = new SpannableString(getString(R.string.dnt_have_sign_up));

        final ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(final View view) {
                DeviceUtils.hideSoftKeyboard(LoginActivity.this);
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

//        signUpString.setSpan(new UnderlineSpan(), 0, 23, 0);
        signUpString.setSpan(clickableSpan, 23, signUpString.length(), 0);
        signUpString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(LoginActivity.this, android.R.color.holo_green_dark)), 23, signUpString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtSignUp.setText(signUpString, TextView.BufferType.SPANNABLE);
        txtSignUp.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void setupEvent() {
        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailLayout.setError(null);
                emailLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordLayout.setError(null);
                passwordLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mPasswordView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                attemptLogin();
                return false;
            }
        });

        findViewById(R.id.txt_frgt_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
            }
        });
    }

    private void attemptLogin() {

        hideSoftKeyboard(this);

        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Enter your email address");
            emailLayout.setErrorEnabled(true);
            return;
        }

        if (!isValidEmail(email)) {
            emailLayout.setError("Enter a valid email address");
            emailLayout.setErrorEnabled(true);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Enter a password");
            passwordLayout.setErrorEnabled(true);
            return;
        }

        if (password.length() < 6) {
            passwordLayout.setError("Enter minimum 6 characters");
            passwordLayout.setErrorEnabled(true);
            return;
        }

        doLogin();
    }

    private void doLogin() {

        if (isFinishing())
            return;
        hideSoftKeyboard(this);
        if (!DeviceUtils.isInternetConnected(this)) {
            Toast.makeText(this, R.string.err_internet, Toast.LENGTH_LONG).show();
            return;
        }

        if (efProgressDialog != null)
            efProgressDialog.show();

        getApp().getFireBaseAuth().signInWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (efProgressDialog != null)
                                efProgressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed ", Toast.LENGTH_LONG).show();
                        } else {
                            final DatabaseReference ref = getApp().getFireBaseDataBase().child(Constants.USERS).child(getApp().getFireBaseAuth().getCurrentUser().getUid());
                            valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    Log.e("Firebase", "user  "+user.email);
                                    isCompanyProfileUpdated = user.isCompanyProfileUpdated;
                                    Log.e("Firebase", "isCompanyProfileUpdated "+isCompanyProfileUpdated);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (efProgressDialog != null)
                                                efProgressDialog.dismiss();
                                            if (isCompanyProfileUpdated) {
                                                getApp().getAppPreference().setCheckRedirect(1);
                                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                finish();
                                            } else {
                                                startActivity(new Intent(LoginActivity.this, CompanyInfoActivity.class));
                                                finish();
                                            }
                                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                            ref.removeEventListener(valueEventListener);
                                        }
                                    },200);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("Firebase", "onCancelled", databaseError.toException());
                                }
                            };
                            ref.addValueEventListener(valueEventListener);
                        }
                    }
                });
    }

    private boolean getWhetherUserCompletedCompanyProfile() {
        DatabaseReference ref = getApp().getFireBaseDataBase().child(Constants.USERS).child(getApp().getFireBaseAuth().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                isCompanyProfileUpdated = user.isCompanyProfileUpdated;
                Log.e("Firebase", "isCompanyProfileUpdated "+isCompanyProfileUpdated);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "onCancelled", databaseError.toException());
            }
        });
        return isCompanyProfileUpdated;
    }
}
