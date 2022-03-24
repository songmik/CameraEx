package com.example.a25_camera.extensions

import android.content.Context
import com.example.a25_camera.R
import java.io.File

internal fun Context.getAppCachePictuerDirectory() : File {
    val mediaDir = filesDir?.let {
        File(it, getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) {
        mediaDir
    } else {
        filesDir
    }
}