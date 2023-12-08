package com.example.quadcare

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.example.quadcare.MyAccessibilityService
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log

class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {
        Log.e(TAG, "onAccessibilityEvent: ")
        val packageName = accessibilityEvent.packageName.toString()
        val packageManager = this.packageManager
        try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val applicationLabel = packageManager.getApplicationLabel(applicationInfo)
            Log.e(TAG, "App name is :  $applicationLabel")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onInterrupt() {
        Log.e(TAG, "onInterrupt: something went wrong")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or
                AccessibilityEvent.TYPE_VIEW_FOCUSED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
        info.notificationTimeout = 100
        this.serviceInfo = info
        Log.e(TAG, "onServiceConnected: ")
    }

    companion object {
        private const val TAG = "MyAccessibilityService"
    }
}
