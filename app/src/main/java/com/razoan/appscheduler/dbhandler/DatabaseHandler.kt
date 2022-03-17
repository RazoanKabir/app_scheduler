package com.razoan.appscheduler.dbhandler

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.razoan.appscheduler.model.AppSelectionModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private var DATABASE_VERSION = 1
        private var DATABASE_NAME = "AppSchedulerDB"
        private var TABLE_SCHEDULER = "AppSchedulerTable"
        private var KEY_ID = "_id"
        private var KEY_NAME = "app_name"
        private var KEY_PACKAGE_NAME = "package_name"
        private var KEY_DATE_TIME = "date_time"
        private var KEY_YEAR = "year"
        private var KEY_MONTH = "month"
        private var KEY_DAY = "day"
        private var KEY_HOUR = "hour"
        private var KEY_MINUTE = "minute"
        private var KEY_IS_REPEATABLE = "is_repeatable"
        private var KEY_IS_EXECUTED = "is_executed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_APP_SCHEDULER_TABLE = ("CREATE TABLE " + TABLE_SCHEDULER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_PACKAGE_NAME + " TEXT,"
                + KEY_DATE_TIME + " TEXT,"
                + KEY_YEAR + " TEXT,"
                + KEY_MONTH + " TEXT,"
                + KEY_DAY + " TEXT,"
                + KEY_HOUR + " TEXT,"
                + KEY_MINUTE + " TEXT,"
                + KEY_IS_REPEATABLE + " TEXT,"
                + KEY_IS_EXECUTED + " TEXT"
                + ")")
        db?.execSQL(CREATE_APP_SCHEDULER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_SCHEDULER")
        onCreate(db)
    }

    fun addApp(app: AppSelectionModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(KEY_NAME, app.appName)
        contentValues.put(KEY_PACKAGE_NAME, app.appPackageName)
        contentValues.put(KEY_DATE_TIME, app.dateTime)
        contentValues.put(KEY_YEAR, app.year)
        contentValues.put(KEY_MONTH, app.month)
        contentValues.put(KEY_DAY, app.day)
        contentValues.put(KEY_HOUR, app.hour)
        contentValues.put(KEY_MINUTE, app.minute)
        contentValues.put(KEY_IS_REPEATABLE, app.isRepeatable)
        contentValues.put(KEY_IS_EXECUTED, app.isExecuted)

        val success = db.insert(TABLE_SCHEDULER, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun viewScheduledApp(): ArrayList<AppSelectionModel> {

        val selectedAppList: ArrayList<AppSelectionModel> = ArrayList<AppSelectionModel>()

        // Query to select all the records from the table.
        val selectQuery = "SELECT  * FROM $TABLE_SCHEDULER"

        val db = this.readableDatabase
        // Cursor is used to read the record one by one. Add them to data model class.
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var appName: String
        var appPackageName: String
        var dateTime: String
        var year: String
        var month: String
        var day: String
        var hour: String
        var minute: String
        var isRepeatable: String
        var isExecuted: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                appName = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                appPackageName = cursor.getString(cursor.getColumnIndex(KEY_PACKAGE_NAME))
                dateTime = cursor.getString(cursor.getColumnIndex(KEY_DATE_TIME))
                year = cursor.getString(cursor.getColumnIndex(KEY_YEAR))
                month = cursor.getString(cursor.getColumnIndex(KEY_MONTH))
                day = cursor.getString(cursor.getColumnIndex(KEY_DAY))
                hour = cursor.getString(cursor.getColumnIndex(KEY_HOUR))
                minute = cursor.getString(cursor.getColumnIndex(KEY_MINUTE))
                isRepeatable = cursor.getString(cursor.getColumnIndex(KEY_IS_REPEATABLE))
                isExecuted = cursor.getString(cursor.getColumnIndex(KEY_IS_EXECUTED))

                val appSelected = AppSelectionModel(
                    id = id,
                    appName = appName,
                    appPackageName = appPackageName,
                    dateTime = dateTime,
                    year = year,
                    month = month,
                    day = day,
                    hour = hour,
                    minute = minute,
                    isRepeatable = isRepeatable,
                    isExecuted = isExecuted
                )
                selectedAppList.add(appSelected)

            } while (cursor.moveToNext())
        }
        return selectedAppList
    }
}