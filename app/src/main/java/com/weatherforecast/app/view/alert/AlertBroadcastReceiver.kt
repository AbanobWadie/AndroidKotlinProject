package com.weatherforecast.app.view.alert

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.Intent.ACTION_TIME_TICK
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.preference.PreferenceManager
import com.weatherforecast.app.R
import com.weatherforecast.app.model.Alert
import com.weatherforecast.app.model.datasource.external.WeatherService
import com.weatherforecast.app.model.datasource.internal.AlertDao
import com.weatherforecast.app.model.datasource.internal.AppDatabase
import com.weatherforecast.app.view.main.MainActivity
import kotlinx.coroutines.*
import java.util.*


class AlertBroadcastReceiver: BroadcastReceiver() {

    private val NOTIFICATION_ID = 2
    private val CHANNEL_ID = "1"

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.i("call", "11111111111111111111111111111111111111111111111111")
        when (p1?.action) {
            //ACTION_DATE_CHANGED  -> getAlert()
            //ACTION_TIME_TICK -> getDatabaseAlert(p0!!)
        }
    }

    private fun getApiAlert(context: Context, databaseData: List<Alert>) {
//        val pref = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
//        val lat = pref.getFloat("lat", 0.0F)
//        val lon = pref.getFloat("lon", 0.0F)
//        val unit = pref.getString("unit", "metric")!!
//        val language = pref.getString("language", "en")!!
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = WeatherService.getWeatherService().getWeatherInfo(lat.toDouble(), lon.toDouble(), unit, "current,minutely,hourly,daily", language, "67bc71589f11ab9e108b887f0bab9bfc")
//            withContext(Dispatchers.Main){
//                if(response.isSuccessful){
//                    if(!response.body()!!.alert.isNullOrEmpty()) {
//                        val data = response.body()!!.alert!!
//                        checkAlert(context, databaseData, data)
//                    }
//                }
//            }
//        }

        val newAlert = Alert()
        newAlert. event = "Temp"
        newAlert.start = 0L
        newAlert. end = 0L
        newAlert.description = "heat heat heat"
        val data = listOf(newAlert)
        checkAlert(context, databaseData, data)
    }

    private fun getDatabaseAlert(context: Context) {
//        val db: AppDatabase = AppDatabase.getDatabase(context.applicationContext)!!
//        val alertDao: AlertDao = db.alertDao()
//
//        val calendar = Calendar.getInstance()
//        val currentTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
//        CoroutineScope(Dispatchers.IO).launch {
//            val data = alertDao.getSome(currentTime, true)
//            withContext(Dispatchers.Main) {
//                if(!data.isNullOrEmpty()){
//                    getApiAlert(context, data)
//                }
//            }
//        }

        val newAlert = Alert()
        newAlert.alertTime = "19:55"
        newAlert. alertDay = "MON,TUE"
        newAlert. alertDayAr = "الاثنين,الثلاثاء"
        newAlert.alertEvent = 0
        newAlert. alertType = 0
        newAlert.  enabled = true
        newAlert. event = ""
        newAlert.start = 0L
        newAlert. end = 0L
        newAlert.description = ""

        val data = listOf(newAlert)
        getApiAlert(context, data)
    }

    private fun checkAlert(context: Context, databaseAlertInfo: List<Alert>, apiAlertInfo: List<Alert>) {
        val calendar = Calendar.getInstance()
        val currentDayNum = calendar.get(Calendar.DAY_OF_WEEK)
        var currentDay = ""

        when (currentDayNum) {
            1 -> currentDay = "SUN"
            2 -> currentDay = "MON"
            3 -> currentDay = "TUE"
            4 -> currentDay = "WED"
            5 -> currentDay = "THU"
            6 -> currentDay = "FRI"
            7 -> currentDay = "SAT"
        }

        for (api in apiAlertInfo){
            for (db in databaseAlertInfo){
                val alertEventArray: List<String> = context.resources.getStringArray(R.array.alertEvent).toList()
                if(api.event == alertEventArray[db.alertEvent]) {
                    if (db.alertDay.contains(currentDay) || db.alertDay == "ALL" || (db.alertDay == "WEEKEND" && (currentDay == "FRI" || currentDay == "SAT"))) {
                        if (db.alertType == 0){
                            showAlert(context, api.event, api.description, context.getText(R.string.Open).toString(), context.getText(R.string.Stop).toString())
                        }else{
                            showForegroundNotification(api.event, api.description, context)
                        }
                    } else if (db.alertDay == "NONE") {
                        if (db.alertType == 0){
                            showAlert(context, api.event, api.description, context.getText(R.string.Open).toString(), context.getText(R.string.Stop).toString())
                        }else{
                            showForegroundNotification(api.event, api.description, context)
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

    @SuppressLint("ResourceAsColor")
    private fun showForegroundNotification(notificationTitle: String, notificationBody: String, context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK

        val lowIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        val mNotifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.channel_name)
            val description: String = context.getString(R.string.channel_description) //user visible
            val importance = NotificationManager.IMPORTANCE_LOW
            val att = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = description
            mChannel.enableLights(false)
            mChannel.enableVibration(false)
            mChannel.vibrationPattern = longArrayOf(0L)
            mChannel.setSound(null, att)
            mNotifyManager.createNotificationChannel(mChannel)
            notificationBuilder
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setVibrate(longArrayOf(0L))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setColor(ContextCompat.getColor(context, R.color.teal_700))
                    .setContentTitle(notificationTitle)
                    .setAutoCancel(true)
                    .setContentIntent(lowIntent)
        } else {
            notificationBuilder.setContentTitle(notificationTitle)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setVibrate(longArrayOf(0L))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setColor(ContextCompat.getColor(context, R.color.teal_700))
                    .setAutoCancel(true)
                    .setContentIntent(lowIntent)
        }
        notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(notificationBody))
        notificationBuilder.setContentText(notificationBody)
        mNotifyManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}