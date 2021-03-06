package com.example.myapplication.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.Model.Aksyon;
import com.example.myapplication.Model.getEvents;
import com.example.myapplication.Model.rutinAyar;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HastaMainFragment extends Fragment {

    private FirebaseAuth mAuth;
    String patientUid;
    private ArrayList<Aksyon> hareketler;
    private ArrayList<Aksyon> todayList;
    private ArrayList<Aksyon> yesterdayList;
    private ArrayList<Aksyon> oldList;
    FirebaseDatabase database;
    private ConstraintLayout clHastaExp;
    private TextView tvHastaExp;
    private ImageView imHastaExp;
    private TextView tvHastaftit;
    private ImageView imHastafFood;
    private ImageView imHastafPill;
    private ImageView imHastafWat;
    private ImageView imHastafShow;
    private ImageView imHastatoi;
    private SwitchCompat sw_HastafS;
    private ArrayList<rutinAyar> myRutinSettings;
    Context appContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Fragment myFragment;

    public HastaMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_hasta_main, container, false);
        appContext=getContext();
        mAuth = FirebaseAuth.getInstance();
        String uid=mAuth.getCurrentUser().getUid();

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshHastaMain);
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

        hareketler=new ArrayList<Aksyon>();
        todayList=new ArrayList<Aksyon>();
        yesterdayList=new ArrayList<Aksyon>();
        oldList=new ArrayList<Aksyon>();
        myRutinSettings=new ArrayList<rutinAyar>();

        database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("Kayitlar").child(this.patientUid);
        DatabaseReference myRef = database.getReference("Kayitlar").child(mAuth.getCurrentUser().getUid());
        DatabaseReference myRefGeneral=database.getReference("Users").child(mAuth.getCurrentUser().getUid().toString());
        Date tarih= Calendar.getInstance().getTime();
        SimpleDateFormat dayFormat=new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
        SimpleDateFormat formatClock = new SimpleDateFormat("HH:mm:ss");
        String current_clock=formatClock.format(tarih);
        String current_time=format.format(tarih);
        String current_day=dayFormat.format(tarih);

        hastalogsinifla(uid,myRef,dayFormat,format,current_time,current_day,tarih,current_clock,formatClock);

        sw_HastafS=view.findViewById(R.id.sw_HastafS);
        clHastaExp=view.findViewById(R.id.cl_Hastaexp);
        tvHastaExp=view.findViewById(R.id.tv_Hastafexp);
        imHastaExp=view.findViewById(R.id.im_Hastafexp);
        tvHastaftit=view.findViewById(R.id.tv_Hastaftit);
        imHastafFood=view.findViewById(R.id.im_HastafFoodplus);
        imHastafPill=view.findViewById(R.id.im_HastafPilplus);
        imHastafWat=view.findViewById(R.id.im_HastafWatplus);
        imHastafShow=view.findViewById(R.id.im_HastafShoplus);
        imHastatoi=view.findViewById(R.id.im_HastafToiplus);
        imHastafFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventLog("yemek",format,myRef);
            }
        });
        imHastafPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventLog("ilac",format,myRef);
            }
        });
        imHastafShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventLog("dus",format,myRef);
            }
        });
        imHastafWat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventLog("su",format,myRef);
            }
        });
        imHastatoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventLog("tuvalet",format,myRef);
            }
        });

        boolean status=sw_HastafS.isChecked();
        sw_HastafS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sw_HastafS.setText("Durumun d??zeldiyse buraya basarak bize bildir ");
                }
                else {
                    sw_HastafS.setText("Durumun iyi g??z??k??yor,acil durumda buraya bas");
                }
            }
        });

        sw_HastafS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEvents getEylemYer=new getEvents(getContext());
                Date an= Calendar.getInstance().getTime();
                String now=format.format(an);
                getEylemYer.getEylemYer("eylem",uid, new getEvents.EylemYerCallback() {
                    @Override
                    public void onCallback(String value) throws ParseException {
                        myRef.child(now).child("eylem").setValue(value);
                        getEylemYer.getEylemYer("yer",uid, new getEvents.EylemYerCallback() {
                            @Override
                            public void onCallback(String value) throws ParseException {
                                myRef.child(now).child("yer").setValue(value);
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Durum Kaydetme Onay??")
                                        .setMessage("De??i??ikli??i kaydetmek istedi??inize emin misiniz?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(R.string.evet, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                if (sw_HastafS.isChecked()){
                                                    myRef.child(now).child("hareket").setValue("kotu");

                                                }else {
                                                    myRef.child(now).child("hareket").setValue("iyi");
                                                }

                                            }})
                                        .setNegativeButton(R.string.hay??r, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                sw_HastafS.setChecked(!sw_HastafS.isChecked());
                                            }
                                        }).show();
                            }
                        });
                    }
                });

            }
        });

        return view;
    }

    private void eventLog(String event, SimpleDateFormat format, DatabaseReference myRef) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Rutin Kaydetme Onay??")
                .setMessage("Eylemi kaydetmek istedi??inize emin misiniz?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.evet, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Date an= Calendar.getInstance().getTime();
                        String now=format.format(an);
                        myRef.child(now).child("hareket").setValue("rutin");
                        myRef.child(now).child("eylem").setValue(event);
                        myRef.child(now).child("yer").setValue("");

                    }})
                .setNegativeButton(R.string.hay??r, null).show();

    }

    private void hastalogsinifla(String patientUid, DatabaseReference myRef, SimpleDateFormat dayFormat, SimpleDateFormat format, String current_time, String current_day, Date tarih, String current_clock, SimpleDateFormat formatClock) {

                try {
                    getEvents event=new getEvents(getContext());
                    event.getList(patientUid,"today",new getEvents.EventLogsCallback() {
                        @Override
                        public void onCallback(ArrayList<Aksyon> value) {
                            todayList=value;
                            event.getList(patientUid,"yesterday",new getEvents.EventLogsCallback() {
                                @Override
                                public void onCallback(ArrayList<Aksyon> value) {
                                    yesterdayList=value;
                                    event.getList(patientUid,"hareketler",new getEvents.EventLogsCallback() {
                                        @Override
                                        public void onCallback(ArrayList<Aksyon> value) throws ParseException {
                                            //todayList=value;
                                            hareketler=value;


                                            if (!todayList.isEmpty()){
                                                event.getRutinAyar(patientUid,new getEvents.rutinAyarlari() {
                                                    @Override
                                                    public void onCallback(ArrayList<rutinAyar> value) throws ParseException {
                                                        myRutinSettings=value;

                                                        event.zaman(current_clock, formatClock, new getEvents.zamanDonemiCallback() {
                                                            @Override
                                                            public void onCallback(String value) throws ParseException {
                                                                String gunDonemi=value;
                                                                event.gunlukRutinSayilari(formatClock, todayList, new getEvents.rutinSayilari() {
                                                                    @Override
                                                                    public void onCallback(ArrayList<Integer> value) throws ParseException {
                                                                        String aciklama="";
                                                                        ArrayList<Integer> cevap=new ArrayList<Integer>();
                                                                        cevap=value;
                                                                        String yemekExplain="";
                                                                        int alertKontroll=0;
                                                                        int alertKontrol=0;
                                                                        int uyari=0;
                                                                        int yemekKontrol=0;
                                                                        int ilacKontrol=0;
                                                                        int suKontrol=0;
                                                                        int banyoKontrol=0;
                                                                        int tuvKontrol=0;
                                                                        if (!hareketler.isEmpty()){

                                                                            for (int i=0;i<hareketler.size();i++) {
                                                                                if (hareketler.get(i).getHareket().equals("acil")||hareketler.get(i).getHareket().equals("SonD??sme")||hareketler.get(i).getHareket().equals("kotu")){
                                                                                    uyari=1;
                                                                                    alertKontroll++;
                                                                                    for (int j=i;j<hareketler.size();j++){
                                                                                        if (hareketler.get(j).getHareket().equals("aktivite")&&!(hareketler.get(j).getEylem().equals("hareketsiz (Bekleme)"))){
                                                                                            aciklama=gecenSure(hareketler.get(i).getZaman(),current_time,format)+" ??nce kritik bir durum ya??ad??n ama ??uanda atlatm???? g??z??k??yorsun.";
                                                                                            uyari=0;
                                                                                        }else if (hareketler.get(j).getHareket().equals("iyi")){
                                                                                            aciklama=gecenSure(hareketler.get(j).getZaman(),current_time,format)+" ??nce kritik bir durum ya??ad??n ama sonras??nda iyi oldu??unu belirttin.";
                                                                                            uyari=0;
                                                                                        }

                                                                                    }
                                                                                    if (uyari==1){
                                                                                        aciklama="En son "+gecenSure(hareketler.get(i).getZaman(),current_time,format)+" ??nce tehlikeli bir durum ya??ad??n ve sonras??nda harekete ge??ti??ini veya iyi oldu??unu bildirmedin.";
                                                                                    }
                                                                                }

                                                                            }
                                                                            if (alertKontroll==0)aciklama=aciklama+"Bug??n hi?? d????medin veya acil bir durum ya??amad??n.";
                                                                        }

                                                                        else {
                                                                            Drawable drawable = getResources().getDrawable(R.drawable.yellow_btn);
                                                                            clHastaExp.setBackground(drawable);
                                                                            imHastaExp.setImageDrawable(getResources().getDrawable(R.drawable.ic_alert));
                                                                            sw_HastafS.setChecked(false);
                                                                            sw_HastafS.setText("Acil bir durum i??erisinde oldu??unda buraya basarak bildirebilirsin.");
                                                                            tvHastaExp.setText("Hen??z yak??n zamanda alg??lad??????m??z bir hareketin yok. Zamanla belirledi??imiz ??nemli hat??rlatmalar?? burada bulabilirsin.");
                                                                        }
                                                                        if (gunDonemi.equals("sabah")){
                                                                            if (myRutinSettings.get(0).isSabah()){
                                                                                if (cevap.get(1)>1){
                                                                                    aciklama=aciklama+("Sabah alman??z gerekenden fazla doz ila?? ald??n??z ! ("+cevap.get(1)+" doz ald??n??z.)");
                                                                                    alertKontrol++;
                                                                                }
                                                                            }else {
                                                                                if (cevap.get(1)!=0){
                                                                                    aciklama=aciklama+("Bu sabah ila?? i??memeniz gerekiyordu,"+cevap.get(1)+" doz ald??n??z.");
                                                                                    alertKontrol++;
                                                                                }
                                                                            }
                                                                            if (myRutinSettings.get(1).isSabah()){
                                                                                if (cevap.get(5)>1){
                                                                                    aciklama=aciklama+("Sabah birden fazla kahvalt?? yapt??n??z ! ("+cevap.get(5)+" kez )");
                                                                                    alertKontrol++;
                                                                                }
                                                                            }
                                                                            if (cevap.get(9)==0){
                                                                                aciklama=aciklama+("Hen??z hi?? su i??medin.");
                                                                                alertKontrol++;
                                                                            }
                                                                            else if (cevap.get(9)>10){
                                                                                aciklama=aciklama+("Bug??n yeterince su i??tiniz! ("+cevap.get(9)+" bardak.)");
                                                                                alertKontrol++;
                                                                            }
                                                                            if (cevap.get(13)>4){
                                                                                aciklama=aciklama+("Bu sabah fazla tuvalete gittiniz. ( "+cevap.get(13)+" kez) ");
                                                                                alertKontrol++;
                                                                            }

                                                                            if (cevap.get(17)>2){
                                                                                aciklama=aciklama+("Bu sabah "+cevap.get(17)+" kez banyo yapt??n??z.");
                                                                                alertKontrol++;
                                                                            }

                                                                        }
                                                                        else if (gunDonemi.equals("ogle")){
                                                                            //ila?? rutini uyar??lar??
                                                                            if (myRutinSettings.get(0).isLevel()){
                                                                                if (cevap.get(0)>2){
                                                                                    alertKontrol++;
                                                                                    aciklama=aciklama+"G??n i??inde  fazla doz ila?? ald??.";
                                                                                }
                                                                                if (myRutinSettings.get(0).isSabah()){
                                                                                    if (cevap.get(1)==0){
                                                                                        aciklama=aciklama+"Sabah ilac??n?? almad??.";
                                                                                        alertKontrol++;
                                                                                    }
                                                                                    else if (cevap.get(1)>1){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Hasta sabah "+cevap.get(1)+" kez ila?? ald??.";
                                                                                    }
                                                                                }
                                                                                if (myRutinSettings.get(0).isOgle()){
                                                                                    if(cevap.get(2)>1){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Hasta ????le "+cevap.get(2)+" kez ila?? ald??.";
                                                                                    }
                                                                                }

                                                                            }

                                                                            //yemek rutini uyar??lar??
                                                                            if (myRutinSettings.get(1).isLevel()){
                                                                                if (cevap.get(4)>2){
                                                                                    alertKontrol++;
                                                                                    aciklama=aciklama+"G??n i??inde  fazla yemek yedi.";
                                                                                }
                                                                                if (myRutinSettings.get(1).isSabah()){
                                                                                    if (cevap.get(5)==0){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Sabah kahvalt?? yapmad??.";
                                                                                    }
                                                                                    else if (cevap.get(5)>1){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Hasta sabah "+cevap.get(5)+" kez yemek yedi.";
                                                                                    }
                                                                                }
                                                                                if (myRutinSettings.get(1).isOgle()){
                                                                                    if (cevap.get(6)>1){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Hasta ????le "+cevap.get(6)+" kez yemek yedi.";
                                                                                    }
                                                                                }

                                                                            }
                                                                        }
                                                                        else if (gunDonemi.equals("aksam")){
                                                                            if (cevap.get(4)>4){
                                                                                alertKontrol++;
                                                                                aciklama=aciklama+"G??n i??inde  fazla yemek yedi.";
                                                                            }
                                                                            if (cevap.get(0)>4){
                                                                                alertKontrol++;
                                                                                aciklama=aciklama+"G??n i??inde  fazla say??da ila?? ald??.";
                                                                            }
                                                                            if (myRutinSettings.get(0).isLevel()){

                                                                                if (myRutinSettings.get(0).isSabah()){
                                                                                    if (cevap.get(1)==0){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Sabah ilac??n?? almad??.";
                                                                                    }
                                                                                    else if (cevap.get(1)>1){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Hasta sabah "+cevap.get(1)+" kez ila?? ald??.";
                                                                                    }
                                                                                }
                                                                                if (myRutinSettings.get(0).isOgle()){
                                                                                    if (cevap.get(2)==0){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"????le ilac??n?? almad??.";
                                                                                    }
                                                                                    else if (cevap.get(2)>1){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Hasta ????le "+cevap.get(2)+" kez ila?? ald??.";
                                                                                    }
                                                                                }
                                                                                if (myRutinSettings.get(0).isAksam()){
                                                                                    if (cevap.get(3)>1){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Ak??am "+cevap.get(3)+" kez ila?? ald??.";
                                                                                    }
                                                                                }

                                                                            }

                                                                            if (myRutinSettings.get(1).isLevel()){

                                                                                if (myRutinSettings.get(1).isSabah()){
                                                                                    if (cevap.get(5)==0){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Sabah kahvalt?? yapmad??.";
                                                                                    }
                                                                                    else if (cevap.get(5)>1){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Hasta sabah "+cevap.get(5)+" kez yemek yedi.";
                                                                                    }
                                                                                }
                                                                                if (myRutinSettings.get(1).isOgle()){
                                                                                    if (cevap.get(6)>1){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Hasta ????le "+cevap.get(6)+" kez yemek yedi.";
                                                                                    }
                                                                                }
                                                                                if (myRutinSettings.get(1).isAksam()){
                                                                                    if (cevap.get(7)>2){
                                                                                        alertKontrol++;
                                                                                        aciklama=aciklama+"Hasta ak??am "+cevap.get(7)+" kez yemek yedi.";
                                                                                    }
                                                                                }

                                                                            }

                                                                        }
                                                                        if (uyari==0&&alertKontrol==0){
                                                                            sw_HastafS.setChecked(false);
                                                                        }
                                                                        else if (uyari==0&&alertKontrol!=0){
                                                                            sw_HastafS.setChecked(false);
                                                                            sw_HastafS.setText("Baz?? aksakl??klar alg??land??. ??yi hissetmiyorsan buraya basarak bildir.");
                                                                            Drawable drawable = getResources().getDrawable(R.drawable.yellow_btn);
                                                                            clHastaExp.setBackground(drawable);
                                                                            imHastaExp.setImageDrawable(getResources().getDrawable(R.drawable.ic_alert));
                                                                        }

                                                                        else {
                                                                            Drawable drawable = getResources().getDrawable(R.drawable.redbutton);
                                                                            clHastaExp.setBackground(drawable);
                                                                            imHastaExp.setImageDrawable(getResources().getDrawable(R.drawable.ic_alert));
                                                                            sw_HastafS.setChecked(true);
                                                                            sw_HastafS.setText("Durumun d??zeldiyse buraya basarak bize bildir ");
                                                                        }

                                                                        tvHastaExp.setText(aciklama);


                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                            else {
                                                tvHastaExp.setText("Bug??n i??in ??uana kadar hi??bir hareketini tespit edemedik.");
                                                Drawable drawable = ContextCompat.getDrawable(appContext,R.drawable.yellow_btn);
                                                clHastaExp.setBackground(drawable);
                                                imHastaExp.setImageDrawable(ContextCompat.getDrawable(appContext,R.drawable.ic_alert));
                                            }




                                        }
                                    });
                                }
                            });
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
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


}