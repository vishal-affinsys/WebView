package com.example.webview

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class LocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_activity)
        supportActionBar?.hide()
        val myWebView: WebView = findViewById(R.id.locationView)
        myWebView.settings.javaScriptEnabled = true;
        myWebView.settings.domStorageEnabled = true
        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url);
                return true;
            }
        }
        myWebView.webChromeClient = (object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        })
        val value = 101
        fun makeRequest() {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                value)
        }
        fun setView() {
            myWebView.loadUrl("https://mylocation.org/")
        }
        makeRequest()
        setView();

    }
}