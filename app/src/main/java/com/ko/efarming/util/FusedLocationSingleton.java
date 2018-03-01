package com.ko.efarming.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ko.efarming.EFApp;
import com.ko.efarming.company_info.OnLocationListener;

import static com.ko.efarming.util.Constants.REQUEST_CHECK_SETTINGS;


public class FusedLocationSingleton implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private static FusedLocationSingleton mInstance = null;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public final static int FAST_LOCATION_FREQUENCY = 1000;
    public final static int LOCATION_FREQUENCY = 2 * 1000;

    public FusedLocationSingleton() {
        buildGoogleApiClient();
    }

   public OnLocationListener onLocationListener;

    public void setOnLocationListener(OnLocationListener onLocationListener){
        this.onLocationListener = onLocationListener;
    }



    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stopLocationUpdates();
    }

    public static FusedLocationSingleton getInstance() {
        if (null == mInstance) {
            mInstance = new FusedLocationSingleton();
        }
        return mInstance;
    }




    private synchronized void buildGoogleApiClient() {
        // setup googleapi client
        mGoogleApiClient = new GoogleApiClient.Builder(EFApp.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // setup location updates
        configRequestLocationUpdate();
    }

    ///////////// 2
    /**
     * config request location update
     */
    private void configRequestLocationUpdate() {
        mLocationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_FREQUENCY)
                .setFastestInterval(FAST_LOCATION_FREQUENCY);
    }

    ///////////// 3
    /**
     * request location updates
     */
    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        );


    }

    /**
     * start location updates
     */
    public void startLocationUpdates(final Activity activity) {
        // connect and force the updates
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            requestLocationUpdates();
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

                    final Status status = locationSettingsResult.getStatus();

                    Log.e("Fused", "onResult() called with: " + "result = [" + status.getStatusMessage() + "]");
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                Log.d("Fused", "", e);
                                // Ignore the error.
                            }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }

                }
            });
        }

    }

    /**
     * removes location updates from the FusedLocationApi
     */
    public void stopLocationUpdates() {
        // stop updates, disconnect from google api
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    /**
     * get last available location
     * @return last known location
     */
    @SuppressLint("MissingPermission")
    public Location getLastLocation(Activity activity) {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            // return last location
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        else {
            startLocationUpdates(activity); // start the updates
            return null;
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        // do location updates
        requestLocationUpdates();
        onLocationListener.onLocationConnected();

    }

    @Override
    public void onConnectionSuspended(int i) {
        // connection to Google Play services was lost for some reason
        onLocationListener.onLocationConnectionSuspended();
        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect(); // attempt to establish a new connection
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        onLocationListener.onLocationConnectionFailed();
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            onLocationListener.onLocationChanged(location);
            // send location in broadcast
            Intent intent = new Intent(Constants.INTENT_FILTER_LOCATION_UPDATE);
            intent.putExtra(Constants.LBM_EVENT_LOCATION_UPDATE, location);
            LocalBroadcastManager.getInstance(EFApp.getContext()).sendBroadcast(intent);
        }
    }




}