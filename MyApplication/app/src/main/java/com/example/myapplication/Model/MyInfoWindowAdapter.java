package com.example.myapplication.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View myContentsView;
    ImageView imInfo;
    TextView tvInfoTittle;
    TextView tvInfoTime;

    @Override
    public View getInfoWindow(Marker marker) {
        View v = myContentsView;
        imInfo=v.findViewById(R.id.imInfoImage);
        tvInfoTittle=v.findViewById(R.id.tvInfoTittle);
        tvInfoTime=v.findViewById(R.id.tvInfoTime);
        tvInfoTime.setText(marker.getTitle());
        tvInfoTime.setText(marker.getSnippet());
        return  v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public MyInfoWindowAdapter(Context mContext) {
        this.myContentsView = LayoutInflater.from(mContext).inflate(R.layout.fragment_custom_info_window,null);
    }
}
