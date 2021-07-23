package com.example.myapplication.Model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

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
import java.util.List;

public class getEvents {

    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    String patientUid;
    private ArrayList<Aksyon> hareketler;
    private ArrayList<Aksyon> todayList;
    private ArrayList<Aksyon> yesterdayList;
    private ArrayList<Aksyon> oldList;
    private ArrayList<rutinAyar> myRutinSettings;
    Context mContext;


    public getEvents(Context context){
        this.mContext=context;
    }

    public interface EventLogsCallback {
        void onCallback(ArrayList<Aksyon> value) throws ParseException;
    }
    public interface EylemYerCallback {
        void onCallback(String value) throws ParseException;
    }
    public interface rutinAyarlari {
        void onCallback(ArrayList<rutinAyar> value) throws ParseException;
    }
    public interface zamanDonemiCallback {
        void onCallback(String value) throws ParseException;
    }
    public interface rutinSayilari {
        void onCallback(ArrayList<Integer> value) throws ParseException;
    }
    public interface koordinatListesi {
        void onCallback(ArrayList<logCoordinats> value) throws ParseException;
    }
    public void getList(String uid,String day,final EventLogsCallback myCallback) {
        Date tarih = Calendar.getInstance().getTime();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
        SimpleDateFormat saatformat = new SimpleDateFormat("HH:mm:ss");
        String current_clock = saatformat.format(tarih);
        String current_time = format.format(tarih);
        String current_day = dayFormat.format(tarih);
        mAuth = FirebaseAuth.getInstance();
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //patientUid=currentUser.getUid();
        hareketler=new ArrayList<Aksyon>();
        todayList=new ArrayList<Aksyon>();
        yesterdayList=new ArrayList<Aksyon>();
        oldList=new ArrayList<Aksyon>();

        database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("Kayitlar").child(this.patientUid);

        DatabaseReference myRefLog = database.getReference("Kayitlar").child(uid);
        myRefLog.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){

                    Aksyon aksyon = new Aksyon();
                    aksyon.setZaman(snap.getKey());
                    aksyon.setEylem(snap.child("eylem").getValue().toString());
                    aksyon.setHareket(snap.child("hareket").getValue().toString());
                    aksyon.setYer(snap.child("yer").getValue().toString());
                    hareketler.add(aksyon);
                }
                try {
                    int size=hareketler.size();
                    //Onceki Gün tarihini elde etme
                    Date myDate = dayFormat.parse(current_day);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(myDate);
                    calendar.add(Calendar.DAY_OF_YEAR, -1);

                    // Use the date formatter to produce a formatted date string
                    Date previousDate = calendar.getTime();
                    String previousDay=dayFormat.format(previousDate);

                    for (int i=0;i<size;i++){

                        Date day=dayFormat.parse(hareketler.get(i).getZaman());
                        String dayy=dayFormat.format(day);
                        oldList.add(hareketler.get(i));

                        if (dayy.equals(current_day)){
                            todayList.add(hareketler.get(i));
                        }
                        else if (dayy.equals(previousDay)){
                            yesterdayList.add(hareketler.get(i));
                        }
                    }

                    if (day.equals("today")){
                        myCallback.onCallback(todayList);
                    }
                    else if (day.equals("yesterday")){
                        myCallback.onCallback(yesterdayList);
                    }
                    else if (day.equals("old")){
                        myCallback.onCallback(oldList);
                    }
                    else if (day.equals("hareketler")){
                        hareketler.clear();
                        //hareketler.addAll(oldList);
                        hareketler.addAll(yesterdayList);
                        hareketler.addAll(todayList);
                        myCallback.onCallback(hareketler);
                    }


                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getEylemYer(String day,String uid,final EylemYerCallback myCallback) {

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(uid);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String eylem="";String yer="";
                eylem=snapshot.child("activity").getValue().toString();
                yer=snapshot.child("Yakın_Yer").getValue().toString();
                try {
                    if (day.equals("eylem")){
                        myCallback.onCallback(eylem);
                    }else if (day.equals("yer")){
                        myCallback.onCallback(yer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    public void getRutinAyar(String uid,final rutinAyarlari myCallback){
        myRutinSettings=new ArrayList<rutinAyar>();
        DatabaseReference myRef = database.getReference("Users").child(uid).child("rutinAyar");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               boolean ilacSabah,ilacOgle,ilacAksam,ilacLevel,yemekSabah,yemekOgle,yemekAksam,yemekLevel;
               ilacSabah= (boolean) snapshot.child("ilac").child("sabah").getValue();
               ilacOgle=(boolean) snapshot.child("ilac").child("ogle").getValue();
               ilacAksam=(boolean) snapshot.child("ilac").child("aksam").getValue();
               ilacLevel=(boolean) snapshot.child("ilac").child("level").getValue();

               yemekSabah= (boolean) snapshot.child("yemek").child("sabah").getValue();
               yemekOgle=(boolean) snapshot.child("yemek").child("ogle").getValue();
               yemekAksam=(boolean) snapshot.child("yemek").child("aksam").getValue();
               yemekLevel=(boolean) snapshot.child("yemek").child("level").getValue();

               rutinAyar yemekRutini= new rutinAyar(yemekSabah,yemekOgle,yemekAksam,yemekLevel);
               rutinAyar ilacRutini= new rutinAyar(ilacSabah,ilacOgle,ilacAksam,ilacLevel);
               myRutinSettings.add(ilacRutini);
               myRutinSettings.add(yemekRutini);

                try {
                    myCallback.onCallback(myRutinSettings);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void zaman(String zaman,SimpleDateFormat format,final zamanDonemiCallback myCallback) throws ParseException {
        String sonuc="";
        String ogle="12:00:00";
        String aksam="20:00:00";
        long zamanFarkiOgle=format.parse(zaman).getTime()-format.parse(ogle).getTime();
        long zamanFarkiAksam=format.parse(zaman).getTime()-format.parse(aksam).getTime();

        if (zamanFarkiOgle<=0){
            sonuc="sabah";

        }else if (zamanFarkiOgle>0 && zamanFarkiAksam<=0){
            sonuc="ogle";

        }else if (zamanFarkiAksam>0){
            sonuc="aksam";

        }
        myCallback.onCallback(sonuc);
    };

    public void gunlukRutinSayilari(SimpleDateFormat format,List<Aksyon> todayList,final rutinSayilari myCallback) throws ParseException {
        String ogle="12:00:00";
        String aksam="20:00:00";
        int ilac=0;int sabahIlac=0;int ogleIlac=0;int aksamIlac=0;
        int yemek=0;int sabahYemek=0;int ogleYemek=0;int aksamYemek=0;
        int su=0; int tuvalet=0; int dus=0; int sabahSu=0;int ogleSu=0; int aksamSu=0;
        int sabahTuvalet=0; int ogleTuvalet=0; int aksamTuvalet=0;
        int sabahDus=0; int ogleDus=0; int aksamDus=0;
        ArrayList<Integer> cevap=new ArrayList<Integer>();

        SimpleDateFormat zamanFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");

        for (int i=0;i <todayList.size();i++){
            Date olayZamani=zamanFormat.parse(todayList.get(i).getZaman());
            String olaySaati=format.format(olayZamani);

            if (todayList.get(i).getHareket().equals("rutin")){
                if (todayList.get(i).getEylem().equals("ilac")){
                    ilac++;
                    if (format.parse(olaySaati).getTime()-format.parse(ogle).getTime()<0){
                        sabahIlac++;
                    }
                    else if (format.parse(olaySaati).getTime()-format.parse(aksam).getTime()<0){
                        ogleIlac++;
                    }
                    else aksamIlac++;
                }
                else if (todayList.get(i).getEylem().equals("yemek")){
                    yemek++;
                    if (format.parse(olaySaati).getTime()-format.parse(ogle).getTime()<0){
                        sabahYemek++;
                    }
                    else if (format.parse(olaySaati).getTime()-format.parse(aksam).getTime()<0){
                        ogleYemek++;
                    }
                    else aksamYemek++;
                }
                else if (todayList.get(i).getEylem().equals("su")){
                    su++;
                    if (format.parse(olaySaati).getTime()-format.parse(ogle).getTime()<0){
                        sabahSu++;
                    }
                    else if (format.parse(olaySaati).getTime()-format.parse(aksam).getTime()<0){
                        ogleSu++;
                    }
                    else aksamSu++;
                }
                else if (todayList.get(i).getEylem().equals("tuvalet")){
                    tuvalet++;
                    if (format.parse(olaySaati).getTime()-format.parse(ogle).getTime()<0){
                        sabahTuvalet++;
                    }
                    else if (format.parse(olaySaati).getTime()-format.parse(aksam).getTime()<0){
                        ogleTuvalet++;
                    }
                    else aksamTuvalet++;
                }
                else if (todayList.get(i).getEylem().equals("dus")){
                    dus++;
                    if (format.parse(olaySaati).getTime()-format.parse(ogle).getTime()<0){
                        sabahDus++;
                    }
                    else if (format.parse(olaySaati).getTime()-format.parse(aksam).getTime()<0){
                        ogleDus++;
                    }
                    else aksamDus++;
                }

            }
        }
        cevap.add(ilac);
        cevap.add(sabahIlac);
        cevap.add(ogleIlac);
        cevap.add(aksamIlac);
        cevap.add(yemek);
        cevap.add(sabahYemek);
        cevap.add(ogleYemek);
        cevap.add(aksamYemek);
        cevap.add(su);
        cevap.add(sabahSu);
        cevap.add(ogleSu);
        cevap.add(aksamSu);
        cevap.add(tuvalet);
        cevap.add(sabahTuvalet);
        cevap.add(ogleTuvalet);
        cevap.add(aksamTuvalet);
        cevap.add(dus);
        cevap.add(sabahDus);
        cevap.add(ogleDus);
        cevap.add(aksamDus);
        myCallback.onCallback(cevap);


    }

    public void koordinatlar(String day,String patientUid,final koordinatListesi myCallback)throws ParseException{
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("Kayitlar").child(patientUid);

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
        ArrayList<logCoordinats> logCoordinats=new ArrayList<logCoordinats>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snap: snapshot.getChildren()){
                    String hareket=snap.child("hareket").getValue().toString();
                    String eylem=snap.child("eylem").getValue().toString();
                    Double lat,lon;
                    String adres;
                    String yer;
                    String zamanKey;
                    try {
                        Date zaman=format.parse(snap.getKey());
                        String dayLog=dayFormat.format(zaman);
                        if (hareket.equals("yer")){
                            if (dayLog.equals(day)){
                                lat= (Double) snap.child("lat").getValue();
                                lon= (Double) snap.child("long").getValue();
                                adres=snap.child("Adres").getValue().toString();
                                yer=snap.child("yer").getValue().toString();
                                zamanKey=snap.getKey();
                                logCoordinats log=new logCoordinats(lat,lon,adres,yer,zamanKey,"","","");
                                logCoordinats.add(log);

                            }

                        }else if(dayLog.equals(day)){
                            int index=logCoordinats.size()-1;
                            if (!logCoordinats.isEmpty()){
                                if (logCoordinats.get(index).getMotionOne().equals("")){
                                    if (hareket.equals("kotu")||hareket.equals("acil"))logCoordinats.get(index).setMotionOne("bad");
                                    else if (hareket.equals("SonDüsme"))logCoordinats.get(index).setMotionOne("fall");
                                }
                                if (logCoordinats.get(index).getMotionTwo().equals("")){
                                    if (hareket.equals("aktivite")&&(eylem.equals("Koşma")||eylem.equals("Araç içinde hareket")))logCoordinats.get(index).setMotionTwo(eylem);

                                }
                                if (logCoordinats.get(index).getMotionThree().equals("")){
                                    if (hareket.equals("rutin")&&(eylem.equals("yemek")||eylem.equals("ilac"))){
                                        logCoordinats.get(index).setMotionThree(eylem);
                                    }
                                }
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                if (logCoordinats.isEmpty()){
                        Log.d("Kooridanat sayısı:","0000000000000000000000");
                        DatabaseReference reff=database.getReference("Users").child(patientUid);
                        reff.addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Double latitude= (Double) snapshot.child("latitude").getValue();
                                Double longitude= (Double) snapshot.child("longitude").getValue();
                                String adres= (String) snapshot.child("Adres").getValue();
                                String yer= (String) snapshot.child("Yakın_Yer").getValue();
                                String zamanKey="Gün boyunca buradaydı.Yer değişikliği algılanmadı.";
                                logCoordinats log= new logCoordinats(latitude,longitude,adres,yer,zamanKey,"","","");
                                logCoordinats.add(log);
                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot snap: snapshot.getChildren()){
                                            try {
                                                Date zaman=format.parse(snap.getKey());
                                                String dayLog=dayFormat.format(zaman);
                                                String hareket=snap.child("hareket").getValue().toString();
                                                String eylem=snap.child("eylem").getValue().toString();
                                                if (dayLog.equals(day)){
                                                    int index=logCoordinats.size()-1;
                                                    if (!logCoordinats.isEmpty()){
                                                        if (logCoordinats.get(index).getMotionOne().equals("")){
                                                            if (hareket.equals("kotu")||hareket.equals("acil"))logCoordinats.get(index).setMotionOne("bad");
                                                            else if (hareket.equals("SonDüsme"))logCoordinats.get(index).setMotionOne("fall");
                                                        }
                                                        if (logCoordinats.get(index).getMotionTwo().equals("")){
                                                            if (hareket.equals("aktivite")&&(eylem.equals("Koşma")||eylem.equals("Araç içinde hareket")))logCoordinats.get(index).setMotionTwo(eylem);
                                                        }
                                                        if (logCoordinats.get(index).getMotionThree().equals("")){
                                                            if (hareket.equals("rutin")&&(eylem.equals("yemek")||eylem.equals("ilac"))){
                                                                logCoordinats.get(index).setMotionThree(eylem);
                                                            }
                                                        }
                                                    }
                                                }
                                            }catch (ParseException e ){
                                                e.printStackTrace();
                                            }
                                        }
                                        try {
                                            myCallback.onCallback(logCoordinats);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }});
                    }
                else {
                    for (int j=0;j<logCoordinats.size();j++){
                        Log.d("lollololololoo,",logCoordinats.get(j).yer);
                        SimpleDateFormat clockformat = new SimpleDateFormat("HH:mm:ss");
                        try {
                            if (j==0){
                                if (logCoordinats.size()==1){
                                    Date date=format.parse(logCoordinats.get(j).getZamanKey());
                                    String clock=clockformat.format(date);
                                    logCoordinats.get(j).setZamanKey(clock+" dan itibaren burada.");
                                }else {
                                    Date date=format.parse(logCoordinats.get(j).getZamanKey());
                                    String clock=clockformat.format(date);
                                    logCoordinats.get(j).setZamanKey(clock+ "de buraya gelmişti.");
                                }
                            }else {
                                if (!(j+1<logCoordinats.size())){
                                    Date date=format.parse(logCoordinats.get(j).getZamanKey());
                                    String clock=clockformat.format(date);
                                    logCoordinats.get(j).setZamanKey(clock+ " anından itibaren burada");
                                }else {
                                    Date dateStart=format.parse(logCoordinats.get(j).getZamanKey());
                                    Date dateFinal=format.parse(logCoordinats.get(j+1).getZamanKey());
                                    String clockStart=clockformat.format(dateStart);
                                    String clockFinal=clockformat.format(dateFinal);
                                    logCoordinats.get(j).setZamanKey(clockStart+ " - " +clockFinal+" arasında.");

                                }

                            }
                        }catch (Exception e){

                        }

                    }
                    try {
                        myCallback.onCallback(logCoordinats);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

};


