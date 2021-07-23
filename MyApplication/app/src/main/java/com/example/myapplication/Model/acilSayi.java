package com.example.myapplication.Model;

public class acilSayi {
    String day;
    int adet;

    public acilSayi() {
    }

    public acilSayi(String day, int adet) {
        this.day = day;
        this.adet = adet;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getAdet() {
        return adet;
    }

    public void setAdet(int adet) {
        this.adet = adet;
    }
}
