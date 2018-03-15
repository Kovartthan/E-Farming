package com.ko.efarming.util;

import android.Manifest;

/**
 * Created by admin on 2/1/2018.
 */

public interface Constants {
    public static String USERS = "users";
    public static String COMPANY_INFO = "company_info";
    public static String COMPNAY_PROFILE_UPDATED = "isCompanyProfileUpdated";
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
    int REQUEST_PERMISSION_READ_STORAGE = 2;
    int REQUEST_PICTURE_FROM_GALLERY = 200;
    int REQUEST_PICTURE_FROM_CAMERA = 201;
    int RC_MARSH_MALLOW_LOCATION_PERMISSION = 1001;
    int RC_MARSH_MALLOW_CAMERA_PERMISSION = 1002;
    int RC_MARSH_MALLOW_READ_EXTERNAL_STORAGE_PERMISSION = 1003;
    int RC_GPS = 1004;
    int REQUEST_CHECK_SETTINGS = 1000;
    int RC_ADDRESS = 1005;
    String LBM_EVENT_LOCATION_UPDATE = "lbmLocationUpdate";
    String INTENT_FILTER_LOCATION_UPDATE = "intentFilterLocationUpdate";
    String GET_ADDRESS = "get_address";
    String GET_LATITUDE = "get_latitude";
    String GET_LONGITUDE = "get_longitude";
    String  LOCATION_FETCH_FAILED = "location_fetch_failed";
    String SEND_RECT = "send_rect";
    public static String PRODUCT_INFO = "product_info";
    public  int REFRESH_PRODUCT = 1100;
    String EDIT_PRODUCT = "edit_product";
    public static String USERS_INFO = "user_info";
    public String CHAT_ROOMS ="chat_rooms";
    public static final String ARG_RECEIVER = "receiver";
    public static final String ARG_RECEIVER_UID = "receiver_uid";
    public static final String ARG_CHAT_ROOMS = "chat_rooms";
    public static final String ARG_FIREBASE_TOKEN = "firebaseToken";
    public static final int REQUEST_USE_FINGERPRINT = 300;
    public static final String RC_CITY = "city";
    public String ONLINE_STATUS = "online_status";
}
