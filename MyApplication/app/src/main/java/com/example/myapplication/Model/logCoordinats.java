package com.example.myapplication.Model;

public class logCoordinats {
    Double lat;
    Double lon;
    String adres;
    String yer;
    String zamanKey;
    String motionOne;
    String motionTwo;
    String motionThree;

    public logCoordinats(Double lat, Double lon, String adres) {
        this.lat = lat;
        this.lon = lon;
        this.adres = adres;
    }

    public logCoordinats(Double lat, Double lon, String adres, String yer, String zamanKey) {
        this.lat = lat;
        this.lon = lon;
        this.adres = adres;
        this.yer=yer;
        this.zamanKey=zamanKey;
    }

    public String getMotionOne() {
        return motionOne;
    }

    public void setMotionOne(String motionOne) {
        this.motionOne = motionOne;
    }

    public String getMotionTwo() {
        return motionTwo;
    }

    public void setMotionTwo(String motionTwo) {
        this.motionTwo = motionTwo;
    }

    public String getMotionThree() {
        return motionThree;
    }

    public void setMotionThree(String motionThree) {
        this.motionThree = motionThree;
    }

    public logCoordinats(Double lat, Double lon, String adres, String yer, String zamanKey, String motionOne, String motionTwo, String motionThree) {
        this.lat = lat;
        this.lon = lon;
        this.adres = adres;
        this.yer = yer;
        this.zamanKey = zamanKey;
        this.motionOne = motionOne;
        this.motionTwo = motionTwo;
        this.motionThree = motionThree;
    }

    public String getYer() {
        return yer;
    }

    public void setYer(String yer) {
        this.yer = yer;
    }

    public String getZamanKey() {
        return zamanKey;
    }

    public void setZamanKey(String zamanKey) {
        this.zamanKey = zamanKey;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }
}
