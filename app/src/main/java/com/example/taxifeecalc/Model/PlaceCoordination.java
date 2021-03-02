package com.example.taxifeecalc.Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaceCoordination implements Serializable {

    private String title;
    private String address;
    private String longitude;
    private String latitude;
    private String meta;

    public  PlaceCoordination(String title, String address, String longitude, String latitude){
        this.title = title;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String title) {
        this.address = address;
    }

    public String getMeta() { return meta; }
    public void setMeta(String meta) { this.meta = meta; }

    public String getRepresentName(){
        if(title != null)
            return this.title;
        else
            return this.address;
    }

    @Override
    public String toString() {
        return "PlaceCoordination{" +
                "title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
