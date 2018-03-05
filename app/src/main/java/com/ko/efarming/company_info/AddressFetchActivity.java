package com.ko.efarming.company_info;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ko.efarming.EFApp;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.util.AlertUtils;
import com.ko.efarming.util.Constants;
import com.ko.efarming.util.FusedLocationSingleton;
import com.ko.efarming.util.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ko.efarming.util.Constants.GET_ADDRESS;
import static com.ko.efarming.util.Constants.GET_LATITUDE;
import static com.ko.efarming.util.Constants.GET_LONGITUDE;
import static com.ko.efarming.util.Constants.REQUEST_CHECK_SETTINGS;

public class AddressFetchActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener, OnAddressListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private boolean isPermissionFlag = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private String TAG = AddressFetchActivity.class.getSimpleName();
    private Geocoder geocoder;
    private TextView txtTitle;
    private ImageView imgSubmitAddress, imgBack;
    private String address = "";
    private TextView txtAddress;
    private double getLat, getLong;
    private boolean isMarkerPlaced;
    private GeoCodingAsyncTask geoCodingAsyncTask;
    private FusedLocationSingleton fusedLocationSingleton;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public final static int FAST_LOCATION_FREQUENCY = 1000;
    public final static int LOCATION_FREQUENCY = 2 * 1000;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        startLocationUpdates(AddressFetchActivity.this);
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_fetch);
        init();
        setupDefault();
        setupEvent();
    }

    private void init() {
        buildGoogleApiClient();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        txtTitle = findViewById(R.id.txt_title);
        imgSubmitAddress = findViewById(R.id.img_right_first);
        txtAddress = findViewById(R.id.display_address);
        imgBack = findViewById(R.id.img_menu);
    }

    private void setupDefault() {
        txtTitle.setText("Point your address");
        txtAddress.setText(R.string.address_marker);
        imgBack.setImageResource(R.drawable.ic_arrow_back_white_24dp);
        imgBack.setVisibility(View.VISIBLE);
        imgSubmitAddress.setImageResource(R.drawable.ic_done_white_24dp);
        imgSubmitAddress.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startLocationUpdates(AddressFetchActivity.this);
            }
        }, 200);
    }

    private void setupEvent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imgSubmitAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isNullOrEmpty(address)) {
                    Intent addressIntent = new Intent();
                    addressIntent.putExtra(GET_ADDRESS, address);
                    addressIntent.putExtra(GET_LATITUDE, getLat);
                    addressIntent.putExtra(GET_LONGITUDE, getLong);
                    setResult(RESULT_OK, addressIntent);
                    finish();
                } else {
                    Toast.makeText(AddressFetchActivity.this, "No address found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
        addLocationButton();
    }

    private void addLocationButton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        txtAddress.setText(R.string.fetch_address);
        imgSubmitAddress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        txtAddress.setText(R.string.fetch_address);
        imgSubmitAddress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        final LatLng position = marker.getPosition();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                address = getAddressFromTheMarker(position.latitude, position.longitude);
                if (!TextUtils.isNullOrEmpty(address)) {
                    imgSubmitAddress.setVisibility(View.VISIBLE);
                    txtAddress.setText(address);
                } else {
                    if (efProgressDialog != null) {
                        efProgressDialog.show();
                    }
                    geoCodingAsyncTask = new GeoCodingAsyncTask(position.latitude, position.longitude, AddressFetchActivity.this, efProgressDialog);
                    geoCodingAsyncTask.setOnAddressListener(AddressFetchActivity.this);
                    geoCodingAsyncTask.execute();
                }
            }
        }, 300);
    }


    /**
     * Creating the latlng object to store lat, long coordinates
     * adding marker to map
     * move the camera with animation
     */
    private void moveMap(double latitude, double longitude) {
        mMap.clear();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            moveMap(location.getLatitude(), location.getLongitude());
                        } else {

                        }
                    }
                });
    }


    private String getAddressFromTheMarker(double latitude, double longitude) {
        List<Address> addresses = new ArrayList<>();

        StringBuilder locationAddress = new StringBuilder();
        ArrayList<String> locationList = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() <= 0) {
                return "";
            }
            if (addresses.size() != 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                if (!TextUtils.isEmpty(address)) {
                    locationAddress.append(address);
                } else {
                    if (!TextUtils.isEmpty(city))
                        locationList.add(city);
                    if (!TextUtils.isEmpty(state))
                        locationList.add(state);
                    if (!TextUtils.isEmpty(country))
                        locationList.add(country);
                    if (!TextUtils.isEmpty(postalCode))
                        locationList.add(postalCode);
                    for (int i = 0; i < locationList.size(); i++) {
                        if (i == locationList.size() - 1) {
                            locationAddress.append(locationList.get(i) + ".");
                        } else {
                            locationAddress.append(locationList.get(i) + ",");
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationAddress.toString();
    }

    @Override
    public void onFetchedAddress(String address) {
        if (!TextUtils.isEmpty(address)) {
            imgSubmitAddress.setVisibility(View.VISIBLE);
            txtAddress.setText(address);
        } else {
            imgSubmitAddress.setVisibility(View.INVISIBLE);
            txtAddress.setText("Address fetching failed, check your Gps or Internet connection is enabled or not");
        }
    }

    @Override
    public void onFetchFailure(String status) {
        if (Constants.LOCATION_FETCH_FAILED.equals(status)) {
            AlertUtils.showAlert(this, "Address fetching failed, check your Gps or Internet connection is enabled or not", null, false);
        } else {
            AlertUtils.showAlert(this, "Address fetching failed, check your Gps or Internet connection is enabled or not", null, false);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stopLocationUpdates();
    }


    private synchronized void buildGoogleApiClient() {
        // setup googleapi client
        mGoogleApiClient = new GoogleApiClient.Builder(EFApp.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        // setup location updates
        configRequestLocationUpdate();
    }


    /**
     * config request location update
     */
    private void configRequestLocationUpdate() {
        mLocationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_FREQUENCY)
                .setFastestInterval(FAST_LOCATION_FREQUENCY);
    }


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
     */
    @SuppressLint("MissingPermission")
    public Location getLastLocation(Activity activity) {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            // return last location
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            startLocationUpdates(activity); // start the updates
            return null;
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        // do location updates
        requestLocationUpdates();
        if (isMarkerPlaced) {
            getLastKnownLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // connection to Google Play services was lost for some reason
        Toast.makeText(AddressFetchActivity.this, "Retrying....", Toast.LENGTH_LONG).show();
        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect(); // attempt to establish a new connection
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(AddressFetchActivity.this, "Check your gps signal or click and try again", Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // send location in broadcast
            Intent intent = new Intent(Constants.INTENT_FILTER_LOCATION_UPDATE);
            intent.putExtra(Constants.LBM_EVENT_LOCATION_UPDATE, location);
            LocalBroadcastManager.getInstance(EFApp.getContext()).sendBroadcast(intent);
            if(!isMarkerPlaced) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                moveMap(latLng.latitude, latLng.longitude);
                isMarkerPlaced = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}
