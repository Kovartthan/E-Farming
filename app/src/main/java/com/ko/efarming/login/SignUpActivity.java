package com.ko.efarming.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.model.User;
import com.ko.efarming.util.AlertUtils;
import com.ko.efarming.util.CompressImage;
import com.ko.efarming.util.Constants;
import com.ko.efarming.util.DeviceUtils;
import com.ko.efarming.util.FileUtils2;
import com.ko.efarming.util.TempManager;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private Button mEmailSignInButton;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICTURE_FROM_GALLERY) {
                imagPaths = getPaths(this, data, true);
                File file = new File(imagPaths[0]);
                if (file.exists()) {
                    startCrop(file.getAbsolutePath());
                }

            } else if (requestCode == REQUEST_PICTURE_FROM_CAMERA) {
                File f = TempManager.getTempPictureFile(this);
                if (f != null) {
                    String path = f.getAbsolutePath();

                    CompressImage compressImage = new CompressImage(this);
                    path = compressImage.compressImage(path);

                    imagPaths = new String[]{path};
                    File file = new File(imagPaths[0]);
                    if (file.exists()) {
                        startCrop(file.getAbsolutePath());
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
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
        mEmailSignInButton = findViewById(R.id.email_sign_up_button);
    }

    private void setupDefault() {
        setupSpannableForSignIn();
    }

    private void setupSpannableForSignIn() {
        SpannableString signUpString = new SpannableString(getString(R.string.dnt_have_sign_up));

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
                ds.setColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        };

        signUpString.setSpan(new UnderlineSpan(), 0, 24, 0);
        signUpString.setSpan(clickableSpan, 24, signUpString.length(), 0);
        signUpString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SignUpActivity.this,android.R.color.white)), 24, signUpString.length(),  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtSignUp.setText(signUpString, TextView.BufferType.SPANNABLE);
        txtSignUp.setMovementMethod(LinkMovementMethod.getInstance());
    }



    private void setupEvent() {
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceUtils.hideSoftKeyboard(SignUpActivity.this, view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkAndRequestCameraPermissions()) {
                        promptMediaOption();
                    }
                } else {
                    promptMediaOption();
                }
            }
        });
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });
    }

    private void promptMediaOption() {

        final String[] ITEMS = {"Take Picture", "Choose Image"};

        openOptionDialog(this, ITEMS, "" + getString(R.string.app_name), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    openCamera();
                } else {
                    openGallery();
                }
            }
        });
    }

    private void openCamera() {
        String filePath = TempManager.createTempPictureFile(this).getAbsolutePath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));
        } else {
            File file = new File(filePath);
            Uri photoUri = FileProvider
                    .getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_PICTURE_FROM_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent = Intent.createChooser(intent, "Choose Image");
        startActivityForResult(intent, REQUEST_PICTURE_FROM_GALLERY);
    }


    public String[] getPaths(Context context, Intent intent, boolean isPicture) {
        ClipData clipData = intent.getClipData();
        String[] paths = new String[0];
        if (clipData != null) {
            paths = new String[clipData.getItemCount()];
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                String path = FileUtils2.getPath(context, item.getUri());
                paths[i] = path;
            }
        } else {
            if (intent.getData() != null) {
                paths = new String[1];
                paths[0] = FileUtils2.getPath(context, intent.getData());
            }
        }
        return paths;
    }

    private static void openOptionDialog(final Context context, String[] items, String title, DialogInterface.OnClickListener positiveClick) {
        ListAdapter adapter = new ArrayAdapter<String>(
                context, android.R.layout.select_dialog_item, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(getItem(position));
                textView.setTextSize(16f);
                if (position == 0) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_camera, 0, 0, 0);
                    textView.setCompoundDrawablePadding(DeviceUtils.getPixelFromDp(context, 15));
                } else {
                    textView.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_gallery, 0, 0, 0);
                    textView.setCompoundDrawablePadding(DeviceUtils.getPixelFromDp(context, 15));
                }
                return view;
            }
        };
        android.support.v7.app.AlertDialog.Builder builder = AlertUtils.getBuilder(context);
        builder.setTitle(title);
        builder.setAdapter(adapter, positiveClick);
        builder.create().show();
    }

    private boolean checkAndRequestCameraPermissions() {
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_PERMISSION_READ_STORAGE);

            return false;
        }
        return true;
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
                            promptMediaOption();
                        }
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                            showRequestDialog();
                            AlertUtils.showAlert(this, getResources().getString(R.string.storage_permission_required), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    checkAndRequestCameraPermissions();
                                }
                            },false);
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
                    promptMediaOption();
                } else {
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        promptSettings(getResources().getString(R.string.camera));
                    } else {
                        //                            showRequestDialog();
                        AlertUtils.showAlert(this, getResources().getString(R.string.storage_permission_required), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkAndRequestCameraPermissions();
                            }
                        },false);
                    }
                }
        }
    }

    private void startCrop(String source) {
//        String outputUrl=TempManager.createTempPictureFile(this).getAbsolutePath();
        Crop.of(Uri.fromFile(new File(source)), Uri.fromFile(new File(source))).start(SignUpActivity.this);
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
            passwordLayout.setError("Enter a valid email address");
            passwordLayout.setErrorEnabled(true);
            return;
        }

        if (password.length() < 6) {
            passwordLayout.setError("Enter minimum 6 characters");
            passwordLayout.setErrorEnabled(true);
            return;
        }

        doSignUp();
    }

    private void doSignUp() {
        getApp().getFireBaseAuth().createUserWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign up successful" + task.getException(),
                                    Toast.LENGTH_LONG).show();
                            addUserToDatabase(SignUpActivity.this,getApp().getFireBaseAuth().getCurrentUser());
//                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
//                            finish();
                        }
//                        alertUtils.dismissLoadingAlert();
                    }
                });
    }


    public void addUserToDatabase(Context context, FirebaseUser firebaseUser) {
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                FirebaseInstanceId.getInstance().getToken());
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void> () {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // successfully added user
                        } else {
                            // failed to add user
                        }
                    }
                });
    }


}
