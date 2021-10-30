package com.devanand.weather.database

import androidx.room.*
import java.util.*

@Dao
interface CoordinateDAO {

    @Query("SELECT * FROM coordinate")
    fun getAll(): List<Coordinate>

    @Query("SELECT * FROM coordinate WHERE date IN (:date)")
    fun loadAllByDate(date: Date): List<Coordinate>

    @Insert
    fun insert(coordinates: Coordinate)

    @Delete
    fun delete(user: Coordinate)

}