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
    private TextView tvHastaAdÄ±;
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
                                    else if (hareketler.get(k).getHareket().equals("SonDÃ¼sme")){
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

                                        String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,1);
                                        String aciklama=hareketler.get(i).getYer()+" yakÄ±nlarÄ±nda "+hareketler.get(i).getEylem()+" durumunda."+ekAciklama+eskiAciklama(hareketler,size,"aktivite",current_day,dayFormat,current_time,zaman,format);
                                        String imEylem="";
                                        imEylem=hareketler.get(i).getEylem();
                                        if (alert==1)imEylem="acil";
                                        else if (alert==2)imEylem="SonDÃ¼sme";

                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };
                                if (hareketler.get(i).getHareket().equals("acil")){

                                    try {

                                        String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,1);
                                        String aciklama=eylemBulma(hareketler,size,"aktivite",current_time,format)+" acil bir durum iÃ§inde  olduÄunu belirtti.HenÃ¼z hareketinde bir iyileÅme gÃ¶rÃ¼lmedi.";

                                        String imEylem=hareketler.get(i).getHareket();
                                        if (hareketler.get(i-1).getHareket().equals("acil")){
                                            aciklama="Hasta arka arkaya acil bildirim yolladÄ±.HenÃ¼z hareketinde bir iyileÅme gÃ¶rÃ¼lmedi.En son "+eylem +" durumunda ve "+yer+" yakÄ±nlarÄ±ndaydÄ±." ;
                                        }
                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };
                                if (hareketler.get(i).getHareket().equals("yer")){
                                    try {
                                        String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,0);
                                        String aciklama=hareketler.get(i).getYer()+" yakÄ±nlarÄ±na hareket etti.Åuan "+hareketler.get(i).getEylem()+" durumunda.";
                                        String imEylem=hareketler.get(i).getHareket();
                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };
                                if (hareketler.get(i).getHareket().equals("SonDÃ¼sme")){
                                    try {
                                        String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,1);
                                        String aciklama=eylemBulma(hareketler,size,"aktivite",current_time,format)+" dÃ¼ÅtÃ¼.";
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
                                        String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,0);
                                        String aciklama=hareketler.get(i).getEylem()+" durumuna geÃ§ti.";
                                        if (i>0){
                                            if (hareketler.get(i).getEylem().equals("hareketsiz (Bekleme)")){

                                                if ((hareketler.get(i-1).getHareket().equals("acil")||hareketler.get(i-1).getHareket().equals("SonDÃ¼sme"))){
                                                    aciklama="Hala Ã¶nemli bir durum olabilir Ã§Ã¼nkÃ¼ kiÅi acil olaydan sonra harekete geÃ§medi.";
                                                }
                                                else if (hareketler.get(i-1).getHareket().equals("aktivite")){
                                                    aciklama="Hasta "+zamanFarkÄ±(hareketler.get(i).getZaman(),hareketler.get(i-1).getZaman(),format,0)+" baÅladÄ±ÄÄ± "+hareketler.get(i-1).getEylem()+" durumunu sonlandÄ±rdÄ±, Åuan aynÄ± yerde dinleniyor.";
                                                    if (hareketler.get(i-1).getEylem().equals("YÃ¼rÃ¼me")){
                                                        aciklama="YÃ¼rÃ¼meyi bÄ±raktÄ±,dinleniyor. Durumu iyi.";
                                                    }
                                                    else if  (hareketler.get(i-1).getEylem().equals("AraÃ§ iÃ§inde hareket")){
                                                        aciklama="ArtÄ±k araÃ§ ile ilerlemiyor.Hareketsiz bir Åekilde "+yer+" tarafÄ±nda";
                                                    }
                                                    else if  (hareketler.get(i-1).getEylem().equals("KoÅma")){
                                                        aciklama="KoÅmayÄ± bÄ±raktÄ±,hareketsiz durumda. Bu esnada herhangi bir dÃ¼Åme veya benzeri bir problem algÄ±lanmadÄ±.";
                                                    }

                                                }
                                                else if (hareketler.get(i-1).getHareket().equals("yer")){
                                                    aciklama="YakÄ±n zamanda hareket ettiÄi yerde artÄ±k hareketsiz durumda.";
                                                }

                                            }
                                            if (hareketler.get(i).getEylem().equals("YÃ¼rÃ¼me")){
                                                if ((hareketler.get(i-1).getHareket().equals("acil")||hareketler.get(i-1).getHareket().equals("SonDÃ¼sme"))){
                                                    aciklama="Ãnemli bir problem yok gibi. Åuanda yÃ¼rÃ¼yor.";
                                                }
                                                else if (hareketler.get(i-1).getHareket().equals("aktivite")){
                                                    aciklama="Hasta "+zamanFarkÄ±(hareketler.get(i).getZaman(),hareketler.get(i-1).getZaman(),format,0)+" baÅladÄ±ÄÄ± "+hareketler.get(i-1).getEylem()+" durumunu sonlandÄ±rdÄ±, Åuan aynÄ± yerde yÃ¼rÃ¼yor.";
                                                    if (hareketler.get(i-1).getEylem().equals("hareketsiz (Bekleme)")){
                                                        if (hareketler.get(i+1).getHareket().equals("yer")){
                                                            aciklama="Tekrar harekete geÃ§ti. YÃ¼rÃ¼meye baÅladÄ±. ";
                                                        }
                                                        else aciklama="AynÄ± yerde harekete geÃ§ti,bulunduÄu ortamda yÃ¼rÃ¼yor.";
                                                    }
                                                    else if  (hareketler.get(i-1).getEylem().equals("AraÃ§ iÃ§inde hareket")){
                                                        aciklama="AraÃ§tan indi ."+yer+" tarafÄ±nda yÃ¼rÃ¼yÃ¼Å yapÄ±yor.";
                                                    }
                                                    else if  (hareketler.get(i-1).getEylem().equals("KoÅma")){
                                                        aciklama="KoÅmayÄ± bÄ±raktÄ±,yÃ¼rÃ¼yor. Bu esnada herhangi bir dÃ¼Åme veya benzeri bir problem algÄ±lanmadÄ±.";
                                                    }

                                                }
                                                else if (hareketler.get(i-1).getHareket().equals("yer")){
                                                    aciklama="AynÄ± yerde yÃ¼rÃ¼yÃ¼Å yapÄ±yor.";
                                                }


                                            }

                                            else if (!hareketler.get(i).getEylem().equals("hareketsiz (Bekleme)")&&(hareketler.get(i-1).getHareket().equals("acil")||hareketler.get(i-1).getHareket().equals("SonDÃ¼sme"))){
                                                aciklama="Ãnemli bir sorun yok gibi.Hasta tekrar harekete geÃ§ti.Åuan da "+hareketler.get(i).getEylem()+" durumuna geÃ§ti.";
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
                                        String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,0);
                                        String aciklama="Acil durum Ã§aÄrÄ±sÄ±nda bulunmuÅtu.";
                                        String imEylem=hareketler.get(i).getHareket();
                                        if (i>0){
                                            if (hareketler.get(i-1).getHareket().equals("acil")||hareketler.get(i-1).getHareket().equals("SonDÃ¼sme")){
                                                aciklama="Ãnemli bir durum olabilir,hastada arka arkaya kritik olabilecek durumlar algÄ±landÄ±.";
                                            }
                                            else if (hareketler.get(i-1).getHareket().equals("aktivite")){
                                                aciklama="Hasta "+yer+" mevkinde "+hareketler.get(i-1).getEylem()+" durumundaydÄ±.Bu sÄ±rada size acil bir durumda olduÄunu belirtti.";
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
                                        String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,0);
                                        String aciklama=hareketler.get(i).getYer()+" yakÄ±nlarÄ±na hareket etti.";
                                        String imEylem=hareketler.get(i).getHareket();
                                        if (i>0){
                                            if (hareketler.get(i-1).getHareket().equals("aktivite")&&!hareketler.get(i-1).getEylem().equals("hareketsiz (Bekleme)")){
                                                aciklama="Hasta "+eylem+" durumunda konum deÄiÅtirdi.Åuan da "+yer+" konumunda.";
                                            }
                                        }

                                        listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                        liste.add(listeBilgisi);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                };
                                if (hareketler.get(i).getHareket().equals("SonDÃ¼sme")){
                                    try {
                                        String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,0);
                                        String aciklama=hareketler.get(i).getEylem()+" durumunda iken dÃ¼ÅtÃ¼.";
                                        String imEylem=hareketler.get(i).getHareket();
                                        if (i>0){
                                            if (hareketler.get(i-1).getHareket().equals("acil")||hareketler.get(i-1).getHareket().equals("SonDÃ¼sme")){
                                                aciklama="Hastada kritik bir durum olabilir.Ardarda acil bildirimler algÄ±landÄ± ve durumunda iyileÅme gÃ¶rÃ¼lmedi."+yer+" tarafÄ±nda ve dÃ¼ÅmÃ¼Å durumda.";
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
                                String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,0);
                                String aciklama="Hasta iyi hissettiÄini,bir problem olmadÄ±ÄÄ±nÄ± belirtti.";
                                String imEylem=hareketler.get(i).getHareket();
                                listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                liste.add(listeBilgisi);
                            }
                            else if (hareketler.get(i).getHareket().equals("kotu")){
                                String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,0);
                                String aciklama="Hasta uygulama iÃ§erisinden size kÃ¶tÃ¼ durumda olduÄunu bildirdi.";
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
                                    aciklama="Ä°laÃ§ aldÄ±.";

                                }
                                else if (eylem.equals("su")){
                                    aciklama="Su iÃ§ti.";
                                }
                                else if (eylem.equals("tuvalet")){
                                    aciklama="Tuvalete gitti.";
                                }
                                else if (eylem.equals("dus")){
                                    aciklama="Hasta banyo yaptÄ±ÄÄ±nÄ± bildirdi.";

                                }
                                String zamanBilgisi=zamanFarkÄ±(current_time,zaman,format,0);
                                String imEylem=hareketler.get(i).getHareket();
                                listeBilgisi listeBilgisi=new listeBilgisi(zamanBilgisi,aciklama,imEylem);
                                liste.add(listeBilgisi);

                            }


                        }

                        //EÄer gÃ¼n iÃ§inde hiÃ§ hareket algÄ±lanmamÄ±Å ise bilgi verilir
                        if (hareketSayisi==0){
                            String zamanBilgisi="UyarÄ±:";
                            String aciklama="GÃ¼n iÃ§inde hastada yeterince hareket tespit edemedik.";
                            String imEylem="uyarÄ±";
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
        String cevapN="BugÃ¼n iÃ§erisinde ";
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
                if (hareketler.get(k).getHareket().equals("SonDÃ¼sme")){
                    dusme++;
                }
                if (hareketler.get(k).getHareket().equals("yer")){
                    if (yer==0){
                        cevapYp=cevapYp + "Åuanki konumuna "+zamanFarkÄ±(currentTime,hareketler.get(k).getZaman(),dateFormat,0)+" ulaÅtÄ±.";
                    }
                    if (yer==1){
                        cevapYp=cevapYp+"Bundan Ã¶nce "+hareketler.get(k).getYer()+" tarafÄ±ndaydÄ±.";
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
            if (dusme>0)cevapN=cevapN+dusme+" kez dÃ¼Åme, ";
            if (yer>0)cevapN=cevapN+yer+" kez konum deÄiÅikliÄi ";
            cevapN=cevapN+" yaÅandÄ±.";

        }else {
            cevapN=cevapN+" herhangi acil durum,dÃ¼Åme,konum deÄiÅikliÄine rastlanmadÄ±.";
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
                yerMetin=hareketler.get(i).getYer()+" konumuna geÃ§tiÄi gÃ¶rÃ¼ldÃ¼.Åuan hala burada ve ";
                yerKontrol=true;
            }
            i--;
        }
        if (yerMetin.equals("")){
            yerMetin="bu sÃ¼reÃ§te "+yer+" yakÄ±nlarÄ±nda kalmaya devam ederken ";
        }
        String zaman=hareketler.get(i).getZaman();
        String eylem=hareketler.get(i).getEylem();
        String eylemzamanFarkÄ±=zamanFarkÄ±(current_time,zaman,format,0);
        return "Hasta "+eylemzamanFarkÄ±+" "+eylem+" durumuna geÃ§ti ve "+yerMetin;
    }

    private String zamanFarkÄ±(String current_time, String zaman, SimpleDateFormat format, int i) throws ParseException {

        String cevap="";

        long zamanFarkÄ±=format.parse(current_time).getTime()-format.parse(zaman).getTime();
        int differenceMinutes= (int) (zamanFarkÄ± / (60 * 1000) % 60);
        int diffSeconds = (int) (zamanFarkÄ± / 1000 % 60);
        int diffHours = (int) (zamanFarkÄ± / (60 * 60 * 1000) % 24);
        int diffDays = (int) (zamanFarkÄ± / (24 * 60 * 60 * 1000));
        if (i==1){
            if (diffDays>0){
                cevap=diffDays+" gÃ¼n "+diffHours+" saat Ã¶nce";
            }
            else if (diffHours>0){
                cevap="Åuan da son "+diffHours+" saattir ";
            }
            else if (differenceMinutes>0){
                cevap="Åuan da son "+differenceMinutes+" dakikadÄ±r";
            }
            else {
                cevap="Åuan da  son "+diffSeconds+" saniyedir ";
            }
        }
        else {
            if (diffDays>0){
                cevap=diffDays+" gÃ¼n "+diffHours+" saat Ã¶nce";
            }
            else if (diffHours>0){
                cevap=+diffHours+" saat "+differenceMinutes+" dakika Ã¶nce ";
            }
            else if (differenceMinutes>0){
                cevap=differenceMinutes+" dakika Ã¶nce";
            }
            else {
                cevap=diffSeconds+" saniye Ã¶nce ";
            }


        }

       return cevap;
    }

}