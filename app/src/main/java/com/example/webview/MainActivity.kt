package com.example.webview

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fab: View = findViewById(R.id.cameraButton)
        fab.setOnClickListener { view ->
            val i = Intent(this, WebViewActivity::class.java)
            startActivity(i)
        }
        val locationButton: View = findViewById(R.id.locationButton)
        locationButton.setOnClickListener { view ->
            val i = Intent(this, LocationActivity::class.java)
            startActivity(i)
        }
        val fileUpload: View = findViewById(R.id.fileUploadButton)
        fileUpload.setOnClickListener { view ->
            val i = Intent(this, FileUploadActivity::class.java)
            startActivity(i)
        }
    }

}