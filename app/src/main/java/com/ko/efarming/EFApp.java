package com.ko.efarming;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.ko.efarming.preference.AppPreference;

/**
 * Created by ko on 2/27/2018.
 */

public class EFApp extends Application {
    private static EFApp mInstance;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    public static Context mContext;
    private AppPreference mSharedPreferences;
    private DatabaseReference firebaseDatabase;
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        auth = FirebaseAuth.getInstance();
        mContext = getApplicationContext();
        mSharedPreferences = new AppPreference(this);
        storage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static EFApp getApp() {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new EFApp();
            mInstance.onCreate();
            return mInstance;
        }
    }


    public FirebaseAuth getFireBaseAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
            return auth;
        }
        return auth;
    }

    public FirebaseStorage getFireBaseStorage() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance();
            return storage;
        }
        return storage;
    }

    public DatabaseReference getFireBaseDataBase(){
        if(firebaseDatabase == null){
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseDatabase;
    }

    public static synchronized Context getContext() {
        return mContext;
    }

    public AppPreference getAppPreference() {
        return mSharedPreferences;
    }
}
