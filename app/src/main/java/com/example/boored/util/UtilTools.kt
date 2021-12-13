package com.example.boored.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.downloader.Error
import com.downloader.OnDownloadListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

fun focusKeyboard(context: Context, view: SearchView) {
//    view.requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)

}

fun createFileName(fileUrl: String): String {
    return "[^/]+$".toRegex().find(fileUrl)!!.value
}

suspend fun hasInternet(context: Context) : Boolean {
    Log.i("Test", "Internet check...")
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    connectivityManager?.let {
        Log.i("Test", "Connectivity manager")
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        capabilities?.let {
            Log.i("Test", "Capabilities")
            val urlc = URL("https://clients3.google.com/generate_204").openConnection() as HttpURLConnection
            urlc.apply {
                setRequestProperty("User-Agent", "Android")
                setRequestProperty("Connection", "close")
                connectTimeout = 1500
                connect()
            }
            Log.i("test", urlc.responseCode.toString())
            return true
        }
    }
    return false
}