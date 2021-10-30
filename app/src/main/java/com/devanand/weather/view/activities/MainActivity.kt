package com.devanand.weather.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.devanand.weather.R
import com.devanand.weather.database.Coordinate
import com.devanand.weather.database.DatabaseBuilder
import com.devanand.weather.database.DatabaseHelperImpl
import com.devanand.weather.databinding.ActivityMainBinding
import com.devanand.weather.model.Constants
import com.devanand.weather.model.Constants.TAG_WEATHEARACTIVITY
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_nav.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity(),OnMapReadyCallback{

    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var auth: FirebaseAuth
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLastLocation: Location
    private var mCurrLocationMarker: Marker? = null
    private lateinit var mMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private var TAG:String ="MainActivity"
    private lateinit var latLng : LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)
        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        checkUser()
        checkAndAskPermission()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        navView.setNavigationItemSelectedListener{
            it.isChecked = true
            when(it.itemId){
                R.id.nav_history ->{
                    val intent = Intent(applicationContext,HistoryActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()

                    Toast.makeText(this, "History clicked", Toast.LENGTH_SHORT).show()
                }

                R.id.nav_logout ->{
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
            }
            true
        }

    }

    //Check and ask for Permissions
    private fun checkAndAskPermission() {
            Dexter.withActivity(this)
                .withPermissions(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {

                            requestLocationData()
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@MainActivity,
                                "You have denied location permission. Please enabled them.",
                                Toast.LENGTH_SHORT
                            ).show()
                            showRationalDialogForPermission()
                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermission()
                        //token?.continuePermissionRequest()
                    }
                }).onSameThread().check()

    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData(){

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
            mLocationCallback,
            Looper.getMainLooper())
    }

    private val mLocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            mLastLocation = locationResult!!.lastLocation
            val latitude = mLastLocation.latitude
            Log.i("Current Location","$latitude")

            val longitude = mLastLocation.longitude
            Log.i("Current Location","$longitude")

            if (mCurrLocationMarker != null) {
                mCurrLocationMarker!!.remove()
            }

            //Place current location marker
            latLng = LatLng(mLastLocation!!.latitude, mLastLocation.longitude)
            drawMarker(latLng)

            mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
                override fun onMarkerDragStart(marker: Marker) {
                    Log.d(Constants.TAG_MAINACTIVITY, "onMarkerDragStart: ")
                }

                override fun onMarkerDrag(marker: Marker) {
                    Log.d(Constants.TAG_MAINACTIVITY, "onMarkerDrag: ")
                }

                override fun onMarkerDragEnd(marker: Marker) {
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker!!.remove()
                    }
                    Log.d(TAG_WEATHEARACTIVITY, "onMarkerDragEnd: ")
                    var newLatLng = LatLng(marker!!.position.latitude,marker?.position.longitude)
                    drawMarker(newLatLng)
                }
            })
        }
    }

    //If Permission isDisabled then ask for permission
    private fun showRationalDialogForPermission(){
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions.")
            .setPositiveButton(
                "GO TO SETTINGS"
            ){_,_->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){ dialog,_->dialog.dismiss()}.show()

    }

    private fun isLocationEnabled():Boolean{

        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkUser() {
        //Reference
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null){
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }else{
            //logged in
            val phone = currentUser.phoneNumber
            val navHomeTitle:MenuItem = navView.menu.findItem(R.id.nav_user)
            navHomeTitle.setTitle(phone)
            navHomeTitle.isEnabled = false
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            //Location Permission already granted
            mMap.isMyLocationEnabled = true
        }
        if(!isLocationEnabled()){
            Toast.makeText(this,"Your location is turned off. Please turn it on.",Toast.LENGTH_SHORT).show()

            //Opens Location Settings Page
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            mMap.isMyLocationEnabled = true
        }

    }

    private fun drawMarker(latLng: LatLng) {
        val markerOption = MarkerOptions().position(latLng).title("Current Position")
            .snippet(getAddress(latLng.latitude,latLng.longitude)).draggable(true)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        mCurrLocationMarker = mMap.addMarker(markerOption)
        Log.d("WeatherActivity","${latLng.longitude},${latLng.longitude}")
        val address = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1)
        address[0].getAddressLine(0)

        //Save to database
        GlobalScope.launch(Dispatchers.IO) {
            //saveToDB(latLng.latitude, latLng.longitude, address.toString())
        }

        fab.setOnClickListener( object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(applicationContext, WeatherActivity::class.java)
                //intent.putExtra("LatLng",latLng)
                //var bundle: Bundle = Bundle()
                intent.putExtra("Latitude",latLng.latitude)
                intent.putExtra("Longitude",latLng.longitude)

                startActivity(intent)
                //setupUI()

            }

        })
    }

    private suspend fun saveToDB(lat: Double, lon: Double, address: String) {
        val coordinate: Coordinate = Coordinate(1, lon, lat, address, Date())
        DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext)).insert(coordinate)
    }

    private fun getAddress(lat: Double,lon: Double):String?{
        val address = geocoder.getFromLocation(lat,lon,1)
        address[0].getAddressLine(0)
        return address[0].getAddressLine(0).toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }

}
