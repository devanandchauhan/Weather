package com.devanand.weather.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Coordinate::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coordinateDao(): CoordinateDAO?
}

