package com.example.myapplication.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.Adaptor.hareketAdaptor;
import com.example.myapplication.Model.Aksyon;
import com.example.myapplication.Model.getEvents;
import com.example.myapplication.Model.listeBilgisi;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class hareketler extends Fragment {

    private List<Aksyon> hareketler= new ArrayList<>();
    private List<listeBilgisi> liste= new ArrayList<>();
    private RecyclerView recyclerView;
    private hareketAdaptor adaptor;
    private FirebaseAuth mAuth;
    private TextView tvHastaAdı;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Fragment myFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hareketler, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshHareketler);
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        DatabaseReference child=database.getReference("Users").child(mAuth.getCurrentUser().getUid());
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid;
                if (snapshot.child("kullanici_tipi").getValue().equals("Hasta")){
                    uid=mAuth.getCurrentUser().getUid();
                }else {
                    uid=snapshot.child("Uid").getValue().toString();
                }

                // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference myRef = database.getReference("Kayitlar").child("JkjgcdAZPfXQWPP2sPwZDUC895i1");
                Date tarih= Calendar.getInstance().getTime();
                SimpleDateFormat dayFormat=new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
                String current_time=format.format(tarih);
                String current_day=dayFormat.format(tarih);
                String current_timeFinal=format.format(tarih);
                getEvents events=new getEvents(getContext());
                if (!(liste.size() ==0)){
                    liste.clear();
                }
                events.getList(uid,"hareketler", new getEvents.EventLogsCallback() {
                    @Override
                    public void onCallback(ArrayList<Aksyon> value) throws ParseException {
                        hareketler=value;
                        int size=hareketler.size()-1;
                        int hareketSayisi=0;
                        int alert=0;
                        for (int i=size;i>=0;i--){
                            String zaman=hareketler.get(i).getZaman();
                            String eylem=hareketler.get(i).getEylem();
                            String yer=hareketler.get(i).getYer();

                            if (i==size){
                                for (int k=0;k<hareketler.size();k++){
                                    if (hareketler.get(k).getHareket().equals("acil")||hareketler.get(k).equals("kotu")){
                                        alert=1;
                                        for (int l=k;l<hareketler.size();l++){
                                            if (hareketler.get(l).getHareket().equals("iyi")||(hareketler.get(l).getHareket().equals("aktivite")&&!hareketler.get(l).getEylem().equals("hareketsiz (Bekleme)"))){
                                                alert=0;
                                            }
                                        }
                                    }
                                    else if (hareketler.get(k).getHareket().equals("SonDüsme")){
                                        alert=2;
                                        for (int l=k;l<hareketler.size();l++){
                                            if (hareketler.get(l).getHareket().equals("iyi")||(hareketler.get(l).getHareket().equals("aktivite")&&!hareketler.get(l).getEylem().equals("hareketsiz (Bekleme)"))){
                                                alert=0;
                                            }
                                        }
                                    }
                                }
                                if (hareketler.get(i).getHareket().equals("aktivite")){
                                    hareketSayisi++;
                                    try {
                                        String ekAciklama="";

                                        String zamanBilgisi=zamanFarkı(current_time,zaman,format,1);
                                        String aciklama=hareketler.get(i).getYer()+" yakınlarında "+hareketler.get(i).getEylem()+" durumunda."+ekAciklama+eskiAciklama(hareketler,size,"aktivite",current_day,dayFormat,current_time,zaman,format);
                                        String imEylem="";
                                        imEylem=hareketler.get(i).getEylem();
                                        if (alert==1)imEylem="acil";
                                        else if (alert==2)imEylem="SonDüsme";

                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };
                                if (hareketler.get(i).getHareket().equals("acil")){

                                    try {

                                        String zamanBilgisi=zamanFarkı(current_time,zaman,format,1);
                                        String aciklama=eylemBulma(hareketler,size,"aktivite",current_time,format)+" acil bir durum içinde  olduğunu belirtti.Henüz hareketinde bir iyileşme görülmedi.";

                                        String imEylem=hareketler.get(i).getHareket();
                                        if (hareketler.get(i-1).getHareket().equals("acil")){
                                            aciklama="Hasta arka arkaya acil bildirim yolladı.Henüz hareketinde bir iyileşme görülmedi.En son "+eylem +" durumunda ve "+yer+" yakınlarındaydı." ;
                                        }
                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };
                                if (hareketler.get(i).getHareket().equals("yer")){
                                    try {
                                        String zamanBilgisi=zamanFarkı(current_time,zaman,format,0);
                                        String aciklama=hareketler.get(i).getYer()+" yakınlarına hareket etti.Şuan "+hareketler.get(i).getEylem()+" durumunda.";
                                        String imEylem=hareketler.get(i).getHareket();
                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };
                                if (hareketler.get(i).getHareket().equals("SonDüsme")){
                                    try {
                                        String zamanBilgisi=zamanFarkı(current_time,zaman,format,1);
                                        String aciklama=eylemBulma(hareketler,size,"aktivite",current_time,format)+" düştü.";
                                        String imEylem=hareketler.get(i).getHareket();
                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };

                            }
                            else {
                                if (hareketler.get(i).getHareket().equals("aktivite")){
                                    hareketSayisi++;
                                    try {
                                        String zamanBilgisi=zamanFarkı(current_time,zaman,format,0);
                                        String aciklama=hareketler.get(i).getEylem()+" durumuna geçti.";
                                        if (i>0){
                                            if (hareketler.get(i).getEylem().equals("hareketsiz (Bekleme)")){

                                                if ((hareketler.get(i-1).getHareket().equals("acil")||hareketler.get(i-1).getHareket().equals("SonDüsme"))){
                                                    aciklama="Hala önemli bir durum olabilir çünkü kişi acil olaydan sonra harekete geçmedi.";
                                                }
                                                else if (hareketler.get(i-1).getHareket().equals("aktivite")){
                                                    aciklama="Hasta "+zamanFarkı(hareketler.get(i).getZaman(),hareketler.get(i-1).getZaman(),format,0)+" başladığı "+hareketler.get(i-1).getEylem()+" durumunu sonlandırdı, şuan aynı yerde dinleniyor.";
                                                    if (hareketler.get(i-1).getEylem().equals("Yürüme")){
                                                        aciklama="Yürümeyi bıraktı,dinleniyor. Durumu iyi.";
                                                    }
                                                    else if  (hareketler.get(i-1).getEylem().equals("Araç içinde hareket")){
                                                        aciklama="Artık araç ile ilerlemiyor.Hareketsiz bir şekilde "+yer+" tarafında";
                                                    }
                                                    else if  (hareketler.get(i-1).getEylem().equals("Koşma")){
                                                        aciklama="Koşmayı bıraktı,hareketsiz durumda. Bu esnada herhangi bir düşme veya benzeri bir problem algılanmadı.";
                                                    }

                                                }
                                                else if (hareketler.get(i-1).getHareket().equals("yer")){
                                                    aciklama="Yakın zamanda hareket ettiği yerde artık hareketsiz durumda.";
                                                }

                                            }
                                            if (hareketler.get(i).getEylem().equals("Yürüme")){
                                                if ((hareketler.get(i-1).getHareket().equals("acil")||hareketler.get(i-1).getHareket().equals("SonDüsme"))){
                                                    aciklama="Önemli bir problem yok gibi. Şuanda yürüyor.";
                                                }
                                                else if (hareketler.get(i-1).getHareket().equals("aktivite")){
                                                    aciklama="Hasta "+zamanFarkı(hareketler.get(i).getZaman(),hareketler.get(i-1).getZaman(),format,0)+" başladığı "+hareketler.get(i-1).getEylem()+" durumunu sonlandırdı, şuan aynı yerde yürüyor.";
                                                    if (hareketler.get(i-1).getEylem().equals("hareketsiz (Bekleme)")){
                                                        if (hareketler.get(i+1).getHareket().equals("yer")){
                                                            aciklama="Tekrar harekete geçti. Yürümeye başladı. ";
                                                        }
                                                        else aciklama="Aynı yerde harekete geçti,bulunduğu ortamda yürüyor.";
                                                    }
                                                    else if  (hareketler.get(i-1).getEylem().equals("Araç içinde hareket")){
                                                        aciklama="Araçtan indi ."+yer+" tarafında yürüyüş yapıyor.";
                                                    }
                                                    else if  (hareketler.get(i-1).getEylem().equals("Koşma")){
                                                        aciklama="Koşmayı bıraktı,yürüyor. Bu esnada herhangi bir düşme veya benzeri bir problem algılanmadı.";
                                                    }

                                                }
                                                else if (hareketler.get(i-1).getHareket().equals("yer")){
                                                    aciklama="Aynı yerde yürüyüş yapıyor.";
                                                }


                                            }

                                            else if (!hareketler.get(i).getEylem().equals("hareketsiz (Bekleme)")&&(hareketler.get(i-1).getHareket().equals("acil")||hareketler.get(i-1).getHareket().equals("SonDüsme"))){
                                                aciklama="Önemli bir sorun yok gibi.Hasta tekrar harekete geçti.Şuan da "+hareketler.get(i).getEylem()+" durumuna geçti.";
                                            }


                                        }

                                        String imEylem=hareketler.get(i).getEylem();
                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                };
                                if (hareketler.get(i).getHareket().equals("acil")){
                                    try {
                                        String zamanBilgisi=zamanFarkı(current_time,zaman,format,0);
                                        String aciklama="Acil durum çağrısında bulunmuştu.";
                                        String imEylem=hareketler.get(i).getHareket();
                                        if (i>0){
                                            if (hareketler.get(i-1).getHareket().equals("acil")||hareketler.get(i-1).getHareket().equals("SonDüsme")){
                                                aciklama="Önemli bir durum olabilir,hastada arka arkaya kritik olabilecek durumlar algılandı.";
                                            }
                                            else if (hareketler.get(i-1).getHareket().equals("aktivite")){
                                                aciklama="Hasta "+yer+" mevkinde "+hareketler.get(i-1).getEylem()+" durumundaydı.Bu sırada size acil bir durumda olduğunu belirtti.";
                                            }

                                        }
                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };
                                if (hareketler.get(i).getHareket().equals("yer")){
                                    try {
                                        String zamanBilgisi=zamanFarkı(current_time,zaman,format,0);
                                        String aciklama=hareketler.get(i).getYer()+" yakınlarına hareket etti.";
                                        String imEylem=hareketler.get(i).getHareket();
                                        if (i>0){
                                            if (hareketler.get(i-1).getHareket().equals("aktivite")&&!hareketler.get(i-1).getEylem().equals("hareketsiz (Bekleme)")){
                                                aciklama="Hasta "+eylem+" durumunda konum değiştirdi.Şuan da "+yer+" konumunda.";
                                            }
                                        }

                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };
                                if (hareketler.get(i).getHareket().equals("SonDüsme")){
                                    try {
                                        String zamanBilgisi=zamanFarkı(current_time,zaman,format,0);
                                        String aciklama=hareketler.get(i).getEylem()+" durumunda iken düştü.";
                                        String imEylem=hareketler.get(i).getHareket();
                                        if (i>0){
                                            if (hareketler.get(i-1).getHareket().equals("acil")||hareketler.get(i-1).getHareket().equals("SonDüsme")){
                                                aciklama="Hastada kritik bir durum olabilir.Ardarda acil bildirimler algılandı ve durumunda iyileşme görülmedi."+yer+" tarafında ve düşmüş durumda.";
                                            }
                                        }
                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };

                            }
                            if (hareketler.get(i).getHareket().equals("iyi")){
                                String zamanBilgisi=zamanFarkı(current_time,zaman,format,0);
                                String aciklama="Hasta iyi hissettiğini,bir problem olmadığını belirtti.";
                                String imEylem=hareketler.get(i).getHareket();
                                listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                liste.add(listeBilgisi);
                            }
                            else if (hareketler.get(i).getHareket().equals("kotu")){
                                String zamanBilgisi=zamanFarkı(current_time,zaman,format,0);
                                String aciklama="Hasta uygulama içerisinden size kötü durumda olduğunu bildirdi.";
                                String imEylem=hareketler.get(i).getHareket();
                                listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                liste.add(listeBilgisi);

                            }
                            else if (hareketler.get(i).getHareket().equals("rutin")){
                                String aciklama="";
                                if (eylem.equals("yemek")){
                                    aciklama="Yemek yedi.";
                                }
                                else if (eylem.equals("ilac")){
                                    aciklama="İlaç aldı.";

                                }
                                else if (eylem.equals("su")){
                                    aciklama="Su içti.";
                                }
                                else if (eylem.equals("tuvalet")){
                                    aciklama="Tuvalete gitti.";
                                }
                                else if (eylem.equals("dus")){
                                    aciklama="Hasta banyo yaptığını bildirdi.";

                                }
                                String zamanBilgisi=zamanFarkı(current_time,zaman,format,0);
                                String imEylem=hareketler.get(i).getHareket();
                                listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                liste.add(listeBilgisi);

                            }


                        }

                        //Eğer gün içinde hiç hareket algılanmamış ise bilgi verilir
                        if (hareketSayisi==0){
                            String zamanBilgisi="Uyarı:";
                            String aciklama="Gün içinde hastada yeterince hareket tespit edemedik.";
                            String imEylem="uyarı";
                            listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                            liste.add(listeBilgisi);
                            Collections.rotate(liste,1);

                        }
                        adaptor = new hareketAdaptor(getContext(), liste);
                        recyclerView.setAdapter(adaptor);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }

    private String eskiAciklama(List<Aksyon> hareketler, int size, String aktivite, String current_day, SimpleDateFormat dayformat,String currentTime,String eventTime,SimpleDateFormat dateFormat) throws ParseException {
        String cevapN="Bugün içerisinde ";
        String cevap="";
        String cevapY="";
        String cevapYp="";
        String cevapD="";
        int aktKontrol=0;
        int acil=0;
        int dusme=0;
        int yer=0;



        for (int k=0;k<hareketler.size();k++){
            Date time=dateFormat.parse(hareketler.get(k).getZaman());
            String day=dayformat.format(time);
            if (day.equals(current_day)){
                if (hareketler.get(k).getHareket().equals("acil")||hareketler.get(k).getHareket().equals("kotu")){
                    acil++;
                }
                if (hareketler.get(k).getHareket().equals("SonDüsme")){
                    dusme++;
                }
                if (hareketler.get(k).getHareket().equals("yer")){
                    if (yer==0){
                        cevapYp=cevapYp + "Şuanki konumuna "+zamanFarkı(currentTime,hareketler.get(k).getZaman(),dateFormat,0)+" ulaştı.";
                    }
                    if (yer==1){
                        cevapYp=cevapYp+"Bundan önce "+hareketler.get(k).getYer()+" tarafındaydı.";
                    }
                    yer++;
                }
                if (hareketler.get(k).getHareket().equals("hareket")){
                    aktKontrol++;
                }

            }

        }

        if (acil>0||dusme>0||yer>0){
            if (acil>0)cevapN=cevapN+acil+" kez acil durum, ";
            if (dusme>0)cevapN=cevapN+dusme+" kez düşme, ";
            if (yer>0)cevapN=cevapN+yer+" kez konum değişikliği ";
            cevapN=cevapN+" yaşandı.";

        }else {
            cevapN=cevapN+" herhangi acil durum,düşme,konum değişikliğine rastlanmadı.";
        }

        cevap=cevap+cevapN+cevapY+cevapD+cevapYp;

        return cevap;
    }

    private String eylemBulma(List<Aksyon> hareketler, int size, String aktivite, String current_time, SimpleDateFormat format) throws ParseException {
        boolean yerKontrol=false;
        String yerMetin="";
        int i =size;
        String yer=hareketler.get(i).getYer();
        while (!hareketler.get(i).getHareket().equals(aktivite)&&i>1){
            if (hareketler.get(i).getHareket().equals("yer")&& !yerKontrol){
                yerMetin=hareketler.get(i).getYer()+" konumuna geçtiği görüldü.Şuan hala burada ve ";
                yerKontrol=true;
            }
            i--;
        }
        if (yerMetin.equals("")){
            yerMetin="bu süreçte "+yer+" yakınlarında kalmaya devam ederken ";
        }
        String zaman=hareketler.get(i).getZaman();
        String eylem=hareketler.get(i).getEylem();
        String eylemzamanFarkı=zamanFarkı(current_time,zaman,format,0);
        return "Hasta "+eylemzamanFarkı+" "+eylem+" durumuna geçti ve "+yerMetin;
    }

    private String zamanFarkı(String current_time, String zaman, SimpleDateFormat format, int i) throws ParseException {

        String cevap="";

        long zamanFarkı=format.parse(current_time).getTime()-format.parse(zaman).getTime();
        int differenceMinutes= (int) (zamanFarkı / (60 * 1000) % 60);
        int diffSeconds = (int) (zamanFarkı / 1000 % 60);
        int diffHours = (int) (zamanFarkı / (60 * 60 * 1000) % 24);
        int diffDays = (int) (zamanFarkı / (24 * 60 * 60 * 1000));
        if (i==1){
            if (diffDays>0){
                cevap=diffDays+" gün "+diffHours+" saat önce";
            }
            else if (diffHours>0){
                cevap="Şuan da son "+diffHours+" saattir ";
            }
            else if (differenceMinutes>0){
                cevap="Şuan da son "+differenceMinutes+" dakikadır";
            }
            else {
                cevap="Şuan da  son "+diffSeconds+" saniyedir ";
            }
        }
        else {
            if (diffDays>0){
                cevap=diffDays+" gün "+diffHours+" saat önce";
            }
            else if (diffHours>0){
                cevap=+diffHours+" saat "+differenceMinutes+" dakika önce ";
            }
            else if (differenceMinutes>0){
                cevap=differenceMinutes+" dakika önce";
            }
            else {
                cevap=diffSeconds+" saniye önce ";
            }


        }

       return cevap;
    }

}