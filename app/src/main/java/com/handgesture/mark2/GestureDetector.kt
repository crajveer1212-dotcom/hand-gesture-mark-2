package com.handgesture.mark2

import android.util.Log
import kotlin.math.abs
import kotlin.math.sqrt

class GestureDetector {
    
    companion object {
        private const val TAG = "GestureDetector"
        
        // Landmark indices
        private const val THUMB_TIP = 4
        private const val INDEX_TIP = 8
        private const val MIDDLE_TIP = 12
        private const val RING_TIP = 16
        private const val PINKY_TIP = 20
        private const val WRIST = 0
        private const val INDEX_MCP = 5
        
        // Thresholds
        private const val PINCH_THRESHOLD = 0.05f
        private const val POINT_THRESHOLD = 0.1f
        private const val SWIPE_THRESHOLD = 0.15f
        private const val HOLD_DURATION_MS = 1000L
    }
    
    enum class Gesture {
        NONE,
        POINT,
        SWIPE_LEFT,
        SWIPE_RIGHT,
        PINCH_IN,
        PINCH_OUT,
        TWO_FINGERS
    }
    
    interface GestureListener {
        fun onGestureDetected(gesture: Gesture, x: Float, y: Float)
    }
    
    private var listener: GestureListener? = null
    private var lastPointPosition: Pair<Float, Float>? = null
    private var pointStartTime: Long = 0
    private var lastSwipeX: Float = 0f
    private var lastPinchDistance: Float = 0f
    
    fun setListener(listener: GestureListener) {
        this.listener = listener
    }
    
    fun detectGesture(landmarks: List<FloatArray>) {
        if (landmarks.size < 21) return
        
        // Check for two fingers (activation gesture)
        if (isTwoFingers(landmarks)) {
            listener?.onGestureDetected(Gesture.TWO_FINGERS, 0f, 0f)
            return
        }
        
        // Check for pinch
        val pinchDistance = calculateDistance(
            landmarks[THUMB_TIP],
            landmarks[INDEX_TIP]
        )
        
        if (pinchDistance < PINCH_THRESHOLD) {
            val gesture = if (lastPinchDistance > 0 && pinchDistance < lastPinchDistance) {
                Gesture.PINCH_IN
            } else {
                Gesture.PINCH_OUT
            }
            
            val centerX = (landmarks[THUMB_TIP][0] + landmarks[INDEX_TIP][0]) / 2
            val centerY = (landmarks[THUMB_TIP][1] + landmarks[INDEX_TIP][1]) / 2
            
            listener?.onGestureDetected(gesture, centerX, centerY)
            lastPinchDistance = pinchDistance
            return
        }
        
        lastPinchDistance = pinchDistance
        
        // Check for pointing gesture
        if (isPointing(landmarks)) {
            val indexTip = landmarks[INDEX_TIP]
            val currentX = indexTip[0]
            val currentY = indexTip[1]
            
            // Check if finger is held still
            if (lastPointPosition != null) {
                val distance = calculateDistance(
                    floatArrayOf(currentX, currentY, 0f),
                    floatArrayOf(lastPointPosition!!.first, lastPointPosition!!.second, 0f)
                )
                
                if (distance < POINT_THRESHOLD) {
                    val holdTime = System.currentTimeMillis() - pointStartTime
                    
                    if (holdTime >= HOLD_DURATION_MS) {
                        listener?.onGestureDetected(Gesture.POINT, currentX, currentY)
                        pointStartTime = System.currentTimeMillis() // Reset to avoid multiple taps
                    }
                } else {
                    // Finger moved, reset
                    lastPointPosition = Pair(currentX, currentY)
                    pointStartTime = System.currentTimeMillis()
                }
            } else {
                lastPointPosition = Pair(currentX, currentY)
                pointStartTime = System.currentTimeMillis()
            }
            
            // Check for swipe
            if (lastSwipeX > 0) {
                val swipeDistance = currentX - lastSwipeX
                
                if (abs(swipeDistance) > SWIPE_THRESHOLD) {
                    val gesture = if (swipeDistance > 0) {
                        Gesture.SWIPE_RIGHT
                    } else {
                        Gesture.SWIPE_LEFT
                    }
                    
                    listener?.onGestureDetected(gesture, currentX, currentY)
                    lastSwipeX = currentX
                }
            } else {
                lastSwipeX = currentX
            }
            
        } else {
            // Reset point tracking
            lastPointPosition = null
            pointStartTime = 0
            lastSwipeX = 0f
        }
    }
    
    private fun isPointing(landmarks: List<FloatArray>): Boolean {
        // Index finger extended, others curled
        val indexExtended = landmarks[INDEX_TIP][1] < landmarks[INDEX_MCP][1]
        val middleCurled = landmarks[MIDDLE_TIP][1] > landmarks[INDEX_MCP][1]
        val ringCurled = landmarks[RING_TIP][1] > landmarks[INDEX_MCP][1]
        val pinkyCurled = landmarks[PINKY_TIP][1] > landmarks[INDEX_MCP][1]
        
        return indexExtended && middleCurled && ringCurled && pinkyCurled
    }
    
    private fun isTwoFingers(landmarks: List<FloatArray>): Boolean {
        // Index and middle fingers extended, others curled
        val indexExtended = landmarks[INDEX_TIP][1] < landmarks[INDEX_MCP][1]
        val middleExtended = landmarks[MIDDLE_TIP][1] < landmarks[INDEX_MCP][1]
        val ringCurled = landmarks[RING_TIP][1] > landmarks[INDEX_MCP][1]
        val pinkyCurled = landmarks[PINKY_TIP][1] > landmarks[INDEX_MCP][1]
        
        return indexExtended && middleExtended && ringCurled && pinkyCurled
    }
    
    private fun calculateDistance(point1: FloatArray, point2: FloatArray): Float {
        val dx = point1[0] - point2[0]
        val dy = point1[1] - point2[1]
        val dz = point1[2] - point2[2]
        return sqrt(dx * dx + dy * dy + dz * dz)
    }
    
    fun reset() {
        lastPointPosition = null
        pointStartTime = 0
        lastSwipeX = 0f
        lastPinchDistance = 0f
    }
}
