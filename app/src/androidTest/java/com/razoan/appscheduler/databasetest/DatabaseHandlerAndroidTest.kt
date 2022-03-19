package com.razoan.appscheduler.databasetest

import android.content.Context
import android.provider.Settings
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.razoan.appscheduler.handler.dbhandler.DatabaseHandler
import com.razoan.appscheduler.model.AppSelectionModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseHandlerAndroidTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private var mDataSource: DatabaseHandler? = null
    private lateinit var instrumentationCtx: Context

    @Before
    fun setUp() {
        instrumentationCtx = ApplicationProvider.getApplicationContext<Context>()
        mDataSource = DatabaseHandler(instrumentationCtx)
    }

    @After
    fun finish() {
        mDataSource?.close()
    }

    @Test
    fun testPreConditionsAndroid() {
        assertThat(Settings.canDrawOverlays(instrumentationCtx))
        assertThat(mDataSource).isNotEqualTo(null)
    }

    @Test
    @Throws(Exception::class)
    fun testAndroidAddAppTwoToList() {
        var apps: ArrayList<AppSelectionModel>? = mDataSource?.viewScheduledApp()
        mDataSource?.deleteAll(apps)
        mDataSource?.addApp(
            AppSelectionModel(
                0,
                "Pathao",
                "com.pathao.user",
                "Testing add Pathao App for scheduling",
                "19/03/2022 4:12 pm",
                "2022",
                "3",
                "19",
                "16",
                "12",
                "50",
                "0",
                "0"
            ), instrumentationCtx
        )
        mDataSource?.addApp(
            AppSelectionModel(
                1,
                "YouTube",
                "com.google.android.youtube",
                "Testing add YouTube App for scheduling",
                "19/03/2022 4:20 pm",
                "2022",
                "3",
                "19",
                "16",
                "20",
                "50",
                "0",
                "0"
            ), instrumentationCtx
        )
        apps = mDataSource?.viewScheduledApp()
        assertThat(apps?.size).isEqualTo(2)
        assertThat(apps?.get(0)?.appName).isEqualTo("Pathao")
        assertThat(apps?.get(0)?.appPackageName).isEqualTo("com.pathao.user")
    }

    @Test
    @Throws(Exception::class)
    fun testAndroidCheckLatestToLaunch() {
        var apps: ArrayList<AppSelectionModel>? = mDataSource?.viewScheduledApp()
        mDataSource?.deleteAll(apps)
        mDataSource?.addApp(
            AppSelectionModel(
                0,
                "Pathao",
                "com.pathao.user",
                "Testing add Pathao App for scheduling",
                "19/03/2022 4:12:12 pm",
                "2022",
                "3",
                "19",
                "16",
                "12",
                "50",
                "0",
                "0"
            ), instrumentationCtx
        )
        mDataSource?.addApp(
            AppSelectionModel(
                1,
                "YouTube",
                "com.google.android.youtube",
                "Testing add YouTube App for scheduling",
                "19/03/2022 4:20:22 pm",
                "2022",
                "3",
                "19",
                "16",
                "20",
                "50",
                "0",
                "0"
            ), instrumentationCtx
        )
        apps = mDataSource?.getLatest()
        assertThat(apps?.get(apps.size - 1)?.dateTime).isEqualTo("19/03/2022 4:12:12 pm")
        mDataSource?.updateSchedule(
            AppSelectionModel(
                apps?.get(apps.size - 1)?.id,
                "YouTube",
                "com.google.android.youtube",
                "Testing add YouTube App for scheduling",
                "19/03/2022 4:10:22 pm",
                "2022",
                "3",
                "19",
                "16",
                "20",
                "50",
                "0",
                "0"
            ), instrumentationCtx
        )
        apps = mDataSource?.getLatest()
        assertThat(apps?.get(apps.size - 1)?.dateTime).isEqualTo("19/03/2022 4:10:22 pm")
    }

    @Test
    fun testAndroidDeleteAll() {
        var apps = mDataSource?.viewScheduledApp()
        mDataSource?.deleteAll(apps)
        apps = mDataSource?.viewScheduledApp()
        assertThat(apps?.size).isEqualTo(0)
    }

    @Test
    @Throws(Exception::class)
    fun testAndroidAddAppOneToList() {
        var apps = mDataSource?.viewScheduledApp()
        mDataSource?.deleteAll(apps)
        mDataSource?.addApp(
            AppSelectionModel(
                1,
                "YouTube",
                "com.google.android.youtube",
                "Testing add YouTube App for scheduling",
                "19/03/2022 4:20 pm",
                "2022",
                "3",
                "19",
                "16",
                "20",
                "50",
                "0",
                "0"
            ), instrumentationCtx
        )
        apps = mDataSource?.viewScheduledApp()
        assertThat(apps?.size).isEqualTo(1)
        assertThat(apps?.get(0)?.appName).isEqualTo("YouTube")
        assertThat(apps?.get(0)?.appPackageName).isEqualTo("com.google.android.youtube")
    }

    @Test
    fun testDeleteOnlyOne() {
        var apps: ArrayList<AppSelectionModel>? = mDataSource?.viewScheduledApp()
        if (apps?.size!! > 0) {
            mDataSource?.deleteSchedule(apps.get(apps.size - 1).id, instrumentationCtx)
            apps = mDataSource?.viewScheduledApp()
        }
        assertThat(apps?.size).isEqualTo(0)
    }

    @Test
    fun testAndroidAddAndDelete() {
        val appsSizeStart = mDataSource?.viewScheduledApp()
        mDataSource?.deleteAll(appsSizeStart)

        mDataSource?.addApp(
            AppSelectionModel(
                0,
                "Pathao",
                "com.pathao.user",
                "Testing add Pathao App for scheduling",
                "19/03/2022 4:12 pm",
                "2022",
                "3",
                "19",
                "16",
                "12",
                "30",
                "0",
                "0"
            ), instrumentationCtx
        )
        mDataSource?.addApp(
            AppSelectionModel(
                1,
                "YouTube",
                "com.google.android.youtube",
                "Testing add YouTube App for scheduling",
                "19/03/2022 4:20 pm",
                "2022",
                "3",
                "19",
                "16",
                "20",
                "50",
                "0",
                "0"
            ), instrumentationCtx
        )
        var apps = mDataSource?.viewScheduledApp()
        mDataSource?.deleteSchedule(apps?.get(apps.size - 1)?.id, instrumentationCtx)
        apps = mDataSource?.viewScheduledApp()
        mDataSource?.deleteSchedule(apps?.get(apps.size - 1)?.id, instrumentationCtx)

        apps = mDataSource?.viewScheduledApp()
        assertThat(apps?.size).isEqualTo(0)
    }

    @Test
    fun testAndroidUpdate() {
        mDataSource?.addApp(
            AppSelectionModel(
                0,
                "Pathao",
                "com.pathao.user",
                "Testing add Pathao App for scheduling",
                "19/03/2022 4:12 pm",
                "2022",
                "3",
                "19",
                "16",
                "12",
                "50",
                "0",
                "0"
            ), instrumentationCtx
        )
        val appsSizeStart = mDataSource?.viewScheduledApp()
        mDataSource?.updateSchedule(
            AppSelectionModel(
                appsSizeStart?.get(appsSizeStart.size - 1)?.id,
                "YouTube",
                "com.google.android.youtube",
                "Testing Update YouTube App",
                "19/03/2022 4:20 pm",
                "2022",
                "3",
                "19",
                "16",
                "20",
                "50",
                "0",
                "0"
            ), instrumentationCtx
        )
        val apps = mDataSource?.viewScheduledApp()
        assertThat(apps?.get(apps.size - 1)?.appName).isEqualTo("YouTube")
    }

    @Test
    fun testAddHistory() {
        val appsHistorySizeStart = mDataSource?.viewScheduledAppHistory()
        mDataSource?.addAppHistory(
            AppSelectionModel(
                0,
                "YouTube",
                "com.google.android.youtube",
                "Testing Update YouTube App",
                "19/03/2022 4:20 pm",
                "2022",
                "3",
                "19",
                "16",
                "20",
                "50",
                "0",
                "0"
            )
        )
        val appsHistorySize = mDataSource?.viewScheduledAppHistory()
        assertThat(appsHistorySizeStart?.size!! < appsHistorySize?.size!!)
    }

    @Test
    fun testAndroidDeleteAllHistory() {
        var apps = mDataSource?.viewScheduledAppHistory()
        mDataSource?.deleteAllHistory(apps)
        apps = mDataSource?.viewScheduledAppHistory()
        assertThat(apps?.size).isEqualTo(0)
    }
}

