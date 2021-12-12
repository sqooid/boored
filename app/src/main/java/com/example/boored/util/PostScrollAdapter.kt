package com.example.boored.util

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.boored.R
import com.example.boored.databinding.ImagePreviewBinding
import com.example.boored.databinding.ImagePreviewLowresBinding
import com.example.boored.databinding.VideoPreviewBinding

class PostScrollAdapter(
    private val columns: Int,
    private val screenWidth: Int,
    private val imageOnClickListener: OnClickListener,
    private val lowRes: Boolean
) :
    ListAdapter<DisplayModel, RecyclerView.ViewHolder>(DiffUtilCallback) {

    /**
     * Live data to notify fragment that more posts should be retrieved from network
     */
    private val _postsExhausted = MutableLiveData<Boolean>()
    val postsExhausted: LiveData<Boolean>
        get() = _postsExhausted

    /**
     * Item view types
     */
    private val STATIC_ITEM = 0
    private val VIDEO_ITEM = 1

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).fileExt) {
            "mp4", "webm" -> VIDEO_ITEM
            else -> STATIC_ITEM
        }
    }

    /**
     * Standard adapter methods
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            STATIC_ITEM -> ImageViewHolder(ImagePreviewBinding.inflate(LayoutInflater.from(parent.context)))
            else -> return VideoViewHolder(VideoPreviewBinding.inflate(LayoutInflater.from(parent.context)))
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = getItem(position)
        holder.itemView.setOnClickListener {
            imageOnClickListener.onClick(post, position)
        }
        if (position == itemCount - 5) {
            _postsExhausted.value = true
            Log.i("test", "Posts exhausted")
        }
        when (holder) {
            is ImageViewHolder -> holder.bind(post, position)
            is VideoViewHolder -> holder.bind(post, position)
        }
    }

    inner class VideoViewHolder(val binding: VideoPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DisplayModel, itemNumber: Int) {
            binding.post = item
            binding.postNumber = itemNumber + 1
            val width = screenWidth / columns
            val height = (width * item.imageHeight) / item.imageWidth
            binding.previewImageView.layoutParams.width = width
            binding.previewImageView.layoutParams.height = height
            binding.executePendingBindings()
        }
    }

    /**
     * ViewHolder for posts. Width of imageView is set here, to scale image to edge of device screen
     */
    inner class ImageViewHolder(val binding: ImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DisplayModel, itemNumber: Int) {
            binding.post = item
            binding.lowRes = lowRes
//            binding.previewImageView.transitionName = item.id.toString()
            binding.postNumber = itemNumber + 1
            val width = screenWidth / columns
            val height = (width * item.imageHeight) / item.imageWidth
            binding.previewImageView.layoutParams.width = width
            binding.previewImageView.layoutParams.height = height
            binding.executePendingBindings()
        }
    }

    /**
     * Diff util callback functions
     */
    companion object DiffUtilCallback : DiffUtil.ItemCallback<DisplayModel>() {
        override fun areItemsTheSame(oldItem: DisplayModel, newItem: DisplayModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DisplayModel, newItem: DisplayModel): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * onClick listener for navigation
     */
    class OnClickListener(val clickListener: (post: DisplayModel, postNumber: Int) -> Unit) {
        fun onClick(post: DisplayModel, postNumber: Int) =
            clickListener(post, postNumber)
    }

    /**
     * Function to be called after more posts have been retrieved from network in order to reset detection system
     */
    fun doneGettingPosts() {
        _postsExhausted.value = false
    }
}