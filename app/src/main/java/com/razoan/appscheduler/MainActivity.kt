package com.razoan.appscheduler
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.razoan.appscheduler.adapter.ScheduledAppListAdapter
import com.razoan.appscheduler.handler.DatabaseHandler
import com.razoan.appscheduler.model.AppSelectionModel
import com.razoan.appscheduler.util.UtilClass
import com.razoan.appscheduler.util.ViewDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ViewDialog.DeletedApp {
    private var scheduledAppListAdapter: ScheduledAppListAdapter? = null
    private var toolbarMenu: Menu? = null
    private var menuOption: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        initActionBar()
        checkOverlayPermission()
        initListener()
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
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
                scheduledAppListAdapter = ScheduledAppListAdapter(selectedAppList, this)
                rvAppList?.adapter = scheduledAppListAdapter
                rlAppList.visibility = View.VISIBLE
            } else {
                rlAppList.visibility = View.GONE
                rlEmptyView.visibility = View.VISIBLE

            }
        }
    }

    private fun initListener() {
        fabNewApp.setOnClickListener {
            UtilClass.goToNextActivity(this, AddAppActivity:: class.java)
        }
        srDashboard.setOnRefreshListener {
            checkOverlayPermission()
            srDashboard.isRefreshing = false
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_scheduler_menu, menu)
        toolbarMenu = menu
        menuOption = menu.findItem(R.id.app_scheduler_menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll -> {

            }

            R.id.restartAll -> {

            }

            R.id.appHistory -> {
                UtilClass.goToNextActivity(this, AppHistoryActivity::class.java)
            }
        }
        return true
    }

    override fun appDeleted(isDeleted: Boolean) {
        if(isDeleted) checkOverlayPermission()
    }
}