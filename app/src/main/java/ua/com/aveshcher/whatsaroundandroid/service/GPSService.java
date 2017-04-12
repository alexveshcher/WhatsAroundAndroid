package ua.com.aveshcher.whatsaroundandroid.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class GPSService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private String tag = "locatishow: ";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

//        Log.d(tag, "onCreate()");
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                Log.d(tag, "onLocationChanged()");
                Intent i = new Intent("location_update");
                i.putExtra("latitude", location.getLatitude());
                i.putExtra("longitude", location.getLongitude());
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
//                Log.d(tag, "onProviderDisabled()");
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int refreshTime = Integer.valueOf(sharedPref.getString("refresh_time", "120"));

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,refreshTime*1000,0,listener);

    }

    @Override
    public void onDestroy() {
//        Log.d(tag, "onDestroy()");
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
