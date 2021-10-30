package com.devanand.weather.view.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devanand.weather.R
import com.devanand.weather.databinding.ActivityWeatherBinding
import com.devanand.weather.model.Constants
import com.devanand.weather.model.WeatherResponse
import com.devanand.weather.view.WeatherService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    private var latitude:Double = 0.0
    private var longtitude:Double = 0.0
    private var mProgressDialog: Dialog?=null
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var binding:ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.title = "Weather Details"

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val intent: Intent = getIntent()

        if (intent!=null){
            latitude = intent.getDoubleExtra("Latitude", 0.0)
            longtitude = intent.getDoubleExtra("Longitude",0.0)
            Log.d(Constants.TAG_WEATHEARACTIVITY,"${latitude},${longtitude}")
        }else{
            Log.d(Constants.TAG_WEATHEARACTIVITY,"Error")
        }

        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        setupUI()

        getLocationWeatherDetails(latitude, longtitude)

    }

    private fun getLocationWeatherDetails(latitude:Double, longitude: Double){
        if(Constants.isNetworkAvailable(this)){
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()

            val service: WeatherService = retrofit
                .create<WeatherService>(WeatherService::class.java)

            val listCall: Call<WeatherResponse> = service.getWeather(
                latitude,longitude, Constants.METRIC_UNIT, Constants.APP_ID
            )

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>?) {
                    if(response!!.isSuccessful){

                        hideProgressDialog()
                        val weatherList: WeatherResponse? = response.body()

                        val weatherResponseJSONString = Gson().toJson(weatherList)
                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE_DATA, weatherResponseJSONString)
                        editor.apply()
                        setupUI()
                        //setupUI(weatherList!!)
                        Log.i("Response Result","$weatherList")

                    }else{
                        val responseCode = response.code()
                        when(responseCode){
                            400 ->{
                                Log.e("Error 400","Bad Connection")
                            }
                            404 ->{
                                Log.e("Error 404","Not Found")
                            } else ->{
                            Log.e("Error","Generic Error")
                        }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("Error",t!!.message.toString())
                    hideProgressDialog()
                }

            })


        }else{
            Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCustomProgressDialog(){
        mProgressDialog = Dialog(this)

        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog!!.dismiss()
        }
    }

    private fun setupUI(){
        val weatherResponseJsonString = mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA,"")

        if(!weatherResponseJsonString.isNullOrEmpty()){
            val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)
            for(i in weatherList.weather.indices){
                Log.i("Weather Name",weatherList.weather.toString())

                tv_temp.text = weatherList.main.temp.toString() + getUnit(application.resources.configuration.toString())
                tv_sunrise_time.text = unixTime(weatherList.sys.sunrise)
                tv_sunset_time.text = unixTime(weatherList.sys.sunset)
                tv_name.text = weatherList.name
                tv_country.text = weatherList.sys.country
            }
        }
    }

    private fun unixTime(timex : Long) : String?{
        val date = Date(timex *1000L)
        val sdf = SimpleDateFormat("HH:mm")
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    private fun getUnit(value: String): String {
        var value ="°C"

        if("US" == value || "LR" == value || "MM" == value){
            value = "°F"
        }

        return value
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}