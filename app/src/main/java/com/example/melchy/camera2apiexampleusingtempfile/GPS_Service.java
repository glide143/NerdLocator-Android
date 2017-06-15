package com.example.melchy.camera2apiexampleusingtempfile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import java.util.Calendar;

/**
 * Created by Melchy on 6/11/2017.
 */

public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    public boolean isGpsEnable = false;
    public boolean isNetworkEnable = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String timeDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Intent i = new Intent("location_update");
                i.putExtra("longitude",location.getLongitude());
                i.putExtra("latitude",location.getLatitude());
                i.putExtra("timeDate",timeDate);
                sendBroadcast(i);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(isNetworkEnable){
            //120000 -2mins
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,120000,0,listener);
        }
        if(isGpsEnable){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,120000,0,listener);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
