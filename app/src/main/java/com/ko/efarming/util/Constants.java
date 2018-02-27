package com.ko.efarming.util;

import android.Manifest;

/**
 * Created by admin on 2/1/2018.
 */

public interface Constants {
    public static String USERS = "users";
    public static String CHAT_ROOMS = "chat_rooms";
    public String GET_CHAT_OBJECT = "get_chat_object";
    public String ONLINE_STATUS = "online_status";
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
    int REQUEST_PERMISSION_READ_STORAGE = 2;
    int REQUEST_PICTURE_FROM_GALLERY = 200;
    int REQUEST_PICTURE_FROM_CAMERA = 201;
    int RC_MARSH_MALLOW_READ_EXTERNAL_STORAGE_PERMISSION = 1003;
}
