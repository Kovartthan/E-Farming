package com.ko.efarming.company_info;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.home.activities.HomeActivity;
import com.ko.efarming.model.CompanyInfo;
import com.ko.efarming.model.CompanyInfoPublic;
import com.ko.efarming.util.AlertUtils;
import com.ko.efarming.util.CameraUtils;
import com.ko.efarming.util.CompressImage;
import com.ko.efarming.util.Constants;
import com.ko.efarming.util.DeviceUtils;
import com.ko.efarming.util.MarshMallowPermissionUtils;
import com.ko.efarming.util.TempManager;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.ko.efarming.util.Constants.GET_ADDRESS;
import static com.ko.efarming.util.Constants.GET_LATITUDE;
import static com.ko.efarming.util.Constants.GET_LONGITUDE;
import static com.ko.efarming.util.Constants.PERMISSIONS;
import static com.ko.efarming.util.Constants.RC_ADDRESS;
import static com.ko.efarming.util.Constants.RC_MARSH_MALLOW_LOCATION_PERMISSION;
import static com.ko.efarming.util.Constants.REQUEST_PERMISSION_READ_STORAGE;
import static com.ko.efarming.util.Constants.REQUEST_PICTURE_FROM_CAMERA;
import static com.ko.efarming.util.Constants.REQUEST_PICTURE_FROM_GALLERY;
import static com.ko.efarming.util.DeviceUtils.hideSoftKeyboard;

