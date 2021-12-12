package com.example.boored.util

import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.boored.R

/**
 * Updating Recycler View
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<DisplayModel>?) {
    data?.let {
        val adapter = recyclerView.adapter as PostScrollAdapter
        adapter.submitList(data)
    }
}


/**
 * Loading images and gifs from URL
 */
@BindingAdapter("imageUrlWidthHeight")
fun displayImageWidthHeightConstraint(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        val imageUri = imageUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imageView.context)
            .load(imageUri)
            .placeholder(R.drawable.ic_baseline_image_24)
            .thumbnail(0.2f)
            .apply(
                RequestOptions().override(
                    imageView.layoutParams.width,
                    imageView.layoutParams.height
                )
            )
            .into(imageView)
    }
}

/**
 * Loading images and gifs from URL in original res
 */
@BindingAdapter("imageUrlFullRes")
fun displayImageFullRes(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        val imageUri = imageUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imageView.context)
            .load(imageUri)
            .placeholder(R.drawable.ic_baseline_image_24)
            .thumbnail(0.2f)
            .into(imageView)
    }
}

/**
 * Random binding adapter for basic textView
 */
@BindingAdapter("loadedText")
fun displayText(textView: TextView, text: String?) {
    text?.let {
        textView.text = text
    }
}