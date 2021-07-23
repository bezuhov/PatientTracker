package com.example.myapplication.Fragment;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.ExampleDialog;
import com.example.myapplication.LocationService;
import com.example.myapplication.Login;
import com.example.myapplication.R;
import com.example.myapplication.Util;
import com.example.myapplication.VasiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.CLIPBOARD_SERVICE;

public class profileSettings extends Fragment {

    TextView tvSettingsUid;
    ImageButton imCopyUid;
    ImageButton imAddUser;
    ImageButton imLogout;
    CardView cvFollowingPerson;
    TextView tvSettingsFollowPerson;
    ImageView imDeleteUser;
    ImageView imFollowingP;
    Button btConfirmChange;
    EditText editTextTextPersonName;
    EditText editTextTextEmailAddress;
    EditText editTextPhone3;
    EditText editTextTextPassword;
    FirebaseAuth mAuth;
    String mUser;
    FirebaseDatabase mDatabase;
    DatabaseReference myRef;
    Activity mActivity;
    VasiService mVasiService;
    Intent mServiceIntent ;
    LocationService mLocationService;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Fragment myFragment;

    public profileSettings() {
        // Required empty public constructor
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser().getUid();
        mDatabase=FirebaseDatabase.getInstance();
        myRef=mDatabase.getReference("Users");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile_settings, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshProfile);
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


        tvSettingsUid=view.findViewById(R.id.tvSettingsUid);
        imCopyUid=view.findViewById(R.id.imCopyUid);
        imAddUser=view.findViewById(R.id.imAddUser);
        imLogout=view.findViewById(R.id.imLogout);
        cvFollowingPerson=view.findViewById(R.id.cvFollowingPerson);
        tvSettingsFollowPerson=view.findViewById(R.id.tvSetiingsFollowPerson);
        imDeleteUser=view.findViewById(R.id.imDeleteUser);
        imFollowingP=view.findViewById(R.id.imFollowingP);
        btConfirmChange=view.findViewById(R.id.btConfirmChange);
        editTextTextPersonName=view.findViewById(R.id.editTextTextPersonName);
        editTextTextEmailAddress=view.findViewById(R.id.editTextTextEmailAddress);
        editTextPhone3=view.findViewById(R.id.editTextPhone3);

        tvSettingsUid.setText(mUser);
        imCopyUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(tvSettingsUid.getText());
            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String takipEdilen=snapshot.child(mUser).child("Uid").getValue().toString();
                if (snapshot.hasChild(takipEdilen)){
                    tvSettingsFollowPerson.setText(snapshot.child(takipEdilen).child("fullname").getValue().toString());
                    if (snapshot.child(takipEdilen).child("kullanici_tipi").getValue().toString().equals("Hasta")){
                        Drawable patientImg=getContext().getDrawable(R.drawable.ic_hospitalisation);
                        imFollowingP.setImageDrawable(patientImg);
                    }else {


                    }
                }else {
                    cvFollowingPerson.setVisibility(View.GONE);

                }
                editTextTextPersonName.setText(snapshot.child(mUser).child("fullname").getValue().toString());
                editTextTextEmailAddress.setText(snapshot.child(mUser).child("email").getValue().toString());
                editTextPhone3.setText(snapshot.child(mUser).child("telefon").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btConfirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isim=editTextTextPersonName.getText().toString();
                String email=editTextTextEmailAddress.getText().toString();
                String tel=editTextPhone3.getText().toString();

                if (isim.isEmpty()){
                    editTextTextPersonName.setError("İsim gerekli");
                    editTextTextPersonName.requestFocus();
                    return;
                }
                if (email.isEmpty()){
                    editTextTextPersonName.setError("E-Mail gerekli.");
                    editTextTextPersonName.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editTextTextEmailAddress.setError("Geçerli E-Mail giriniz.");
                    editTextTextEmailAddress.requestFocus();
                    return;
                }
                if (tel.isEmpty()){
                    editTextPhone3.setError("Lütfen geçerli telefon numarası giriniz");
                    editTextPhone3.requestFocus();
                    return;
                }


                myRef.child(mUser).child("fullname").setValue(editTextTextPersonName.getText());
                myRef.child(mUser).child("email").setValue(editTextTextEmailAddress.getText());
                myRef.child(mUser).child("telefon").setValue(editTextPhone3.getText());
            }
        });
        imLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity=getActivity();

                mVasiService= new VasiService();
                mLocationService=new LocationService();
                mServiceIntent= new Intent(getContext(),mVasiService.getClass());

                if (Util.INSTANCE.isMyServiceRunning(mVasiService.getClass(), mActivity)) {
                    Intent myService = new Intent(mActivity, VasiService.class);
                    getActivity().stopService(myService);
                }

                else if (Util.INSTANCE.isMyServiceRunning(mLocationService.getClass(), mActivity)) {
                    Intent myService = new Intent(mActivity, LocationService.class);
                    getActivity().stopService(myService);

                }
                mAuth.signOut();
                Intent loginScreen=new Intent(mActivity, Login.class);
                startActivity(loginScreen);
            }
        });
        imAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cvFollowingPerson.getVisibility()==View.GONE){
                    ExampleDialog exampleDialog = new ExampleDialog();
                    exampleDialog.show(getActivity().getSupportFragmentManager(), "example dialog");

                }else Toast.makeText(getContext(),"Zaten bir bağlantın var",Toast.LENGTH_SHORT).show();
            }
        });
        imDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(mUser).child("Uid").setValue("");
                cvFollowingPerson.setVisibility(View.GONE);
            }
        });

        return view;
    }



}