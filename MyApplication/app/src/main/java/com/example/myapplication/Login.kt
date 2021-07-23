package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.*
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.*


class Login : AppCompatActivity() {
    lateinit var tv_register:TextView;
    lateinit var et_email:TextView
    lateinit var et_password:TextView
    lateinit var bt_login:Button
    lateinit var tv_forgot:TextView
    lateinit var myRef:String
    lateinit var emailii:String
    lateinit var im_google:ImageButton
    lateinit var im_facebook:LoginButton
    lateinit var mCallbackManager:CallbackManager
    private lateinit var mAuth: FirebaseAuth
    private lateinit var auth: FirebaseAuth
    lateinit var mLocationService: VasiService
    lateinit var mHastaService:LocationService
    lateinit var mServiceIntent: Intent
    lateinit var mActivity: Activity
    lateinit var database:FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mActivity = this
        FacebookSdk.sdkInitialize(applicationContext);

        mAuth = FirebaseAuth.getInstance()
        //val user = FirebaseAuth.getInstance().currentUser

        if (mAuth.currentUser != null) {
            database= FirebaseDatabase.getInstance()
            val controlRef=database.getReference("Users")
            controlRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(mAuth.currentUser.uid)) {
                        val ref = database.getReference("Users").child(mAuth.currentUser.uid)
                        ref.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.child("kullanici_tipi").value?.equals("Hasta") == true) {
                                    val intent = Intent(this@Login, HastaActivity::class.java);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    val intent = Intent(this@Login, Analiz_activity::class.java);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                        // User is signed in (getCurrentUser() will be null if not signed in)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        }
        auth = Firebase.auth

        im_google=findViewById(R.id.im_google_login)
        et_email=findViewById(R.id.et_email)
        et_password=findViewById(R.id.et_password)
        bt_login=findViewById(R.id.bt_login)
        tv_forgot=findViewById(R.id.tv_forgot)

        mCallbackManager= CallbackManager.Factory.create()

        tv_register=findViewById(R.id.tv_register)
        tv_register.setOnClickListener {
            val intent=Intent(this, Register::class.java)
            startActivity(intent)

        }
        bt_login.setOnClickListener {
            userLogin()
        }

        im_google.setOnClickListener {
            google_login()
        }
        tv_forgot.setOnClickListener {
            val intent_forgot=Intent(this, Reset_password::class.java)
            startActivity(intent_forgot)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 50
            )

        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("facebook", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("facebook", "signInWithCredential:success")
                    val user = auth.currentUser
                    val intent=Intent(this, Socialregister::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("facebook", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    private fun google_login() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.

        val account = GoogleSignIn.getLastSignedInAccount(this)

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 90)

    }

    private fun userLogin() {
        val email=et_email.text.toString().trim()
        val password=et_password.text.toString().trim()

        if (email.isEmpty()){
            et_email.setError("E-Mail giriniz.")
            et_email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Geçerli bir mail adresi giriniz")
            et_email.requestFocus()
            return
        }
        if (password.isEmpty()){
            et_password.setError("Parola girmeniz gerekli.")
            et_password.requestFocus()
            return
        }
        if (password.length<6){
            et_password.setError("Parola uzunluğu en az 6 olmalı.")
            et_password.requestFocus()
            return
        }
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful){

                val id= mAuth!!.currentUser?.uid.toString()

                val database = FirebaseDatabase.getInstance()
                val ref=database.getReference("Users").child(id)

                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild("kullanici_tipi")) {
                            if (snapshot.child("kullanici_tipi").value.toString() == "Vasi") {
                                val intent = Intent(this@Login, Analiz_activity::class.java)
                                startActivity(intent)
                            } else if (snapshot.child("kullanici_tipi").value.toString() == "Hasta") {
                                val intent = Intent(this@Login, HastaActivity::class.java)
                                startActivity(intent)
                            }

                        } else {

                            Toast.makeText(
                                    this@Login,
                                    "Lütfen yeniden kaydolunuz.",
                                    Toast.LENGTH_LONG
                            ).show()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error!!.message)
                    }

                })

            } else {
                Toast.makeText(this, "Giriş işlemi başarısız" + it.exception, Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 90) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!

            //Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            Toast.makeText(this, "firebaseAuthWithGoogle:" + account.id, Toast.LENGTH_LONG).show()
            firebaseAuthWithGoogle(account.idToken!!)
            // Signed in successfully, show authenticated UI.
            //updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, "Hata:" + e.message, Toast.LENGTH_LONG).show()
            //updateUI(null)
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "signInWithCredential:success", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    val profileintent=Intent(this, Userprofile::class.java)
                    startActivity(profileintent)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                            this,
                            "signInWithCredential:failure" + task.exception,
                            Toast.LENGTH_LONG
                    ).show()

                    //updateUI(null)
                }
            }
    }

    override fun onStart() {
        super.onStart()

    }
    private fun requestPermissionsSafely(permissions: Array<String>,
                                         requestCode: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }
}