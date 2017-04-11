package ua.com.aveshcher.whatsaroundandroid.dto;


import org.json.JSONException;
import org.json.JSONObject;

public class Place {

    private String id;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private int distance;

    public Place(JSONObject placeObject) throws JSONException {
        String id = placeObject.getString("id");
        String name = placeObject.getString("name");
        String address = placeObject.getString("address");
        double lat = placeObject.getDouble("lat");
        double lng = placeObject.getDouble("lng");
        int distance = placeObject.getInt("distance");

        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;

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

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", distance=" + distance +
                '}';
    }
}
