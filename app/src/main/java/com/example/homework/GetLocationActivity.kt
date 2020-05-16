package com.example.homework

import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.get_location.*


class GetLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.get_location);
        getLocation.setOnClickListener {
//            val locationManager =
//                getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }
}