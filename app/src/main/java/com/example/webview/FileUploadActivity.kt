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
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class FileUploadActivity : AppCompatActivity() {
    private var mUploadMessage:ValueCallback<Uri>? = null

    var uploadMessage:ValueCallback<Array<Uri>>? = null

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
        val myWebView: WebView = findViewById(R.id.fileUploadView)
        myWebView.settings.javaScriptEnabled = true;
        myWebView.settings.domStorageEnabled = true
        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url);
                return true;
            }
        }
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

            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            fun openFileChooser(uploadMsg : ValueCallback<Uri>, acceptType:String) {
                mUploadMessage = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "*/*"
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE)
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

            //For Android 4.1 only
            fun openFileChooser(uploadMsg:ValueCallback<Uri>, acceptType:String, capture:String) {
                mUploadMessage = uploadMsg
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE)
            }

            fun openFileChooser(uploadMsg:ValueCallback<Uri>) {
                makeRequest()
                mUploadMessage = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "*/*"
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE)
            }
        }
        fun setView() {
            myWebView.loadUrl("https://www.ilovepdf.com/pdf_to_word")
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
}
