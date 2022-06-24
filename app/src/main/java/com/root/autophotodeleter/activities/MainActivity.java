package com.root.autophotodeleter.activities;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.root.autophotodeleter.R;
import com.root.autophotodeleter.fragments.ImageLoadingFragment;
import com.root.autophotodeleter.operationHandlers.CameraPicsReader;
import com.root.autophotodeleter.utils.Constants;
import com.root.autophotodeleter.utils.PermissionManager;
import com.root.autophotodeleter.vo.FileInfoVO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AlertDialog dialog;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constants.READ_WRITE_STORAGE_REQUEST_CODE
                && PermissionManager.isReadStorageAllowed(this)
                && PermissionManager.isBasicWriteStorageAllowed(this)){
            if(SDK_INT >= 30){
                dialog = getPermissionDialog(this);
                dialog.show();
            }
            else {
                loadFragment();
            }
        }
        else {
            finish();;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("PERMISSION LOOP", "ON Activity");

        if(requestCode == Constants.MANAGE_STORAGE_REQUEST_CODE
                && PermissionManager.hasAllAccess(this)){
            Log.i("PERMISSION LOOP", "ALL ACCESS");
            dialog.dismiss();
            loadFragment();
        }
        else {
            Log.i("PERMISSION LOOP", "ALL ACCESS");
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(PermissionManager.hasAllAccess(this)){
            Log.i("HERE", "ALL ACCESS");
            loadFragment();
        }
        else {
            Log.i("REQUEST PERMISSIONS", "NO ACCESS");
            PermissionManager.requestForPermission(this);
        }
    }

    private void loadFragment(){
        getSupportFragmentManager()
                .beginTransaction().add(R.id.fragment_container, ImageLoadingFragment.newInstance(), "IMAGE_LOADING")
                //.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right)
                .commit();
    }

    public AlertDialog getPermissionDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialog = View.inflate(activity, R.layout.info_dialogue, null);
        TextView titleText = dialog.findViewById(R.id.title);
        TextView messageText = dialog.findViewById(R.id.message);
        Button button = dialog.findViewById(R.id.ok);

        button.setOnClickListener(v -> PermissionManager.checkAndRequestForSpecialAccess(activity));

        titleText.setText(Constants.permissionTitle);
        messageText.setText(Constants.permissionMessage);
        builder.setView(dialog);
        builder.setCancelable(false);
        return builder.create();
    }

}