package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : WearableActivity() {
private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var queue: RequestQueue? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Enables Always-on
        setAmbientEnabled()

        btn_location.setOnClickListener {
            getLocation()
            sendLocation()
        }
        queue = Volley.newRequestQueue(this)

    }

    private fun getLocation() {
        Toast.makeText(this, "Getting location", Toast.LENGTH_SHORT).show()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0f, LocationListener {

            this.latitude = it.latitude
            this.longitude = it.longitude
            text_latitud.text = "Latitud: "+it.latitude.toString()
            text_longitud.text = "Longitud: "+it.longitude.toString()

        })
    }



    private fun sendLocation(){
        Toast.makeText(this, "Send Location Api", Toast.LENGTH_SHORT).show()
        val url = "http://127.0.0.1:8000/api/locations";
        val data = JSONObject()

        data.put("latitude", this.latitude)
        data.put("longitude", this.longitude)

        // Volley post request with parameters
        val request = JsonObjectRequest(
            Request.Method.POST,url,data,
            Response.Listener { response ->
                Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show()

            }, Response.ErrorListener{
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            })
       queue?.add(request)
    }


}

