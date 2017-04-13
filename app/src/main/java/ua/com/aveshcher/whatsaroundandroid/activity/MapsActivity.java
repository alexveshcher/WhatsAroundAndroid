package ua.com.aveshcher.whatsaroundandroid.activity;

import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;

import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.ui.IconGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ua.com.aveshcher.whatsaroundandroid.R;
import ua.com.aveshcher.whatsaroundandroid.dto.Place;
import ua.com.aveshcher.whatsaroundandroid.request.MySingleton;
import ua.com.aveshcher.whatsaroundandroid.request.RequestManager;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private String TAG = "MAPACT";
//    private String category;
//    private int radius;
//    private int refreshTime;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
//    private RequestQueue requestQueue;
//    private BroadcastReceiver broadcastReceiver;
    private HashSet<Place> places;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Intent intent = getIntent();
//        category = intent.getStringExtra(MainActivity.CATEGORY);
        places = (HashSet<Place>) intent.getExtras().get("new_places");
        Log.d(TAG, "PLACES FROM INTENT: "+ places.size());
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        radius = Integer.valueOf(sharedPref.getString("search_radius", "494"));
//        refreshTime = Integer.valueOf(sharedPref.getString("refresh_time", "120"));
//        Toast.makeText(getApplicationContext(),
//                "search_radius: " + radius, Toast.LENGTH_LONG).show();
        // Get a RequestQueue
//        requestQueue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        navigateToLastLocation();
        getPlacesByLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void navigateToLastLocation() {
        //        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng))      // Sets the center of the map to Mountain View
                    .zoom(12)                   // Sets the zoom
//                    .bearing(90)                // Sets the orientation of the camera to east
//                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000, null);
        }
    }

    private void getPlacesByLocation() {

        if (mLastLocation != null) {

            double lastLat = mLastLocation.getLatitude();
            double lastLng = mLastLocation.getLongitude();

            //show user's current location
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lastLat, lastLng))
                    .title("You")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            );

            for(Place place : places){
                Marker placeMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLat(), place.getLng()))
                        .title(place.getName())
                        .snippet(place.getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                );
                placeMarker.setTag(place);
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.getTag() != null){
            Place place = (Place) marker.getTag();
//        Toast.makeText(this, place.getName(),
//                Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PlaceDetailsActivity.class);
            intent.putExtra("place", place);
            startActivity(intent);
        }
    }
}
