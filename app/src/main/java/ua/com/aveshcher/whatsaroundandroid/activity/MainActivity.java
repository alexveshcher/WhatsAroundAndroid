package ua.com.aveshcher.whatsaroundandroid.activity;

import android.content.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import ua.com.aveshcher.whatsaroundandroid.R;
import ua.com.aveshcher.whatsaroundandroid.dto.Place;
import ua.com.aveshcher.whatsaroundandroid.request.RequestManager;
import ua.com.aveshcher.whatsaroundandroid.service.GPSService;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public final static String CATEGORY = "CATEGORY";
    private String category;
    private int radius;
    private int refreshTime;
    private BroadcastReceiver broadcastReceiver;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        radius = Integer.valueOf(sharedPref.getString("search_radius", "494"));
        refreshTime = Integer.valueOf(sharedPref.getString("refresh_time", "120"));
//        Toast.makeText(getApplicationContext(),
//                "search_radius: " + radius, Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    public void findPlaces(View view) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();

            RequestManager requestManager = new RequestManager();
            requestManager.receiveJSON(new RequestManager.VolleyCallback() {
                @Override
                public void onSuccess(Set<Place> places) {
                    HashSet<Place> places1 = (HashSet<Place>) places;
                    Log.d("MAIN_ACTIVITY", "PLACES FROM INTENT: " + places1.size());
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("new_places", places1);
                    startActivity(intent);
                }
            }, getApplicationContext(),lat,lng,radius,category);
        } else {
            Toast.makeText(this,
                "Can't get your location" + radius, Toast.LENGTH_LONG).show();
        }



    }

    public void monitorPlaces(View view) {
        Intent intent = new Intent(this, GPSService.class);
        intent.putExtra("CATEGORY",category);
        startService(intent);
    }

    public void selectCategory(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if(checked){
            Button findButton = (Button) findViewById(R.id.find_places_button);
            Button monitorButton = (Button) findViewById(R.id.monitor_places_button);
            findButton.setEnabled(true);
            monitorButton.setEnabled(true);
        }
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.food:
                if (checked)
                    category = "food";
                    break;
            case R.id.sights:
                if (checked)
                    category = "sights";
                    break;
            case R.id.random:
                if (checked)
                    category = "random";
                break;
            case R.id.historic:
                if (checked)
                    category = "historic";
                break;
        }
    }

    public void openSettings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}
