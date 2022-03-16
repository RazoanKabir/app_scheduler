package com.razoan.appscheduler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.razoan.appscheduler.util.UtilClass
import kotlinx.android.synthetic.main.activity_add_app.*

class AddAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)
        initListener()
    }

    private fun initListener() {
        rlGoToAppList.setOnClickListener {
            UtilClass.goToNextActivity(this, AppListActivity::class.java)
        }
    }
}