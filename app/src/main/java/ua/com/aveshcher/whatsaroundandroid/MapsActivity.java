package ua.com.aveshcher.whatsaroundandroid;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String tag = "CONNECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


//        final String res = "no res";
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url ="http://10.0.128.117:3000/tests/try";
//
//        LatLng ternopil = new LatLng(49.51106183969694, 25.60793317765834);
//        mMap.addMarker(new MarkerOptions().position(ternopil).title("Цукровий Завод"));
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(ternopil));
//
//// Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//
//                        String res = "Response is: "+ response;
//                        Log.d(tag, res);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(tag, res)   ;
//            }
//        });
//// Add the request to the RequestQueue.
//        queue.add(stringRequest);



        final String res = "no res";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String urlJsonArry ="http://192.168.1.82:3000/tests/try/49.51106183969694/25.60793317765834";

//        LatLng ternopil = new LatLng(49.51106183969694, 25.60793317765834);
//        mMap.addMarker(new MarkerOptions().position(ternopil).title("Цукровий Завод"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(ternopil));


        JsonArrayRequest jsonObjReq = new JsonArrayRequest(urlJsonArry,
                new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(tag, response.toString());
                String jsonResponse = "";

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
//                        JSONObject phone = place
//                                .getJSONObject("categories");
//                        String category_id = phone.getString("id");
//                        String mobile = phone.getString("name");

                        jsonResponse += "ID: " + id + "\n\n";
                        jsonResponse += "Name: " + name + "\n\n";
                        jsonResponse += "Lat: " + lat + "\n\n";
                        jsonResponse += "Lng: " + lng + "\n\n";
                        jsonResponse += "Distance: " + distance + "\n\n";
//                        jsonResponse += "lng: " + mobile + "\n\n\n";
                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name));


                    }

//                    txtResponse.setText(jsonResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                Log.d(tag, jsonResponse);
//                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(tag, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
//                hidepDialog();
            }
        });

        queue.add(jsonObjReq);
    }


}
