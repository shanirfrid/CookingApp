package com.example.shanir.cookingappofshanir.classs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SupportActivity;
import android.widget.Toast;


@SuppressLint("RestrictedApi")
public class Permission extends SupportActivity {
    private String[] PERMISSIONS;
    private Activity mActivity;
    private Context mContext;
    private int cameraPermmision, readPermmision , writePermission;

    public Permission(Activity activity, Context context){
        this.PERMISSIONS =  new String[] {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        this.mActivity=activity;
        this.mContext=context;
        this.cameraPermmision=0;
        this.readPermmision=1;
        this.writePermission=2;
    }

    public void requestMultiplePermissions(){
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(mActivity,PERMISSIONS,1);
        }
    }
    private boolean hasPermissions() {
        if (this.mContext != null && PERMISSIONS != null) {
            for (String permission: PERMISSIONS){
                if (ActivityCompat.checkSelfPermission(this.mContext,permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[cameraPermmision] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.mContext, "Camera Permission is granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this.mContext, "Camera Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[readPermmision] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.mContext, "Read Permission is granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this.mContext, "Read Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[writePermission] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.mContext, "Write Permission is granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this.mContext, "Write Permission is denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
