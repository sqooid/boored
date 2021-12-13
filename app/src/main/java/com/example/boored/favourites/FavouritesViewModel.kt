package com.example.boored.favourites

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codekidlabs.storagechooser.utils.DiskUtil
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.example.boored.R
import com.example.boored.database.PostsDao
import com.example.boored.database.getDatabase
import com.example.boored.detailedview.DetailedViewViewModel
import com.example.boored.util.Constants
import com.example.boored.util.DisplayModel
import com.example.boored.util.PostsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class FavouritesViewModel(private val application: Application) : ViewModel() {

    private val repository = PostsRepository(getDatabase(application), application)
    val posts = repository.favouritesList
    private val preferences: SharedPreferences = application.getSharedPreferences(Constants.PREFERENCE_NAME, 0)
    var downloadInProgress = false

    val targetPost = repository.singlePost
    lateinit var existingTargetPost: DisplayModel

    /**
     * Downloads all posts in user's favourites
     */
    fun initiateDownloadAll() {
        downloadInProgress = true
        // Getting download directory
        val directoryString: String =
            requireNotNull(
                preferences.getString(
                    DiskUtil.SC_PREFERENCE_KEY,
                    "/storage/emulated/0/boored"
                )
            )
        Log.i("test", "Directory: $directoryString")
        repository.downloadAllFavourites(application, directoryString)
    }

    /**
     * Shows notification with details about download progress
     */
    fun showNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_baseline_cloud_12)
            setContentTitle("Downloading")
            setContentText("Download in progress")
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(true)
        }

        val totalItems = repository.remainingItemsInDownload
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var currentProgress = 0
        NotificationManagerCompat.from(context).apply {
            builder.setProgress(totalItems, currentProgress, false).setSound(null)
            notify(Constants.NOTIFICATION_ID, builder.build())
            viewModelScope.launch {
                while (repository.remainingItemsInDownload > 0) {
                    Log.i("test", "Loop entered")
                    delay(500)
                    currentProgress = totalItems - repository.remainingItemsInDownload
                    builder.setProgress(totalItems, currentProgress, false)
                    notify(Constants.NOTIFICATION_ID, builder.build())
                }
                downloadInProgress = false
                with (builder) {
                    setContentTitle("Done")
                    setContentText("Download complete")
                    setProgress(0, 0, false)
                }
                notify(Constants.NOTIFICATION_ID, builder.build())
            }
        }
    }

    /**
     * Gets post info from network
     */
    fun getTargetPost(post: DisplayModel) {
        existingTargetPost = post
        viewModelScope.launch {
            repository.getSinglePostFromNetwork(post)
        }
    }

    /**
     * Refreshes post data  in database
     */
    fun refreshInDatabase(oldPost: DisplayModel, newPost: DisplayModel) {
        viewModelScope.launch {
            repository.removeFromFavourites(oldPost)
            repository.addToFavourites(newPost, oldPost.dateFavourited)
        }
    }

    /**
     * Navigation done thing
     */
    fun doneNavigatingToTarget() {
        repository.clearSinglePost()
    }


    /**
     * ViewModel factory for Favourites viewModel
     */
    class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavouritesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavouritesViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}