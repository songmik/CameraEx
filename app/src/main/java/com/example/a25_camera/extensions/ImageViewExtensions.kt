package com.example.a25_camera.extensions

import android.view.RoundedCorner
import android.widget.ImageView

private val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

internal fun ImageView.clear() = Glide.with(context).clear(this)

internal fun ImageView.loadCenterCrop(url: String, corner: Float = 0f){
    Glide.with(this)
        .load(url)
        .transison(DrawableTransisonOptions.withCrossFade(factory))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply{
            if (corner > 0) transforms(CenterCrop(), RoundedCorner(corner.fromDpToPx()))
        }
        .into(this)
}