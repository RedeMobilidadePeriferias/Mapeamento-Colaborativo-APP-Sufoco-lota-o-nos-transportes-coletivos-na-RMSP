package br.com.mapeamentosufoco.app.dao

import android.provider.BaseColumns

object ReportContract {
    // Table contents are grouped together in an anonymous object.
    object ReportEntry : BaseColumns {
        const val TABLE_NAME = "report"
        const val COLUMN_NAME_USER_ID = "user_id"
        const val COLUMN_NAME_USER_LOCATION = "user_location"
        const val COLUMN_NAME_TRAVEL_REASON = "travel_reason"
        const val COLUMN_NAME_TRAVEL_TIME = "travel_time"
        const val COLUMN_NAME_TRAVEL_STATUS = "travel_status"
        const val COLUMN_NAME_TRAVEL_CATEGORY = "travel_category"
        const val COLUMN_NAME_TRAVEL_LINE = "travel_line"
        const val COLUMN_NAME_TRAVEL_STATION = "travel_station"
        const val COLUMN_NAME_TRAVEL_DATE = "travel_date"
        const val COLUMN_NAME_SYNC = "sync"
    }
}