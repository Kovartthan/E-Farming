package com.ko.efarming.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.ko.efarming.R;


public class MarshMallowPermissionUtils {

    public static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE};

    public static boolean checkLocationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((context.checkSelfPermission(PERMISSIONS[0])) != PackageManager.PERMISSION_GRANTED) {
                if (PERMISSIONS[0].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ((Activity)context).requestPermissions(new String[]{PERMISSIONS[0]}, Constants.RC_MARSH_MALLOW_LOCATION_PERMISSION);
                    //   return false;
                }
            }else{
                return true;
            }
        }else{
            return true;
        }
        return false;
    }

    public static boolean checkLocationPermissionStatus(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((context.checkSelfPermission(PERMISSIONS[0])) != PackageManager.PERMISSION_GRANTED) {
                if (PERMISSIONS[0].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    //   return false;
                }
            }else{
                return true;
            }
        }else{
            return true;
        }
        return false;
    }


    public static boolean checkCameraPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((context.checkSelfPermission(PERMISSIONS[1])) != PackageManager.PERMISSION_GRANTED) {
                if (PERMISSIONS[1].equalsIgnoreCase(Manifest.permission.CAMERA)) {
                    ((Activity)context).requestPermissions(new String[]{PERMISSIONS[1]}, Constants.RC_MARSH_MALLOW_CAMERA_PERMISSION);
                    return false;
                }
            }else{
                return true;
            }
        }else{
            return true;
        }
        return false;
    }


    public static boolean checkExternalStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((context.checkSelfPermission(PERMISSIONS[2])) != PackageManager.PERMISSION_GRANTED) {
                if (PERMISSIONS[2].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ((Activity)context).requestPermissions(new String[]{PERMISSIONS[2]}, Constants.RC_MARSH_MALLOW_READ_EXTERNAL_STORAGE_PERMISSION);
                    return false;
                }
            }else{
                return true;
            }
        }else{
            return true;
        }
        return false;
    }


    public static void navigateToSettingsForLocation(final Activity activity) {
        AlertUtils.showCommonAlertDialogWithPositive(activity, activity.getResources().getString(R.string.gps_alert_message), String.format(activity.getResources().getString(R.string.denied_msg), "Location"), activity.getResources().getString(R.string.go_to_appsettings)/*,activity.getResources().getString(R.string.cancel)*/ ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
//                DeviceUtils.hideSoftKeyboard(SignUpActivity.this);
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
//                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivityForResult(myAppSettings, Constants.RC_GPS);
            }
        });
    }




    public static boolean requestCameraPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((context.checkSelfPermission(PERMISSIONS[1])) != PackageManager.PERMISSION_GRANTED) {
                if (PERMISSIONS[1].equalsIgnoreCase(Manifest.permission.CAMERA)) {
                    return false;
                }
            }else{
                return true;
            }
        }else{
            return true;
        }
        return false;
    }

    public static boolean crequestExternalStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((context.checkSelfPermission(PERMISSIONS[2])) != PackageManager.PERMISSION_GRANTED) {
                if (PERMISSIONS[2].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    return false;
                }
            }else{
                return true;
            }
        }else{
            return true;
        }
        return false;
    }


    public static boolean requestExternalLocationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((context.checkSelfPermission(PERMISSIONS[0])) != PackageManager.PERMISSION_GRANTED) {
                if (PERMISSIONS[0].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                       return false;
                }
            }else{
                return true;
            }
        }else{
            return true;
        }
        return false;
    }
}

