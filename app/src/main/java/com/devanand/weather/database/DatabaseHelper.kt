package com.devanand.weather.database

interface DatabaseHelper {
    suspend fun getCoordinates(): List<Coordinate>
    suspend fun insert(coordinate: Coordinate)
}