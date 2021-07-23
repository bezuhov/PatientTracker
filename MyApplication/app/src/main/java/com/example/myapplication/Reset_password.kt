package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Reset_password : AppCompatActivity() {

    lateinit var bt_reset:Button
    lateinit var et_email:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        bt_reset=findViewById(R.id.bt_reset_forgot)
        et_email=findViewById(R.id.et_email_forgot)

        bt_reset.setOnClickListener {
            val email=et_email.text.toString().trim()
            if (email.isEmpty()){
                et_email.setError("Lütfen email adresinizi giriniz")
                et_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                et_email.setError("Geçerli E-Mail giriniz.")
                et_email.requestFocus()
                return@setOnClickListener
            }

            Firebase.auth.languageCode="tr"
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(" " ,"Email gönderildi.")
                        Toast.makeText(this,"Şifre yenileme bağlantınız adresinize gönderildi.",Toast.LENGTH_LONG).show()
                    }
                    else Toast.makeText(this,"Hata oluştu.Tekrar deneyiniz.",Toast.LENGTH_LONG).show()
                }


        }

    }
}