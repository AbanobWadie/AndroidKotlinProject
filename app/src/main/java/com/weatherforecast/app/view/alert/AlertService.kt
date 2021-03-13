package com.weatherforecast.app.view.alert

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.weatherforecast.app.R
import com.weatherforecast.app.view.main.MainActivity


class AlertService : Service() {
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "1"
    private val myReceiver = AlertBroadcastReceiver()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("call", "11111111111111111111111111111111111111111111111111")

        showForegroundNotification("title", "body")
        sendBroadcast()
        // If we get killed, after returning from here, restart
        return START_STICKY
    }



    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        val restartServicePendingIntent = PendingIntent.getService(
                applicationContext,
                1,
                restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT
        )
        val alarmService = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000] =
            restartServicePendingIntent
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("call", "22222222222222222222222222222222222222222222222222222222")
        unregisterReceiver(myReceiver)
    }

    private fun showForegroundNotification(notificationTitle: String, notificationBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK

        val id: String = CHANNEL_ID
        val lowIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder = NotificationCompat.Builder(this, id)
        val mNotifyManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = this.getString(R.string.channel_name)
            val description: String = this.getString(R.string.channel_description) //user visible
            val importance = NotificationManager.IMPORTANCE_LOW
            val att = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = description
            mChannel.enableLights(false)
            mChannel.enableVibration(false)
            mChannel.vibrationPattern = longArrayOf(0L)
            mChannel.setSound(null, att)
            mNotifyManager.createNotificationChannel(mChannel)
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_forecast)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setVibrate(longArrayOf(0L))
                    .setSound(null)
                    .setColor(ContextCompat.getColor(this, R.color.teal_700))
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setContentIntent(lowIntent)
        } else {
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_forecast)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setVibrate(longArrayOf(0L))
                    .setSound(null)
                    .setColor(ContextCompat.getColor(this, R.color.teal_700))
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setContentIntent(lowIntent)
        }
        val contentView = RemoteViews(packageName, R.layout.notification_view)
        notificationBuilder.setContent(contentView)
        mNotifyManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun sendBroadcast() {
        //val intentFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)
        val intentFilter = IntentFilter(Intent.ACTION_TIME_TICK)
        registerReceiver(myReceiver, intentFilter)
    }

}