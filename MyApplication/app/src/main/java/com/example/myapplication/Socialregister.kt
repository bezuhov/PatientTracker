package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class Socialregister : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var et_Uidsocial:EditText
    lateinit var et_phonesocial:EditText
    lateinit var rb_rsocial:RadioButton
    lateinit var rb_rsocialv:RadioButton
    lateinit var bt_rsocial:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socialregister)

        et_Uidsocial=findViewById(R.id.et_Uidsocial)
        et_phonesocial=findViewById(R.id.et_phonesocial)
        rb_rsocial=findViewById(R.id.rb_rsocial)
        rb_rsocialv=findViewById(R.id.rb_rsocialv)
        bt_rsocial=findViewById(R.id.bt_rsocial)

        mAuth = FirebaseAuth.getInstance()
        val user=mAuth.currentUser
        val userId=user!!.uid
        val photo_url=user.photoUrl?.toString()
        val name=user.displayName
        val email=user.email

        bt_rsocial.setOnClickListener {
            register(userId, photo_url, name, email)
        }

    }

    private fun register(userId: String, photo_url: String?, name: String?, email: String?) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")

        val uid=et_Uidsocial.text.toString().trim()
        val phone=et_phonesocial.text.toString().trim()
        var user_type=""

        if (rb_rsocial.isChecked){
            user_type="Hasta"
        }
        if (rb_rsocialv.isChecked){
            user_type="Vasi"
        }
        if (phone.isEmpty()){
            et_phonesocial.setError("Lütfen telefon numarası giriniz")
            et_phonesocial.requestFocus()
            return
        }

        if (uid!=""){
            myRef.get().addOnSuccessListener {
                if (it.hasChild(uid)){
                    Toast.makeText(this,"uid mevcut..ekleniyor", Toast.LENGTH_LONG).show()

                    val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
                    val date = calendar.time
                    val dayy = calendar[Calendar.DATE].toString()
                    val month = (calendar[Calendar.MONTH] + 1).toString()
                    val year = calendar[Calendar.YEAR].toString()
                    val day=dayy+" "+month+" "+year
                    val activity="hareketsiz (Bekleme)"
                    val str=0
                    val sdf = SimpleDateFormat("HH:mm:ss")
                    val actTime=sdf.format(calendar.getTime()).toString()
                    val reg=RegisterAct(day,activity,actTime)
                    myRef.child(userId).setValue(reg)

                    myRef.child(userId).child("fullname").setValue(name)
                    myRef.child(userId).child("email").setValue(email)
                    myRef.child(userId).child("kullanici_tipi").setValue(user_type)
                    myRef.child(userId).child("Uid").setValue(uid)
                    myRef.child(userId).child("telefon").setValue(phone)
                    myRef.child(userId).child("photo_url").setValue(photo_url)
                }
                else {
                    Toast.makeText(this,"bulunamayan kod", Toast.LENGTH_LONG).show()
                    et_Uidsocial.setError("Girdiğiniz kod hatalıdır.Dilerseniz boş bırakabilirsiniz.")
                    et_Uidsocial.requestFocus()
                    return@addOnSuccessListener
                }


            }.addOnFailureListener {
                Toast.makeText(this,"Error getting data", Toast.LENGTH_LONG).show()
            }
        }else{
            val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
            val date = calendar.time
            val dayy = calendar[Calendar.DATE].toString()
            val month = (calendar[Calendar.MONTH] + 1).toString()
            val year = calendar[Calendar.YEAR].toString()
            val day=dayy+" "+month+" "+year
            val activity="hareketsiz (Bekleme)"
            val sdf = SimpleDateFormat("HH:mm:ss")
            val actTime=sdf.format(calendar.getTime()).toString()

            val str=0
            val reg=RegisterAct(day,activity,actTime)
            myRef.child(userId).setValue(reg)

            myRef.child(userId).child("fullname").setValue(name)
            myRef.child(userId).child("email").setValue(email)
            myRef.child(userId).child("kullanici_tipi").setValue(user_type)
            myRef.child(userId).child("telefon").setValue(phone)
            myRef.child(userId).child("photo_url").setValue(photo_url)
        }


    }
}