public class CompanyInfoActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {
    private Toolbar toolbar;
    private ImageView imgPhoto;
    private TextInputLayout cmpanyNameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout locationLayout;
    private EditText edtCmyName, edtCmyEmail, edtCmyPhone, edtCmyLocation;
    private Button btnSubmit;
    private boolean isAddedDbPublic, isAddedDbPrivate;
    private String imagerls = "";
    private String imagePathForFireBase = "";
    private String[] imagPaths = null;
    private CameraUtils cameraUtils;
    private AppBarLayout appBarLayout;
    private double putLat, putLong;
    private boolean isPermissionFlag = false;

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
            } else if (requestCode == RC_ADDRESS) {
                if (data != null) {
                    if (data.hasExtra(GET_ADDRESS)) {
                        edtCmyLocation.setText(data.getStringExtra(GET_ADDRESS));
                    }
                    if (data.hasExtra(GET_LATITUDE) && data.hasExtra(GET_LONGITUDE)) {
                        putLat = data.getDoubleExtra(GET_LATITUDE, 0);
                        putLong = data.getDoubleExtra(GET_LONGITUDE, 0);
                    }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);
        init();
        setupDefault();
        setupEvent();
    }

    private void init() {
        imgPhoto = findViewById(R.id.img_cmpny_photo);
        toolbar = findViewById(R.id.toolbar);
        cmpanyNameLayout = findViewById(R.id.cmpany_name_layout);
        emailLayout = findViewById(R.id.email_layout);
        phoneLayout = findViewById(R.id.phone_layout);
        locationLayout = findViewById(R.id.location_layout);
        edtCmyName = findViewById(R.id.cmpany_name);
        edtCmyEmail = findViewById(R.id.email);
        edtCmyPhone = findViewById(R.id.cmpany_phone);
        edtCmyLocation = findViewById(R.id.cmy_location);
        btnSubmit = findViewById(R.id.submit);
        cameraUtils = new CameraUtils(this, CompanyInfoActivity.this);
        appBarLayout = findViewById(R.id.main_appbar);
        appBarLayout.addOnOffsetChangedListener(this);
    }

    private void setupDefault() {
        setupToolbar();
    }

    private void setupToolbar() {
        toolbar.setTitle("Setup Company Info");
    }

    private void setupEvent() {
        edtCmyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = MarshMallowPermissionUtils.checkLocationPermissionStatus(CompanyInfoActivity.this);
                if (!result) {
                    MarshMallowPermissionUtils.checkLocationPermission(CompanyInfoActivity.this);
                } else {
                    efProgressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            efProgressDialog.dismiss();
                            startActivityForResult(new Intent(CompanyInfoActivity.this, AddressFetchActivity.class), RC_ADDRESS);
                        }
                    },400);
                }
            }
        });
        addTextChangeListener(edtCmyName, cmpanyNameLayout);
        addTextChangeListener(edtCmyEmail, emailLayout);
        addTextChangeListener(edtCmyPhone, phoneLayout);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAddCompanyInfo();
            }
        });
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceUtils.hideSoftKeyboard(CompanyInfoActivity.this, view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (cameraUtils.checkAndRequestCameraPermissions()) {
                        cameraUtils.promptMediaOption();
                    }
                } else {
                    cameraUtils.promptMediaOption();
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
                            AlertUtils.showAlert(this, getResources().getString(R.string.storage_permission_required), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cameraUtils.checkAndRequestCameraPermissions();
                                }
                            }, false);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle(getResources().getString(R.string.go_to_settings_enable_permission));
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
                break;
            case RC_MARSH_MALLOW_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    efProgressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            efProgressDialog.dismiss();
                            startActivityForResult(new Intent(CompanyInfoActivity.this, AddressFetchActivity.class), RC_ADDRESS);
                        }
                    },400);
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    AlertUtils.showAlert(this, getResources().getString(R.string.location_permission_required), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MarshMallowPermissionUtils.checkLocationPermission(CompanyInfoActivity.this);
                        }
                    }, false);
                }
                break;
        }
    }


    private void attemptAddCompanyInfo() {

        if (TextUtils.isEmpty(edtCmyName.getText())) {
            cmpanyNameLayout.setError("Enter your name");
            cmpanyNameLayout.setErrorEnabled(true);
            return;
        }

        if (TextUtils.isEmpty(edtCmyPhone.getText())) {
            phoneLayout.setError("Enter your name");
            phoneLayout.setErrorEnabled(true);
            return;
        }

        if (TextUtils.isEmpty(edtCmyLocation.getText())) {
            AlertUtils.showAlert(this, "Please add your company location", null, false);
            return;
        }

        if (edtCmyLocation.getText().length() < 10) {
            AlertUtils.showAlert(this, "It seems your address looks short , point your accurate address", null, false);
            return;
        }


        submitCompanyInfo();
    }

    private void submitCompanyInfo() {

        if (isFinishing())
            return;

        hideSoftKeyboard(this);

        if (!DeviceUtils.isInternetConnected(this)) {
            Toast.makeText(this, R.string.err_internet, Toast.LENGTH_LONG).show();
            return;
        }

        if (efProgressDialog != null)
            efProgressDialog.show();

        addCompanyInfoToUsersDatabase();

    }

    private void addCompanyInfoToPublic() {
        CompanyInfoPublic companyInfo = new CompanyInfoPublic(edtCmyName.getText().toString(), edtCmyEmail.getText().toString(), edtCmyPhone.getText().toString(), edtCmyLocation.getText().toString(), imagerls, putLat, putLong);
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.COMPANY_INFO)
                .child(edtCmyName.getText().toString())
                .setValue(companyInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isAddedDbPublic = true;
                            redirectToHomeScreen();
                        } else {
                            isAddedDbPublic = false;
                        }
                    }
                });
    }

    private void addCompanyInfoToUsersDatabase() {
        CompanyInfo companyInfo = new CompanyInfo(edtCmyName.getText().toString(), edtCmyEmail.getText().toString(), edtCmyPhone.getText().toString(), edtCmyLocation.getText().toString());
        FirebaseDatabase.getInstance()
                .getReference(Constants.USERS)
                .child(getApp().getFireBaseAuth().getCurrentUser().getUid())
                .child(Constants.COMPANY_INFO)
                .setValue(companyInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isAddedDbPrivate = true;
                            Uri mImageUri = Uri.fromFile(new File(imagePathForFireBase));
                            if (!TextUtils.isEmpty(imagePathForFireBase)) {
                                StorageReference filepath = getApp().getFireBaseStorage().getReference().child("company_photo").child(imagePathForFireBase);
                                filepath.putFile(mImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        if (progress == 100) {
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
                                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                                        if (downloadUri != null) {
                                            imagerls = downloadUri.toString();
                                        }
//                                        redirectToHomeScreen();
                                    }

                                });

                            } else {
//                                redirectToHomeScreen();
                            }
                            addCompanyInfoToPublic();
                        } else {
                            isAddedDbPrivate = false;
                        }
                    }
                });
    }

    private void redirectToHomeScreen() {

        if (efProgressDialog.isShowing())
            efProgressDialog.dismiss();

        if (isAddedDbPrivate && isAddedDbPublic) {
            getApp().getAppPreference().setCheckRedirect(1);
            getApp().getFireBaseDataBase().child(Constants.USERS).child(getApp().getFireBaseAuth().getCurrentUser().getUid()).child(Constants.COMPNAY_PROFILE_UPDATED).setValue(true);
            startActivity(new Intent(CompanyInfoActivity.this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, R.string.err_msg, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }
}
