package com.razoan.appscheduler

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.razoan.appscheduler.util.Constants
import com.razoan.appscheduler.util.UtilClass
import kotlinx.android.synthetic.main.activity_add_app.*
import android.content.pm.PackageManager

class AddAppActivity : AppCompatActivity() {
    private var appName: String? = null
    private var appPackageName: String? = null
    private var appIcon: Drawable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)
        checkIntent()
        initListener()
    }

    private fun checkIntent() {
        if (intent != null) {
            if (intent.getStringExtra(Constants.appName) != null && intent.getStringExtra(Constants.appPackageName) != null) {
                appName = intent.getStringExtra(Constants.appName)
                appPackageName = intent.getStringExtra(Constants.appPackageName)
                setAppInfo(appName, appPackageName)
            }
        }
    }

    private fun setAppInfo(appName: String?, appPackageName: String?) {
        rlGoToAppList.visibility = View.GONE
        try {
            appIcon = this.packageManager.getApplicationIcon(appPackageName.toString())
            ivSelectedAppIcon.setImageDrawable(appIcon)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        tvAppName.text = appName
        tvPackageName.text = appPackageName
        rlSelectApp.visibility = View.VISIBLE
    }

    private fun initListener() {
        rlGoToAppList.setOnClickListener {
            UtilClass.goToNextActivity(this, AppListActivity::class.java)
        }

        rlSelectApp.setOnClickListener {
            UtilClass.goToNextActivity(this, AppListActivity::class.java)
        }
    }
}