package com.example.onerootv1

// for location
// https://www.youtube.com/watch?v=I5ektSfv4lw
// https://github.com/foxandroid/CurrentLocation/tree/master
// https://www.androidhire.com/current-location-in-android-using-kotlin/
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup


import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*

class LocationFragment : Fragment() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val requestCode = 100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view1 = inflater.inflate(R.layout.fragment_location,container,false)
        val latitude = view1?.findViewById<TextView>(R.id.latitudeText)
        val longitude = view1?.findViewById<TextView>(R.id.longitudeText)
        val address = view1?.findViewById<TextView>(R.id.addressText)!!
        val city = view1.findViewById<TextView>(R.id.cityText)!!
        val country = view1.findViewById<TextView>(R.id.countryText)!!
        val locationButton = view1.findViewById<Button>(R.id.getLocation)!!


        val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val mLastLocation: Location = locationResult.lastLocation
                println("requested new location: $mLastLocation.latitude")
                view1.findViewById<TextView>(R.id.latitudeText)?.text = mLastLocation.latitude.toString()
                view1.findViewById<TextView>(R.id.longitudeText)?.text =  mLastLocation.longitude.toString()
            }
        }

        fun requestNewLocationData() {
            val mLocationRequest = LocationRequest()
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.interval = 1000
            mLocationRequest.fastestInterval = 1000
            mLocationRequest.numUpdates = 1

            val mFusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
            if (activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED && activity?.let {
                    ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } == PackageManager.PERMISSION_GRANTED)
            {
                mFusedLocationClient!!.requestLocationUpdates(
                    mLocationRequest, mLocationCallback,
                    Looper.myLooper()
                )
            }
            else
            {
                requestPermissions()
            }

        }

        fun getLocation()
        {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
                    println("location$location")
                    if (location != null) {
                        val geocoder = activity?.let { Geocoder(it, Locale.getDefault()) }
                        try {
                            val addresses: MutableList<Address>? =
                                geocoder?.getFromLocation(location.latitude, location.longitude, 1)
                            println("addresses: $addresses")
                            if (latitude != null) {
                                latitude.text = "Latitude: " + location.latitude
                            }
                            if (longitude != null) {
                                longitude.text = "Longitude: " + location.longitude
                            }
                            address.text =
                                "Address: " + (addresses?.get(0)?.getAddressLine(0) ?: addresses)
                            city.text = "City: " + (addresses?.get(0)?.locality ?: addresses)
                            country.text =
                                "Country: " + (addresses?.get(0)?.countryName ?: addresses)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    else
                    {
                        Toast.makeText(activity, "Location not found", Toast.LENGTH_SHORT).show()
                        requestNewLocationData()
                    }
                }
        }

        fusedLocationProviderClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!

        locationButton.setOnClickListener {
            if (checkPermissions())
            {
                if (isLocationEnabled()) {
                    getLocation()
                }
                else
                {
                    // open settings to turn on
                    Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            }
            else
            {
                // request permission
                requestPermissions()
            }
        }
        return view1
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
    }

    private fun checkPermissions(): Boolean {
        if (activity?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) } == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        activity?.let { ActivityCompat.requestPermissions(it,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                requestCode
            )
        }
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        if (requestCode == this.requestCode) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//                Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}