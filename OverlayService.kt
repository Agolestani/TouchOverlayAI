package com.touchoverlay.ai.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.ImageView

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: View? = null
    override fun onCreate() {
        super.onCreate()
        createNotification()
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START; params.x = 50; params.y = 200
        bubble = ImageView(this).apply {
            setImageResource(android.R.drawable.btn_star_big_on); alpha = 0.7f
            setOnTouchListener(DragTouchListener(params))
            setOnClickListener {
                val i = Intent("com.touchoverlay.ai.ACTION_TAP"); i.putExtra("x", 0.5f); i.putExtra("y", 0.5f); sendBroadcast(i)
            }
        }
        wm.addView(bubble, params)
    }
    private fun createNotification() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "overlay"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) nm.createNotificationChannel(NotificationChannel(channelId, "Overlay", NotificationManager.IMPORTANCE_LOW))
        val n = Notification.Builder(this, channelId).setContentTitle("TouchOverlayAI فعال است").setSmallIcon(android.R.drawable.ic_media_play).build()
        startForeground(1, n)
    }
    override fun onDestroy() { bubble?.let { wm.removeView(it) }; bubble = null; super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null
    inner class DragTouchListener(private val params: WindowManager.LayoutParams) : View.OnTouchListener {
        private var initialX = 0; private var initialY = 0; private var initialTouchX = 0f; private var initialTouchY = 0f
        override fun onTouch(v: View?, e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> { initialX = params.x; initialY = params.y; initialTouchX = e.rawX; initialTouchY = e.rawY; return false }
                MotionEvent.ACTION_MOVE -> { params.x = initialX + (e.rawX - initialTouchX).toInt(); params.y = initialY + (e.rawY - initialTouchY).toInt(); wm.updateViewLayout(bubble, params); return true }
            }; return false
        }
    }
}