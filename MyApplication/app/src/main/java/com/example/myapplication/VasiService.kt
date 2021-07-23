package com.example.myapplication

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class VasiService : Service() {

    val database = FirebaseDatabase.getInstance()
    private var mAuth: FirebaseAuth? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        //val broadcastIntent = Intent()
        //broadcastIntent.action = "restartservice"
        //broadcastIntent.setClass(this, RestartVasiService::class.java)
        //this.sendBroadcast(broadcastIntent)
    }

    override fun onCreate() {
        super.onCreate()


        mAuth = FirebaseAuth.getInstance()
        //Servisin çalıştığını bildiren bildirim ayarlanıyor.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) createNotificationChanel() else startForeground(
            1,
            Notification()
        )
        //Hastanın Uid si alınıyor..
        val myRef=database.getReference("Users")
        val id= mAuth!!.currentUser.uid.toString()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(id)) {
                    val uid = snapshot.child(id).child("Uid").value.toString()
                    hastaIlkVeri(uid)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })



    }

    private fun hastaIlkVeri(uid: String) {
        //Verileri karşılaştırmak için hastanın ilk verileri alınıyor...

        val initref=database.getReference("Users")
        initref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var aktivite = snapshot.child(uid).child("Aktivite").value.toString()
                var lastFall = snapshot.child(uid).child("SonDüsme").value.toString()
                var shake = snapshot.child(uid).child("acil").value.toString()
                hastaTakibi(aktivite, lastFall, uid, shake)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun hastaTakibi(aktivite: String, lastFall: String, uid: String, shake: String) {
        //Toast.makeText(this,aktivite, Toast.LENGTH_LONG).show()
        //Hastanın Yeni bir düşme veya aktivite geçişinde bildirim gönderiliyor.
        val ref=database.getReference("Users")
        var act=aktivite
        var fall=lastFall
        var shakeTime=shake
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (act != snapshot.child(uid).child("Aktivite").value.toString()) {
                    act = snapshot.child(uid).child("Aktivite").value.toString()
                    bildirimGonder(act, 0)
                }
                if (fall != snapshot.child(uid).child("SonDüsme").value.toString()) {
                    fall = snapshot.child(uid).child("SonDüsme").value.toString()
                    val fallText = "Hastada düşme durumu gözlendi. " + fall
                    bildirimGonder(fallText, 1)
                }
                if (shakeTime != snapshot.child(uid).child("acil").value.toString()) {
                    shakeTime = snapshot.child(uid).child("acil").value.toString()
                    val shakeText = "Acil durum algılandı.  " + shakeTime
                    bildirimGonder(shakeText, 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun bildirimGonder(text: String, notificationCode: Int) {

        val intent = Intent(this, Analiz_activity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, "com.example.myapplication")
                .setSmallIcon(R.drawable.triangle)
                .setContentTitle("Hastada yeni bir hareket gözlendi")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(20, builder.build())
        }
        if (notificationCode==1){
            importantNotify(text)
        }
    }

    private fun importantNotify(text: String) {
        val fullScreenIntent = Intent(this, Analiz_activity::class.java)

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val CHANNEL_ID="PatientTracker kanalı"
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            val notificationBuilder =
                    NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.triangle)
                            .setContentTitle("PatientTracker Alarm Mesajı")
                            .setContentText(text + "Hastanın bilgilerini görüntülemek için tıklayın.")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)

                            // Use a full-screen intent only for the highest-priority alerts where you
                            // have an associated activity that you would like to launch after the user
                            // interacts with the notification. Also, if your app targets Android 10
                            // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                            // order for the platform to invoke this notification.
                            .setFullScreenIntent(fullScreenPendingIntent, true)

            val incomingCallNotification = notificationBuilder.build()
            startForeground(99, incomingCallNotification)

    }}

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {

        val NOTIFICATION_CHANNEL_ID = "com.example.myapplication"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running count::")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return START_STICKY

    }








}