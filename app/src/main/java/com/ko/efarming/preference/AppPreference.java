package com.ko.efarming.preference;


import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {

    private final SharedPreferences mPreference;
    private final SharedPreferences.Editor mEditor;
    private Context context;
    private final static String PREF_NAME = "efarming_preference";
    private final static String LOGIN_STATUS = "login_status";
    private final static String CHECK_REDIRECT = "check_redirect";

    public AppPreference(Context context) {
        this.context = context;
        mPreference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mPreference.edit();
    }


    public void logout() {
        mEditor.clear();
        mEditor.commit();
    }

    public void setLoginStatus(boolean status) {
        mEditor.putBoolean(LOGIN_STATUS, status);
        mEditor.commit();
    }

    public boolean getLoginStatus() {
        return mPreference.getBoolean(LOGIN_STATUS, false);
    }

    public void setCheckRedirect(int status) {
        mEditor.putInt(CHECK_REDIRECT, status);
        mEditor.commit();
    }

    public Integer getCheckRedirect() {
        return mPreference.getInt(CHECK_REDIRECT, 0);
    }

}
