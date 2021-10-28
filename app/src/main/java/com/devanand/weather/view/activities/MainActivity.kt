package com.devanand.weather.view.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog

import com.devanand.weather.R
import com.devanand.weather.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),OnMapReadyCallback{

    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var auth: FirebaseAuth
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        checkUser()
        checkAndAskPermission()
        //mapSetup()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //mapSetup()

        navView.setNavigationItemSelectedListener{
            it.isChecked = true
            when(it.itemId){
                /*R.id.nav_home ->{
                    *//*replaceFragment(MapsFragment(),it.title.toString())
                    Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()*//*
                }*/

                R.id.nav_history ->{
                    //replaceFragment(HistoryFragment(),it.title.toString())
                    var intent = Intent(applicationContext,HistoryActivity::class.java)
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

    /*private fun mapSetup() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, MapsFragment())
        fragmentTransaction.commit()
    }

    private fun replaceFragment(fragment: Fragment, title:String){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }

    private fun checkAndAskPermission() {

        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted()){

                        requestLocationData()
                    }

                    if (report.isAnyPermissionPermanentlyDenied){
                        Toast.makeText(this@MainActivity,"You have denied location permission. Please enabled them.",Toast.LENGTH_SHORT).show()
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
            val mLastLocation: Location = locationResult!!.lastLocation
            val latitude = mLastLocation.latitude
            Log.i("Current Location","$latitude")

            val longitude = mLastLocation.longitude
            Log.i("Current Location","$longitude")

            //getLocationWeatherDetails(latitude,longitude)
        }
    }

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
        //        Reference
        var currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null){
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }else{
            //logged in
            val phone = currentUser.phoneNumber
            //set phone number
            //phoneNumber.setText(phone)
            //it.setTitle(phone)

            val navHomeTitle:MenuItem = navView.menu.findItem(R.id.nav_user)
            navHomeTitle.setTitle(phone)
            navHomeTitle.isEnabled = false
        }
    }

    override fun onMapReady(p0: GoogleMap) {

    }

}
