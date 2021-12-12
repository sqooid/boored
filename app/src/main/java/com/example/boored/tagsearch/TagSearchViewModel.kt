package com.example.boored.tagsearch

import android.app.Application
import androidx.lifecycle.*
import com.example.boored.database.getDatabase
import com.example.boored.util.DisplayModel
import com.example.boored.util.PostsRepository
import kotlinx.coroutines.launch

class TagSearchViewModel(application: Application) : ViewModel() {

    /**
     * Repository
     */
    private val repository = PostsRepository(getDatabase(application))

    val posts = repository.searchList

    private val _postSuccess = MutableLiveData<Boolean>()
    val postSuccess: LiveData<Boolean>
        get() = _postSuccess

    private val _searchParameters = MutableLiveData<String>()
    val searchParameters: LiveData<String>
        get() = _searchParameters

    private var currentTags = ""
    private var pageNumber = 1

    val tagList = repository.tagList


    /**
     * Function that is run when user hits search button. Resets posts
     */
    fun doInitialSearchWithTags(server: Int, tags: String) {
        _searchParameters.value = tags
        currentTags = tags
        pageNumber = 0
        repository.searchLimit = 100
        viewModelScope.launch {
            repository.getPostsFromNetwork(server, tags, pageNumber)
            _postSuccess.value = true
            if (posts.value.isNullOrEmpty()) {
                _postSuccess.value = false
            }
        }
    }

    /**
     * Function to call when the next page of posts is requestd
     */
    fun getMorePosts(server: Int) {
        pageNumber++
        viewModelScope.launch {
            repository.addPostsFromNetwork(server, currentTags, pageNumber)
        }
    }

    /**
     * Calls function in repository to get or update list of suggested tags
     */
    fun updateTagSuggestions(tag: String) {
        viewModelScope.launch {
            repository.getTagsFromNetwork(tag)
        }
    }

    /**
     * ViewModel factory for viewModel. Currently not in use
     */
    class viewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TagSearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TagSearchViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
