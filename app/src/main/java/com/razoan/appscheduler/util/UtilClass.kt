package com.razoan.appscheduler.util

import android.content.Context
import android.content.Intent

class UtilClass {
    companion object {
        fun goToNextActivity(context: Context, targetActivity: Class<*>?) {
            val `in` = Intent(context, targetActivity)
            context.startActivity(`in`)
        }
    }
}