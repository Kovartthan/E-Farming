package com.ko.efarming.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;

import static com.ko.efarming.util.DeviceUtils.hideSoftKeyboard;

public class ForgotActivity extends BaseActivity {
    private Button btnReset;
    private EditText inputEmail;
    private TextInputLayout emailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        init();
        setupDefault();
        setupEvents();
    }

    private void init() {
        inputEmail = findViewById(R.id.email);
        btnReset = findViewById(R.id.btn_reset_password);
        emailLayout = findViewById(R.id.email_layout);
    }

    private void setupDefault() {

    }

    private void setupEvents() {

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftKeyboard(ForgotActivity.this);

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailLayout.setError("Enter your email address");
                    emailLayout.setErrorEnabled(true);
                    return;
                }

                if (efProgressDialog != null)
                    efProgressDialog.show();

                getApp().getFireBaseAuth().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotActivity.this, "We have sent you instructions to reset your password to your mail id!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ForgotActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }
                                if (efProgressDialog.isShowing())
                                    efProgressDialog.dismiss();
                            }
                        });
            }
        });

        inputEmail.addTextChangedListener(new TextWatcher() {
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

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
