package com.razoan.appscheduler
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.razoan.appscheduler.util.UtilClass
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        checkOverlayPermission()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        checkOverlayPermission()
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            /*Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }*/
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
        } else {
            UtilClass.goToNextActivity(this, AddAppActivity:: class.java)
        }
    }

    private fun initListener() {

        /*btPermission.setOnClickListener {


        }*/
    }
}