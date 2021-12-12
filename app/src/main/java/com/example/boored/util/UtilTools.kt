package com.example.boored.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.downloader.Error
import com.downloader.OnDownloadListener

fun focusKeyboard(context: Context, view: SearchView) {
//    view.requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)

}

fun createFileName(fileUrl: String): String {
    return "[^/]+$".toRegex().find(fileUrl)!!.value
}