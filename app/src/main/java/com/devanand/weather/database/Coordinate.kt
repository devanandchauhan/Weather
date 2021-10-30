package com.devanand.weather.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class Coordinate(

    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "longitude")
    val lon:Double,

    @ColumnInfo(name = "latitude")
    val lat:Double,

    @ColumnInfo(name = "address")
    val address:String,

    @ColumnInfo(name = "date")
    val date:Date

):Serializable
