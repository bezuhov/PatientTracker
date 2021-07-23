package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Model.rutinAyar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class Register : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    lateinit var et_name:TextView
    lateinit var et_regemail:TextView
    lateinit var et_regpassword:TextView
    lateinit var bt_registerfire:Button
    lateinit var button_giris:TextView
    lateinit var radio_hasta:RadioButton
    lateinit var radio_vasi:RadioButton
    lateinit var et_Uid:TextView
    lateinit var et_tel:TextView
    lateinit var im_facebook:ImageView
    lateinit var im_google:ImageView
    lateinit var im_back:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        et_Uid=findViewById(R.id.et_Uid)
        radio_hasta=findViewById(R.id.radio_hasta)
        radio_vasi=findViewById(R.id.radio_vasi)
        et_name=findViewById(R.id.et_name)
        et_regemail=findViewById(R.id.et_regemail)
        et_regpassword=findViewById(R.id.et_regpassword)
        button_giris=findViewById(R.id.textView5)
        et_tel=findViewById(R.id.editTextPhone)
        im_back=findViewById(R.id.im_back)

        im_back.setOnClickListener {
            val intent=Intent(this, Login::class.java)
            startActivity(intent)
        }

        button_giris.setOnClickListener {
            val intent=Intent(this, Login::class.java)
            startActivity(intent)
        }


        bt_registerfire=findViewById(R.id.bt_registerfire)
        bt_registerfire.setOnClickListener {
            registeruser()
        }


        mAuth = FirebaseAuth.getInstance()
    }

    private fun registeruser() {
        val email=et_regemail.text.toString().trim()
        val sifre=et_regpassword.text.toString().trim()
        val isim=et_name.text.toString().trim()
        val uid="bilinmiyor"
        var kullanici_type:String=""
        val tel="bilinmiyor"
        if (radio_hasta.isChecked){
            kullanici_type="Hasta"
        }
        if (radio_vasi.isChecked){
            kullanici_type="Vasi"
        }

        if (isim.isEmpty()){
            et_name.setError("İsim Gerekli")
            et_name.requestFocus()
            return
        }
        if (email.isEmpty()){
            et_regemail.setError("E-Mail gerekli.")
            et_regemail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_regemail.setError("Geçerli E-Mail giriniz.")
            et_regemail.requestFocus()
            return
        }
        if (sifre.isEmpty()){
            et_regpassword.setError("Sifre Gerekli")
            et_regpassword.requestFocus()
            return
        }
        if (sifre.length<6){
            et_regpassword.setError("Şifre en az 6 uzunlukta olmalı")
            et_regpassword.requestFocus()
            return
        }
        if (radio_hasta.isChecked==false && radio_vasi.isChecked==false){
            radio_hasta.setError("Kullanıcı tipini seçiniz.")
            return
        }
        if (tel.isEmpty()){
            et_tel.setError("Lütfen geçerli telefon numarası giriniz")
            et_tel.requestFocus()
            return
        }



        mAuth?.createUserWithEmailAndPassword(email, sifre)
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    val user=User(isim, email, kullanici_type, uid, tel)
                    val str=0
                    val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
                    val date = calendar.time
                    val dayy = calendar[Calendar.DATE].toString()
                    val month = (calendar[Calendar.MONTH] + 1).toString()
                    val year = calendar[Calendar.YEAR].toString()
                    val day=dayy+" "+month+" "+year
                    val sdf = SimpleDateFormat("HH:mm:ss")
                    val actTime=sdf.format(calendar.getTime()).toString()

                    val activity="hareketsiz (Bekleme)"
                    val reg=RegisterAct(day,activity,actTime)
                    val boolean=false;
                    val rutinIlac= rutinAyar(boolean,boolean,boolean,boolean)
                    val rutinSu=rutinAyar(false)

                    val ref=FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)



                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener {
                            if (it.isSuccessful){
                                ref.child("Adres").setValue("bilinmiyor")
                                ref.child("SonDüsme").setValue("01-07-2021_20:55:15")
                                ref.child("activity").setValue("hareketsiz (Bekleme)")
                                ref.child("telefon").setValue("bilinmiyor")
                                ref.child("latitude").setValue(40.7006676)
                                ref.child("longitude").setValue(29.9607254)
                                ref.child("rutinAyar").child("ilac").child("aksam").setValue(true)
                                ref.child("rutinAyar").child("ilac").child("level").setValue(true)
                                ref.child("rutinAyar").child("ilac").child("ogle").setValue(true)
                                ref.child("rutinAyar").child("ilac").child("sabah").setValue(true)
                                ref.child("rutinAyar").child("su").child("level").setValue(true)
                                ref.child("rutinAyar").child("tuvalet").child("level").setValue(true)
                                ref.child("rutinAyar").child("yemek").child("aksam").setValue(true)
                                ref.child("rutinAyar").child("yemek").child("level").setValue(true)
                                ref.child("rutinAyar").child("yemek").child("ogle").setValue(true)
                                ref.child("rutinAyar").child("yemek").child("sabah").setValue(true)
                                ref.child("tel").setValue("bilinmiyor")
                                Toast.makeText(this, "Kullanıcı başarıyla kayıt oldu", Toast.LENGTH_LONG).show()

                                val intent=Intent(this, Login::class.java)
                                intent.putExtra("isim", isim)
                                intent.putExtra("e-mail", email)
                                startActivity(intent)
                            }
                            else{
                                Toast.makeText(this, "Kullanıcı kayıt işlemi başarısız.", Toast.LENGTH_LONG).show()
                            }
                        }

                }
                else{
                    Toast.makeText(this, "Kullanıcı kayıt işlemi başarısız.", Toast.LENGTH_LONG).show()
                }

            }

    }
}