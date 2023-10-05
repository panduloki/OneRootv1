package com.example.onerootv1

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity



class DisplayImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)
        val imageView = findViewById<ImageView>(R.id.displayImage)

        val path = intent.getStringExtra("path")
        val bitmap = BitmapFactory.decodeFile(path)
        imageView.setImageBitmap(bitmap)

    }
}