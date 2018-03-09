package com.ko.efarming.home.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hendraanggrian.reveallayout.Radius;
import com.hendraanggrian.reveallayout.RevealableLayout;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.model.CompanyInfo;
import com.ko.efarming.model.ProductInfo;
import com.ko.efarming.model.RectClass;
import com.ko.efarming.model.User;
import com.ko.efarming.util.AlertUtils;
import com.ko.efarming.util.CameraUtils;
import com.ko.efarming.util.CompressImage;
import com.ko.efarming.util.Constants;
import com.ko.efarming.util.DeviceUtils;
import com.ko.efarming.util.TempManager;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.ko.efarming.util.Constants.PERMISSIONS;
import static com.ko.efarming.util.Constants.REQUEST_PERMISSION_READ_STORAGE;
import static com.ko.efarming.util.Constants.REQUEST_PICTURE_FROM_CAMERA;
import static com.ko.efarming.util.Constants.REQUEST_PICTURE_FROM_GALLERY;
import static com.ko.efarming.util.DeviceUtils.hideSoftKeyboard;

public class AddOrEditProductActivity extends BaseActivity {
    private RectClass rect;
    private RevealableLayout revealLayout;
    private Toolbar toolbar;
    private ViewGroup layout;
    private ImageView imgMenu;
    private TextView txtTitle;
    private ImageView imgProductPhoto;
    private AppCompatEditText edtProductName;
    private AppCompatEditText edtProductQuantity;
    private AppCompatEditText edtProductPrice;
    private Button btnAdd;
    private String imagerls = "";
    private String imagePathForFireBase = "";
    private CameraUtils cameraUtils;
    private String[] imagPaths = null;
    private boolean isAddedDbPublic;
    private boolean isAddedDbPrivate;
    private ProductInfo editProductInfo;
    private String productName = "";
    private boolean isEdit;
    private String productKey;
    private CompanyInfo companyInfo;
    private User userDetails = null;

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
        setContentView(R.layout.activity_add_product);
        init();
        setupDefault();
        setupEvent();
    }

    private void init() {
        revealLayout = findViewById(R.id.revealLayout);
        toolbar = findViewById(R.id.toolbar);
        layout = findViewById(R.id.layout);
        imgMenu = findViewById(R.id.img_menu);
        txtTitle = findViewById(R.id.txt_title);
        imgProductPhoto = findViewById(R.id.img_cmpny_photo);
        edtProductName = findViewById(R.id.edt_product_name);
        edtProductQuantity = findViewById(R.id.edt_product_quantity);
        edtProductPrice = findViewById(R.id.edt_product_price);
        btnAdd = findViewById(R.id.submit);
        cameraUtils = new CameraUtils(this, AddOrEditProductActivity.this);
    }

    private void setupDefault() {
        getIntentData();
        if (!isEdit) {
            doRevealAction();
        }
        loadCompanyData();
    }

    private void loadCompanyData() {

        if (efProgressDialog != null)
            efProgressDialog.show();

        getCompanyName();

    }




    private void getIntentData() {
        if (getIntent() != null && getIntent().hasExtra(Constants.SEND_RECT)) {
            rect = (RectClass) getIntent().getSerializableExtra(Constants.SEND_RECT);
        }
        if (getIntent() != null && getIntent().hasExtra(Constants.EDIT_PRODUCT)) {
            editProductInfo = (ProductInfo) getIntent().getSerializableExtra(Constants.EDIT_PRODUCT);
            isEdit = true;
            btnAdd.setText("Edit");
            txtTitle.setText("Edit Product");
            productName = editProductInfo.productName;
            productKey = editProductInfo.productID;
            edtProductName.setText(editProductInfo.productName);
            edtProductQuantity.setText(editProductInfo.productQuantity);
            edtProductPrice.setText(editProductInfo.productPrice);
            if (!com.ko.efarming.util.TextUtils.isNullOrEmpty(editProductInfo.imageUrl)) {
                Picasso.with(this).load(editProductInfo.imageUrl).into(imgProductPhoto);
                imagerls = editProductInfo.imageUrl;
            }
        }
    }

    private void doRevealAction() {
        layout.post(new Runnable() {
            @Override
            public void run() {
                Animator animator = revealLayout.reveal(layout, HomeActivity.rectClass.getRect().centerX(), HomeActivity.rectClass.getRect().centerY(), Radius.GONE_ACTIVITY);
                animator.setDuration(700);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (Build.VERSION.SDK_INT >= 21) {
//                            getWindow().setStatusBarColor(Themes.getColor(getTheme(), R.attr.colorPrimaryDark, true));
                        }
                    }
                });
                animator.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!isEdit) {
            exitRevealAction();
        }
    }

    private void exitRevealAction() {
        Animator animator = revealLayout.reveal(layout, HomeActivity.rectClass.getRect().centerX(), HomeActivity.rectClass.getRect().centerY(), Radius.ACTIVITY_GONE);
        animator.setDuration(700);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (Build.VERSION.SDK_INT >= 21) {
//                    getWindow().setStatusBarColor(Themes.getColor(getTheme(), R.attr.colorPrimaryDark, true));
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                layout.setVisibility(View.INVISIBLE);
                finish();
                overridePendingTransition(0, 0);
            }
        });
        animator.start();
    }


    private void setupEvent() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAddProduct();
            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEdit) {
                    exitRevealAction();
                }
            }
        });
        findViewById(R.id.photo_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceUtils.hideSoftKeyboard(AddOrEditProductActivity.this, view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (cameraUtils.checkAndRequestCameraPermissions()) {
                        cameraUtils.promptMediaOption();
                    }
                } else {
                    cameraUtils.promptMediaOption();
                }
            }
        });
        addTextListener(edtProductName);
        addTextListener(edtProductPrice);
        addTextListener(edtProductQuantity);
    }

    private void addTextListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                imgProductPhoto.setImageURI(uri);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptAddProduct() {

        hideSoftKeyboard(this);

        if (TextUtils.isEmpty(edtProductName.getText())) {
            edtProductName.setError("Enter your product name");
            return;
        }

        if (TextUtils.isEmpty(edtProductPrice.getText())) {
            edtProductPrice.setError("Enter your product price");
            return;
        }

        if (TextUtils.isEmpty(edtProductQuantity.getText())) {
            edtProductQuantity.setError("Enter your product quantity");
            return;
        }

        doAddProduct();
    }

    private void doAddProduct() {

        if (isFinishing())
            return;

        hideSoftKeyboard(this);

        if (!DeviceUtils.isInternetConnected(this)) {
            Toast.makeText(this, R.string.err_internet, Toast.LENGTH_LONG).show();
            return;
        }

        if (efProgressDialog != null)
            efProgressDialog.show();

        if (isEdit)
            updateProductInfoOnDb();
        else
            addProductInfoToUsersDatabase();


    }

    private void addProductInfoToPublic(final String key) {
        ProductInfo productInfo = new ProductInfo(edtProductName.getText().toString(), edtProductQuantity.getText().toString(), edtProductPrice.getText().toString(), imagerls, key);
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.PRODUCT_INFO)
                .child(key)
                .setValue(productInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isAddedDbPublic = true;
                            Uri mImageUri = Uri.fromFile(new File(imagePathForFireBase));
                            if (!TextUtils.isEmpty(imagePathForFireBase)) {
                                StorageReference filepath = getApp().getFireBaseStorage().getReference().child("product_photo").child(imagePathForFireBase);
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
                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child(Constants.PRODUCT_INFO)
                                                    .child(key).child("imageUrl").setValue(imagerls);
                                        }
                                    }

                                });

                            } else {
                            }
                            addCompanyInfoIntoProduct(key);

                        } else {
                            isAddedDbPublic = false;
                        }
                    }
                });
    }

    private void addCompanyInfoIntoProduct(final String key) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.PRODUCT_INFO)
                .child(key).child(Constants.COMPANY_INFO).setValue(companyInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    addUserInfoForChatInDb(key);

                }else{
                    Toast.makeText(AddOrEditProductActivity.this, "Product not added, please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addUserInfoForChatInDb(String key) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.PRODUCT_INFO)
                .child(key).child(Constants.USERS_INFO).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    efProgressDialog.dismiss();
                    if (isAddedDbPrivate && isAddedDbPublic) {
                        Toast.makeText(AddOrEditProductActivity.this, "Product added successfully", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                }else{
                    Toast.makeText(AddOrEditProductActivity.this, "Product not added, please try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void addProductInfoToUsersDatabase() {
        final String key = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS)
                .child(getApp().getFireBaseAuth().getCurrentUser().getUid())
                .child(Constants.COMPANY_INFO)
                .child(Constants.PRODUCT_INFO).push().getKey();
        ProductInfo productInfo = new ProductInfo(edtProductName.getText().toString(), edtProductQuantity.getText().toString(), edtProductPrice.getText().toString(), imagerls, key);
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS)
                .child(getApp().getFireBaseAuth().getCurrentUser().getUid())
                .child(Constants.COMPANY_INFO)
                .child(Constants.PRODUCT_INFO)
                .child(key)
                .setValue(productInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isAddedDbPrivate = true;
                            Uri mImageUri = Uri.fromFile(new File(imagePathForFireBase));
                            if (!TextUtils.isEmpty(imagePathForFireBase)) {
                                StorageReference filepath = getApp().getFireBaseStorage().getReference().child("product_photo");
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
                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child(Constants.USERS)
                                                    .child(getApp().getFireBaseAuth().getCurrentUser().getUid())
                                                    .child(Constants.COMPANY_INFO)
                                                    .child(Constants.PRODUCT_INFO)
                                                    .child(key).child("imageUrl").setValue(imagerls);
                                        }
                                    }

                                });

                            } else {
                            }
                            addProductInfoToPublic(key);
                        } else {
                            isAddedDbPrivate = false;
                        }
                    }
                });
    }

    private void getCompanyName() {
        DatabaseReference ref = getApp().getFireBaseDataBase().child(Constants.USERS).child(getApp().getFireBaseAuth().getCurrentUser().getUid()).child(Constants.COMPANY_INFO);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                efProgressDialog.dismiss();
                companyInfo = dataSnapshot.getValue(CompanyInfo.class);
                getUserInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                efProgressDialog.dismiss();
                exitRevealAction();
                Toast.makeText(AddOrEditProductActivity.this,"Cant connect to Database, please try again ",Toast.LENGTH_LONG).show();
            }
        });
    }



    private void updateProductInfoOnDb() {
        ProductInfo productInfo = new ProductInfo(edtProductName.getText().toString(), edtProductQuantity.getText().toString(), edtProductPrice.getText().toString(), imagerls, editProductInfo.productID);
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS)
                .child(getApp().getFireBaseAuth().getCurrentUser().getUid())
                .child(Constants.COMPANY_INFO)
                .child(Constants.PRODUCT_INFO)
                .child(editProductInfo.productID)
                .setValue(productInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isAddedDbPrivate = true;

                            Uri mImageUri = Uri.fromFile(new File(imagePathForFireBase));

                            if (!TextUtils.isEmpty(imagePathForFireBase)) {
                                StorageReference filepath = getApp().getFireBaseStorage().getReference().child("product_photo").child(imagePathForFireBase);
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
                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child(Constants.USERS)
                                                    .child(getApp().getFireBaseAuth().getCurrentUser().getUid())
                                                    .child(Constants.COMPANY_INFO)
                                                    .child(Constants.PRODUCT_INFO)
                                                    .child(editProductInfo.productID).child("imageUrl").setValue(imagerls);
                                        }
                                    }

                                });

                            } else {
                            }
                            updateProductInfoToPublic(editProductInfo.productID);
                        } else {
                            isAddedDbPrivate = false;
                        }
                    }
                });
    }

    private void updateProductInfoToPublic(final String key) {
        ProductInfo productInfo = new ProductInfo(edtProductName.getText().toString(), edtProductQuantity.getText().toString(), edtProductPrice.getText().toString(), imagerls, key);
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.PRODUCT_INFO)
                .child(key)
                .setValue(productInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isAddedDbPublic = true;
                            Uri mImageUri = Uri.fromFile(new File(imagePathForFireBase));
                            if (!TextUtils.isEmpty(imagePathForFireBase)) {
                                StorageReference filepath = getApp().getFireBaseStorage().getReference().child("product_photo").child(imagePathForFireBase);
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
                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child(Constants.PRODUCT_INFO)
                                                    .child(key).child("imageUrl").setValue(imagerls);
                                        }
                                    }

                                });

                            } else {
                            }
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child(Constants.PRODUCT_INFO)
                                    .child(key).child(Constants.COMPANY_INFO).setValue(companyInfo);
                            efProgressDialog.dismiss();
                            if (isAddedDbPrivate && isAddedDbPublic) {
                                Toast.makeText(AddOrEditProductActivity.this, "Product edited successfully", Toast.LENGTH_LONG).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(AddOrEditProductActivity.this, "Product not edited, please try again", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            isAddedDbPublic = false;
                        }
                    }
                });
    }

    private void getUserInfo() {
        DatabaseReference ref = getApp().getFireBaseDataBase().child(Constants.USERS).child(getApp().getFireBaseAuth().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                efProgressDialog.dismiss();
                userDetails = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                efProgressDialog.dismiss();
                exitRevealAction();
                Toast.makeText(AddOrEditProductActivity.this,"Cant connect to Database, please try again ",Toast.LENGTH_LONG).show();
            }
        });
    }

}
