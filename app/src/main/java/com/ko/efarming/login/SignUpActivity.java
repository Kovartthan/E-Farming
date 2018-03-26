package com.ko.efarming.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.model.User;
import com.ko.efarming.model.UserFingerPrint;
import com.ko.efarming.util.AlertUtils;
import com.ko.efarming.util.CameraUtils;
import com.ko.efarming.util.CompressImage;
import com.ko.efarming.util.Constants;
import com.ko.efarming.util.DeviceUtils;
import com.ko.efarming.util.TempManager;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.ko.efarming.util.Constants.PERMISSIONS;
import static com.ko.efarming.util.Constants.REQUEST_PERMISSION_READ_STORAGE;
import static com.ko.efarming.util.Constants.REQUEST_PICTURE_FROM_CAMERA;
import static com.ko.efarming.util.Constants.REQUEST_PICTURE_FROM_GALLERY;
import static com.ko.efarming.util.DeviceUtils.hideSoftKeyboard;
import static com.ko.efarming.util.TextUtils.isValidEmail;

public class SignUpActivity extends BaseActivity {
    private String[] imagPaths = null;
    private ImageView imgPhoto;
    private TextView txtSignUp;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mName;
    private EditText mConfirmPass;
    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPassLayout;
    private Button mEmailSignInButton;
    private String imagerls = "";
    private String imagePathForFireBase = "";
    private CameraUtils cameraUtils;
    private boolean isFingerPrintEnabled;
    private RadioGroup radioGroup;
    private FingerprintManager fingerprintManager;
    private CheckedTextView checkedTextView;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICTURE_FROM_GALLERY) {
                imagPaths = cameraUtils.getPaths(this, data, true);
                imagePathForFireBase = imagPaths[0];
                File file = new File(imagPaths[0]);
                if (file.exists()) {
                    cameraUtils.startCrop(file.getAbsolutePath());
                }

            } else if (requestCode == REQUEST_PICTURE_FROM_CAMERA) {
                File f = TempManager.getTempPictureFile(this);
                if (f != null) {
                    String path = f.getAbsolutePath();
                    imagePathForFireBase = path;

                    CompressImage compressImage = new CompressImage(this);
                    path = compressImage.compressImage(path);


                    imagPaths = new String[]{path};
                    File file = new File(imagPaths[0]);
                    if (file.exists()) {
                        cameraUtils.startCrop(file.getAbsolutePath());
                    }
                }
            }
            if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, data);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        setupDefault();
        setupEvent();
    }

    private void init() {
        imgPhoto = findViewById(R.id.img_profile_photo);
        txtSignUp = findViewById(R.id.txt_sign_up);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        nameLayout = findViewById(R.id.name_layout);
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
        confirmPassLayout = findViewById(R.id.confirm_pass_layout);
        mEmailSignInButton = findViewById(R.id.email_sign_up_button);
        mName = findViewById(R.id.name);
        mConfirmPass = findViewById(R.id.conf_password);
        cameraUtils = new CameraUtils(this, SignUpActivity.this);
        radioGroup = findViewById(R.id.rg_fingerprint);
        checkedTextView = findViewById(R.id.txt_fingerprint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            if (!fingerprintManager.isHardwareDetected()) {
                checkedTextView.setVisibility(View.INVISIBLE);
            }
        } else {
            checkedTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void setupDefault() {
        setupSpannableForSignIn();
    }

    private void setupSpannableForSignIn() {
        SpannableString signUpString = new SpannableString(getString(R.string.already_have_an_account_sign_in));

        final ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(final View view) {
                DeviceUtils.hideSoftKeyboard(SignUpActivity.this);
                finish();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

//        signUpString.setSpan(new UnderlineSpan(), 0, 24, 0);
        signUpString.setSpan(clickableSpan, 24, signUpString.length(), 0);
        signUpString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SignUpActivity.this, android.R.color.holo_green_dark)), 24, signUpString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtSignUp.setText(signUpString, TextView.BufferType.SPANNABLE);
        txtSignUp.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void setupEvent() {

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceUtils.hideSoftKeyboard(SignUpActivity.this, view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (cameraUtils.checkAndRequestCameraPermissions()) {
                        cameraUtils.promptMediaOption();
                    }
                } else {
                    cameraUtils.promptMediaOption();
                }
            }
        });

        addTextChangeListener(mEmailView, emailLayout);
        addTextChangeListener(mPasswordView, passwordLayout);
        addTextChangeListener(mConfirmPass, confirmPassLayout);
        addTextChangeListener(mName, nameLayout);

        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });

        mConfirmPass.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                attemptSignup();
                return false;
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkedTextView.isChecked()){
                    isFingerPrintEnabled = false;
                    checkedTextView.setChecked(false);
                }else{
                    isFingerPrintEnabled = true;
                    checkedTextView.setChecked(true);
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_READ_STORAGE: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(PERMISSIONS, 5);
                            }
                        } else {
                            cameraUtils.promptMediaOption();
                        }
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                            showRequestDialog();
                            AlertUtils.showAlert(this, getResources().getString(R.string.storage_permission_required), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cameraUtils.checkAndRequestCameraPermissions();
                                }
                            }, false);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle(getResources().getString(R.string.go_to_settings_enable_permission));
