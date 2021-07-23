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

import com.example.myapplication.Fragment.AnaVasiFragment;
import com.example.myapplication.Fragment.MapsFragment;
import com.example.myapplication.Fragment.hareketler;
import com.example.myapplication.Fragment.hastaAnaliz;
import com.example.myapplication.Fragment.profileSettings;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Analiz_activity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener{
    Activity mActivity;
    VasiService mVasiService;
    Intent mServiceIntent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analiz);
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
        mVasiService= new VasiService();
        mServiceIntent= new Intent(getBaseContext(),mVasiService.getClass());

        if (!Util.INSTANCE.isMyServiceRunning(mVasiService.getClass(), mActivity)) {

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

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        String mAuth=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference ref=database.getReference("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(snapshot.child(mAuth).child("Uid").getValue().toString())){
                    viewPagerAdapter.addFragment(new AnaVasiFragment(), "Durum");
                    viewPagerAdapter.addFragment(new hareketler() , "Hareketler");
                    viewPagerAdapter.addFragment(new MapsFragment(), "harita");
                    viewPagerAdapter.addFragment(new hastaAnaliz(),"TAKİP");
                    viewPagerAdapter.addFragment(new profileSettings(), "ayarlar");
                }else {
                    viewPagerAdapter.addFragment(new profileSettings(), "ayarlar");
                }
                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);
                tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#E4F8EC"));
                tabLayout.setSelectedTabIndicatorHeight((int) (6 * getResources().getDisplayMetrics().density));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void applyTexts(String username) {
        FirebaseAuth mAuth;
        mAuth=FirebaseAuth.getInstance();
        String mUser=mAuth.getCurrentUser().getUid();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(mUser);
        ref.child("Uid").setValue(username);
        Toast.makeText(getApplicationContext(),"İşlem değişikliği eklendi.",Toast.LENGTH_SHORT).show();

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