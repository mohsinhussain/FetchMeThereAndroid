package com.webmedia7.mohsinhussain.fetchmethere.Model;

import java.util.ArrayList;

/**
 * Created by mohsinhussain on 4/19/15.
 */
public class Location {
    String name;
    String address;
    Double latitude;
    Double longitude;
    ArrayList<String> imagesArray;
    String refId;

    public Location(String refId, String name, String address, Double latitude, Double longitude, ArrayList<String> imagesArray){
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagesArray = imagesArray;
        this.refId = refId;

    }

    public Location(){

    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<String> getImagesArray() {
        return imagesArray;
    }

    public void setImagesArray(ArrayList<String> imagesArray) {
        this.imagesArray = imagesArray;
    }
}
