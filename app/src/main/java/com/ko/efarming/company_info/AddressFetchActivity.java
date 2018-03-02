package com.ko.efarming.company_info;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ko.efarming.R;
import com.ko.efarming.base.BaseActivity;
import com.ko.efarming.util.AlertUtils;
import com.ko.efarming.util.FusedLocationSingleton;
import com.ko.efarming.util.MarshMallowPermissionUtils;
import com.ko.efarming.util.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ko.efarming.util.Constants.GET_ADDRESS;
import static com.ko.efarming.util.Constants.GET_LATITUDE;
import static com.ko.efarming.util.Constants.GET_LONGITUDE;
import static com.ko.efarming.util.Constants.RC_MARSH_MALLOW_LOCATION_PERMISSION;

public class AddressFetchActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener, OnLocationListener, GoogleMap.OnMarkerClickListener, OnAddressListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_fetch);
        init();
        setupDefault();
        setupEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Called");
        if (isPermissionFlag) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean result = MarshMallowPermissionUtils.checkLocationPermissionStatus(AddressFetchActivity.this);
                if (result) {
                    isPermissionFlag = false;
//                    startLocationUpdates();
                } else {
                    MarshMallowPermissionUtils.navigateToSettingsForLocation(AddressFetchActivity.this);
                }
            }
        } else {
            FusedLocationSingleton.getInstance().startLocationUpdates(AddressFetchActivity.this);
        }
        forceLocationUpdates();
    }

    public void forceLocationUpdates() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            FusedLocationSingleton.getInstance().startLocationUpdates(AddressFetchActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FusedLocationSingleton.getInstance().stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case RC_MARSH_MALLOW_LOCATION_PERMISSION: {
                Log.e(TAG, "Request code accept");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Log.e(TAG, "Marshmallow enters");
                    isPermissionFlag = true;
//                    MarshMallowPermissionUtils.navigateToSettingsForLocation(SignUpActivity.this);
                }
            }
        }
    }


    private void init() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        FusedLocationSingleton.getInstance().setOnLocationListener(this);
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
        MarshMallowPermissionUtils.checkLocationPermissionStatus(AddressFetchActivity.this);
        imgSubmitAddress.setImageResource(R.drawable.ic_done_white_24dp);
        imgSubmitAddress.setVisibility(View.INVISIBLE);
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
                    setResult(RESULT_OK);
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
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        txtAddress.setText(R.string.fetch_address);
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        moveMap(latLng.latitude,latLng.longitude);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onLocationConnected() {
        getLastKnownLocation();
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

    @Override
    public void onLocationConnectionSuspended() {
        Toast.makeText(AddressFetchActivity.this, "Exit this page and try again", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationConnectionFailed() {
        Toast.makeText(AddressFetchActivity.this, "Check your gps signal or click and try again", Toast.LENGTH_LONG).show();
        finish();
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
    public void onFetchFailure() {
        AlertUtils.showAlert(this, "Address fetching failed, check your Gps or Internet connection is enabled or not", null, false);
    }
}
