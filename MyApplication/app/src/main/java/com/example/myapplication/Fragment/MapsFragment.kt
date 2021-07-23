package com.example.myapplication.Fragment

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.myapplication.Model.GoogleMapDTO
import com.example.myapplication.Model.getEvents
import com.example.myapplication.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.maps.android.ui.IconGenerator
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MapsFragment : Fragment() {

    lateinit var mapFragment : SupportMapFragment
    lateinit var mMap: GoogleMap
    private var mAuth: FirebaseAuth? = null
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        //İkon ve arkaplanları getirme
        var walking: Drawable? = requireContext().getDrawable(R.drawable.walk)
        val waiting = requireContext().getDrawable(R.drawable.bekleme)
        val car = requireContext().getDrawable(R.drawable.ic_car)
        val running = requireContext().getDrawable(R.drawable.run)
        val alert = requireContext().getDrawable(R.drawable.ic_alert)
        val falling = requireContext().getDrawable(R.drawable.ic_fall)
        val food=requireContext().getDrawable(R.drawable.ic_dinner)

        mMap=googleMap
        var tarih: Date? = Calendar.getInstance().time
        val dayFormat = SimpleDateFormat("dd-MM-yyyy")
        val current_day = dayFormat.format(tarih)

        mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val uid: String = mAuth!!.getUid()?:"";
        val myRefGeneral = database.getReference("Users").child(uid)

        myRefGeneral.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uidd = snapshot.child("Uid").value.toString()
                val korEvent = getEvents(context)
                try {
                    korEvent.koordinatlar(current_day, uidd) { value ->
                        if (!value.isEmpty()) {
                            Log.d("dsadadadadewfgwgwgw", value.size.toString())
                            val builder = LatLngBounds.Builder()
                            for (z in value.indices) {
                                if (z != value.size - 1) {
                                    val pointPre = LatLng(value[z].lat, value[z].lon)
                                    val point = LatLng(value[z + 1].lat, value[z + 1].lon)
                                    val URL = getDirectionURL(pointPre, point)
                                    Log.d("GoogleMap", "URL : $URL")
                                    GetDirection(URL).execute()
                                }
                                val motionOne = value[z].motionOne
                                val motionTwo = value[z].motionTwo
                                val motionThree = value[z].motionThree


                                val point = LatLng(value[z].lat, value[z].lon)
                                val item = LayoutInflater.from(context).inflate(
                                        R.layout.fragment_custom_info_window,
                                        null
                                )
                                val cardView=item.findViewById<CardView>(R.id.custom_viewCard)
                                val image = item.findViewById<ImageView>(R.id.imInfoImage)
                                val imageOne = item.findViewById<ImageView>(R.id.imageView7)
                                val imageTwo = item.findViewById<ImageView>(R.id.imageView8)
                                val imageThree = item.findViewById<ImageView>(R.id.imageView9)

                                if (z != value.size - 1) {
                                    cardView.setBackgroundColor(getResources().getColor(R.color.itemBlue))
                                }

                                if (!motionOne.equals("")) {
                                    cardView.setBackgroundColor(getResources().getColor(R.color.kirmizi))
                                    if (motionOne.equals("bad")) {
                                        imageOne.setImageDrawable(alert)
                                    }else if (motionOne.equals("fall"))imageOne.setImageDrawable(falling)
                                }
                                if (!motionTwo.equals("")){
                                    if (motionTwo.equals("Araç içinde hareket"))imageTwo.setImageDrawable(car)
                                    else if (motionTwo.equals("Koşma"))imageTwo.setImageDrawable(running)
                                }else imageTwo.setImageDrawable(walking)

                                if (!motionThree.equals("")){
                                    if (motionThree.equals("yemek"))imageThree.setImageDrawable(food)
                                }else imageThree.visibility=View.GONE

                                val texxt = item.findViewById<TextView>(R.id.tvInfoTittle)
                                val texxtExp = item.findViewById<TextView>(R.id.tvInfoTime)
                                texxt.setText(value[z].yer)
                                texxtExp.setText(value[z].zamanKey)
                                //val text = TextView(context)
                                //text.text = value[z].yer+"   "+value[z].zamanKey
                                val generator = IconGenerator(context)
                                generator.setBackground(context!!.getDrawable(R.drawable.btn_bg))

                                generator.setContentView(item)
                                val icon = generator.makeIcon()
                                val tp = MarkerOptions().position(point).icon(
                                        BitmapDescriptorFactory.fromBitmap(
                                                icon
                                        )
                                )
                                googleMap.addMarker(tp)

                                builder.include(point)
                            }
                            val bounds = builder.build()
                            val padding = 0 // offset from edges of the map in pixels
                            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                            googleMap.moveCamera(cu)
                        } else Log.d("GoogleMapsadsafasf", "URLurufskfsdkfsfhsdfjsdnfwı ")
                    }

                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })




    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }

    fun getDirectionURL(origin: LatLng, dest: LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving&key=AIzaSyCttZT4r3DYJ1sM6Zc_RBUWuZ313P1W4YM"
    }

    private inner class GetDirection(val url: String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()
            Log.d("GoogleMap", " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
//                    val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e: Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }

    public fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }


    
}