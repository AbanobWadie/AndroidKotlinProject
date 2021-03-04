package com.weatherforecast.app.view.alert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.Intent.ACTION_TIME_TICK
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.weatherforecast.app.R
import com.weatherforecast.app.model.WeatherInfo


class AlertBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.i("call", "11111111111111111111111111111111111111111111111111")
        when (p1?.action) {
            //ACTION_DATE_CHANGED  -> getAlert()
            ACTION_TIME_TICK -> getAlert(p0!!)
        }
    }

    private fun getAlert(context: Context) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = WeatherService.getWeatherService().getWeatherInfo(33.441792, -94.037689, "metric", "current,minutely,hourly,daily", "en", "67bc71589f11ab9e108b887f0bab9bfc")
//            withContext(Dispatchers.Main){
//                if(response.isSuccessful){
//                    val alertInfo = response.body()!!
//                    println(alertInfo.lat)
//                    //checkAlert(alertInfo, context)
//                    showAlert(context, "Alert", "alertInfo.alert!![0].description", "Yes", "No")
//                }
//            }
//        }
    }

    private fun checkAlert(data: WeatherInfo, context: Context) {
//        if(data.alert != null){
//            for (alert in data.alert) {
//                if(alert.event == alertEvent) {
//                    var start = java.util.Date(alert.start - 3600 * 12000)
//                    var end = java.util.Date(alert.end - 3600 * 12000)
//
//                    if((startAlert >= sdf.format(start) && startAlert <= alert.end) || (endAlert >= alert.start && endAlert <= alert.end)){
//                        showAlert(context, "Alert", data.alert[0].description, "Yes", "No")
//                    }
//
//                }
//            }
//        }
    }

    private fun showAlert(context: Context, title: String?, body: String?, yes: String?, no: String?) {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context, notification)
        ringtone.play()

        val manager = context.applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.gravity = Gravity.CENTER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.alpha = 1.0f
        layoutParams.packageName = context.packageName
        layoutParams.buttonBrightness = 1f
        val view = View.inflate(context.applicationContext, R.layout.alert_view, null)
        val titleLbl = view.findViewById<View>(R.id.alertTitle) as TextView
        val bodyLbl = view.findViewById<View>(R.id.alertBody) as TextView
        val yesButton = view.findViewById<View>(R.id.yesBtn) as Button
        val noButton = view.findViewById<View>(R.id.noBtn) as Button
        titleLbl.text = title
        bodyLbl.text = body
        yesButton.text = yes
        noButton.text = no
        yesButton.setOnClickListener {
            manager.removeView(view)
            ringtone.stop()
        }
        noButton.setOnClickListener {
            manager.removeView(view)
            ringtone.stop()
        }
        manager.addView(view, layoutParams)
    }
}