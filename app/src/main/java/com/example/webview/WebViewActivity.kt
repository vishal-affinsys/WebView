package com.example.webview

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_activity)
        supportActionBar?.hide()
        val myWebView: WebView = findViewById(R.id.cameraView)
        myWebView.settings.javaScriptEnabled = true;
        myWebView.settings.domStorageEnabled = true
        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url);
                return true;
            }
        }
        myWebView.webChromeClient = (object : WebChromeClient() {
            // Need to accept permissions to use the camera
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        })
        val value = 101;
        fun makeRequest() {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),value)
        }
        fun setView() {
            myWebView.loadUrl("https://webcamtests.com")
        }
        makeRequest()
        setView();
    }
}