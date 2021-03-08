package com.weatherforecast.app.view.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import androidx.core.app.NotificationCompat
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.datasource.external.WeatherService
import com.weatherforecast.app.model.datasource.internal.AlertDao
import com.weatherforecast.app.model.datasource.internal.AppDatabase
import kotlinx.coroutines.*
import java.util.*


class AlertBroadcastReceiver: BroadcastReceiver() {

    private val NOTIFICATION_ID = 2
    private val CHANNEL_ID = "1"

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.i("call", "11111111111111111111111111111111111111111111111111")
        when (p1?.action) {
            //ACTION_DATE_CHANGED  -> getAlert()
            ACTION_TIME_TICK -> getDatabaseAlert(p0!!)
        }
    }

    private fun getApiAlert(context: Context, databaseData: List<Alert>) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = WeatherService.getWeatherService().getWeatherInfo(
                33.441792,
                -94.037689,
                "metric",
                "current,minutely,hourly,daily",
                "en",
                "67bc71589f11ab9e108b887f0bab9bfc"
            )
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    if(!response.body()!!.alert.isNullOrEmpty()) {
                        val data = response.body()!!.alert!!
                        checkAlert(context, databaseData, data)
                    }
                }
            }
        }
    }

    private fun getDatabaseAlert(context: Context) {
//        val db: AppDatabase = AppDatabase.getDatabase(context.applicationContext)!!
//        val alertDao: AlertDao = db.alertDao()
//
//        val calendar = Calendar.getInstance()
//        val currentTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
//        val data = alertDao.getSome(currentTime, true)
//
//        if(!data.isNullOrEmpty()){
//            getApiAlert(context, data)
//        }
    }

    private fun checkAlert(context: Context, databaseAlertInfo: List<Alert>, apiAlertInfo: List<Alert>) {
        val calendar = Calendar.getInstance()
        val currentDay = "${calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH)}"

        for (api in apiAlertInfo){
            for (db in databaseAlertInfo){
                if(api.alertEvent == db.alertEvent) {
                    if (db.alertDay.contains(currentDay) || db.alertDay == "ALL" || (db.alertDay == "WEEKEND" && (currentDay == "FRI" || currentDay == "SAT"))) {
                        if (db.alertType == "Alarm"){
                            showAlert(context, api.alertEvent, api.description, "Open", "Stop")
                        }else{
                            createNotificationChannel(context)
                        }
                    } else if (db.alertDay == "NONE") {
                        if (db.alertType == "Alarm"){
                            showAlert(context, api.alertEvent, api.description, "Open", "Stop")
                        }else{
                            createNotificationChannel(context)
                        }

                        db.enabled = false
                        changeEnable(context, db)
                    }
                }
            }
        }
    }

    private fun changeEnable(context: Context, alert: Alert) {
        val db: AppDatabase = AppDatabase.getDatabase(context.applicationContext)!!
        val alertDao: AlertDao = db.alertDao()

        CoroutineScope(Dispatchers.IO).launch {
            alertDao.insert(alert)
        }
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

            val intent = Intent(context, AlertActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
        noButton.setOnClickListener {
            manager.removeView(view)
            ringtone.stop()
        }
        manager.addView(view, layoutParams)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.channel_name)
            val description = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
            showNotification(notificationManager, context)
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            showNotification(notificationManager, context)
        }
    }

    private fun showNotification(notificationManager: NotificationManager, context: Context?) {
        val intent = Intent(context, AlertActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val builder = NotificationCompat.Builder(
            context!!
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("File Downloaded")
            .setContentText("Downloaded")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}