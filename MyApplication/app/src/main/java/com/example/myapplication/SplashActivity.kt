package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class SplashActivity : AppCompatActivity() {
    lateinit var mActivity: Activity
    lateinit var database:FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mActivity = this
        FacebookSdk.sdkInitialize(applicationContext);

        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null) {
            database= FirebaseDatabase.getInstance()
            val ref=database.getReference("Users").child(mAuth.currentUser.uid)
            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("kullanici_tipi").value?.equals("Hasta") == true){
                        val intent = Intent(this@SplashActivity, HastaActivity::class.java);
                        startActivity(intent);
                        finish();
                    }else{
                        val intent = Intent(this@SplashActivity, Analiz_activity::class.java);
                        startActivity(intent);
                        finish();
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
            // User is signed in (getCurrentUser() will be null if not signed in)
        }else{
            val intent = Intent(this@SplashActivity, Login::class.java);
            startActivity(intent);
            finish();
        }
    }
    override fun onPause() {
        super.onPause()
        finish()
    }
}