package com.razoan.appscheduler.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.razoan.appscheduler.R
import com.razoan.appscheduler.adapter.AppHistoryListAdapter
import com.razoan.appscheduler.handler.DatabaseHandler
import com.razoan.appscheduler.model.AppSelectionModel
import kotlinx.android.synthetic.main.activity_app_history.*

class AppHistoryActivity : AppCompatActivity(), AppHistoryListAdapter.Deleted {
    private var appHistoryListAdapter: AppHistoryListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_history)
        initView()
        intiListener()
    }

    private fun intiListener() {
        srDashboard.setOnRefreshListener {
            initView()
            srDashboard.isRefreshing = false
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initView() {
        val selectedAppList: ArrayList<AppSelectionModel> =
            DatabaseHandler(this).viewScheduledAppHistory()
        if (selectedAppList.size > 0) {
            rvAppHistoryList?.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvAppHistoryList?.setHasFixedSize(true)
            rvAppHistoryList?.isNestedScrollingEnabled = false
            appHistoryListAdapter = AppHistoryListAdapter(selectedAppList, this)
            rvAppHistoryList?.adapter = appHistoryListAdapter
            rvAppHistoryList.visibility = View.VISIBLE
        } else {
            rvAppHistoryList.visibility = View.GONE
            rlEmptyView.visibility = View.VISIBLE
        }
    }

    override fun deleted(isDeleted: Boolean) {
        if(isDeleted) initView()
    }
}