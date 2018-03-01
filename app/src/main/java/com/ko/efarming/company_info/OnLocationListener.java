package com.ko.efarming.company_info;

import android.location.Location;

public interface OnLocationListener {
    void onLocationChanged(Location location);

    void onLocationConnected();

    void onLocationConnectionSuspended();

    void onLocationConnectionFailed();
}