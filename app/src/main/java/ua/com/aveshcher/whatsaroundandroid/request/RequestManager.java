package ua.com.aveshcher.whatsaroundandroid.request;


import android.content.Context;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ua.com.aveshcher.whatsaroundandroid.dto.Place;

import java.util.ArrayList;
import java.util.List;

public class RequestManager {
    private static final String DOMAIN = "whats-around.herokuapp.com";
    private String TAG = "REQUEST_MANAGER";
    private double lat;
    private double lng;
    private int radius;

    public void receiveJSON(final VolleyCallback callback, Context context, double lat, double lng, int radius, String category){


        String reqUrl = " ";
        if(category.equals("random")){
            reqUrl = "http://" + DOMAIN + "/api/v1/places/random/" + lat + "/" + lng + "/" + radius;
        } else if(category.equals("historic")){
            reqUrl = "http://" + DOMAIN + "/api/v1/places/by_category/" + lat + "/" + lng + "/"  + radius + "/" + category;
        } else {
            reqUrl = "http://" + DOMAIN + "/api/v1/places/" + lat + "/" + lng + "/" + category;
        }


        JsonArrayRequest req = new JsonArrayRequest(reqUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Response:" + response.toString());
                        String jsonResponseOutput = ""; //for debug purposes
                        try {
                            List<Place> places = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject placeObject = (JSONObject) response
                                        .get(i);

                                Place place = new Place(placeObject);
                                places.add(place);
//                                Log.d(TAG, "places.size: " + places.size());
                                jsonResponseOutput += place.toString();
//                                   TODO: maybe it is needed to parse categories of place
                            }
                            callback.onSuccess(places);

                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(context,
//                                    "JSON parsing error:" + e.getMessage(),
//                                    Toast.LENGTH_LONG).show();
                        }
                        Log.d(TAG, jsonResponseOutput);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(context,
//                        "Response error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //increase wait time for response from api server
        req.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).getRequestQueue().add(req);

    }

    public interface VolleyCallback{
        void onSuccess(List<Place> places);
    }
}

