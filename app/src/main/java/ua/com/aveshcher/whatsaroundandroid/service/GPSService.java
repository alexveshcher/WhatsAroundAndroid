package ua.com.aveshcher.whatsaroundandroid.service;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import ua.com.aveshcher.whatsaroundandroid.R;
import ua.com.aveshcher.whatsaroundandroid.activity.MainActivity;
import ua.com.aveshcher.whatsaroundandroid.activity.MapsActivity;
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
    HashSet<Place> diffPlaces;

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
        diffPlaces = new HashSet<>();
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
                i.putExtra("new_places",diffPlaces);
                sendBroadcast(i);

                RequestManager requestManager = new RequestManager();
                requestManager.receiveJSON(new RequestManager.VolleyCallback() {
                    @Override
                    public void onSuccess(Set<Place> places) {
//                        Toast.makeText(getApplicationContext(),
//                                "service places found" + places.size() + " " + radius + " " + category, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "service places found" + places.size() + " " + radius + " " + category);

                        //finding new places
                        diffPlaces = Comparer.diffPlaces(oldPlaces, places);
                        oldPlaces = places;
                        Log.d(TAG, "New places found " + diffPlaces.size());
//                        Toast.makeText(getApplicationContext(),
//                                "New places found " + diffPlaces.size() , Toast.LENGTH_SHORT).show();
                        notificate();
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

    private void notificate(){
        int newPlacesCount = diffPlaces.size();
        if(newPlacesCount > 0){

            String info = "";
            for(Place p : diffPlaces){
                info += p.getName() + " " + p.getAddress() + "\n";
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.common_full_open_on_phone)
                            .setContentTitle("WhatsAround")
                            .setContentText(info)
                            .setTicker("We found " + newPlacesCount + " new places for you");

            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(this, MapsActivity.class);


            resultIntent.putExtra("new_places", diffPlaces);


            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MapsActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);


            // Sets an ID for the notification
            int mNotificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            Log.d(TAG, "notificate: ");
            notificationManager.notify(mNotificationId, mBuilder.build());

        }
    }
}
