package com.ko.efarming.util;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.ko.efarming.R;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ko.efarming.util.Constants.REQUEST_PERMISSION_READ_STORAGE;
import static com.ko.efarming.util.Constants.REQUEST_PICTURE_FROM_CAMERA;
import static com.ko.efarming.util.Constants.REQUEST_PICTURE_FROM_GALLERY;

public class CameraUtils {

    public Context context;
    public Activity activity;
    
    public CameraUtils(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void promptMediaOption() {

        final String[] ITEMS = {"Take Picture", "Choose Image"};

        openOptionDialog(context, ITEMS, "" + context.getString(R.string.app_name), new DialogInterface.OnClickListener() {
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

    public void openCamera() {
        String filePath = TempManager.createTempPictureFile(context).getAbsolutePath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));
        } else {
            File file = new File(filePath);
            Uri photoUri = FileProvider
                    .getUriForFile(context, context.getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivityForResult(intent, REQUEST_PICTURE_FROM_CAMERA);
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent = Intent.createChooser(intent, "Choose Image");
        activity.startActivityForResult(intent, REQUEST_PICTURE_FROM_GALLERY);
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

    public static void openOptionDialog(final Context context, String[] items, String title, DialogInterface.OnClickListener positiveClick) {
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

    public boolean checkAndRequestCameraPermissions() {
        int storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_PERMISSION_READ_STORAGE);

            return false;
        }
        return true;
    }

    public void startCrop(String source) {
//        String outputUrl=TempManager.createTempPictureFile(this).getAbsolutePath();
        Crop.of(Uri.fromFile(new File(source)), Uri.fromFile(new File(source))).start(activity);
    }

}
