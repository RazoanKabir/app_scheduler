package com.razoan.appscheduler.databasetest
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.razoan.appscheduler.handler.DatabaseHandler
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
        assertThat(mDataSource).isNotEqualTo(null)
    }

    @Test
    @Throws(Exception::class)
    fun testAndroidAddAppTwoToList() {
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
                "0",
                "0"
            )
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
                "0",
                "0"
            )
        )
        val apps: ArrayList<AppSelectionModel>? = mDataSource?.viewScheduledApp()
        assertThat(apps?.size).isEqualTo(2)
        assertThat(apps?.get(0)?.appName).isEqualTo("Pathao")
        assertThat(apps?.get(0)?.appPackageName).isEqualTo("com.pathao.user")
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
                "0",
                "0"
            )
        )
        val apps: ArrayList<AppSelectionModel>? = mDataSource?.viewScheduledApp()
        assertThat(apps?.size).isEqualTo(1)
        assertThat(apps?.get(0)?.appName).isEqualTo("YouTube")
        assertThat(apps?.get(0)?.appPackageName).isEqualTo("com.google.android.youtube")
    }

    @Test
    fun testDeleteOnlyOne() {
        var apps: ArrayList<AppSelectionModel>? = mDataSource?.viewScheduledApp()
        if(apps?.size!! > 0) {
            mDataSource?.deleteSchedule(apps.get(apps.size-1).id)
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
                "0",
                "0"
            )
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
                "0",
                "0"
            )
        )
        var apps = mDataSource?.viewScheduledApp()
        mDataSource?.deleteSchedule(apps?.get(apps.size-1)?.id)
        apps = mDataSource?.viewScheduledApp()
        mDataSource?.deleteSchedule(apps?.get(apps.size-1)?.id)

        apps = mDataSource?.viewScheduledApp()
        assertThat(apps?.size).isEqualTo(0)
    }
}

