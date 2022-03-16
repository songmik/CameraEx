package com.example.a25_camera

import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

class Application: Application(), CameraXConfig.Provider {
    override fun getCameraXConfig(): CameraXConfig = Camera2Config.defaultConfig()
}