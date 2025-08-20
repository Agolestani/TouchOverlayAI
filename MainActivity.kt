package com.touchoverlay.ai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.touchoverlay.ai.overlay.OverlayService
import com.touchoverlay.ai.util.AccessibilityUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppUI() }
    }
    @Composable
    private fun AppUI() {
        Surface {
            Column(Modifier.fillMaxSize().padding(24.dp)) {
                Text("TouchOverlayAI", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    if (!Settings.canDrawOverlays(this@MainActivity)) {
                        val i = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                        startActivity(i)
                    }
                }) { Text("۱) فعال‌سازی مجوز نمایش روی برنامه‌ها") }
                Spacer(Modifier.height(12.dp))
                Button(onClick = { AccessibilityUtils.openAccessibilitySettings(this@MainActivity) }) {
                    Text("۲) فعال‌سازی سرویس دسترس‌پذیری")
                }
                Spacer(Modifier.height(12.dp))
                Button(onClick = { startService(Intent(this@MainActivity, OverlayService::class.java)) }) {
                    Text("۳) شروع اورلی شناور")
                }
                Spacer(Modifier.height(12.dp))
                Text("راهنما: بعد از فعال‌سازی، روی حباب بزن؛ یک Tap آزمایشی وسط صفحه اجرا می‌شود.")
            }
        }
    }
}