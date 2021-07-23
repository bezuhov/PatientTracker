package com.example.myapplication.Model;

public class oldListClass {
    String zaman;
    String hareket;
    String yer;

    public oldListClass(String zaman, String hareket, String yer) {
        this.zaman = zaman;
        this.hareket = hareket;
        this.yer = yer;
    }

    public oldListClass() {
    }

    public String getZaman() {
        return zaman;
    }

    public void setZaman(String zaman) {
        this.zaman = zaman;
    }

    public String getHareket() {
        return hareket;
    }

    public void setHareket(String hareket) {
        this.hareket = hareket;
    }

    public String getYer() {
        return yer;
    }

    public void setYer(String yer) {
        this.yer = yer;
    }
}