//                                builder.setMessage(String.format(getString(R.string.denied_msg), type));
                            builder.setPositiveButton(getResources().getString(R.string.go_to_appsettings), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    goToSettings();
                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.cancel), null);
                            builder.setCancelable(false);
                            builder.show();

                        }
                    }
                }
            }

            break;
            case 5:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraUtils.promptMediaOption();
                } else {
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        promptSettings(getResources().getString(R.string.camera));
                    } else {
                        //                            showRequestDialog();
                        AlertUtils.showAlert(this, getResources().getString(R.string.storage_permission_required), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cameraUtils.checkAndRequestCameraPermissions();
                            }
                        }, false);
                    }
                }
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            imagPaths = new String[]{uri.getPath()};
            File file = new File(imagPaths[0]);
            if (file.exists()) {
                imgPhoto.setImageURI(uri);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptSignup() {

        hideSoftKeyboard(this);

        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        if (TextUtils.isEmpty(mName.getText())) {
            nameLayout.setError("Enter your name");
            nameLayout.setErrorEnabled(true);
            return;
        }

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

        if (TextUtils.isEmpty(mConfirmPass.getText().toString())) {
            confirmPassLayout.setError("Enter a confirm password");
            confirmPassLayout.setErrorEnabled(true);
            return;
        }

        if (password.length() < 6) {
            passwordLayout.setError("Enter minimum 6 characters");
            passwordLayout.setErrorEnabled(true);
            return;
        }

        if (mConfirmPass.length() < 6) {
            confirmPassLayout.setError("Enter minimum 6 characters");
            confirmPassLayout.setErrorEnabled(true);
            return;
        }

        if (!password.equals(mConfirmPass.getText().toString())) {
            AlertUtils.showAlert(this, "Please enter same password in both fields", null, false);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isFingerPrintEnabled && !fingerprintManager.hasEnrolledFingerprints()) {
                AlertUtils.showAlert(this, "No fingerprints enrolled in your device, go to settings to enroll your fingerprints.", null, false);
            }
        }

        doSignUp();
    }

    private void doSignUp() {

        if (isFinishing())
            return;

        hideSoftKeyboard(this);

        if (!DeviceUtils.isInternetConnected(this)) {
            Toast.makeText(this, R.string.err_internet, Toast.LENGTH_LONG).show();
            return;
        }

        if (efProgressDialog != null)
            efProgressDialog.show();

        getApp().getFireBaseAuth().createUserWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (efProgressDialog.isShowing())
                                efProgressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            final String uid = task.getResult().getUser().getUid();
                            Uri mImageUri = Uri.fromFile(new File(imagePathForFireBase));
                            if (!TextUtils.isEmpty(imagePathForFireBase)) {
                                StorageReference filepath = getApp().getFireBaseStorage().getReference().child("user_profile").child(imagePathForFireBase);
                                filepath.putFile(mImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        if (progress == 100) {
//                                        hideProgressDialog();
                                            //upload();
                                        }
                                        System.out.println("Upload is " + progress + "% done");
                                    }
                                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                        System.out.println("Upload is paused");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        /** Get Image Download Path**/
                                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                                        /** Converting Image Uri In String **/

                                        if (downloadUri != null) {
                                            imagerls = downloadUri.toString();
                                        }

                                        //Add user data and image URL to firebase database
                                        if (efProgressDialog.isShowing())
                                            efProgressDialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_LONG).show();
                                        if (isFingerPrintEnabled)
                                            addFingerPrintToDatabase(SignUpActivity.this, getApp().getFireBaseAuth().getCurrentUser());
                                        else
                                            addUserToDatabase(SignUpActivity.this, getApp().getFireBaseAuth().getCurrentUser());
                                        finish();
                                    }

                                });

                            } else {
                                if (efProgressDialog.isShowing())
                                    efProgressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_LONG).show();
                                if (isFingerPrintEnabled)
                                    addFingerPrintToDatabase(SignUpActivity.this, getApp().getFireBaseAuth().getCurrentUser());
                                else
                                    addUserToDatabase(SignUpActivity.this, getApp().getFireBaseAuth().getCurrentUser());
                                finish();
                            }
                        }

                    }
                });
    }


    public void addUserToDatabase(Context context, final FirebaseUser firebaseUser) {
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                FirebaseInstanceId.getInstance().getToken(), imagerls, false,"");
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        } else {
                        }
                    }
                });
    }

    public void addFingerPrintToDatabase(Context context, final FirebaseUser firebaseUser) {
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                FirebaseInstanceId.getInstance().getToken(), imagerls, false, mConfirmPass.getText().toString());
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                                addFingerPrintAuthentication(firebaseUser);
                        } else {

                        }
                    }
                });
    }


    @SuppressLint("HardwareIds")
    public void addFingerPrintAuthentication(FirebaseUser firebaseUser) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("fingerprint")
                .child(android.os.Build.SERIAL)
                .setValue(firebaseUser.getUid());
    }
}
