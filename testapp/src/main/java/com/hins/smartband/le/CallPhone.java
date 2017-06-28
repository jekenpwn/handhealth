package com.hins.smartband.le;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

public class CallPhone {
    private String phone;
    private Context context;

    public CallPhone(Context context, String phone) {
        this.phone = phone;//电话号码
        this.context = context;
    }

    public void call() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            context.startActivity(intent);// 将Intent传递给Activity
        }

    }
}
