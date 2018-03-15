package com.ko.efarming.base;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.ko.efarming.EFApp;
import com.ko.efarming.R;
import com.ko.efarming.model.OnlineStatus;
import com.ko.efarming.ui.EFProgressDialog;
import com.ko.efarming.util.Constants;
import com.ko.efarming.util.GpsUtils;

import java.util.Calendar;

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

    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {

                if(GpsUtils.isGpsEnabled(context)){
                    Log.e("BaseActivity ", " Gps Enabled");
                    onGpsStatusChanged(true);
                }else {
                    Log.e("BaseActivity ", " Gps Disabled");
                    onGpsStatusChanged(false);
                }
            }
        }
    };

    private void onGpsStatusChanged(boolean b) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gpsReceiver);
    }


    public void addTextChangeListener(EditText editText, final TextInputLayout textInputLayout){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void setOnlineStatus(boolean isOnline) {
        OnlineStatus onlineStatus = new OnlineStatus(Calendar.getInstance().getTimeInMillis(), isOnline);
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS)
                .child(getApp().getFireBaseAuth().getCurrentUser().getUid()).child(Constants.ONLINE_STATUS)
                .setValue(onlineStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    }
                });
    }



}
