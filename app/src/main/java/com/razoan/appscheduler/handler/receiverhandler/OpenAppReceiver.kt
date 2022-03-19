package com.razoan.appscheduler.handler.receiverhandler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.razoan.appscheduler.R
import com.razoan.appscheduler.handler.dbhandler.DatabaseHandler
import com.razoan.appscheduler.model.AppSelectionModel
import com.razoan.appscheduler.util.Constants
import com.razoan.appscheduler.util.UtilClass


class OpenAppReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val app = Gson().fromJson<AppSelectionModel>(
            intent?.getStringExtra(Constants.appToJSON),
            AppSelectionModel::class.java
        )
        val pm: PackageManager = context?.packageManager!!
        val isInstalled = isPackageInstalled(app.appPackageName!!, pm)
        if (isInstalled) {
            launchNotification(context, app)
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

    private fun launchNotification(context: Context, app: AppSelectionModel) {
        val notificationChannel: NotificationChannel
        val builder: NotificationCompat.Builder
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, context.javaClass)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = "AppScheduler"
        val description = "App scheduler open"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = NotificationCompat.Builder(context, channelId)
                .setContentTitle("${app.appName} was scheduled")
                .setContentText("${app.appName} was scheduled to open at ${app.dateTime}")
                .setSmallIcon(R.drawable.ic_splash)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_splash))
                .setContentIntent(pendingIntent)
        } else {
            builder = NotificationCompat.Builder(context)
                .setContentTitle("${app.appName} was scheduled")
                .setContentText("${app.appName} was scheduled to open at ${app.dateTime}")
                .setSmallIcon(R.drawable.ic_splash)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_splash))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(UtilClass.getRandom(), builder.build())
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
                app.second,
                "0",
                "1"
            )
        )
        if (status != null) {
            if (status > -1) {
                status = DatabaseHandler(context).deleteSchedule(app.id, context)?.toLong()
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
