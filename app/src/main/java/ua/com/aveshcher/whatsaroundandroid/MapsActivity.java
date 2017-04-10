package ua.com.aveshcher.whatsaroundandroid;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    String tag = "CONNECT";
    private static final String IP = "192.168.0.27";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


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

    private void navigateToLastLocation(){
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
                    .zoom(15)                   // Sets the zoom
//                    .bearing(90)                // Sets the orientation of the camera to east
//                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
        }
    }

    private void getPlacesByLocation() {

        if (mLastLocation != null) {

            double lastLat = mLastLocation.getLatitude();
            double lastLng = mLastLocation.getLongitude();



            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lastLat,lastLng))      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
//                    .bearing(90)                // Sets the orientation of the camera to east
//                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000, null);


            RequestQueue queue = Volley.newRequestQueue(this);
            String reqUrl = "http://"+IP+":3000/tests/try/" + lastLat + "/" + lastLng;

            JsonArrayRequest req = new JsonArrayRequest(reqUrl,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(tag, "Response:"+ response.toString());
                            String jsonResponse = ""; //for debug purposes
                            try {
                                // Parsing json object response
                                // response will be a json object
                                for (int i = 0; i < response.length(); i++) {

                                    JSONObject place = (JSONObject) response
                                            .get(i);

                                    String id = place.getString("id");
                                    String name = place.getString("name");
                                    double lat = place.getDouble("lat");
                                    double lng = place.getDouble("lng");
                                    int distance = place.getInt("distance");
                                    JSONArray categories = place.getJSONArray("categories");
                                    jsonResponse += "ID: " + categories + "\n\n";
//                                    for(int j = 0; j < categories.length(); i++){
//                                        JSONObject category = (JSONObject) categories.get(j);
//                                        String categoryId = category.getString("id");
//                                        String categoryName = category.getString("name");
//                                        jsonResponse += j + "categoryId: " + categoryId + "\n\n";
//                                        jsonResponse += j + "categoryName: " + categoryName + "\n\n";
//                                    }


                                    jsonResponse += "ID: " + id + "\n\n";
                                    jsonResponse += "Name: " + name + "\n\n";
                                    jsonResponse += "Lat: " + lat + "\n\n";
                                    jsonResponse += "Lng: " + lng + "\n\n";
                                    jsonResponse += "Distance: " + distance + "\n\n";
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(name));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "JSON parsing error:" + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                            Log.d(tag, jsonResponse);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(tag, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Response error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            //increase timeout time for response from api server
            req.setRetryPolicy(new DefaultRetryPolicy(
                    7500,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(req);
        }
    }
}
