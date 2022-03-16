package com.razoan.appscheduler.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

class UtilClass {
    companion object {
        fun goToNextActivity(context: Context, targetActivity: Class<*>?) {
            val `in` = Intent(context, targetActivity)
            context.startActivity(`in`)
        }

        fun goToNextActivityWithBundle(context: Context, bundle: Bundle?, targetActivity: Class<out Activity?>?) {
            val `in` = Intent(context, targetActivity)
            `in`.putExtras(bundle!!)
            `in`.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(`in`)
        }
        fun goToPreviousActivityWithBundle(context: Context, bundle: Bundle?, targetActivity: Class<out Activity?>?) {
            (context as Activity).finish()
            val intent = Intent(context, targetActivity)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtras(bundle!!)
            context.startActivity(intent)
        }
    }
}