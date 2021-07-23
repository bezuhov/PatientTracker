package com.example.myapplication.Model;

public class rutinAyar {
    boolean sabah,ogle,aksam,level;

    public rutinAyar(boolean sabah, boolean ogle, boolean aksam, boolean level) {
        this.sabah = sabah;
        this.ogle = ogle;
        this.aksam = aksam;
        this.level = level;
    }

    public rutinAyar() {
    }
    public rutinAyar(boolean level){
        this.level=level;
    }

    public boolean isSabah() {
        return sabah;
    }

    public void setSabah(boolean sabah) {
        this.sabah = sabah;
    }

    public boolean isOgle() {
        return ogle;
    }

    public void setOgle(boolean ogle) {
        this.ogle = ogle;
    }

    public boolean isAksam() {
        return aksam;
    }

    public void setAksam(boolean aksam) {
        this.aksam = aksam;
    }

    public boolean isLevel() {
        return level;
    }

    public void setLevel(boolean level) {
        this.level = level;
    }
}
