package com.ko.efarming.company_info;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.ko.efarming.ui.EFProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

class GeoCodingAsyncTask extends AsyncTask<String, Void, String[]> {
    private String Address1 = "", Address2 = "", City = "", State = "", Country = "", County = "", PIN = "";
    private double latitiude,longtitude;
    private OnAddressListener delegate = null;
    private EFProgressDialog efProgressDialog;
    private Context addressFetchActivity;

    public void setOnAddressListener(OnAddressListener delegate){
        this.delegate = delegate;
    }
    public GeoCodingAsyncTask(double latitiude, double longtitude, Context addressFetchActivity, EFProgressDialog efProgressDialog){
        this.latitiude = latitiude;
        this.longtitude = longtitude;
        this.efProgressDialog = efProgressDialog;
        this.addressFetchActivity = addressFetchActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String[] doInBackground(String... params) {
        String response;
        try {
            response = getLatLongByURL("https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyAfPYXZkpF_Ll5Gvz8XhoHo0aY8jx_TCFY&latlng="+latitiude+","+longtitude+"&sensor=false");
            Log.d("response",""+response);
            return new String[]{response};
        } catch (Exception e) {
            delegate.onFetchFailure();
            return new String[]{"error"};
        }
    }

    @Override
    protected void onPostExecute(String... result) {
        try {
            JSONObject jsonObject = new JSONObject(result[0]);
            String Status = jsonObject.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObject.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                JSONArray address_components = zero.getJSONArray("address_components");

                for (int i = 0; i < address_components.length(); i++) {
                    JSONObject zero2 = address_components.getJSONObject(i);
                    String long_name = zero2.getString("long_name");
                    JSONArray mtypes = zero2.getJSONArray("types");
                    String Type = mtypes.getString(0);

                    if (!TextUtils.isEmpty(long_name)) {
                        if (Type.equalsIgnoreCase("street_number")) {
                            Address1 = long_name + " ";
                        } else if (Type.equalsIgnoreCase("route")) {
                            Address1 = Address1 + long_name;
                        } else if (Type.equalsIgnoreCase("sublocality")) {
                            Address2 = long_name;
                        } else if (Type.equalsIgnoreCase("locality")) {
                            // Address2 = Address2 + long_name + ", ";
                            City = long_name;
                        } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                            County = long_name;
                        } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                            State = long_name;
                        } else if (Type.equalsIgnoreCase("country")) {
                            Country = long_name;
                        } else if (Type.equalsIgnoreCase("postal_code")) {
                            PIN = long_name;
                        }
                    }

                    StringBuilder locationAddress = new StringBuilder();
                    ArrayList<String> locationList = new ArrayList<>();

                    if (!com.ko.efarming.util.TextUtils.isEmpty(Address1))
                        locationList.add(Address1);
                    if (!com.ko.efarming.util.TextUtils.isEmpty(Address2))
                        locationList.add(Address2);
                    if (!com.ko.efarming.util.TextUtils.isEmpty(City))
                        locationList.add(City);
                    if (!com.ko.efarming.util.TextUtils.isEmpty(State))
                        locationList.add(State);
                    if (!com.ko.efarming.util.TextUtils.isEmpty(Country))
                        locationList.add(Country);
                    if (!com.ko.efarming.util.TextUtils.isEmpty(PIN))
                        locationList.add(PIN);

                    for (int j = 0; j < locationList.size(); j++) {
                        if (j == locationList.size() - 1) {
                            locationAddress.append(locationList.get(j) + ".");
                        } else {
                            locationAddress.append(locationList.get(j) + ",");
                        }
                    }
                    delegate.onFetchedAddress(locationAddress.toString());
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
            delegate.onFetchFailure();
        }
        efProgressDialog.dismiss();

    }
    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            delegate.onFetchFailure();
            e.printStackTrace();
        }
        return response;
    }
}


