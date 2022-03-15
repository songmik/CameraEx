package com.example.a25_camera

import androidx.camera.core.CameraX
import androidx.camera.core.CameraXConfig

class Application: Application(), CameraXConfig.Provider {
    override fun getCameraXConfig(): CameraXConfig {
        TODO("Not yet implemented")
    }
}