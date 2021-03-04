package com.weatherforecast.app.view.alert

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.Day
import com.weatherforecast.app.viewmodel.AlertViewModel
import java.lang.StringBuilder


class AlertRecyclerViewAdapter(var alertData: ArrayList<Alert>): RecyclerView.Adapter<AlertRecyclerViewAdapter.AlertViewHolder>() {
    lateinit var context: Context
    lateinit var viewModel: AlertViewModel

    fun updateList(newList: List<Alert>/*, viewModel: AlertViewModel*/) {
        //this.viewModel = viewModel

        alertData.clear()
        alertData.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        context = parent.context
        return AlertViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.alert_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(alertData[position], this)
    }

    override fun getItemCount(): Int {
        return alertData.size
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val alertTimeLbl = view.findViewById<TextView>(R.id.alertTimeLbl)
        private val alertDayLbl = view.findViewById<TextView>(R.id.alertDayLbl)
        private val alertEventLbl = view.findViewById<TextView>(R.id.alertEventLbl)
        private val alertTypeLbl = view.findViewById<TextView>(R.id.alertTypeLbl)
        private val alertSwitch = view.findViewById<Switch>(R.id.alertSwitch)
        private val updateButton = view.findViewById<Button>(R.id.updateButton)
        private val deleteButton = view.findViewById<Button>(R.id.deleteButton)


        @SuppressLint("SetTextI18n")
        fun bind(alert: Alert, adapter: AlertRecyclerViewAdapter) {
            alertTimeLbl.text = alert.alertTime
            alertEventLbl.text = alert.alertEvent
            alertTypeLbl.text = alert.alertType
            alertSwitch.isChecked = alert.enabled

            val str = StringBuilder()
            if(alert.alertDay.size == 1 && alert.alertDay[0] == Day.ALL){
                alertDayLbl.text = "Every Day"
            }else if (alert.alertDay.size == 1 && alert.alertDay[0] == Day.WEEKEND){
                alertDayLbl.text = "Every Weekend"
            }else if (alert.alertDay.size == 1 && alert.alertDay[0] == Day.NONE){
                alertDayLbl.text = "No Repeat"
            }else{
                for (day in alert.alertDay){
                    str.append("$day,")
                }
                str.deleteCharAt(str.lastIndex)
                alertDayLbl.text = str
            }

            alertSwitch.setOnDragListener {_, dragEvent ->
                alert.enabled = dragEvent.result
                //viewModel.insertOrUpdate(alert)
                true
            }

            updateButton.setOnClickListener {
                //adapter.viewModel.getSome(alert.alertTime, alert.enabled)
            }

            deleteButton.setOnClickListener {
                //adapter.viewModel.delete(alert)
                adapter.alertData.remove(alert)
                adapter.notifyDataSetChanged()
            }
        }
    }
}