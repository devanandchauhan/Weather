package com.devanand.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devanand.weather.R
import com.devanand.weather.database.Coordinate
import kotlinx.android.synthetic.main.item_location.view.*

class LocationAdapter(private val coordinate: ArrayList<Coordinate>) : RecyclerView.Adapter<LocationAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(coordinate: Coordinate) {
            itemView.latitude.text = coordinate.lat.toString()
            itemView.longitude.text = coordinate.lat.toString()
            itemView.address.text = coordinate.address
            itemView.date.text = coordinate.date.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.activity_history, parent,
            false
        )
    )

    override fun getItemCount(): Int = coordinate.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(coordinate[position])

    fun addData(list: List<Coordinate>) {
        coordinate.addAll(list)
    }
}