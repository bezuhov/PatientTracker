package com.example.myapplication.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class hastaEvents extends Fragment {

    private CheckBox  cbPillSabah;
    private CheckBox  cb_Pillogle;
    private CheckBox  cbPillaksam;
    private CheckBox  cb_Pillderece;
    private CheckBox  cbToiletroutine;
    private CheckBox  cbWaterroutine;
    private CheckBox  cbYemekSabah;
    private CheckBox  cbYemekOgle;
    private CheckBox  cbYemekAksam;
    private CheckBox  cbFoodroutine;
    private Button btEventschange;
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Fragment myFragment;

    public hastaEvents() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_hasta_events, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshEvent);
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


        cbPillSabah=view.findViewById(R.id.cbPillSabah);
        cb_Pillogle=view.findViewById(R.id.cb_Pillogle);
        cbPillaksam=view.findViewById(R.id.cbPillaksam);
        cb_Pillderece=view.findViewById(R.id.cb_Pillderece);

        cbYemekSabah=view.findViewById(R.id.cbYemekSabah);
        cbYemekOgle=view.findViewById(R.id.cbYemekOgle);
        cbYemekAksam=view.findViewById(R.id.cbYemekAksam);
        cbFoodroutine=view.findViewById(R.id.cbFoodroutine);
        btEventschange=view.findViewById(R.id.btEventschange);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("Kayitlar").child(this.patientUid);
        DatabaseReference myRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid()).child("rutinAyar");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean ilacSabah= (Boolean) snapshot.child("ilac").child("sabah").getValue();
                Boolean ilacOgle= (Boolean) snapshot.child("ilac").child("ogle").getValue();
                Boolean ilacAksam= (Boolean) snapshot.child("ilac").child("aksam").getValue();
                Boolean ilacLev= (Boolean) snapshot.child("ilac").child("level").getValue();
                Boolean tuvaletLev= (Boolean) snapshot.child("tuvalet").child("level").getValue();
                Boolean suLev= (Boolean) snapshot.child("su").child("level").getValue();
                Boolean yemekSabah= (Boolean) snapshot.child("yemek").child("sabah").getValue();
                Boolean yemekOgle= (Boolean) snapshot.child("yemek").child("ogle").getValue();
                Boolean yemekAksam= (Boolean) snapshot.child("yemek").child("aksam").getValue();
                Boolean yemekLev= (Boolean) snapshot.child("yemek").child("level").getValue();

                cbPillSabah.setChecked(ilacSabah);
                cb_Pillogle.setChecked(ilacOgle);
                cbPillaksam.setChecked(ilacAksam);
                cb_Pillderece.setChecked(ilacLev);


                cbYemekSabah.setChecked(yemekSabah);
                cbYemekOgle.setChecked(yemekOgle);
                cbYemekAksam.setChecked(yemekAksam);
                cbFoodroutine.setChecked(yemekLev);
                btEventschange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myRef.child("ilac").child("sabah").setValue(cbPillSabah.isChecked());
                        myRef.child("ilac").child("ogle").setValue(cb_Pillogle.isChecked());
                        myRef.child("ilac").child("aksam").setValue(cbPillaksam.isChecked());
                        myRef.child("ilac").child("level").setValue(cb_Pillderece.isChecked());
                        myRef.child("yemek").child("sabah").setValue(cbYemekSabah.isChecked());
                        myRef.child("yemek").child("ogle").setValue(cbYemekOgle.isChecked());
                        myRef.child("yemek").child("aksam").setValue(cbYemekAksam.isChecked());
                        myRef.child("yemek").child("level").setValue(cbFoodroutine.isChecked());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }
}