package com.weatherforecast.app.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Alert


class AlertRecyclerViewAdapter(var alertData: ArrayList<Alert>): RecyclerView.Adapter<AlertRecyclerViewAdapter.AlertViewHolder>() {
    lateinit var context: Context

    fun updateList(newList: List<Alert>) {
        alertData.clear()
        alertData.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        context = parent.context
        return AlertViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.weather_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(alertData[position], context)
    }

    override fun getItemCount(): Int {
        return alertData.size
    }

    class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tempLbl = view.findViewById<TextView>(R.id.tempLbl)
        private val humidityLbl = view.findViewById<TextView>(R.id.humidityLbl)
        private val windLbl = view.findViewById<TextView>(R.id.windLbl)
        private val pressureLbl = view.findViewById<TextView>(R.id.pressureLbl)
        private val rainLbl = view.findViewById<TextView>(R.id.rainLbl)
        private val cloudsLbl = view.findViewById<TextView>(R.id.cloudsLbl)
        private val sunriseLbl = view.findViewById<TextView>(R.id.sunriseLbl)
        private val sunsetLbl = view.findViewById<TextView>(R.id.sunsetLbl)
        private val zoneLbl = view.findViewById<TextView>(R.id.zoneLbl)
        private val descriptionLbl = view.findViewById<TextView>(R.id.descriptionLbl)
        private val statusImageView = view.findViewById<ImageView>(R.id.statusImageView)

        fun bind(alert: Alert, context: Context) {

        }
    }
}