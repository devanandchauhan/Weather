package com.devanand.weather.database

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {
    override suspend fun getCoordinates(): List<Coordinate> = appDatabase.coordinateDao()!!.getAll()
    override suspend fun insert(coordinate: Coordinate) = appDatabase.coordinateDao()!!.insert(coordinate)
}