package com.devanand.weather.utility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.devanand.weather.database.DatabaseHelper
import com.devanand.weather.viewmodel.LocationViewModel

class ViewModelFactory(private val dbHelper: DatabaseHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}