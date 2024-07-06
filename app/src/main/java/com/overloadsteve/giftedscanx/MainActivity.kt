package com.overloadsteve.giftedscanx

import android.content.Intent
import android.view.View
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var nextbutton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nextbutton = findViewById(R.id.Next)

        nextbutton.setOnClickListener {
            val intent = Intent(this@MainActivity, OcrActivity::class.java)
            startActivity(intent)
        }
    }
}
