package com.touchoverlay.ai.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils

object AccessibilityUtils {
    fun isServiceEnabled(context: Context): Boolean {
        val expected = ComponentName(context, com.touchoverlay.ai.access.TouchService::class.java).flattenToString()
        val enabled = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        if (!enabled.isNullOrEmpty()) {
            val s = TextUtils.SimpleStringSplitter(':'); s.setString(enabled)
            while (s.hasNext()) if (s.next().equals(expected, true)) return true
        }
        return false
    }
    fun openAccessibilitySettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}