package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.Fragment.HastaMainFragment;
import com.example.myapplication.Fragment.hareketler;
import com.example.myapplication.Fragment.hastaAnaliz;
import com.example.myapplication.Fragment.hastaEvents;
import com.example.myapplication.Fragment.profileSettings;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HastaActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener{
    Activity mActivity;
    LocationService mHastaService;
    Intent mServiceIntent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasta);
        mActivity=this;

        if (!Util.INSTANCE.isLocationEnabledOrNot(this)) {
            Util.INSTANCE.showAlertLocation(
                    mActivity,
                    getString(R.string.gps_enable),
                    getString(R.string.please_turn_on_gps),
                    getString(
                            R.string.ok
                    )
            );
        }

        mHastaService= new LocationService();

        mServiceIntent= new Intent(getBaseContext(),mHastaService.getClass());
        if (!Util.INSTANCE.isMyServiceRunning(mHastaService.getClass(), mActivity)) {
            startService(mServiceIntent);
            Toast.makeText(
                    mActivity,
                    getString(R.string.service_start_successfully),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    mActivity,
                    getString(R.string.service_already_running),
                    Toast.LENGTH_SHORT
            ).show();
        }


        final TabLayout tabLayout = findViewById(R.id.tab_layoutHasta);
        final ViewPager viewPager = findViewById(R.id.view_pagerHasta);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new HastaMainFragment(), "Ana Sayfa");
        viewPagerAdapter.addFragment(new hastaAnaliz(),"TAKİP");
        viewPagerAdapter.addFragment(new hareketler() , "Hareketler");
        viewPagerAdapter.addFragment(new hastaEvents(),"Detaylar");
        viewPagerAdapter.addFragment(new profileSettings(),"Ayarlar");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#E4F8EC"));
        tabLayout.setSelectedTabIndicatorHeight((int) (6 * getResources().getDisplayMetrics().density));

        //Hastanın uygulama koduna ulaşma
    }

    @Override
    public void applyTexts(String username) {

        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("Users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tip=snapshot.child(uid).child("kullanici_tipi").getValue().toString();
                if (snapshot.hasChild(username)){
                    if (snapshot.child(username).child("kullanici_tipi").getValue().toString().equals(tip)){
                        Toast.makeText(getApplicationContext(),"Yanlış kullanıcı Id si.Kişi mevcut değil.",Toast.LENGTH_SHORT).show();
                    }else {
                        myRef.child(uid).child("Uid").setValue(username);

                    }

                }else Toast.makeText(getApplicationContext(),"Yanlış kullanıcı Id si.Kişi mevcut değil.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        // Ctrl + O

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


}