package com.touchoverlay.ai.access

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

class TouchService : AccessibilityService() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.touchoverlay.ai.ACTION_TAP" -> {
                    val x = intent.getFloatExtra("x", 0.5f); val y = intent.getFloatExtra("y", 0.5f)
                    tapRatio(x, y)
                }
                "com.touchoverlay.ai.ACTION_SWIPE" -> {
                    val x1 = intent.getFloatExtra("x1", 0.5f); val y1 = intent.getFloatExtra("y1", 0.5f)
                    val x2 = intent.getFloatExtra("x2", 0.7f); val y2 = intent.getFloatExtra("y2", 0.7f)
                    swipeRatio(x1, y1, x2, y2, intent.getLongExtra("dur", 200L))
                }
            }
        }
    }
    override fun onServiceConnected() {
        super.onServiceConnected()
        val f = IntentFilter().apply { addAction("com.touchoverlay.ai.ACTION_TAP"); addAction("com.touchoverlay.ai.ACTION_SWIPE") }
        registerReceiver(receiver, f)
    }
    override fun onUnbind(intent: Intent?): Boolean { try { unregisterReceiver(receiver) } catch (_: Exception) {}; return super.onUnbind(intent) }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) { }
    override fun onInterrupt() { }
    private fun screen(): Pair<Int,Int> {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val m = DisplayMetrics(); @Suppress("DEPRECATION") wm.defaultDisplay.getMetrics(m); return m.widthPixels to m.heightPixels
    }
    private fun tapRatio(rx: Float, ry: Float) {
        val (w,h) = screen(); val x = w * rx; val y = h * ry
        val path = Path().apply { moveTo(x, y) }
        dispatchGesture(GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(path, 0, 60)).build(), null, null)
    }
    private fun swipeRatio(x1: Float, y1: Float, x2: Float, y2: Float, dur: Long) {
        val (w,h) = screen(); val path = Path().apply { moveTo(w*x1, h*y1); lineTo(w*x2, h*y2) }
        dispatchGesture(GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(path, 0, dur)).build(), null, null)
    }
}