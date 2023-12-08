package com.example.quadcare.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.quadcare.R

class SplashActivity : AppCompatActivity() {

    lateinit var handler: Handler;
    private lateinit var logoview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.d("SplashScreen", "Splash Triggered");

        logoview=findViewById(R.id.logo)

        this.handler = Handler()
        this.handler.postDelayed({
            var intent = Intent(this,MainActivity::class.java);
            startActivity(intent)
            finish()
        },5000);
    }
}
