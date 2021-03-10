package com.weatherforecast.app.view.alert

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.viewmodel.AlertViewModel
import java.util.*
import kotlin.collections.ArrayList


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

            val alertEventArray: List<String> = adapter.context.resources.getStringArray(R.array.alertEvent).toList()
            alertEventLbl.text = alertEventArray[alert.alertEvent]

            val alertTypeArray: List<String> = adapter.context.resources.getStringArray(R.array.alertType).toList()
            alertTypeLbl.text = alertTypeArray[alert.alertType]

            alertSwitch.isChecked = alert.enabled

            //val str = StringBuilder()
            if(alert.alertDay  == "ALL"){
                alertDayLbl.text = adapter.context.getText(R.string.EveryDay)
            }else if (alert.alertDay == "WEEKEND"){
                alertDayLbl.text = adapter.context.getText(R.string.EveryWeekend)
            }else if (alert.alertDay == "NONE"){
                alertDayLbl.text = adapter.context.getText(R.string.NoRepeat)
            }else{
//                for (day in alert.alertDay){
//                    str.append("$day,")
//                }
//                str.deleteCharAt(str.lastIndex)

                val pref = PreferenceManager.getDefaultSharedPreferences(adapter.context.applicationContext)
                val language = pref.getString("language", "en")!!

                if (language == "en"){
                    alertDayLbl.text = alert.alertDay
                }else{
                    alertDayLbl.text = alert.alertDayAr
                }
            }

            alertSwitch.setOnClickListener() {
                alert.enabled = alertSwitch.isChecked
                //adapter.viewModel.insertOrUpdate(alert)
                true
            }

            updateButton.setOnClickListener {
                val intent = Intent(adapter.context, AddAlertActivity::class.java)
                intent.putExtra("status", "update")
                intent.putExtra("alert", alert)
                adapter.context.startActivity(intent)
            }

            deleteButton.setOnClickListener {
                val builder = AlertDialog.Builder(adapter.context)
                builder.setTitle("Warning")
                builder.setMessage("Are you sure you want delete this?")

                builder.setPositiveButton("Yes") { _, _ ->
                    //adapter.viewModel.delete(alert)
                    adapter.alertData.remove(alert)
                    adapter.notifyDataSetChanged()
                }

                builder.setNegativeButton("No", null)
                builder.show()
            }
        }
    }
}