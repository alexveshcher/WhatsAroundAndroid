package ua.com.aveshcher.whatsaroundandroid.dto;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Place implements Serializable {

    private String id;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private int distance;
    private String photoUrl;
    private double rating;


    public Place(JSONObject placeObject) throws JSONException {
        String id = placeObject.getString("id");
        String name = placeObject.getString("name");
        String address = placeObject.getString("address");
        if(address.equals("null")){
            address = " ";
        }
        double lat = placeObject.getDouble("lat");
        double lng = placeObject.getDouble("lng");
        int distance = placeObject.getInt("distance");
        String photoUrl = placeObject.getString("photo_url");
        double rating = 0;
        if(!placeObject.isNull("rating")){
            rating = placeObject.getDouble("rating");
        }

        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.photoUrl = photoUrl;
        this.rating = rating;

    }

    public Place(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (!id.equals(place.id)) return false;
        return name.equals(place.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", distance=" + distance +
                ", photoUrl='" + photoUrl + '\'' +
                ", rating=" + rating +
                '}';
    }
}
