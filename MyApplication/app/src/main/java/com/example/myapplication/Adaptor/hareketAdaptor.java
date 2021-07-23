package com.example.myapplication.Adaptor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.listeBilgisi;
import com.example.myapplication.R;

import java.util.List;


public class hareketAdaptor extends RecyclerView.Adapter {


    private Context mContext;
    private List<listeBilgisi> hareketler;


    public hareketAdaptor(Context mContext, List<listeBilgisi> hareketler) {
        this.hareketler = hareketler;
        this.mContext = mContext;


           }

    public hareketAdaptor(){

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v =  LayoutInflater.from(mContext).inflate(R.layout.aksiyon, parent, false);
        vh = new UViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Drawable walking=mContext.getDrawable(R.drawable.walk);
        Drawable waiting=mContext.getDrawable(R.drawable.bekleme);
        Drawable car=mContext.getDrawable(R.drawable.ic_car);
        Drawable running=mContext.getDrawable(R.drawable.run);
        Drawable location=mContext.getDrawable(R.drawable.ic_location);
        Drawable alert=mContext.getDrawable(R.drawable.ic_alert);
        Drawable falling=mContext.getDrawable(R.drawable.ic_fall);
        Drawable drawableRed = mContext.getResources().getDrawable(R.drawable.redbutton);
        Drawable drawableYellow = mContext.getResources().getDrawable(R.drawable.yellow_btn);
        Drawable drawableGreen = mContext.getResources().getDrawable(R.drawable.green_button);
        Drawable drawableBlue = mContext.getResources().getDrawable(R.drawable.btn_blue);
        Drawable drawableWhite = mContext.getResources().getDrawable(R.drawable.btn_bg);
        Drawable drawableRoutine = mContext.getResources().getDrawable(R.drawable.ic_routine);
        Drawable drawableTick = mContext.getResources().getDrawable(R.drawable.ic_tick);

        listeBilgisi aksyon = hareketler.get(position);

        ((UViewHolder) holder).zaman.setText(String.valueOf(aksyon.getZamanBilgisi()));
        ((UViewHolder) holder).aciklama.setText(String.valueOf(aksyon.getAciklama()));

        switch (aksyon.getImEylem()) {
            case "Yürüme":
                ((UViewHolder) holder).icon.setImageDrawable(walking);
                ((UViewHolder) holder).constraintLayout.setBackground(drawableGreen);
                break;
            case "hareketsiz (Bekleme)":
                ((UViewHolder) holder).icon.setImageDrawable(waiting);
                ((UViewHolder) holder).constraintLayout.setBackground(drawableBlue);
                break;
            case "Araç içinde hareket":
                ((UViewHolder) holder).icon.setImageDrawable(car);
                ((UViewHolder) holder).constraintLayout.setBackground(drawableGreen);
                break;
            case "Koşma":
                ((UViewHolder) holder).icon.setImageDrawable(running);
                ((UViewHolder) holder).constraintLayout.setBackground(drawableGreen);
                break;
            case "acil":
            case "kotu":
                ((UViewHolder) holder).icon.setImageDrawable(alert);
                ((UViewHolder) holder).constraintLayout.setBackground(drawableRed);
                break;
            case "SonDüsme":
                ((UViewHolder) holder).icon.setImageDrawable(falling);
                ((UViewHolder) holder).constraintLayout.setBackground(drawableRed);
                break;
            case "iyi":
                ((UViewHolder) holder).icon.setImageDrawable(drawableTick);
                ((UViewHolder) holder).constraintLayout.setBackground(drawableGreen);
                break;
            case "rutin":
                ((UViewHolder) holder).icon.setImageDrawable(drawableRoutine);
                ((UViewHolder) holder).constraintLayout.setBackground(drawableBlue);
                break;
            case "uyarı":
                ((UViewHolder) holder).icon.setImageDrawable(alert);
                ((UViewHolder) holder).constraintLayout.setBackground(drawableYellow);
                break;
            case "yer" :
                ((UViewHolder) holder).icon.setImageDrawable(location);
                ((UViewHolder) holder).constraintLayout.setBackground(drawableBlue);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return hareketler.size();
    }


    public class UViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public ConstraintLayout constraintLayout;
        public TextView aciklama,zaman;

        public UViewHolder(View itemView) {
            super(itemView);
            constraintLayout=itemView.findViewById(R.id.frameItem);
            zaman = itemView.findViewById(R.id.date);
            aciklama = itemView.findViewById(R.id.aciklama);
            icon = itemView.findViewById(R.id.icon);

        }

    }





}