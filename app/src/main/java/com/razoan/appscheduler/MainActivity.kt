package com.razoan.appscheduler
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.razoan.appscheduler.adapter.ScheduledAppListAdapter
import com.razoan.appscheduler.handler.DatabaseHandler
import com.razoan.appscheduler.model.AppSelectionModel
import com.razoan.appscheduler.util.UtilClass
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.rlAppList
import kotlinx.android.synthetic.main.activity_main.rvAppList

class MainActivity : AppCompatActivity() {
    private var scheduledAppListAdapter: ScheduledAppListAdapter? = null
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
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
        } else {
            val selectedAppList: ArrayList<AppSelectionModel> = DatabaseHandler(this).viewScheduledApp()
            if(selectedAppList.size > 0) {
                rvAppList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rvAppList?.setHasFixedSize(true)
                rvAppList?.isNestedScrollingEnabled = false
                scheduledAppListAdapter = ScheduledAppListAdapter(selectedAppList)
                rvAppList?.adapter = scheduledAppListAdapter
                rlAppList.visibility = View.VISIBLE
            } else {
                rlAppList.visibility = View.GONE
                rlEmptyView.visibility = View.VISIBLE

            }
        }
    }

    private fun initListener() {
        addApp.setOnClickListener {
            UtilClass.goToNextActivity(this, AddAppActivity:: class.java)
        }
        srDashboard.setOnRefreshListener {
            checkOverlayPermission()
            srDashboard.isRefreshing = false
        }

    }
}