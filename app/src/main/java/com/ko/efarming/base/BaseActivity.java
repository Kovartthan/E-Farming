package com.ko.efarming.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ko.efarming.EFApp;
import com.ko.efarming.R;
import com.ko.efarming.ui.EFProgressDialog;

public class BaseActivity extends AppCompatActivity {
    public EFProgressDialog efProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        efProgressDialog = new EFProgressDialog(this);
    }

    public EFApp getApp() {
        return (EFApp) getApplication();
    }


    public void promptSettings(String type ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(String.format(getResources().getString(R.string.denied_title), type));
        builder.setMessage(String.format(getString(R.string.denied_msg), type));
        builder.setPositiveButton(getString(R.string.go_to_appsettings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goToSettings();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setCancelable(false);
        builder.show();
    }

    public void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + this.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(myAppSettings);
    }
}
