package com.devanand.weather.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment

import com.devanand.weather.R
import com.devanand.weather.databinding.ActivityMainBinding
import com.devanand.weather.view.fragments.HistoryFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){

    private lateinit var binding:ActivityMainBinding
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
        mapSetup()

        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mapSetup()

        navView.setNavigationItemSelectedListener{

            it.isChecked = true
            when(it.itemId){
                R.id.nav_home ->{
                    replaceFragment(MapsFragment(),it.title.toString())
                    Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
                }

                R.id.nav_history ->{
                    replaceFragment(HistoryFragment(),it.title.toString())
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

    private fun mapSetup() {
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
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
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

}
