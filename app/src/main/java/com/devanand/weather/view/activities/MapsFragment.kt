package com.devanand.weather.view.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationRequest
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.devanand.weather.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.io.IOException

class MapsFragment : Fragment(),OnMapReadyCallback {
    private val SOME_VALUE_KEY:String = "someValueToSave";
    private lateinit var geocoder: Geocoder
    private var TAG:String ="MapsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geocoder = Geocoder(activity)
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
        mapFragment?.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SOME_VALUE_KEY,1)
        super.onSaveInstanceState(outState)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        var mMap = googleMap
        val sydney = LatLng(-33.852, 151.211)

        mMap.uiSettings.isMyLocationButtonEnabled = true

        /*if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        mMap.setMyLocationEnabled(true)
        mMap.setOnMyLocationButtonClickListener(object : GoogleMap.OnMyLocationButtonClickListener{
            override fun onMyLocationButtonClick(): Boolean {
                //checkGPS()
                return true
            }

        })*/

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL)

        mMap.setOnMapLongClickListener(object : GoogleMap.OnMapLongClickListener{
            override fun onMapLongClick(latLng: LatLng) {
                Log.d(TAG,"setOnMapLongClickListener: $latLng")
                try {
                    var addresses: List<Address> =
                        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (addresses.size > 0) {
                        Log.d("Address Map Fragment", "Address: $latLng")
                        var address: Address = addresses.get(0)
                        var streetAddress: String = address.getAddressLine(0)
                        mMap.addMarker(MarkerOptions()
                            .position(latLng)
                            .title(streetAddress)
                            .draggable(true)
                        )
                    }
                }catch (e: IOException){
                    e.printStackTrace()
                }
            }
        })

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
            override fun onMarkerDragStart(marker: Marker) {
                Log.d(TAG, "onMarkerDragStart: ")
            }

            override fun onMarkerDrag(marker: Marker) {
                Log.d(TAG, "onMarkerDrag: ")
            }

            override fun onMarkerDragEnd(marker: Marker) {
                Log.d(TAG, "onMarkerDragEnd: ")
                var latLng:LatLng = marker.position
                try {
                    var addresses: List<Address> =
                        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (addresses.size > 0) {
                        Log.d("Address Map Fragment", "Address: $latLng")
                        var address: Address = addresses.get(0)
                        var streetAddress: String = address.getAddressLine(0)
                        marker.title = streetAddress
                    }
                }catch (e: IOException){
                    e.printStackTrace()
                }
            }

        })

        googleMap.addMarker(
            //MarkerOptions().position(LatLng(-34.0, 151.0)).title("Marker")
            MarkerOptions().position(sydney).title("Marker")
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16f))
    }

    /*private fun checkGPS() {
        var mLocationRequest = com.google.android.gms.location.LocationRequest.create()
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(5000)
        mLocationRequest.setFastestInterval(3000)

        var builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
    }*/

}