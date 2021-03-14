package com.weatherforecast.app.view.splashScreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.weatherforecast.app.R
import com.weatherforecast.app.view.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {
    lateinit var gifView: ImageView
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        gifView = findViewById(R.id.splach)

        Glide.with(this)
            .load(R.drawable.googleweather)
            .into(gifView)

        val timer = object: CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
        timer.start()
    }
}