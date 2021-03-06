package com.example.myapplication.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.example.myapplication.Model.acilSayi;
import com.example.myapplication.Model.getEvents;
import com.example.myapplication.Model.oldListClass;
import com.example.myapplication.Model.rutinAyar;
import com.example.myapplication.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.GoogleMap;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AnaVasiFragment extends Fragment  {

    private List<Aksyon> hareketler= new ArrayList<>();
    private List<Aksyon> todayList= new ArrayList<>();
    private List<Aksyon> yesterdayList= new ArrayList<>();
    private List<Aksyon> oldList= new ArrayList<>();
    private FirebaseAuth mAuth;
    private TextView tvClockEvent;
    private TextView tvExpEvent;
    private TextView tvGeneralExp;
    private ImageView imEvent;
    private ConstraintLayout clEvent;
    private GoogleMap mMap;
    private BarChart barChart;
    private PieChart pieChart;
    private BarChart lineChart;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Fragment myFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ana_vasi, container, false);

        tvClockEvent=view.findViewById(R.id.tvClockEvent);
        tvExpEvent=view.findViewById(R.id.tvExpEvent);
        imEvent=view.findViewById(R.id.imEvent);
        clEvent=view.findViewById(R.id.clEvent);
        tvGeneralExp=view.findViewById(R.id.tvGeneralExp);
        barChart=view.findViewById(R.id.barChart);
        pieChart=view.findViewById(R.id.pieChart);
        lineChart=view.findViewById(R.id.lineChart);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
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

        //??kon ve arkaplanlar?? getirme
        Drawable walking=getContext().getDrawable(R.drawable.walk);
        Drawable waiting=getContext().getDrawable(R.drawable.bekleme);
        Drawable car=getContext().getDrawable(R.drawable.ic_car);
        Drawable running=getContext().getDrawable(R.drawable.run);
        Drawable alert=getContext().getDrawable(R.drawable.ic_alert);
        Drawable falling=getContext().getDrawable(R.drawable.ic_fall);
        Drawable drawableRed = getContext().getResources().getDrawable(R.drawable.redbutton);
        Drawable drawableYellow = getContext().getResources().getDrawable(R.drawable.yellow_btn);
        Drawable drawableGreen = getContext().getResources().getDrawable(R.drawable.green_button);
        Drawable drawableBlue = getContext().getResources().getDrawable(R.drawable.btn_blue);
        Drawable drawableWhite = getContext().getResources().getDrawable(R.drawable.btn_bg);
        //??kon ve arkaplanlar?? getirme

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference child=database.getReference("Users").child(mAuth.getCurrentUser().getUid());
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid=snapshot.child("Uid").getValue().toString();
                // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference myRefGeneral = database.getReference("Users").child(uid);
                Date tarih= Calendar.getInstance().getTime();
                SimpleDateFormat dayFormat=new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
                SimpleDateFormat saatFormat=new SimpleDateFormat("HH:mm:ss");
                String current_clock=saatFormat.format(tarih);
                String current_time=format.format(tarih);
                String current_day=dayFormat.format(tarih);
                String current_timeFinal=format.format(tarih);

                getEvents events=new getEvents(getContext());
                events.getList(uid,"today", new getEvents.EventLogsCallback() {
                    @Override
                    public void onCallback(ArrayList<Aksyon> value) throws ParseException {
                        todayList=value;
                        events.getList(uid,"yesterday", new getEvents.EventLogsCallback() {
                            @Override
                            public void onCallback(ArrayList<Aksyon> value) throws ParseException {
                                yesterdayList=value;
                                events.getList(uid,"old", new getEvents.EventLogsCallback() {
                                    @Override
                                    public void onCallback(ArrayList<Aksyon> value) throws ParseException {
                                        oldList=value;
                                        events.getList(uid,"hareketler", new getEvents.EventLogsCallback() {
                                            @Override
                                            public void onCallback(ArrayList<Aksyon> value) throws ParseException {
                                                hareketler=value;
                                                myRefGeneral.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String ogle="12:00:00";
                                                        String aksam="20:00:00";

                                                        events.getRutinAyar(uid,new getEvents.rutinAyarlari() {
                                                            //String aciklama="";
                                                            @Override
                                                            public void onCallback(ArrayList<rutinAyar> value) throws ParseException {
                                                                try {
                                                                    ArrayList<rutinAyar> finalAyarlar = value;
                                                                    events.zaman(current_clock, saatFormat, new getEvents.zamanDonemiCallback() {
                                                                        @Override
                                                                        public void onCallback(String value) throws ParseException {
                                                                            String zaman=value;
                                                                            events.gunlukRutinSayilari(saatFormat, todayList, new getEvents.rutinSayilari() {
                                                                                @Override
                                                                                public void onCallback(ArrayList<Integer> value) throws ParseException {
                                                                                    String aciklama="";
                                                                                    ArrayList<Integer> finalSayilar = value;
                                                                                    //yeni ??yeler i??in tekrar bak!!!!!

                                                                                    String zamanAciklama="";String yer=""; String expacil=""; String expfall="";
                                                                                    String hareket="";
                                                                                    int alertControl=0;

                                                                                    int hareketIndis=0;int yerIndis=0;int acilIndis=0;
                                                                                    int hareketFlag=0;
                                                                                    int yerFlag=0;
                                                                                    int acil=0; int fall=0;
                                                                                    int rutinFlag=0;
                                                                                    int rutinIndis=0;
                                                                                    int duzelmeIndis=0;

                                                                                    //Hastan??n ??evrimi??i olma durumunu kontrol etme.
                                                                                    String cevapOnline="";
                                                                                    long zamanFark??=format.parse(current_time).getTime()-format.parse(snapshot.child("zaman").getValue().toString()).getTime();
                                                                                    int differenceMinutes= (int) (zamanFark?? / (60 * 1000) % 60);
                                                                                    int diffSeconds = (int) (zamanFark?? / 1000 % 60);
                                                                                    int diffHours = (int) (zamanFark?? / (60 * 60 * 1000) % 24);
                                                                                    int diffDays = (int) (zamanFark?? / (24 * 60 * 60 * 1000));
                                                                                    if (diffDays==0&&diffHours==0&&differenceMinutes<10){

                                                                                    }else {
                                                                                        alertControl++;
                                                                                        cevapOnline="Hastadan veri al??nam??yor.Son bilgilere g??re  ";
                                                                                        zamanAciklama="Son "+zamanCevirme(zamanFark??)+" dir";
                                                                                        aciklama=aciklama+cevapOnline;
                                                                                    }
                                                                                    //??evrimi??i kontrol??n??n sonu
                                                                                    //??evrimi??i kontrol??nden sonra son hareket ve yer durumu kontrol ediliyor
                                                                                    int size=hareketler.size()-1;
                                                                                    for (int i=size;i>=0;i--){

                                                                                        //mevcut hareket durumunu getirme
                                                                                        if (hareketFlag==0&&hareketler.get(i).getHareket().equals("aktivite")){
                                                                                            hareket=hareketler.get(i).getEylem()+" durumunda. ";
                                                                                            hareketIndis=i;
                                                                                            hareketFlag++;
                                                                                        }
                                                                                        //mevcut konumunu getirme
                                                                                        if (yerFlag==0&&hareketler.get(i).getHareket().equals("yer")){
                                                                                            yer=hareketler.get(i).getYer()+" yak??nlar??nda ";
                                                                                            yerIndis=i;
                                                                                            yerFlag++;
                                                                                        }

                                                                                        //acil,d????me durumunu getirme
                                                                                        if (acil==0&&(hareketler.get(i).getHareket().equals("acil")||hareketler.get(i).getHareket().equals("kotu"))){
                                                                                            acil++;
                                                                                            if (duzelmeTara(hareketler,i,size)){
                                                                                                expacil="";
                                                                                                duzelmeIndis=i;
                                                                                            }else {
                                                                                                if (!zamanAciklama.equals("")){
                                                                                                    expacil=expacil+gecenSure(hareketler.get(i).getZaman(),current_time,format)+" ??nce ";
                                                                                                }

                                                                                                acilIndis=i;
                                                                                                expacil=expacil+"Acil bir durum i??erisinde oldu??unu belirtti ve sonras??nda durumunda bir iyile??me g??remedik.";
                                                                                                //alertControl++;
                                                                                            }
                                                                                        }
                                                                                        if (acil==0&&hareketler.get(i).getHareket().equals("SonD??sme")){
                                                                                            acil++;
                                                                                            if (duzelmeTara(hareketler,i,size)){
                                                                                                expfall="";
                                                                                                duzelmeIndis=0;
                                                                                            }else {
                                                                                                if (!zamanAciklama.equals("")){
                                                                                                    expfall=expfall+gecenSure(hareketler.get(i).getZaman(),current_time,format)+" ??nce ";
                                                                                                }
                                                                                                acilIndis=i;
                                                                                                expfall=expfall+"D????me alg??land?? ve sonras??nda durumunda bir iyile??me g??remedik.";
                                                                                                //alertControl++;
                                                                                            }
                                                                                        }
                                                                                        if (rutinFlag==0&&hareketler.get(i).getHareket().equals("rutin")){
                                                                                            rutinIndis=i;
                                                                                            rutinFlag++;

                                                                                        }

                                                                                    }


                                                                                    //E??er hastadan g??n i??erisinde eylem ve yer bilgisi elde edilememi?? ise
                                                                                    if (yer.equals("")){
                                                                                        yer=snapshot.child("Yak??n_Yer").getValue().toString()+ " yak??nlar??ndayd??. ";
                                                                                    }
                                                                                    if (hareket.equals(""))hareket="Uzun zamand??r herhangi bir hareket alg??lanmad??.";
                                                                                    if (zamanAciklama.equals("")){
                                                                                        int zamanIndisler[]={hareketIndis,yerIndis,acilIndis,rutinIndis,duzelmeIndis};
                                                                                        int max=zamanIndisler[0];
                                                                                        for (int k=0;k<zamanIndisler.length;k++){
                                                                                            if (max<zamanIndisler[k]) max=zamanIndisler[k];
                                                                                        }
                                                                                        long zamanBilgisi=0;
                                                                                        zamanBilgisi=format.parse(current_time).getTime()-format.parse(hareketler.get(size).getZaman()).getTime();
                                                                                        zamanAciklama="Son "+zamanCevirme(zamanBilgisi)+" dir";
                                                                                    }


                                                                                    if (zaman.equals("sabah")){
                                                                                        if (finalAyarlar.get(0).isLevel()){
                                                                                            if (finalSayilar.get(0)>1){
                                                                                                alertControl++;
                                                                                                aciklama=aciklama+"Sabah "+finalSayilar.get(0)+" kez ila?? kulland??.";
                                                                                            }

                                                                                        }
                                                                                        if (finalAyarlar.get(1).isLevel()){

                                                                                            if (finalSayilar.get(4)>1){
                                                                                                alertControl++;
                                                                                                aciklama=aciklama+"Sabah "+finalSayilar.get(4).toString()+" kez yemek yedi.";
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    if (zaman.equals("ogle")){
                                                                                        //ila?? rutini uyar??lar??
                                                                                        if (finalAyarlar.get(0).isLevel()){
                                                                                            if (finalSayilar.get(0)>2){
                                                                                                alertControl++;
                                                                                                aciklama=aciklama+"G??n i??inde  fazla doz ila?? ald??.";
                                                                                            }
                                                                                            if (finalAyarlar.get(0).isSabah()){
                                                                                                if (finalSayilar.get(1)==0){
                                                                                                    aciklama=aciklama+"Sabah ilac??n?? almad??.";
                                                                                                    alertControl++;
                                                                                                }
                                                                                                else if (finalSayilar.get(1)==1)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(1)>1){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Hasta sabah "+finalSayilar.get(1)+" kez ila?? ald??.";
                                                                                                }
                                                                                            }
                                                                                            if (finalAyarlar.get(0).isOgle()){
                                                                                                if (finalSayilar.get(2)==0)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(2)==1)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(2)>1){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Hasta ????le "+finalSayilar.get(2)+" kez ila?? ald??.";
                                                                                                }
                                                                                            }

                                                                                        }

                                                                                        //yemek rutini uyar??lar??
                                                                                        if (finalAyarlar.get(1).isLevel()){
                                                                                            if (finalSayilar.get(4)>2){
                                                                                                alertControl++;
                                                                                                aciklama=aciklama+"G??n i??inde  fazla yemek yedi.";
                                                                                            }
                                                                                            if (finalAyarlar.get(1).isSabah()){
                                                                                                if (finalSayilar.get(5)==0){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Sabah kahvalt?? yapmad??.";
                                                                                                }
                                                                                                else if (finalSayilar.get(5)==1)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(5)>1){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Hasta sabah "+finalSayilar.get(5)+" kez yemek yedi.";
                                                                                                }
                                                                                            }
                                                                                            if (finalAyarlar.get(1).isOgle()){
                                                                                                if (finalSayilar.get(6)==0)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(6)==1)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(6)>1){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Hasta ????le "+finalSayilar.get(6)+" kez yemek yedi.";
                                                                                                }
                                                                                            }

                                                                                        }
                                                                                    }
                                                                                    if (zaman.equals("aksam")){
                                                                                        if (finalSayilar.get(4)>4){
                                                                                            alertControl++;
                                                                                            aciklama=aciklama+"G??n i??inde  fazla yemek yedi.";
                                                                                        }
                                                                                        if (finalSayilar.get(0)>4){
                                                                                            alertControl++;
                                                                                            aciklama=aciklama+"G??n i??inde  fazla say??da ila?? ald??.";
                                                                                        }
                                                                                        if (finalAyarlar.get(0).isLevel()){

                                                                                            if (finalAyarlar.get(0).isSabah()){
                                                                                                if (finalSayilar.get(1)==0){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Sabah ilac??n?? almad??.";
                                                                                                }
                                                                                                else if (finalSayilar.get(1)==1)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(1)>1){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Hasta sabah "+finalSayilar.get(1)+" kez ila?? ald??.";
                                                                                                }
                                                                                            }
                                                                                            if (finalAyarlar.get(0).isOgle()){
                                                                                                if (finalSayilar.get(2)==0){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"????le ilac??n?? almad??.";
                                                                                                }
                                                                                                else if (finalSayilar.get(2)==1)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(2)>1){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Hasta ????le "+finalSayilar.get(2)+" kez ila?? ald??.";
                                                                                                }
                                                                                            }
                                                                                            if (finalAyarlar.get(0).isAksam()){
                                                                                                if (finalSayilar.get(3)>1){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Ak??am "+finalSayilar.get(3)+" kez ila?? ald??.";
                                                                                                }
                                                                                            }

                                                                                        }

                                                                                        if (finalAyarlar.get(1).isLevel()){

                                                                                            if (finalAyarlar.get(1).isSabah()){
                                                                                                if (finalSayilar.get(5)==0){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Sabah kahvalt?? yapmad??.";
                                                                                                }
                                                                                                else if (finalSayilar.get(5)==1)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(5)>1){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Hasta sabah "+finalSayilar.get(5)+" kez yemek yedi.";
                                                                                                }
                                                                                            }
                                                                                            if (finalAyarlar.get(1).isOgle()){
                                                                                                if (finalSayilar.get(6)==0)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(6)==1)aciklama=aciklama+"";
                                                                                                else if (finalSayilar.get(6)>1){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Hasta ????le "+finalSayilar.get(6)+" kez yemek yedi.";
                                                                                                }
                                                                                            }
                                                                                            if (finalAyarlar.get(1).isAksam()){
                                                                                                if (finalSayilar.get(7)>1){
                                                                                                    alertControl++;
                                                                                                    aciklama=aciklama+"Hasta ak??am "+finalSayilar.get(7)+" kez yemek yedi.";
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    aciklama=aciklama+yer+hareket+expacil+expfall;
                                                                                    //Arkaplan?? alarm durumuna g??re ayarlama
                                                                                    if (alertControl!=0){
                                                                                        clEvent.setBackground(drawableRed);
                                                                                        imEvent.setImageDrawable(alert);

                                                                                    }else {
                                                                                        aciklama=aciklama+"??uan da ??nemli bir aksilik g??z??km??yor.";
                                                                                    }
                                                                                    tvClockEvent.setText(zamanAciklama);
                                                                                    tvExpEvent.setText(aciklama);
                                                                                    // Anl??k durum a????klamalar??n??n sonu

                                                                                    //G??nl??k gezilen yerler a????klamas??
                                                                                    if (todayList.isEmpty()){
                                                                                        //tvLocationExp.setText("Bug??n i??in bir kay??t bulunamad??.");
                                                                                    }else {
                                                                                        int locCounter=0;
                                                                                        for (int i=0;i<todayList.size();i++){
                                                                                            if (todayList.get(i).getHareket().equals("yer")){
                                                                                                locCounter++;
                                                                                            }
                                                                                        }
                                                                                        //if (locCounter==0)tvLocationExp.setText("??uana kadar bulundu??u konumdan ayr??lmad??.G??ne ba??lad?????? yerde.");
                                                                                    }
                                                                                    int oldSize=oldList.size();
                                                                                    int gunSayisi=0;
                                                                                    int oldKotu=0;int oldDusme=0;int oldYer=0;
                                                                                    int oldSabahKotu=0; int oldOgleKotu=0; int aksamKotu=0;
                                                                                    ArrayList<oldListClass> oldKotuZaman=new ArrayList<>();
                                                                                    ArrayList<oldListClass> oldDusmeZaman=new ArrayList<>();
                                                                                    ArrayList<oldListClass> oldYerZaman=new ArrayList<>();

                                                                                    //Acil durumlar??n haftal??k g??sterimi i??in ??nce son 7 g??ne ve kay??tlar??na
                                                                                    //ula????l??p grafi??e eklenmesi
                                                                                    Calendar calendar=Calendar.getInstance();
                                                                                    SimpleDateFormat dayNameFormat=new SimpleDateFormat("EEEE", Locale.ENGLISH);
                                                                                    calendar.add(Calendar.DAY_OF_YEAR,-6);
                                                                                    ArrayList<acilSayi> hedefGunler=new ArrayList<>();
                                                                                    ArrayList<String> labelGunler=new ArrayList<>();
                                                                                    ArrayList<String> labelToparlanma=new ArrayList<>();
                                                                                    ArrayList<Long> degerToparlanma=new ArrayList<>();
                                                                                    // olaylar??n ya??and?????? andaki durumu i??in de??i??kenler
                                                                                    String eylem="";
                                                                                    int bekleme=0;
                                                                                    int y??r??me=0;
                                                                                    int ge??is=0;
                                                                                    int arac=0;
                                                                                    int kosma=0;

                                                                                    for(int i = 0; i< 6; i++){
                                                                                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                                                                                        hedefGunler.add(new acilSayi(dayFormat.format(calendar.getTime()),0));
                                                                                        labelGunler.add(dayNameFormat.format(calendar.getTime()));
                                                                                    }
                                                                                    for(int k=0;k<oldSize;k++){
                                                                                        Date curday=format.parse(oldList.get(k).getZaman());
                                                                                        String day=dayFormat.format(curday);
                                                                                        String oldClock=saatFormat.format(curday);

                                                                                        if (oldList.get(k).getHareket().equals("kotu")||oldList.get(k).getHareket().equals("SonD??sme")||oldList.get(k).getHareket().equals("acil")){
                                                                                            Date timeLog=format.parse(oldList.get(k).getZaman());
                                                                                            String dayLog=dayFormat.format(timeLog);

                                                                                            if (olayDurum(timeLog,oldList,format)==0){
                                                                                                eylem=oldList.get(k).getEylem();
                                                                                                if (eylem.equals("hareketsiz (Bekleme)"))bekleme++;
                                                                                                else if (eylem.equals("Y??r??me"))y??r??me++;
                                                                                                else if (eylem.equals("Ara?? i??inde hareket")) arac++;
                                                                                                else if (eylem.equals("Ko??ma"))   kosma++;
                                                                                            }else {
                                                                                                ge??is++;
                                                                                            }
                                                                                            for (int j=0;j<hedefGunler.size();j++){
                                                                                                if (dayLog.equals(hedefGunler.get(j).getDay())){
                                                                                                    hedefGunler.get(j).setAdet(hedefGunler.get(j).getAdet()+1);
                                                                                                }
                                                                                            }

                                                                                            //Toparlanma s??releri i??in veri toplan??yor
                                                                                            int kontrolToparlama=0;
                                                                                            for (int m=k;m<oldList.size();m++){
                                                                                                Date timeLogK=format.parse(oldList.get(m).getZaman());
                                                                                                String dayLogK=dayFormat.format(timeLogK);
                                                                                                if (dayLog.equals(dayLogK)){
                                                                                                    if (oldList.get(m).getHareket().equals("iyi")||(oldList.get(m).getHareket().equals("hareket")&&oldList.get(m).getEylem().equals("hareketsiz (Bekleme)"))){
                                                                                                        String xLabelData=format.format(timeLogK);
                                                                                                        long fark=timeLogK.getTime()-timeLog.getTime();
                                                                                                        int Minutes= (int) (fark / (60 * 1000) % 60);
                                                                                                        int Seconds = (int) (fark / 1000 % 60);
                                                                                                        int Hours = (int) (fark / (60 * 60 * 1000) % 24);
                                                                                                        kontrolToparlama++;
                                                                                                        long max=18000000;
                                                                                                        if (Hours>5){
                                                                                                            degerToparlanma.add(max);
                                                                                                            labelToparlanma.add(xLabelData);
                                                                                                        }
                                                                                                        else if (Hours>=1){
                                                                                                            degerToparlanma.add(fark);
                                                                                                            labelToparlanma.add(xLabelData);
                                                                                                        }else {
                                                                                                            degerToparlanma.add(fark);
                                                                                                            labelToparlanma.add(xLabelData);
                                                                                                        }

                                                                                                    };
                                                                                                }
                                                                                            }
                                                                                            if (kontrolToparlama==0){
                                                                                                degerToparlanma.add((long) 18000000);
                                                                                                labelToparlanma.add(format.format(curday));
                                                                                            }
                                                                                        }
                                                                                        if (k!=0){
                                                                                            Date previousLog=format.parse(oldList.get(k-1).getZaman());
                                                                                            String previousday=dayFormat.format(previousLog);
                                                                                            if (!day.equals(previousday))gunSayisi++;
                                                                                        }
                                                                                        String logHareket=oldList.get(k).getHareket();
                                                                                        if (logHareket.equals("acil")||logHareket.equals("kotu")){
                                                                                            oldListClass eklenecek=new oldListClass(zaman(oldClock,saatFormat),oldList.get(k).getEylem(),oldList.get(k).getYer());
                                                                                            oldKotuZaman.add(eklenecek);
                                                                                            oldKotu++;
                                                                                        }
                                                                                        else if (logHareket.equals("SonD??sme")){
                                                                                            oldListClass eklenecek=new oldListClass(zaman(oldClock,saatFormat),oldList.get(k).getEylem(),oldList.get(k).getYer());
                                                                                            oldDusmeZaman.add(eklenecek);
                                                                                            oldDusme++;
                                                                                        }
                                                                                        else if (logHareket.equals("yer")){
                                                                                            oldListClass eklenecek=new oldListClass(zaman(oldClock,saatFormat),oldList.get(k).getEylem(),oldList.get(k).getYer());
                                                                                            oldYerZaman.add(eklenecek);
                                                                                            oldYer++;
                                                                                        }

                                                                                    }
                                                                                    //acil bildirimlerin nerede ne s??kl??kla hangi durumda yap??ld??????n??n a????klamas??
                                                                                    String genelAciklama="";
                                                                                    if (!oldKotuZaman.isEmpty()){
                                                                                        if (oldKotuZaman.size()>2){
                                                                                            genelAciklama=genelAciklama+"Kay??tlar??m??z i??erisindeki "+gunSayisi+" g??nde "+oldKotuZaman.size()+" kez size acil bildirim iletti ve o s??rada genellikle  "+genellemeOldList(oldKotuZaman)+ "yd??.\n";
                                                                                        }else{
                                                                                            genelAciklama=genelAciklama+"Kay??tlar??m??z i??erisindeki "+gunSayisi+" g??nde "+oldKotuZaman.size()+" kez acil durum bildirildi.";
                                                                                            if (oldKotuZaman.size()==1)genelAciklama=genelAciklama+"O s??rada"+genellemeOldList(oldKotuZaman)+" yd??";

                                                                                        }
                                                                                    }
                                                                                    else {
                                                                                            genelAciklama=genelAciklama+"??uana kadar acil bildirim g??nderilmedi.";
                                                                                    }
                                                                                    if (!oldDusmeZaman.isEmpty()){
                                                                                        if (oldDusmeZaman.size()>2){
                                                                                            genelAciklama=genelAciklama+" D????me durumunda ise  a????rl??kl?? olarak "+genellemeOldList(oldDusmeZaman)+ "yd??.??uana kadar "+oldDusmeZaman.size()+" kez d????t??.";
                                                                                        }else{
                                                                                            genelAciklama=genelAciklama+"??uana kadar  "+oldDusmeZaman.size()+" kez d????me alg??lad??k.";
                                                                                            if (oldDusmeZaman.size()==1)genelAciklama=genelAciklama+"O s??rada "+genellemeOldList(oldDusmeZaman)+ " yd??.";

                                                                                        }
                                                                                    }else  genelAciklama=genelAciklama+"??uana kadar hi?? d????me alg??lanmad??.";
                                                                                    tvGeneralExp.setText(genelAciklama);

                                                                                    //son yedi g??nl??k acil olaylar??n grafik ??izimi
                                                                                    ArrayList<BarEntry> haftalikAcil=new ArrayList<>();
                                                                                    for (int i=0;i<hedefGunler.size();i++){
                                                                                        haftalikAcil.add(new BarEntry(i,hedefGunler.get(i).getAdet()));
                                                                                    }
                                                                                    BarDataSet barDataSet =new BarDataSet(haftalikAcil,"acil durumlar");
                                                                                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                                                                    barDataSet.setValueTextSize(16f);
                                                                                    BarData barData=new BarData(barDataSet);
                                                                                    barChart.setFitBars(true);
                                                                                    barChart.setData(barData);
                                                                                    barChart.setDrawBorders(false);
                                                                                    barChart.setDrawGridBackground(false);
                                                                                    barChart.getXAxis().setDrawGridLines(false);
                                                                                    barChart.getAxisRight().setDrawLabels(false);
                                                                                    barChart.getAxisRight().setDrawGridLines(false);
                                                                                    barChart.getAxisLeft().setDrawGridLines(false);
                                                                                    barChart.getAxisLeft().setDrawLabels(false);

                                                                                    barChart.getDescription().setText("");
                                                                                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelGunler));
                                                                                    barChart.animateY(3000);

                                                                                    //olaylar??n ya??and?????? anki durumu g??steren pasta grafik
                                                                                    ArrayList<PieEntry> pieEntries=new ArrayList<>();
                                                                                    pieEntries.add(new PieEntry(bekleme,"Hareketsiz"));
                                                                                    pieEntries.add(new PieEntry(y??r??me,"Y??r??rken"));
                                                                                    pieEntries.add(new PieEntry(ge??is,"Ge??i?? esnas??nda"));
                                                                                    pieEntries.add(new PieEntry(arac,"Ara?? i??inde "));
                                                                                    pieEntries.add(new PieEntry(kosma,"Ko??arken"));
                                                                                    PieDataSet pieDataSet=new PieDataSet(pieEntries,"");
                                                                                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                                                                    pieDataSet.setValueTextColor(R.color.yazi_baslik);
                                                                                    pieDataSet.setValueTextSize(18f);
                                                                                    PieData pieData=new PieData(pieDataSet);
                                                                                    pieChart.setData(pieData);
                                                                                    pieChart.getDescription().setEnabled(false);
                                                                                    pieChart.setDrawEntryLabels(false);
                                                                                    pieChart.setCenterText("Acil durumlar ne zaman oluyor?");
                                                                                    pieChart.animateY(4000);

                                                                                    //Acil durumlar??n d??zelme durum haritas??
                                                                                    ArrayList<BarEntry> barToparlama=new ArrayList<>();
                                                                                    ArrayList<String> labels=new ArrayList<>();
                                                                                    int k=0;
                                                                                    for (int i=degerToparlanma.size()-1;i>=degerToparlanma.size()-6;i--){
                                                                                        barToparlama.add(new BarEntry(k,degerToparlanma.get(k)));
                                                                                        labels.add(labelToparlanma.get(k));
                                                                                        k++;
                                                                                    }
                                                                                    BarDataSet toparlamaDataSet =new BarDataSet(barToparlama,"acil durumlar");
                                                                                    toparlamaDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                                                                    toparlamaDataSet.setValueTextSize(12f);
                                                                                    BarData toparlamaData=new BarData(toparlamaDataSet);
                                                                                    toparlamaData.setValueFormatter(new MyValueFormatter());
                                                                                    lineChart.setFitBars(true);
                                                                                    lineChart.setData(toparlamaData);
                                                                                    lineChart.setDrawBorders(false);
                                                                                    lineChart.setDrawGridBackground(false);
                                                                                    lineChart.getXAxis().setDrawGridLines(false);
                                                                                    lineChart.getAxisRight().setDrawLabels(false);
                                                                                    lineChart.getAxisRight().setDrawGridLines(false);
                                                                                    lineChart.getAxisLeft().setDrawGridLines(false);
                                                                                    lineChart.getAxisLeft().setDrawLabels(false);
                                                                                    lineChart.getDescription().setText("");
                                                                                    lineChart.getXAxis().setTextSize(14);
                                                                                    lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                                                                                    lineChart.getXAxis().setLabelRotationAngle(90);
                                                                                    lineChart.setExtraOffsets(20,120,20,20);
                                                                                    lineChart.animateY(3000);

                                                                                }
                                                                            });


                                                                        }
                                                                    });

                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        });

                                                        try {


                                                        }catch (Exception e){
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;

    }

    private int olayDurum(Date timeLog, List<Aksyon> oldList, SimpleDateFormat format) throws  ParseException {
        int yakinZamanHareket=0;
        try {
            for (int j=0;j<oldList.size();j++){
                Date logDate=format.parse(oldList.get(j).getZaman());
                long zamanFarki=timeLog.getTime()-logDate.getTime();
                int differenceMinutes= (int) (zamanFarki / (60 * 1000) % 60);
                int diffSeconds = (int) (zamanFarki / 1000 % 60);
                int diffHours = (int) (zamanFarki / (60 * 60 * 1000) % 24);
                int diffDays = (int) (zamanFarki/ (24 * 60 * 60 * 1000));
                if (diffDays<1&&diffHours<1&&differenceMinutes<1){
                    if (oldList.get(j).getHareket().equals("hareket")) {
                        yakinZamanHareket++;
                        return yakinZamanHareket;
                    }
                }
            }
            return yakinZamanHareket;
        }catch (Exception e){ return yakinZamanHareket;

    }}

    private String genellemeOldList(ArrayList<oldListClass> oldList) {
        ArrayList<String> zamanlar=new ArrayList<>();
        ArrayList<String> hareketler=new ArrayList<>();
        ArrayList<String> yer=new ArrayList<>();
        for (int i=0;i<oldList.size();i++){
            zamanlar.add(oldList.get(i).getZaman());
            hareketler.add(oldList.get(i).getHareket());
            yer.add(oldList.get(i).getYer());
        }
        Log.d("stringstringstring",findPopular(zamanlar));
        String zamanCevap=findPopular(zamanlar);
        String hareketCevap=findPopular(hareketler);
        String yerCevap=findPopular(yer);
        String cevap=zamanCevap+" saatlerinde "+ hareketCevap+" durumunda ";
        return cevap;
    }

    private boolean duzelmeTara(List<Aksyon> hareketler, int i, int size) {
        boolean cevap=false;
        for (int j=i;j<hareketler.size();j++){
            if (hareketler.get(j).getHareket().equals("iyi")){
                cevap= true;

            }
            else if  (hareketler.get(j).getHareket().equals("aktivite")&&!hareketler.get(j).getEylem().equals("hareketsiz (Bekleme)")){
                cevap= true;

            }
        }
        return cevap;
    }

    private String zamanCevirme(long ttimeWalking) {
        String cevap="";
        int differenceMinutes= (int) (ttimeWalking / (60 * 1000) % 60);
        int diffSeconds = (int) (ttimeWalking / 1000 % 60);
        int diffHours = (int) (ttimeWalking / (60 * 60 * 1000) % 24);
        int diffDays = (int) (ttimeWalking / (24 * 60 * 60 * 1000));

        if (diffDays>0){
            cevap=diffDays+" g??n "+diffHours+" saat";
        }
        else if (diffHours>0){
            cevap=+diffHours+" saat "+differenceMinutes+" dakika";
        }
        else if (differenceMinutes>0){
            cevap=differenceMinutes+" dakika";
        }
        else {
            cevap=diffSeconds+" saniye";
        }
        return cevap;

    }

    private String gecenSure(String olayZamani, String duzelmeZamani, SimpleDateFormat format) throws ParseException {
        String cevap="";

        long zamanFark??=format.parse(duzelmeZamani).getTime()-format.parse(olayZamani).getTime();
        int differenceMinutes= (int) (zamanFark?? / (60 * 1000) % 60);
        int diffSeconds = (int) (zamanFark?? / 1000 % 60);
        int diffHours = (int) (zamanFark?? / (60 * 60 * 1000) % 24);
        int diffDays = (int) (zamanFark?? / (24 * 60 * 60 * 1000));

            if (diffDays>0){
                cevap=diffDays+" g??n "+diffHours+" saat";
            }
            else if (diffHours>0){
                cevap=+diffHours+" saat "+differenceMinutes+" dakika";
            }
            else if (differenceMinutes>0){
                cevap=differenceMinutes+" dakika";
            }
            else {
                cevap=diffSeconds+" saniye";
            }

        return cevap;
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
    public String findPopular (List<String> array) {
        Map<String, Integer> stringsCount = new HashMap<String, Integer>();
        for(String string:array)
        {
            if (string.length() > 0) {
                string = string.toLowerCase();
                Integer count = stringsCount.get(string);
                if(count == null) count = new Integer(0);
                count++;
                stringsCount.put(string,count);
            }
        }
        Map.Entry<String,Integer> mostRepeated = null;
        for(Map.Entry<String, Integer> e: stringsCount.entrySet())
        {
            if(mostRepeated == null || mostRepeated.getValue()<e.getValue())
                mostRepeated = e;
        }
        try {
            return mostRepeated.getKey();
        } catch (NullPointerException e) {
            System.out.println("Cannot find most popular value at the List. Maybe all strings are empty");
            return "";
        }

    }
    public class MyValueFormatter extends ValueFormatter{
        public MyValueFormatter() {
            super();
        }

        @Override
        public String getFormattedValue(float value) {
            int Minutes= (int) (value / (60 * 1000) % 60);
            int Seconds = (int) (value / 1000 % 60);
            int Hours = (int) (value/ (60 * 60 * 1000) % 24);
            long max=18000000;
            if (value==max){
                return "Bilinmiyor";
            }
            else if (Hours>5){
                return (Hours+" saat "+Minutes+" dakika");
            }
            else if (Hours>=1){
                return (Hours+" saat "+Minutes+" dakika");
            }else if (Minutes>=1){
                return (Minutes+" dakika");
            }
            else return Seconds+" saniye";

        }
    }


}