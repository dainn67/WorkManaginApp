package com.example.workmanagingapp.model

import android.net.Uri

class Constants {
    companion object {
        const val TAG = "aaa"

        object DatabaseContract {
            const val DATABASE_NAME = "work_database.db"
            const val DATABASE_VERSION = 1
        }

        const val CHANNEL_ID = "ChannelID"

        const val TABLE_NAME = "WORK_TABLE"
        const val KEY_ID = "id"
        const val KEY_TITLE = "title"
        const val KEY_TIME = "time"
        const val KEY_CONTENT = "content"
        const val KEY_STATUS = "status"
        val TABLE_URI: Uri = Uri.parse("content://com.example.workmanagingapp/$TABLE_NAME")

        enum class ViewDetailType {
            CURRENT, UPCOMING, ALL, UNFINISHED
        }
    }
}