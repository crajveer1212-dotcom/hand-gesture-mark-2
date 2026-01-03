package com.handgesture.mark2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import java.io.File

class HandTracker(private val context: Context) {
    
    private var handLandmarker: HandLandmarker? = null
    private var isInitialized = false
    
    companion object {
        private const val TAG = "HandTracker"
        private const val MODEL_FILENAME = "hand_landmarker.task"
    }
    
    interface HandTrackingListener {
        fun onHandDetected(landmarks: List<FloatArray>)
        fun onNoHandDetected()
        fun onError(error: String)
    }
    
    private var listener: HandTrackingListener? = null
    
    fun setListener(listener: HandTrackingListener) {
        this.listener = listener
    }
    
    fun initialize(): Boolean {
        try {
            // Check if model file exists
            val modelFile = File(context.filesDir, MODEL_FILENAME)
            if (!modelFile.exists()) {
                // Copy from assets
                context.assets.open(MODEL_FILENAME).use { input ->
                    modelFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(MODEL_FILENAME)
                .build()
            
            val options = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setNumHands(1)
                .setMinHandDetectionConfidence(0.5f)
                .setMinHandPresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .build()
            
            handLandmarker = HandLandmarker.createFromOptions(context, options)
            isInitialized = true
            
            Log.d(TAG, "HandTracker initialized successfully")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing HandTracker", e)
            listener?.onError("Failed to initialize: ${e.message}")
            return false
        }
    }
    
    fun processFrame(bitmap: Bitmap) {
        if (!isInitialized || handLandmarker == null) {
            listener?.onError("HandTracker not initialized")
            return
        }
        
        try {
            // Convert bitmap to MPImage
            val mpImage = BitmapImageBuilder(bitmap).build()
            
            // Detect hands
            val result = handLandmarker?.detect(mpImage)
            
            // Process results
            if (result != null && result.landmarks().isNotEmpty()) {
                val landmarks = mutableListOf<FloatArray>()
                
                // Get first hand landmarks
                val handLandmarks = result.landmarks()[0]
                
                for (landmark in handLandmarks) {
                    landmarks.add(
                        floatArrayOf(
                            landmark.x(),
                            landmark.y(),
                            landmark.z()
                        )
                    )
                }
                
                listener?.onHandDetected(landmarks)
            } else {
                listener?.onNoHandDetected()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing frame", e)
            listener?.onError("Processing error: ${e.message}")
        }
    }
    
    fun close() {
        handLandmarker?.close()
        handLandmarker = null
        isInitialized = false
    }
}
