package com.example.web


import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


object FileDownloader {
    private const val MEGABYTE = 1024 * 1024
    fun downloadFile(fileUrl: String?, directory: File?) {
        try {
            val url = URL(fileUrl)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect()
            val inputStream: InputStream = urlConnection.inputStream
            val fileOutputStream = FileOutputStream(directory)
            val totalSize: Int = urlConnection.contentLength
            val buffer = ByteArray(MEGABYTE)
            var bufferLength = 0
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                fileOutputStream.write(buffer, 0, bufferLength)
            }
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

class MainActivity: FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/battery"
    private val DOWNLOAD = "DOWNLOAD-NATIVE"

  override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
    super.configureFlutterEngine(flutterEngine)
    MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {

            call, result ->
            if (call.method == "getBatteryLevel") {
                val batteryLevel = getBatteryLevel()

                if (batteryLevel != -1) {
                result.success(batteryLevel)
                } else {
                result.error("UNAVAILABLE", "Battery level not available.", null)
                }
            } else {
                result.notImplemented()
            }
        }
      MethodChannel(flutterEngine.dartExecutor.binaryMessenger, DOWNLOAD).setMethodCallHandler {

              call, result ->
          if(call.method== "download"){
              val hashMap = call.arguments as HashMap<*, *> //Get the arguments as a HashMap

              val link: String? = hashMap["link"] as String?
              val name: String? = hashMap["name"] as String?
              if (link != null && name != null) {
                  DownloadFile().execute(link, name)
                  result.success("File downloaded")
              }else{
                  result.error("SOMETHING WENT WRONG", "NOT ABLE TO DOWNLOAD", null)
              }
          }
      }
    }
    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }

        return batteryLevel
    }

    private class DownloadFile :
        AsyncTask<String?, Void?, Void?>() {
        protected override fun doInBackground(vararg strings: String?): Void? {
            val fileUrl = strings[0] // -> http://maven.apache.org/maven-1.x/maven.pdf
            val fileName = strings[1] // -> maven.pdf
            val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
            val folder = File(extStorageDirectory, "BankBuddy")
            folder.mkdir()
            val pdfFile = File(folder, fileName)
            try {
                pdfFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            FileDownloader.downloadFile(fileUrl, pdfFile)
            return null
        }
    }


}
    







