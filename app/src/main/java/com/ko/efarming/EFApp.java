package com.ko.efarming;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by ko on 2/27/2018.
 */

public class EFApp extends Application {
    private static EFApp mInstance;
    private FirebaseAuth auth;
    private     FirebaseStorage storage;
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

        storage = FirebaseStorage.getInstance();
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



    public FirebaseAuth getFireBaseAuth(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
            return auth;
        }
        return auth;
    }

    public FirebaseStorage getFireBaseStorage(){
        if(storage == null){
            storage = FirebaseStorage.getInstance();
            return storage;
        }
        return storage;
    }
}
