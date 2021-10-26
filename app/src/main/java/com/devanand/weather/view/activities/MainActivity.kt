package com.devanand.weather.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devanand.weather.R
import com.devanand.weather.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}