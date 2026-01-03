package com.handgesture.mark2

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class GestureAccessibilityService : AccessibilityService() {
    
    companion object {
        private var instance: GestureAccessibilityService? = null
        
        fun getInstance(): GestureAccessibilityService? = instance
        
        fun isServiceEnabled(): Boolean = instance != null
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not needed for gesture control
    }
    
    override fun onInterrupt() {
        // Handle interruption
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
    
    // Perform tap gesture at coordinates
    fun performTap(x: Float, y: Float): Boolean {
        val path = Path()
        path.moveTo(x, y)
        
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(
            GestureDescription.StrokeDescription(path, 0, 100)
        )
        
        return dispatchGesture(gestureBuilder.build(), null, null)
    }
    
    // Perform swipe gesture
    fun performSwipe(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        duration: Long = 300
    ): Boolean {
        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)
        
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(
            GestureDescription.StrokeDescription(path, 0, duration)
        )
        
        return dispatchGesture(gestureBuilder.build(), null, null)
    }
    
    // Perform pinch gesture (zoom)
    fun performPinch(
        centerX: Float,
        centerY: Float,
        startDistance: Float,
        endDistance: Float,
        duration: Long = 300
    ): Boolean {
        // Create two paths for pinch gesture
        val path1 = Path()
        val path2 = Path()
        
        // Calculate start and end points for both fingers
        val startOffset = startDistance / 2
        val endOffset = endDistance / 2
        
        // First finger path
        path1.moveTo(centerX - startOffset, centerY)
        path1.lineTo(centerX - endOffset, centerY)
        
        // Second finger path
        path2.moveTo(centerX + startOffset, centerY)
        path2.lineTo(centerX + endOffset, centerY)
        
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(
            GestureDescription.StrokeDescription(path1, 0, duration)
        )
        gestureBuilder.addStroke(
            GestureDescription.StrokeDescription(path2, 0, duration)
        )
        
        return dispatchGesture(gestureBuilder.build(), null, null)
    }
}
