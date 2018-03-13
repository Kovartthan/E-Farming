package com.ko.efarming.login.fingerprint;

/**
 * Created by admin on 3/13/2018.
 */

public interface OnFingerPrintAuthenticationListener {
    void onFingerPrintAuthenticationError(String error);
    void onFingerPrintAuthenticationSucceeded();
    void onFingerPrintAuthenticationFailed();
}
