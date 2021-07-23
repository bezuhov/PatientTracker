package com.example.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Aksyon implements Parcelable {
    String hareket;
    String yer;
    String eylem;
    String zaman;

    public Aksyon(String zaman,String hareket, String yer, String eylem) {
        this.hareket = hareket;
        this.yer = yer;
        this.eylem = eylem;
        this.zaman=zaman;
    }

    protected Aksyon(Parcel in) {
        hareket = in.readString();
        yer = in.readString();
        eylem = in.readString();
        zaman = in.readString();
    }

    public static final Creator<Aksyon> CREATOR = new Creator<Aksyon>() {
        @Override
        public Aksyon createFromParcel(Parcel in) {
            return new Aksyon(in);
        }

        @Override
        public Aksyon[] newArray(int size) {
            return new Aksyon[size];
        }
    };

    public String getZaman() {
        return zaman;
    }

    public void setZaman(String zaman) {
        this.zaman = zaman;
    }

    public Aksyon() {
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

    public String getEylem() {
        return eylem;
    }

    public void setEylem(String eylem) {
        this.eylem = eylem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hareket);
        dest.writeString(yer);
        dest.writeString(eylem);
        dest.writeString(zaman);
    }
}
