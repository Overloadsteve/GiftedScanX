package com.overloadsteve.giftedscanx

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {

    private val loadingScreenDuration = 3000L // 3 second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_screen)

        val iconImageView: ImageView = findViewById(R.id.Alc)
        val fadeInAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.fading_in)
        iconImageView.startAnimation(fadeInAnimation)

        showLoadingScreen()
    }

    private fun showLoadingScreen() {
        Handler().postDelayed({
            // Start the main activity after the delay
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
            finish()
        }, loadingScreenDuration)
    }
}