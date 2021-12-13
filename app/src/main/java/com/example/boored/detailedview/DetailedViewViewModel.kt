package com.example.boored.detailedview

import android.app.Application
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.example.boored.database.getDatabase
import com.example.boored.util.DisplayModel
import com.example.boored.util.PostsRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailedViewViewModel(
    application: Application,
    private val post: DisplayModel,
    private val postNumber: Int
) :
    ViewModel() {

    /**
     * Repository
     */
    private val repository = PostsRepository(getDatabase(application), application)

    /**
     * Live data variables
     */
    private val _linkedTag = MutableLiveData<String>()
    val linkedTag: LiveData<String>
        get() = _linkedTag

    lateinit var isFavourited: LiveData<Boolean>

    private val _sourceClip = MutableLiveData<String>()
    val sourceClip: LiveData<String>
        get() = _sourceClip

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack

    /**
     * General variables for use in UI
     */


    /**
     * Function called from fragment by on-click listener to link tag cards to tag search
     */
    fun searchLinkedTag(tag: String) {
        _linkedTag.value = tag
    }

    fun doneSearchingLinkedTag() {
        _linkedTag.value = null
    }


    /**
     * Observes the current post's favourite status
     */
    fun watchFavouriteStatus() {
       if (post.fileUrl != null) {
           isFavourited = repository.isFavourited(post.fileUrl)
       }
    }

    fun toggleFavourite() {
        if (isFavourited.value!!) {
            removeFromFavourites(post)
        } else {
            addToFavourites(post)
        }
    }

    /**
     * Navigate back from back button on top right
     */
    fun navigateBack() {
        _navigateBack.value = true
    }

    fun doneNavigatingBack() {
        _navigateBack.value = null
    }

    /**
     * Creates Uri out of fileUrl if file is a video
     */
    fun getVideoUri(): Uri {
        return if (post.fileUrl != null) {
            post.fileUrl.toUri().buildUpon().scheme("https").build()
        } else {
            Uri.EMPTY
        }
    }

    /**
     * Initiates videoView with data
     */
    fun setVideoViewData(videoView: VideoView, progressBar: ProgressBar) {
        with (videoView) {
            setVideoURI(getVideoUri())
            canPause()
            canSeekBackward()
            canSeekForward()
            setOnPreparedListener {
                it.setVolume(1F, 1F)
                it.isLooping = true
                progressBar.visibility = View.GONE
            }
            start()
        }
    }

    /**
     * Pastes file source in clipboard
     */
    fun copySourceToClipboard() {
        if ("i.pximg.net".toRegex().containsMatchIn(post.source)) {
            _sourceClip.value = "https://www.pixiv.net/en/artworks/" + "[0-9]+(?=_)".toRegex().find(post.source)?.value
            // + "#big_" + "(?<=_p)[0-9]+".toRegex().find(post.source)?.value ------------> for straight link to single image
        } else {
            _sourceClip.value =  post.source
        }
    }

    fun doneCopyingSourceToClipboard() {
        _sourceClip.value = null
    }

    /**
     * ViewModel factory for this viewModel
     */
    class ViewModelFactory(
        private val application: Application,
        private val post: DisplayModel,
        private val postNumber: Int
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailedViewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailedViewViewModel(application, post, postNumber) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    private fun addToFavourites(post: DisplayModel) {
        viewModelScope.launch {
            repository.addToFavourites(post, java.util.Calendar.getInstance().toString())
        }
    }

    private fun removeFromFavourites(post: DisplayModel) {
        viewModelScope.launch {
            repository.removeFromFavourites(post)
        }
    }
}


