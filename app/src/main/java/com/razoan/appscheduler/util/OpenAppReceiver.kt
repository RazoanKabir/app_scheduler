package com.razoan.appscheduler.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log

class OpenAppReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.getStringExtra(Constants.appPackageName)
        val pm: PackageManager = context?.packageManager!!
        val isInstalled = isPackageInstalled(packageName!!, pm)
        if (isInstalled) {
            val launchIntent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                context.startActivity(launchIntent)

            }
        } else {
            UtilClass.showToast(context, "app not installed!")
        }
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        var found = true
        try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            found = false
        }
        return found
    }
}