package com.razoan.appscheduler.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.google.gson.Gson
import com.razoan.appscheduler.handler.DatabaseHandler
import com.razoan.appscheduler.model.AppSelectionModel

class OpenAppReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val app = Gson().fromJson<AppSelectionModel>(
            intent?.getStringExtra(Constants.appToJSON),
            AppSelectionModel::class.java
        )
        val pm: PackageManager = context?.packageManager!!
        val isInstalled = isPackageInstalled(app.appPackageName!!, pm)
        if (isInstalled) {
            val launchIntent: Intent? =
                context.packageManager.getLaunchIntentForPackage(app.appPackageName)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
                updateLatestAppToOpen(context, app)
            }
        } else {
            UtilClass.showToast(context, "app not installed!")
        }
    }

    private fun updateLatestAppToOpen(context: Context, app: AppSelectionModel) {
        var status = DatabaseHandler(context).addAppHistory(
            AppSelectionModel(
                app.id,
                app.appName,
                app.appPackageName,
                app.note,
                app.dateTime,
                app.year,
                app.month,
                app.day,
                app.hour,
                app.minute,
                "0",
                "1"
            )
        )
        if (status != null) {
            if (status > -1) {
                status = DatabaseHandler(context).deleteSchedule(app.id)?.toLong()
                if (status != null) {
                    if (status > -1)
                        DatabaseHandler(context).setLatestScheduledApp(context)
                }
            }
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