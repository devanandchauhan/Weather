package com.devanand.weather.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.devanand.weather.R
import com.devanand.weather.databinding.ActivitySplashScreenBinding
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar

        // showing the back button in action bar
        if (actionBar != null) {
            getSupportActionBar()?.hide()
        }

        locationWeather.setAnimation(R.raw.locationweather)
        locationWeather.playAnimation()

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

    }
}