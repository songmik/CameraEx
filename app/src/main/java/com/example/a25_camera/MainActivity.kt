package com.example.a25_camera

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics.LENS_FACING
import android.hardware.display.DisplayManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.a25_camera.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private val cameraMainExecutor by lazy { ContextCompat.getMainExecutor(this) }
    private lateinit var imageCapture: ImageCapture
    private val displayManager by lazy {
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }

    private var displayId: Int = -1
    private var camera: Camera? = null
    private var root: View? = null
    private var isFlashEnabled: Boolean = false
    private var uriList = mutableListOf<Uri>()



    private val displayListener = object  : DisplayManager.DisplayListener{
        override fun onDisplayAdded(p0: Int) = Unit

        override fun onDisplayRemoved(p0: Int) = Unit

        override fun onDisplayChanged(p0: Int) {
            if (this@MainActivity.displayId == displayId) {
                if (::imageCapture.isInitialized && root != null) {
                    imageCapture.targetRotation = root?.display?.rotation ?: ImageOutputConfig.INVALID_ROTATION
                }

            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (allPermissionsGranted()){
            startCamera(binding.viewFinder)
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted() = REQUEST_CODE_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera(viewFinder: PreviewView){
        displayManager.registerDisplayListener(displayListener, null)
        cameraExecutor = Excutors.newSingleThreadExecutor()
        viewFinder.postDelayed({
            displayId = viewFinder.display.displayId
            bindCameraUseCase()
        },10)
    }

    private fun bindCameraUseCase() = with(binding) {
        val rotation = viewFinder.display.rotation
        val cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_FACING).build()

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_4_3)
                setTargetRotation(rotation)
            }.build()

            val builder = ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                .setFlashMode(FLASH_MODE_AUTO)

            imageCapture = builder.build()


            try{
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this@MainActivity, cameraSelector, preview, imageCapture
                )
                preview.setSurfaceProvider(viewFinder.surfaceProvider)
                bindCaptureListener()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( requestCode == REQUEST_CODE_PERMISSIONS) {
            startCamera(binding.viewFinder)
        } else {
            Toast.makeText(this, "카메라 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {

        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }
}