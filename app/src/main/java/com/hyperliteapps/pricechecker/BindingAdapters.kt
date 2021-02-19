package com.hyperliteapps.pricechecker

import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

//binding adapter to use glide to load images into ImageView
@BindingAdapter(value = ["setImageUrl"])
fun ImageView.bindImageUrl(url: String?) {
    if (url != null && url.isNotBlank()) {
        Glide.with(this.context)
            .load(url)
            .error(R.drawable.ic_round_broken_image_24)
            .into(this)
    } else {
        this.setImageResource(R.drawable.ic_round_broken_image_24)
    }
}

//binding adapter to allow setting adapter in xml file
@BindingAdapter(value = ["setAdapter"])
fun RecyclerView.bindRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
    this.run {
        this.setHasFixedSize(true)
        this.adapter = adapter
    }
}

//binding adapter for setting visibility
@BindingAdapter(value = ["setupVisibility"])
fun ProgressBar.progressVisibility(isLoading: Boolean) {
    isLoading.let {
        isVisible = isLoading
    }
}