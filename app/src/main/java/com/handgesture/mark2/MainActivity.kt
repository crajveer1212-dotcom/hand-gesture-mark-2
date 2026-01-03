package com.handgesture.mark2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    
    private lateinit var previewView: PreviewView
    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button
    private lateinit var instructionsText: TextView
    private lateinit var cameraExecutor: ExecutorService
    
    private var camera: Camera? = null
    private var isTracking = false
    
    companion object {
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_OVERLAY = 101
        private const val REQUEST_ACCESSIBILITY = 102
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        previewView = findViewById(R.id.previewView)
        statusText = findViewById(R.id.statusText)
        toggleButton = findViewById(R.id.toggleButton)
        instructionsText = findViewById(R.id.instructionsText)
        
        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        
        // Set up button click listener
        toggleButton.setOnClickListener {
            if (isTracking) {
                stopTracking()
            } else {
                startTracking()
            }
        }
        
        // Check and request permissions
        checkPermissions()
    }
    
    private fun checkPermissions() {
        when {
            // Check camera permission
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA
                )
            }
            // Check overlay permission
            !Settings.canDrawOverlays(this) -> {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, REQUEST_OVERLAY)
            }
            // Check accessibility permission
            !isAccessibilityServiceEnabled() -> {
                showAccessibilityDialog()
            }
            // All permissions granted
            else -> {
                startCamera()
            }
        }
    }
    
    private fun isAccessibilityServiceEnabled(): Boolean {
        val service = "${packageName}/${GestureAccessibilityService::class.java.canonicalName}"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(service) == true
    }
    
    private fun showAccessibilityDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Accessibility Permission Required")
            .setMessage("Please enable the Hand Gesture accessibility service to allow gesture control.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivityForResult(intent, REQUEST_ACCESSIBILITY)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            
            // Image analysis for hand tracking (will be added later)
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            
            // Select front camera
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            
            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()
                
                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
                
                updateStatus("Camera ready")
                
            } catch (e: Exception) {
                updateStatus("Camera error: ${e.message}")
            }
            
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun startTracking() {
        if (!isAccessibilityServiceEnabled()) {
            showAccessibilityDialog()
            return
        }
        
        isTracking = true
        toggleButton.text = getString(R.string.stop_tracking)
        updateStatus("Tracking active - Show your hand")
        
        // TODO: Start hand tracking
    }
    
    private fun stopTracking() {
        isTracking = false
        toggleButton.text = getString(R.string.start_tracking)
        updateStatus("Tracking stopped")
        
        // TODO: Stop hand tracking
    }
    
    private fun updateStatus(message: String) {
        runOnUiThread {
            statusText.text = message
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && 
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions()
                } else {
                    Toast.makeText(
                        this,
                        "Camera permission is required",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            REQUEST_OVERLAY, REQUEST_ACCESSIBILITY -> {
                checkPermissions()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
