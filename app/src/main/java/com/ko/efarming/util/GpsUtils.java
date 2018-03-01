package com.ko.efarming.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.util.Log;

import com.ko.efarming.R;


public class GpsUtils {

    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return !(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

    }

    public static void showGpsAlert(final Context context,DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onClickCancelListener) {
        String message = context.getString(R.string.gps_alert_message);
        new AlertDialog.Builder(context).setMessage(message)
                .setTitle(context.getString(R.string.app_name))
                .setCancelable(true)
                .setNegativeButton("Cancel",onClickCancelListener)
                .setPositiveButton(R.string.settings,onClickListener).create()
                .show();
    }

    public static String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
//            distance *= 1000;
            distance = 0;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }
        Log.d("GpsUtils", "formatNumber: "+distance);
        return String.format("%.02f", distance);
    }
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        double km = dist * 1.609344f;
        return (km);
    }

    public static  double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
