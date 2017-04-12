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
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import ua.com.aveshcher.whatsaroundandroid.activity.MainActivity;
import ua.com.aveshcher.whatsaroundandroid.dto.Place;
import ua.com.aveshcher.whatsaroundandroid.request.Comparer;
import ua.com.aveshcher.whatsaroundandroid.request.RequestManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GPSService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private String TAG = "LOCSERVICE: ";
    private int radius;
    private String category;
    private Set<Place> oldPlaces;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        category = intent.getStringExtra(MainActivity.CATEGORY);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        oldPlaces = new HashSet<>();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int refreshTime = Integer.valueOf(sharedPref.getString("refresh_time", "120"));
        radius = Integer.valueOf(sharedPref.getString("search_radius", "494"));



//        Log.d(TAG, "onCreate()");
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                Log.d(TAG, "onLocationChanged()");
                Intent i = new Intent("location_update");
                i.putExtra("latitude", location.getLatitude());
                i.putExtra("longitude", location.getLongitude());
                sendBroadcast(i);

                RequestManager requestManager = new RequestManager();
                requestManager.receiveJSON(new RequestManager.VolleyCallback() {
                    @Override
                    public void onSuccess(Set<Place> places) {
//                        Toast.makeText(getApplicationContext(),
//                                "service places found" + places.size() + " " + radius + " " + category, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "service places found" + places.size() + " " + radius + " " + category);

                        //finding new places
                        Set<Place> newPlaces = places;
                        Set<Place> diffPlaces = Comparer.diffPlaces(oldPlaces,newPlaces);
                        oldPlaces = places;
                        Log.d(TAG, "New places found " + diffPlaces.size());
                        Toast.makeText(getApplicationContext(),
                                "New places found " + diffPlaces.size() , Toast.LENGTH_SHORT).show();
                    }
                }, getApplicationContext(),location.getLatitude(),location.getLongitude(),radius,category);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
//                Log.d(TAG, "onProviderDisabled()");
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);



        //noinspection MissingPermission
        //TODO change to refreshTime*1000
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,refreshTime*100,0,listener);

    }

    @Override
    public void onDestroy() {
//        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
