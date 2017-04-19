package ua.com.aveshcher.whatsaroundandroid.service;


import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
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
import ua.com.aveshcher.whatsaroundandroid.R;
import ua.com.aveshcher.whatsaroundandroid.activity.MainActivity;
import ua.com.aveshcher.whatsaroundandroid.activity.MapsActivity;
import ua.com.aveshcher.whatsaroundandroid.dto.Place;
import ua.com.aveshcher.whatsaroundandroid.request.Comparer;
import ua.com.aveshcher.whatsaroundandroid.request.RequestManager;

import java.util.HashSet;
import java.util.Set;

public class MonitorService extends Service {

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
        if(intent.getAction() != null){
            if(intent.getAction().equals("STOP")) {
                stopSelf();
            }
        }

        category = intent.getStringExtra(MainActivity.CATEGORY);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        showServiceStatusNotification();
        oldPlaces = new HashSet<>();
        diffPlaces = new HashSet<>();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int refreshTime = Integer.valueOf(sharedPref.getString("refresh_time", "120"));
        radius = Integer.valueOf(sharedPref.getString("search_radius", "494"));



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
                        Log.d(TAG, "service places found" + places.size() + " " + radius + " " + category);

                        diffPlaces = Comparer.diffPlaces(oldPlaces, places);
                        oldPlaces = places;
                        Log.d(TAG, "New places found " + diffPlaces.size());
                        notifyAboutNewPlace();
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
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,refreshTime*1000,0,listener);
    }

    @Override
    public void onDestroy() {
//        Log.d(TAG, "onDestroy()");
        Toast.makeText(getApplicationContext(),
                                "Place search stopped", Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(002);
        notificationManager.cancel(001);
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }

    private void notifyAboutNewPlace(){
        int newPlacesCount = diffPlaces.size();
        if(newPlacesCount > 0){

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

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.map_bigger)
                            .setContentTitle("WhatsAround")
                            .setContentText("We found " + newPlacesCount + " new place(s) for you")
                            .setTicker("We found " + newPlacesCount + " new place(s) for you");
            mBuilder.setContentIntent(resultPendingIntent);

            int mNotificationId = 001;
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Log.d(TAG, "notificate: ");
            notificationManager.notify(mNotificationId, mBuilder.build());
        }
    }

    private void showServiceStatusNotification(){
            Intent intent = new Intent(this, MonitorService.class);
            intent.setAction("STOP");

            PendingIntent pIntent = PendingIntent.getService(this,
                    (int) System.currentTimeMillis(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.map_bigger)
                            .setContentTitle("WhatsAround")
                            .setContentText("Click here to stop monitoring")
                            .setTicker("Monitoring started")
                            .setOngoing(true)
                            .setPriority(Notification.PRIORITY_MAX);

        mBuilder.setContentIntent(pIntent);


            int mNotificationId = 002;
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(mNotificationId, mBuilder.build());
    }

}
