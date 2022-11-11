//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.provider.MediaStore
//import android.webkit.ValueCallback
//import android.webkit.WebChromeClient
//import android.webkit.WebView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.FileProvider
//import com.example.webview.R
//import java.io.File
//import java.io.IOException

package com.example.webview

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class FileUploadActivity : AppCompatActivity() {
    private var mUploadMessage:ValueCallback<Uri>? = null

    var uploadMessage:ValueCallback<Array<Uri>>? = null

    var myWebView: WebView? = null

    val REQUEST_SELECT_FILE = 100
    val value = 101
    val FILECHOOSER_RESULTCODE = 1;
    fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            value)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_upload_activity)
        supportActionBar?.hide()
        myWebView = findViewById(R.id.fileUploadView)
        myWebView?.settings?.javaScriptEnabled = true;
        myWebView?.settings?.domStorageEnabled = true
        myWebView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url);
                return true;
            }
        }
        myWebView?.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            val request = DownloadManager.Request(Uri.parse(url))
            request.setMimeType(mimeType)
            //------------------------COOKIE!!------------------------
            val cookies = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("cookie", cookies)
            //------------------------COOKIE!!------------------------
            request.addRequestHeader("User-Agent", userAgent)
            request.setDescription("Downloading file...")
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                URLUtil.guessFileName(url, contentDisposition, mimeType)
            )
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(applicationContext, "Downloading File, Size: ${contentLength/1000000} MB", Toast.LENGTH_LONG).show()
        })
        myWebView?.webChromeClient = object:WebChromeClient() {

            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                Log.d("alert", message)
                val dialogBuilder = AlertDialog.Builder(this@FileUploadActivity)

                dialogBuilder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->
                        result.confirm()
                    }

                val alert = dialogBuilder.create()
                alert.show()

                return true
            }


            // For Lollipop 5.0+ Devices
            override fun onShowFileChooser(mWebView:WebView, filePathCallback:ValueCallback<Array<Uri>>, fileChooserParams:WebChromeClient.FileChooserParams):Boolean {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    if (uploadMessage != null) {
                        uploadMessage?.onReceiveValue(null)
                        uploadMessage = null
                    }
                    uploadMessage = filePathCallback
                    val intent = fileChooserParams.createIntent()
                    try {
                        startActivityForResult(intent, REQUEST_SELECT_FILE)
                    } catch (e: ActivityNotFoundException) {
                        uploadMessage = null
                        Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
                        return false
                    }
                    return true
                }else{
                    return false
                }
            }
        }
        fun setView() {
            myWebView?.loadUrl("www.google.com")
        }

        setView();

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if(requestCode == REQUEST_SELECT_FILE){
                if(uploadMessage != null){
                    uploadMessage?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,data))
                    uploadMessage = null
                }
            }
        }else if(requestCode == FILECHOOSER_RESULTCODE){
            if(mUploadMessage!=null){
                var result = data?.data
                mUploadMessage?.onReceiveValue(result)
                mUploadMessage = null
            }
        }else{
            Toast.makeText(this,"Failed to open file uploader, please check app permissions.",Toast.LENGTH_LONG).show()
            super.onActivityResult(requestCode, resultCode, data)
        }


    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action === KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (myWebView?.canGoBack() == true) {
                        myWebView?.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
