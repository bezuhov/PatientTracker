 package com.example.myapplication

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.seismic.ShakeDetector
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


 class LocationService : Service() ,SensorEventListener,ShakeDetector.Listener{
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private val TAG = "LocationService"
    val database = FirebaseDatabase.getInstance()
    val OkHttpClient = OkHttpClient()
    private var mAuth: FirebaseAuth? = null
    private lateinit var sensorManager: SensorManager
    private var sensorac: Sensor? = null
    val GROUP_KEY = "com.android.example.WORK_EMAIL"
    var  counter = 0

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) createNotificationChanel() else startForeground(
                1,
                Notification()
        )
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sd = ShakeDetector(this)
        sd.start(sensorManager)

        sensorac= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)   //Düşme hesaplaması için ivmeölçer tanımlanması

        mAuth = FirebaseAuth.getInstance()
        requestLocationUpdates(mAuth?.currentUser?.uid.toString())

    }

     private fun yerLog(nearby_place: String, latitude: Double, longitude: Double, adresYeni: String) {
         var mevcut=""
         val myRef = database.getReference("Users").child(mAuth!!.currentUser?.uid.toString()).child("Adres")
         val refLog=database.getReference("Kayitlar").child(mAuth!!.currentUser?.uid.toString())
         val refUser=database.getReference("Users").child(mAuth!!.currentUser?.uid.toString())
         val tarih = Calendar.getInstance().time
         val format = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss")
         val current_time = format.format(tarih)
         refUser.addListenerForSingleValueEvent(object :ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 val adres=snapshot.child("Adres").value.toString()
                 val yakınYer=snapshot.child("Yakın_Yer").value.toString()
                 if (adres != adresYeni&&nearby_place!=yakınYer){
                     val eylem=snapshot.child("activity").value.toString()

                     refLog.child(current_time).child("Adres").setValue(adresYeni)
                     refLog.child(current_time).child("eylem").setValue(eylem)
                     refLog.child(current_time).child("hareket").setValue("yer")
                     refLog.child(current_time).child("lat").setValue(latitude)
                     refLog.child(current_time).child("long").setValue(longitude)
                     refLog.child(current_time).child("yer").setValue(nearby_place)

                     refUser.child("Adres").setValue(adresYeni)
                     refUser.child("latitude").setValue(latitude)
                     refUser.child("longitude").setValue(longitude)
                     refUser.child("Yakın_Yer").setValue(nearby_place)
                 }
             }

             override fun onCancelled(error: DatabaseError) {

             }

         })

     }

     override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        EventBus.getDefault().register(this);

        sensorac?.also { sensorac ->
            sensorManager.registerListener(this, sensorac, SensorManager.SENSOR_DELAY_NORMAL)
        }
        startTimer()

        val task = ActivityRecognition.getClient(this)
                .requestActivityTransitionUpdates(
                        ActivityTransitionsUtil.getActivityTransitionRequest(),
                        getPendingIntent()
                )

        task.addOnSuccessListener {
            Toast.makeText(this, "Hareket takibi başlatıldı.", Toast.LENGTH_LONG).show()
            // Handle success
        }

        task.addOnFailureListener { e: Exception ->
            // Handle error
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }



        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this);
        sensorManager.unregisterListener(this)
        stoptimertask()
       // val broadcastIntent = Intent()
        //broadcastIntent.action = "restartservice"
        //broadcastIntent.setClass(this, RestartBackgroundService::class.java)
        //this.sendBroadcast(broadcastIntent)

    }
     private var timer: Timer? = null
     private var timerTask: TimerTask? = null

     fun startTimer() {
         timer = Timer()
         timerTask = object : TimerTask() {
             override fun run() {
                 val tarih = Calendar.getInstance().time
                 val format = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss")
                 val current_time = format.format(tarih)
                 val currentUserUid=FirebaseAuth.getInstance().currentUser.uid
                 database.getReference("Users").child(currentUserUid).child("zaman").setValue(current_time)
             }
         }
         timer!!.schedule(
                 timerTask,
                 0,
                 5000
         ) //1 * 60 * 1000 1 minute
     }
     fun stoptimertask() {
         if (timer != null) {
             timer!!.cancel()
             timer = null
         }
     }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun requestLocationUpdates(id: String) {

        val request = LocationRequest()
        request.setInterval(10000)
        request.setFastestInterval(5000)
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val myRef = database.getReference("Users").child(id)

        var adres="Adres kaydı hiç yapılmadı"
        var nearby_place = "Yakınında bir yer bulunamadı."

        val client: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        val permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) { // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {

                    val location: Location = locationResult.getLastLocation()

                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude

                        val geocoder = Geocoder(this@LocationService)
                        try {
                            val adresses = geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1
                            )
                            adres = (adresses[0].getAddressLine(0)).toString()

                        } catch (e: Exception) {
                            adres = (e.toString())
                        }

                        val url =
                                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=500&type=point_of_interest,establishment&key=AIzaSyCttZT4r3DYJ1sM6Zc_RBUWuZ313P1W4YM"


                        val request = Request.Builder()
                                .url(url)
                                .build()

                        OkHttpClient.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                e.printStackTrace()

                                nearby_place = "Yakınında bir yer bulunamadı."
                            }

                            override fun onResponse(call: Call, response: Response) {
                                val body = response.body?.string()
                                val jsonObject = JSONObject(body)
                                if (jsonObject.has("results")) {
                                    if (jsonObject.getJSONArray("results").length() >= 2) {
                                        nearby_place =
                                                jsonObject.getJSONArray("results").getJSONObject(1)
                                                        .getString(
                                                                "name"
                                                        )
                                    }
                                } else {
                                    nearby_place = "Cevap geldi,okunamadı.."
                                }
                                if (nearby_place == "Yakınında bir yer bulunamadı.") {
                                    yerLog(nearby_place,latitude,longitude,adres)
                                } else {
                                    yerLog(nearby_place,latitude,longitude,adres)
                                }
                            }
                        })


                        //Log.d("Location Service", "location update $location")
                    }
                }
            }, null)
        }
    }

     //Sensör verilerinin anlık olarak alındığı,düşme durumununun kontrol edildiği
     //düşme bildiriminin gönderildiği yer.
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(p0: SensorEvent?) {

        val lux = p0!!.values[0]
        val luy= p0.values[1]
        val luz = p0.values[2]
        //Log.d("Sensör x,y,z değerleri", "x:" + lux + luy + luz)

        val rootSquare = Math.sqrt(
                Math.pow(lux.toDouble(), 2.0) + Math.pow(luy.toDouble(), 2.0) + Math.pow(
                        luz.toDouble(),
                        2.0
                )
        )
        if(rootSquare<2.0)
        {
            Toast.makeText(this, "Fall detected", Toast.LENGTH_SHORT).show()
            bildirimgonder("SonDüsme")
        }

    }

     //sensör override metotlarından biri.
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

     private fun getPendingIntent(): PendingIntent {
         val intent = Intent(this, ActivityTransitionReceiver::class.java)
         return PendingIntent.getBroadcast(
                 this,
                 5,
                 intent,
                 PendingIntent.FLAG_UPDATE_CURRENT
         )
     }

     @RequiresApi(Build.VERSION_CODES.O)
     override fun hearShake() {
         Toast.makeText(this, "Don't shake me, bro!", Toast.LENGTH_SHORT).show()

         bildirimgonder("acil")

              }

     @RequiresApi(Build.VERSION_CODES.O)
     private fun bildirimgonder(s: String) {
         val fullScreenIntent = Intent(this, hastaAlert::class.java)
         fullScreenIntent.putExtra("type", s)
         val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                 fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)


         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             // Create the NotificationChannel
             val name = getString(R.string.channel_name)
             val descriptionText = getString(R.string.channel_description)
             val importance = NotificationManager.IMPORTANCE_LOW
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
                             .setContentTitle("PatientTracker Alarm")
                             .setContentText("Acil durum mesajı göndermek için dokunun.")
                             .setPriority(NotificationCompat.PRIORITY_MIN)
                             .setCategory(NotificationCompat.CATEGORY_CALL)
                             .setAutoCancel(true)
                             .setDeleteIntent(fullScreenPendingIntent)
                             .setTimeoutAfter(10000)
                             .setGroup(GROUP_KEY)

                             // Use a full-screen intent only for the highest-priority alerts where you
                             // have an associated activity that you would like to launch after the user
                             // interacts with the notification. Also, if your app targets Android 10
                             // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                             // order for the platform to invoke this notification.
                             .setFullScreenIntent(fullScreenPendingIntent, true)

             val incomingCallNotification = notificationBuilder.build()
             startForeground(99, incomingCallNotification)
         }


     }

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
                 .setGroup("Ana Grup")
                 .build()
         startForeground(2, notification)
     }
     fun bayrak(toString: String) {

         val tarih=Calendar.getInstance().time
         val format = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss")
         val current_time=format.format(tarih)

         val clockFormat=SimpleDateFormat("HH:mm:ss")
         val currentClock=clockFormat.format(tarih)

         database.getReference("Users").child(mAuth?.currentUser?.uid.toString()).child(toString).setValue(current_time)

         val myRef=database.getReference("Users")
         myRef.addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 val eylem = snapshot.child(mAuth?.currentUser?.uid.toString()).child("activity").value.toString()
                 val yer = snapshot.child(mAuth?.currentUser?.uid.toString()).child("Yakın_Yer").value.toString()

                 database.getReference("Kayitlar").child(mAuth?.currentUser?.uid.toString()).child(current_time.toString()).child("hareket").setValue(toString)
                 database.getReference("Kayitlar").child(mAuth?.currentUser?.uid.toString()).child(current_time.toString()).child("eylem").setValue(eylem)
                 database.getReference("Kayitlar").child(mAuth?.currentUser?.uid.toString()).child(current_time.toString()).child("yer").setValue(yer)

             }

             override fun onCancelled(error: DatabaseError) {

             }
         })
         //event kaydı ekleme
     }


     @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
     fun onMessageEvent(event: MessageEvent) {
         Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
         bayrak(event.message.toString())
     }

     private fun haveNetworkConnection(): Boolean {
         var haveConnectedWifi = false
         var haveConnectedMobile = false
         val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
         val netInfo = cm.allNetworkInfo
         for (ni in netInfo) {
             if (ni.typeName.equals("WIFI", ignoreCase = true)) if (ni.isConnected) haveConnectedWifi = true
             if (ni.typeName.equals("MOBILE", ignoreCase = true)) if (ni.isConnected) haveConnectedMobile = true
         }
         return haveConnectedWifi || haveConnectedMobile
     }


 }