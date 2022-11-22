package br.com.mapeamentosufoco.app.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import br.com.mapeamentosufoco.app.entities.Report
import kotlin.collections.ArrayList

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${ReportContract.ReportEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${ReportContract.ReportEntry.COLUMN_NAME_USER_ID} TEXT," +
            "${ReportContract.ReportEntry.COLUMN_NAME_USER_LOCATION} TEXT," +
            "${ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_REASON} TEXT," +
            "${ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_TIME} TEXT," +
            "${ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_STATUS} TEXT," +
            "${ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_CATEGORY} TEXT," +
            "${ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_LINE} TEXT," +
            "${ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_STATION} TEXT," +
            "${ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_DATE} TEXT," +
            "${ReportContract.ReportEntry.COLUMN_NAME_SYNC} TEXT)"

private const val SQL_DELETE_ENTRIES =
    "DROP TABLE IF EXISTS ${ReportContract.ReportEntry.TABLE_NAME}"

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
    }

    fun insertReport(report: Report): Boolean {
        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(ReportContract.ReportEntry.COLUMN_NAME_USER_ID, report.userId)
            put(ReportContract.ReportEntry.COLUMN_NAME_USER_LOCATION, report.userLocation)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_REASON, report.travelReason)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_TIME, report.travelTime)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_STATUS, report.travelStatus)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_CATEGORY, report.travelCategory)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_LINE, report.travelLine)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_STATION, report.travelStation)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_DATE, report.travelDate)
            put(ReportContract.ReportEntry.COLUMN_NAME_SYNC, report.sync)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(ReportContract.ReportEntry.TABLE_NAME, null, values)

        return (("$newRowId").toInt() != -1)
    }

    fun updateReport(report: Report): Boolean {
        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(ReportContract.ReportEntry.COLUMN_NAME_USER_ID, report.userId)
            put(ReportContract.ReportEntry.COLUMN_NAME_USER_LOCATION, report.userLocation)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_REASON, report.travelReason)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_TIME, report.travelTime)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_STATUS, report.travelStatus)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_CATEGORY, report.travelCategory)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_STATION, report.travelStation)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_LINE, report.travelLine)
            put(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_DATE, report.travelDate)
            put(ReportContract.ReportEntry.COLUMN_NAME_SYNC, "S")
        }

        // Insert the new row, returning the primary key value of the new row
        val updateRow = db?.update(ReportContract.ReportEntry.TABLE_NAME, values, "${BaseColumns._ID} = ${report.id}", null)

        return (("$updateRow").toInt() > 0)
    }

    fun findReportsNotSync(): ArrayList<Report> {
        val reportList = ArrayList<Report>()
        val db = readableDatabase
        val selectQuery = "SELECT * FROM ${ReportContract.ReportEntry.TABLE_NAME} WHERE ${ReportContract.ReportEntry.COLUMN_NAME_SYNC} = 'N'"
        val cursor = db.rawQuery(selectQuery, null)
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    val report = Report()

                    report.id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
                    report.userId = cursor.getString(cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_NAME_USER_ID))
                    report.userLocation = cursor.getString(cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_NAME_USER_LOCATION))
                    report.travelReason = cursor.getString(cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_REASON))
                    report.travelTime = cursor.getString(cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_TIME))
                    report.travelStatus = cursor.getString(cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_STATUS))
                    report.travelCategory = cursor.getString(cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_CATEGORY))
                    report.travelLine = cursor.getString(cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_LINE))
                    report.travelDate = cursor.getString(cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_DATE))
                    report.travelStation = cursor.getString(cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_NAME_TRAVEL_STATION))
                    report.sync = cursor.getString(cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_NAME_SYNC))

                    reportList.add(report)
                }while(cursor.moveToNext())
            }
        }
        cursor.close()
        return reportList
    }
}