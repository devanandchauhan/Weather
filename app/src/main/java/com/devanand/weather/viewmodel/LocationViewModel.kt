package com.devanand.weather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devanand.weather.database.Coordinate
import com.devanand.weather.database.DatabaseBuilder
import com.devanand.weather.database.DatabaseHelper
import com.devanand.weather.utility.Results
import kotlinx.coroutines.launch

class LocationViewModel(private val dbHelper: DatabaseHelper) : ViewModel() {

    private val locations = MutableLiveData<Results<List<Coordinate>>>()

    init {
        fetchLocations()
    }

    private fun fetchLocations() {
        viewModelScope.launch {
            locations.postValue(Results.loading(null))
            try {
                val coordinateFromDb = dbHelper.getCoordinates()
                locations.postValue(Results.success(coordinateFromDb))
            } catch (e: Exception) {
                locations.postValue(Results.error("Something Went Wrong", null))
            }
        }
    }

    fun getLocations(): LiveData<Results<List<Coordinate>>> {
        return locations
    }
}