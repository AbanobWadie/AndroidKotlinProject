package com.weatherforecast.app.view.alert

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Alert
import java.util.*


class AddAlertActivity : AppCompatActivity() {

    private lateinit var alertEventSpinner: Spinner
    private lateinit var alertTypeSpinner: Spinner
    private lateinit var timeLbl: TextView
    private lateinit var satCheckBox: CheckBox
    private lateinit var sunCheckBox: CheckBox
    private lateinit var monCheckBox: CheckBox
    private lateinit var tueCheckBox: CheckBox
    private lateinit var wedCheckBox: CheckBox
    private lateinit var thuCheckBox: CheckBox
    private lateinit var friCheckBox: CheckBox
    private lateinit var addbtn: Button

    //private lateinit var viewModel: AlertViewModel
    private lateinit var alert: Alert
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alert)

        alertEventSpinner = findViewById(R.id.alertEventSpinner)
        alertTypeSpinner = findViewById(R.id.alertTypeSpinner)
        timeLbl = findViewById(R.id.timeLbl)
        satCheckBox = findViewById(R.id.satCheckBox)
        sunCheckBox = findViewById(R.id.sunCheckBox)
        monCheckBox = findViewById(R.id.monCheckBox)
        tueCheckBox = findViewById(R.id.tueCheckBox)
        wedCheckBox = findViewById(R.id.wedCheckBox)
        thuCheckBox = findViewById(R.id.thuCheckBox)
        friCheckBox = findViewById(R.id.friCheckBox)
        addbtn = findViewById(R.id.addBtn)

        addbtn.setOnClickListener {
            addOrUpdate()
        }

        timeLbl.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)

            val pickerTime = TimePickerDialog(this, { _, sHour, sMinute ->
                timeLbl.text = "$sHour:$sMinute"
            }, hour, minutes, true)
            pickerTime.show()
        }

        if (intent.getStringExtra("status") == "update"){
            alert = intent.getSerializableExtra("alert") as Alert
            setUpdateData()
        }

        //viewModel = ViewModelProvider(this).get(AlertViewModel::class.java)
    }

    private fun setUpdateData() {
        when (alert.alertEvent) {
            "Temp" -> alertEventSpinner.setSelection(0, true)
            "Wind" -> alertEventSpinner.setSelection(1, true)
        }

        when (alert.alertType) {
            "Notification" -> alertTypeSpinner.setSelection(0, true)
            "Alarm" -> alertTypeSpinner.setSelection(1, true)
        }

        timeLbl.text = alert.alertTime

        val days = alert.alertDay.split(",")
        for (day in days){
            when (day){
                "SAT" -> satCheckBox.isChecked = true
                "SUN" -> sunCheckBox.isChecked = true
                "MON" -> monCheckBox.isChecked = true
                "TUE" -> tueCheckBox.isChecked = true
                "WED" -> wedCheckBox.isChecked = true
                "THU" -> thuCheckBox.isChecked = true
                "FRI" -> friCheckBox.isChecked = true
                "ALL" -> {
                    satCheckBox.isChecked = true
                    sunCheckBox.isChecked = true
                    monCheckBox.isChecked = true
                    tueCheckBox.isChecked = true
                    wedCheckBox.isChecked = true
                    thuCheckBox.isChecked = true
                    friCheckBox.isChecked = true
                }
                "WEEKEND" -> {
                    friCheckBox.isChecked = true
                    satCheckBox.isChecked = true
                }
            }
        }

        addbtn.text = getText(R.string.updateBtn)
    }

    private fun addOrUpdate() {
        val days = StringBuilder()

        if (satCheckBox.isChecked && sunCheckBox.isChecked && monCheckBox.isChecked && tueCheckBox.isChecked &&
            wedCheckBox.isChecked && thuCheckBox.isChecked && friCheckBox.isChecked) {
            days.append("ALL")
        }else if (satCheckBox.isChecked && !sunCheckBox.isChecked && !monCheckBox.isChecked && !tueCheckBox.isChecked &&
            !wedCheckBox.isChecked && !thuCheckBox.isChecked && friCheckBox.isChecked) {
            days.append("WEEKEND")
        } else {
            if (satCheckBox.isChecked) {
                days.append("SAT,")
            }
            if (sunCheckBox.isChecked) {
                days.append("SUN,")
            }
            if (monCheckBox.isChecked) {
                days.append("MON,")
            }
            if (tueCheckBox.isChecked) {
                days.append("TUE,")
            }
            if (wedCheckBox.isChecked) {
                days.append("WED,")
            }
            if (thuCheckBox.isChecked) {
                days.append("THU,")
            }
            if (friCheckBox.isChecked) {
                days.append("FRI,")
            }
            if(satCheckBox.isChecked || sunCheckBox.isChecked || monCheckBox.isChecked ||tueCheckBox.isChecked ||
                    wedCheckBox.isChecked || thuCheckBox.isChecked || friCheckBox.isChecked) {
                days.deleteCharAt(days.lastIndex)
            }else {
                days.append("No Repeat")
            }
        }

        val newAlert = Alert()
        newAlert.alertTime = timeLbl.text.toString()
        newAlert. alertDay = days.toString()
        newAlert.alertEvent = alertEventSpinner.selectedItem.toString()
        newAlert. alertType = alertTypeSpinner.selectedItem.toString()
        newAlert.  enabled = true
        newAlert. event = ""
        newAlert.start = 0L
        newAlert. end = 0L
        newAlert.description = ""
        //viewModel.insertOrUpdate(newAlert)
        finish()
    }
}