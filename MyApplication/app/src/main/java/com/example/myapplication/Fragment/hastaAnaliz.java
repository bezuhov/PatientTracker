package com.example.myapplication.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.Model.Aksyon;
import com.example.myapplication.Model.getEvents;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class hastaAnaliz extends Fragment {
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    String patientUid;
    private ArrayList<Aksyon> hareketler;
    private ArrayList<Aksyon> todayList;
    private ArrayList<Aksyon> yesterdayList;
    private ArrayList<Aksyon> oldList;
    ConstraintLayout clTakipPill;
    ConstraintLayout clTakipYemek;
    ConstraintLayout clTakipSu;
    ConstraintLayout clTakipTuvalet;
    ConstraintLayout clTakipGenel;

    TextView tvTakipPill;
    TextView tvTakipYemek;
    TextView tvTakipSu;
    TextView tvTakipTuvalet;
    TextView tvTakipGenel;

    ImageView imTakipPill;
    ImageView imTakipYemek;
    ImageView imTakipSu;
    ImageView imTakipTuv;
    ImageView imTakipGenel;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Fragment myFragment;

    public hastaAnaliz() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_hasta_analiz, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshAnaliz);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.soft_yesil);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(myFragment).attach(myFragment).commit();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        myFragment=this;

        clTakipPill=view.findViewById(R.id.clTakipPill);
        clTakipYemek=view.findViewById(R.id.clTakipYemek);
        clTakipSu=view.findViewById(R.id.clTakipSu);
        clTakipTuvalet=view.findViewById(R.id.clTakipTuvalet);
        clTakipGenel=view.findViewById(R.id.clTakipGenel);

        tvTakipPill=view.findViewById(R.id.tvTakipPill);
        tvTakipYemek=view.findViewById(R.id.tvTakipYemek);
        tvTakipSu=view.findViewById(R.id.tvTakipSu);
        tvTakipTuvalet=view.findViewById(R.id.tvTakipTuvalet);
        tvTakipGenel=view.findViewById(R.id.tvTakipGenel);

        imTakipPill=view.findViewById(R.id.imTakipPill);
        imTakipYemek=view.findViewById(R.id.imTakipYemek);
        imTakipSu=view.findViewById(R.id.imTakipSu);
        imTakipTuv=view.findViewById(R.id.imTakipTuv);
        imTakipGenel=view.findViewById(R.id.imTakipGenel);

        Date tarih= Calendar.getInstance().getTime();
        SimpleDateFormat dayFormat=new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
        SimpleDateFormat saatformat = new SimpleDateFormat("HH:mm:ss");
        String current_clock=saatformat.format(tarih);
        String current_time=format.format(tarih);
        String current_day=dayFormat.format(tarih);

        mAuth = FirebaseAuth.getInstance();

        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //patientUid=currentUser.getUid();
        hareketler=new ArrayList<Aksyon>();
        todayList=new ArrayList<Aksyon>();
        yesterdayList=new ArrayList<Aksyon>();
        oldList=new ArrayList<Aksyon>();

        database = FirebaseDatabase.getInstance();
        DatabaseReference user=database.getReference("Users");
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid="";
                String tip=snapshot.child(mAuth.getCurrentUser().getUid()).child("kullanici_tipi").getValue().toString();
                if (tip.equals("Hasta"))uid=mAuth.getCurrentUser().getUid();
                else {
                    uid=snapshot.child(mAuth.getCurrentUser().getUid()).child("Uid").getValue().toString();
                }
                DatabaseReference myRef = database.getReference("Users").child(uid).child("rutinAyar");
                Boolean ilacSabah= (Boolean) snapshot.child(uid).child("rutinAyar").child("ilac").child("sabah").getValue();
                Boolean ilacOgle= (Boolean) snapshot.child(uid).child("rutinAyar").child("ilac").child("ogle").getValue();
                Boolean ilacAksam= (Boolean) snapshot.child(uid).child("rutinAyar").child("ilac").child("aksam").getValue();
                Boolean ilacLev= (Boolean) snapshot.child(uid).child("rutinAyar").child("ilac").child("level").getValue();
                Boolean tuvaletLev= (Boolean) snapshot.child(uid).child("rutinAyar").child("tuvalet").child("level").getValue();
                Boolean suLev= (Boolean) snapshot.child(uid).child("rutinAyar").child("su").child("level").getValue();
                Boolean yemekSabah= (Boolean) snapshot.child(uid).child("rutinAyar").child("yemek").child("sabah").getValue();
                Boolean yemekOgle= (Boolean) snapshot.child(uid).child("rutinAyar").child("yemek").child("ogle").getValue();
                Boolean yemekAksam= (Boolean) snapshot.child(uid).child("rutinAyar").child("yemek").child("aksam").getValue();
                Boolean yemekLev= (Boolean) snapshot.child(uid).child("rutinAyar").child("yemek").child("level").getValue();
                try {

                    getEvents event=new getEvents(getContext());
                    event.getList(uid,"today", new getEvents.EventLogsCallback() {
                        @Override
                        public void onCallback(ArrayList<Aksyon> value) throws ParseException {
                            todayList=value;
                            String uid="";
                            String tip=snapshot.child(mAuth.getCurrentUser().getUid()).child("kullanici_tipi").getValue().toString();
                            if (tip.equals("Hasta"))uid=mAuth.getCurrentUser().getUid();
                            else {
                                uid=snapshot.child(mAuth.getCurrentUser().getUid()).child("Uid").getValue().toString();
                            }
                            event.getList(uid,"yesterday", new getEvents.EventLogsCallback() {
                                @Override
                                public void onCallback(ArrayList<Aksyon> value) throws ParseException {
                                    yesterdayList=value;
                                    String uid="";
                                    String tip=snapshot.child(mAuth.getCurrentUser().getUid()).child("kullanici_tipi").getValue().toString();
                                    if (tip.equals("Hasta"))uid=mAuth.getCurrentUser().getUid();
                                    else {
                                        uid=snapshot.child(mAuth.getCurrentUser().getUid()).child("Uid").getValue().toString();
                                    }
                                    event.getList(uid,"hareketler",new getEvents.EventLogsCallback(){
                                        @Override
                                        public void onCallback(ArrayList<Aksyon> value) throws ParseException {
                                            hareketler=value;
                                            String zamanDonem=zaman(current_clock,saatformat);

                                            Drawable drawableS = getResources().getDrawable(R.drawable.yellow_btn);
                                            Drawable drawableK = getResources().getDrawable(R.drawable.redbutton);
                                            Drawable drawableY = getResources().getDrawable(R.drawable.green_button);
                                            Drawable drawableWait = getResources().getDrawable(R.drawable.ic_timer);
                                            Drawable drawableAlert = getResources().getDrawable(R.drawable.ic_alert);
                                            if (!todayList.isEmpty()){
                                                int sabahIlac=0;int sabahYemek=0;int sabahSu=0;int sabahTuvalet=0;int sabahBanyo=0;
                                                int ogleIlac=0;int ogleYemek=0;int ogleSu=0;int ogleTuvalet=0;int ogleBanyo=0;
                                                int aksamIlac=0;int aksamYemek=0;int aksamSu=0;int aksamTuvalet=0;int aksamBanyo;

                                                for (int j=0;j<todayList.size();j++){
                                                    if (todayList.get(j).getHareket().equals("rutin")){
                                                        Date evenTime=format.parse(todayList.get(j).getZaman());
                                                        String saat=saatformat.format(evenTime);
                                                        String olayDonem=zaman(saat,saatformat);
                                                        String rutinAdi=todayList.get(j).getEylem();
                                                        if (olayDonem.equals("sabah")){
                                                            if (rutinAdi.equals("ilac"))sabahIlac++;
                                                            else if (rutinAdi.equals("su"))sabahSu++;
                                                            else if (rutinAdi.equals("yemek"))sabahYemek++;
                                                            else if (rutinAdi.equals("tuvalet"))sabahTuvalet++;

                                                        }
                                                        else if (olayDonem.equals("ogle")){
                                                            if (rutinAdi.equals("ilac"))ogleIlac++;
                                                            else if (rutinAdi.equals("su"))ogleSu++;
                                                            else if (rutinAdi.equals("yemek"))ogleYemek++;
                                                            else if (rutinAdi.equals("tuvalet"))ogleTuvalet++;

                                                        }
                                                        else if (olayDonem.equals("aksam")){
                                                            if (rutinAdi.equals("ilac"))aksamIlac++;
                                                            else if (rutinAdi.equals("su"))aksamSu++;
                                                            else if (rutinAdi.equals("yemek"))aksamYemek++;
                                                            else if (rutinAdi.equals("tuvalet"))aksamTuvalet++;

                                                        }

                                                    }
                                                }

                                                if (zamanDonem.equals("sabah")){
                                                    if (ilacSabah){
                                                        if (sabahIlac==0){
                                                            clTakipPill.setBackground(drawableS);
                                                            tvTakipPill.setText("Sabah alınması gereken ilacın vakti geldi,henüz alınmadı.");
                                                            imTakipPill.setImageDrawable(drawableWait);
                                                        }
                                                        else if (sabahIlac==1){
                                                            clTakipPill.setBackground(drawableY);
                                                            tvTakipPill.setText("Sabah ilacı alındı,her şey yolunda gözüküyor.");
                                                        }
                                                        else if (sabahIlac>1){
                                                            clTakipPill.setBackground(drawableK);
                                                            tvTakipPill.setText("Sabah alınması gerekenden fazla doz ilaç alındı ! ("+sabahIlac+" doz alındı.)");
                                                            imTakipPill.setImageDrawable(drawableAlert);
                                                        }
                                                    }else {
                                                        if (sabahIlac==0){
                                                            clTakipPill.setBackground(drawableY);
                                                            tvTakipPill.setText("Şu an ilaç içilmesi gerekmiyor.Bir sorun yok.");
                                                        }else {
                                                            clTakipPill.setBackground(drawableK);
                                                            tvTakipPill.setText("Bu sabah ilaç içilmesi gerekiyordu,"+sabahIlac+" doz alındı.");
                                                        }

                                                    }
                                                    if (yemekSabah){
                                                        if (sabahYemek==0){
                                                            clTakipYemek.setBackground(drawableS);
                                                            tvTakipYemek.setText("Henüz kahvaltı yapılmadı.");
                                                            imTakipYemek.setImageDrawable(drawableWait);
                                                        }
                                                        else if (sabahYemek==1){
                                                            clTakipYemek.setBackground(drawableY);
                                                            tvTakipYemek.setText("Kahvaltı yapıldı,herşey yolunda gözüküyor.");
                                                        }
                                                        else if (sabahYemek>1){
                                                            clTakipYemek.setBackground(drawableK);
                                                            tvTakipYemek.setText("Birden fazla kez kahvaltı yapıldı ! ("+sabahYemek+" kez.)");
                                                            imTakipYemek.setImageDrawable(drawableAlert);
                                                        }
                                                    }else {
                                                        if (sabahYemek>0){
                                                            clTakipYemek.setBackground(drawableS);
                                                            tvTakipYemek.setText("Bu sabah kahvaltı yapılması zorunlu değildi. "+sabahYemek+" kez yapıldı.");
                                                            imTakipYemek.setImageDrawable(drawableAlert);
                                                        }
                                                        else {
                                                            tvTakipYemek.setText("Bu sabah kahvaltı yapmanız zorunlu değil ve şuana kadar yapılmadı.Bir sorun gözükmüyor.");
                                                        }
                                                    }
                                                    if (sabahSu==0){
                                                        clTakipSu.setBackground(drawableK);
                                                        tvTakipSu.setText("Henüz hiç su içmedi.");
                                                        imTakipSu.setImageDrawable(drawableWait);
                                                    }
                                                    else if (sabahSu>10){
                                                        clTakipSu.setBackground(drawableK);
                                                        tvTakipSu.setText("Bugün yeterince su içildi! ("+sabahSu+" bardak.)");
                                                        imTakipSu.setImageDrawable(drawableAlert);
                                                    }
                                                    else if (sabahSu>0){
                                                        clTakipSu.setBackground(drawableY);
                                                        tvTakipSu.setText("("+sabahSu+" bardak) su içildi.Herşey yolunda gözüküyor.");
                                                    }
                                                    if (sabahTuvalet>8){
                                                        clTakipTuvalet.setBackground(drawableK);
                                                        tvTakipTuvalet.setText("Bu sabah fazla tuvalete gidildi. ( "+sabahTuvalet+" kez)");
                                                        imTakipTuv.setImageDrawable(drawableAlert);
                                                    }
                                                    else if (sabahTuvalet==0){
                                                        clTakipTuvalet.setBackground(drawableS);
                                                        tvTakipTuvalet.setText("Henüz hiç tuvalete gidilmedi.");
                                                        imTakipTuv.setImageDrawable(drawableWait);
                                                    }
                                                    else {
                                                        tvTakipTuvalet.setText("Bu sabah "+sabahTuvalet +" kez tuvalete gidildi.");
                                                    }
                                                    if (sabahBanyo==0){
                                                        clTakipGenel.setBackground(drawableS);
                                                        tvTakipGenel.setText("Bugün hiç banyo yapılmadı.");
                                                        imTakipGenel.setImageDrawable(drawableWait);
                                                    }
                                                    else tvTakipGenel.setText("Bu sabah "+sabahBanyo+" kez banyo yaptı.");

                                                }
                                                else if (zamanDonem.equals("ogle")||zamanDonem.equals("aksam")){
                                                    if (ilacOgle){
                                                        if (ogleIlac==0){
                                                            if (sabahIlac==0&&ilacSabah){
                                                                clTakipPill.setBackground(drawableK);
                                                                tvTakipPill.setText("Öğle zamanında alınması gereken ilacın vakti geldi,sabah almanız gereken ilaç da alınmadı.");
                                                                imTakipPill.setImageDrawable(drawableAlert);
                                                            }
                                                            else {
                                                                clTakipPill.setBackground(drawableS);
                                                                tvTakipPill.setText("Öğle zamanında alınması gereken ilacın vakti geldi,henüz alınmadı.");
                                                                imTakipPill.setImageDrawable(drawableWait);
                                                            }

                                                        }
                                                        else if (ogleIlac==1){
                                                            clTakipPill.setBackground(drawableY);
                                                            tvTakipPill.setText("Öğle zamanında almanız gereken ilaç alındı.Bir problem yok.");

                                                        }
                                                        else if (ogleIlac>1){
                                                            clTakipPill.setBackground(drawableK);
                                                            tvTakipPill.setText("Öğle zamanında fazla ilaç alındı. ("+ogleIlac+" doz.)");
                                                            imTakipPill.setImageDrawable(drawableAlert);
                                                        }
                                                    }else {
                                                        if (ogleIlac==0){
                                                            clTakipPill.setBackground(drawableY);
                                                            tvTakipPill.setText("Öğle zamanında ilaç alınması gerekmiyor,şuana kadarda alınmadı.Bir problem yok.");
                                                        }else {
                                                            clTakipPill.setBackground(drawableK);
                                                            tvTakipPill.setText("Öğle zamanında ilaç alınmaması gerekiyordu. ("+ogleIlac+"doz)  aldınız.");
                                                            imTakipPill.setImageDrawable(drawableAlert);
                                                        }

                                                    }
                                                    if (yemekOgle){
                                                        if (ogleYemek==0){
                                                            if (sabahYemek==0&&yemekSabah){
                                                                clTakipYemek.setBackground(drawableK);
                                                                tvTakipYemek.setText("Öğle zamanında yemek yenilmedi,sabah ta birşey yenilmedi.");
                                                                imTakipYemek.setImageDrawable(drawableAlert);
                                                            }
                                                            else {
                                                                clTakipYemek.setBackground(drawableS);
                                                                tvTakipYemek.setText("Öğle zamanında henüz yemek yenilmedi.");
                                                                imTakipYemek.setImageDrawable(drawableWait);
                                                            }

                                                        }
                                                        else if (ogleYemek==1){
                                                            clTakipYemek.setBackground(drawableY);
                                                            tvTakipYemek.setText("Öğle yemeği yenildi,herşey yolunda.");

                                                        }
                                                        else if (ogleYemek>1){
                                                            clTakipYemek.setBackground(drawableK);
                                                            tvTakipYemek.setText("Bu öğle fazla yemek yenildi. ("+ogleYemek+" kez.)");
                                                            imTakipYemek.setImageDrawable(drawableAlert);
                                                        }
                                                    }else {
                                                        if (ogleYemek==0){
                                                            clTakipYemek.setBackground(drawableY);
                                                            tvTakipYemek.setText("Bu öğle yemek yenilmesi  gerekmiyor,şuana kadarda yenilmedi.Bir problem yok.");
                                                        }else {
                                                            clTakipYemek.setBackground(drawableK);
                                                            tvTakipYemek.setText("Öğle zamanında ilaç alınmaması gerekiyordu. ("+ogleIlac+"doz)  alındı.");
                                                            imTakipYemek.setImageDrawable(drawableAlert);
                                                        }

                                                    }
                                                    if (ogleSu==0){
                                                        clTakipSu.setBackground(drawableS);
                                                        tvTakipSu.setText("Bu öğle henüz hiç su içilmedi.");
                                                        imTakipSu.setImageDrawable(drawableWait);
                                                    }
                                                    else if (ogleSu>10){
                                                        clTakipSu.setBackground(drawableK);
                                                        tvTakipSu.setText("Bugün yeterince su içildi! ("+ogleSu+" bardak.)");
                                                        imTakipSu.setImageDrawable(drawableAlert);
                                                    }
                                                    else if (ogleSu>0){
                                                        clTakipSu.setBackground(drawableY);
                                                        tvTakipSu.setText("("+ogleSu+" bardak) su içildi.Herşey yolunda gözüküyor.");
                                                    }

                                                    if (ogleTuvalet+sabahTuvalet>8){
                                                        clTakipTuvalet.setBackground(drawableK);
                                                        tvTakipTuvalet.setText("Bugün fazla tuvalete gidildi. ( "+ogleTuvalet+sabahTuvalet+" kez)");
                                                        imTakipTuv.setImageDrawable(drawableAlert);
                                                    }
                                                    else if (ogleTuvalet==0){
                                                        clTakipTuvalet.setBackground(drawableS);
                                                        tvTakipTuvalet.setText("Öğle zaman diliminde hiç tuvalete gidilmedi.");
                                                        imTakipTuv.setImageDrawable(drawableWait);
                                                    }
                                                    else {
                                                        tvTakipTuvalet.setText("Bu öğle "+sabahTuvalet +" kez tuvalete gidildi.");
                                                    }
                                                    if (sabahBanyo+ogleBanyo==0){
                                                        clTakipGenel.setBackground(drawableS);
                                                        tvTakipGenel.setText("Bugün hiç banyo yapılmadı.");
                                                        imTakipGenel.setImageDrawable(drawableWait);
                                                    }
                                                    else tvTakipGenel.setText("Bugün "+sabahBanyo+ogleBanyo+" kez banyo yapıldı.");
                                                }

                                            }
                                            // Hastanın bugün için herhangi bir kaydı yok ise;
                                            else {

                                                clTakipPill.setBackground(drawableS);
                                                tvTakipPill.setText("Bugün için hastanın herhangi bir kaydı bulunmamaktadır.");
                                                imTakipPill.setImageDrawable(drawableWait);

                                                clTakipYemek.setBackground(drawableS);
                                                tvTakipYemek.setText("Bugün için hastanın herhangi bir kaydı bulunmamaktadır.");
                                                imTakipYemek.setImageDrawable(drawableWait);

                                                clTakipSu.setBackground(drawableS);
                                                tvTakipSu.setText("Bugün için hastanın herhangi bir kaydı bulunmamaktadır.");
                                                imTakipSu.setImageDrawable(drawableWait);

                                                clTakipTuvalet.setBackground(drawableS);
                                                tvTakipTuvalet.setText("Bugün için hastanın herhangi bir kaydı bulunmamaktadır.");
                                                imTakipTuv.setImageDrawable(drawableWait);

                                                clTakipGenel.setBackground(drawableS);
                                                tvTakipGenel.setText("Bugün için hastanın herhangi bir kaydı bulunmamaktadır.");
                                                imTakipGenel.setImageDrawable(drawableWait);


                                            }


                                        }
                                    });
                                }
                            });
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //DatabaseReference myRef = database.getReference("Kayitlar").child(this.patientUid);

        return view;
    }
    private String zaman(String zaman,SimpleDateFormat format) throws ParseException {
        String sonuc="";
        String ogle="12:00:00";
        String aksam="20:00:00";
        long zamanFarkiOgle=format.parse(zaman).getTime()-format.parse(ogle).getTime();
        long zamanFarkiAksam=format.parse(zaman).getTime()-format.parse(aksam).getTime();

        if (zamanFarkiOgle<=0){
            sonuc="sabah";
            return sonuc;
        }else if (zamanFarkiOgle>0 && zamanFarkiAksam<=0){
            sonuc="ogle";
            return sonuc;
        }else if (zamanFarkiAksam>0){
            sonuc="aksam";
            return sonuc;
        }
        return sonuc;
    };



}