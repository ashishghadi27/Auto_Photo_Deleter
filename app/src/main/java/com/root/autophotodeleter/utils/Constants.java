package com.root.autophotodeleter.utils;

import android.os.Environment;

import java.io.File;

public final class Constants {

    private static final String cameraFolder="Camera";
    public static final String cameraPath = Environment.getExternalStorageDirectory().toString() + File.separator + "DCIM" + File.separator + cameraFolder;
    public static final String permissionMessage = "Devices with Android 11 and up need special permission to be able to perform deletion of photos. We need this permission only to delete photos";
    public static final String permissionTitle = "Special Permission";

    //PERMISSIONS
    public static final int READ_WRITE_STORAGE_REQUEST_CODE = 100;
    public static final int MANAGE_STORAGE_REQUEST_CODE = 101;

    //FILTER CODES
    public static final int TODAY = 1;
    public static final int SEVEN_DAYS = 2;
    public static final int WEEK = 3;
    public static final int MONTH = 4;
    public static final int CUSTOM = 5;

}
