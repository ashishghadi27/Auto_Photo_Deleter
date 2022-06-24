package com.root.autophotodeleter.utils;

import static android.os.Build.VERSION.SDK_INT;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import com.root.autophotodeleter.BuildConfig;

public final class PermissionManager {

    private PermissionManager(){

    }

    public static boolean hasAllAccess(Context context){
        return ((isReadStorageAllowed(context)) && (isBasicWriteStorageAllowed(context))
                && (SDK_INT < 30 || hasSpecialFileAccess()));
    }

    public static void requestForPermission(Activity activity){
        requestReadWritePermissions(activity);
    }

    public static void checkAndRequestForSpecialAccess(Activity activity){
        if(SDK_INT >= 30 && !hasSpecialFileAccess()){
            requestSpecialAccess(activity);
        }
    }

    public static boolean isReadStorageAllowed(Context context){
        return checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isBasicWriteStorageAllowed(Context context){
        return checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasSpecialFileAccess(){
        return SDK_INT < 30 || Environment.isExternalStorageManager();
    }

    private static void requestReadWritePermissions(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.READ_WRITE_STORAGE_REQUEST_CODE);
    }

    private static void requestSpecialAccess(Activity activity){
        if(SDK_INT >= 30){
            try {
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                activity.startActivityForResult(intent, Constants.MANAGE_STORAGE_REQUEST_CODE);
            } catch (Exception ex){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, Constants.MANAGE_STORAGE_REQUEST_CODE);
            }
        }
    }

}
