package com.ko.efarming.util;

import android.Manifest;

/**
 * Created by admin on 2/1/2018.
 */

public interface Constants {
    public static String USERS = "users";
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
    int REQUEST_PERMISSION_READ_STORAGE = 2;
    int REQUEST_PICTURE_FROM_GALLERY = 200;
    int REQUEST_PICTURE_FROM_CAMERA = 201;
    int RC_MARSH_MALLOW_LOCATION_PERMISSION = 1001;
    int RC_MARSH_MALLOW_CAMERA_PERMISSION = 1002;
    int RC_MARSH_MALLOW_READ_EXTERNAL_STORAGE_PERMISSION = 1003;
    int RC_GPS = 1004;
    int REQUEST_CHECK_SETTINGS = 1000;
    String LBM_EVENT_LOCATION_UPDATE = "lbmLocationUpdate";
    String INTENT_FILTER_LOCATION_UPDATE = "intentFilterLocationUpdate";
}
