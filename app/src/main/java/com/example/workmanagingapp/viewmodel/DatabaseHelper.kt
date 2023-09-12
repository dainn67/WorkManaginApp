package com.example.workmanagingapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.workmanagingapp.model.Constants.Companion.DatabaseContract.DATABASE_NAME
import com.example.workmanagingapp.model.Constants.Companion.TABLE_NAME
import com.example.workmanagingapp.model.Constants.Companion.DatabaseContract.DATABASE_VERSION
import com.example.workmanagingapp.model.Constants.Companion.KEY_CONTENT
import com.example.workmanagingapp.model.Constants.Companion.KEY_ID
import com.example.workmanagingapp.model.Constants.Companion.KEY_STATUS
import com.example.workmanagingapp.model.Constants.Companion.KEY_TIME
import com.example.workmanagingapp.model.Constants.Companion.KEY_TITLE

class DatabaseHelper(
    private val context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val rDB = this.readableDatabase
    private val wDB = this.writableDatabase

    fun getRDB(): SQLiteDatabase = rDB
    fun getWDB(): SQLiteDatabase = wDB

    init {
        val createQuery = "" +
                "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$KEY_ID INTEGER  PRIMARY KEY AUTOINCREMENT," +
                "$KEY_TITLE TEXT," +
                "$KEY_TIME DATETIME," +
                "$KEY_CONTENT TEXT," +
                "$KEY_STATUS INTEGER )"
        wDB.execSQL(createQuery)
    }

    //Only called when database doesn't exist and need initial create
    override fun onCreate(db: SQLiteDatabase?) {
        val createQuery = "" +
                "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$KEY_ID INTEGER  PRIMARY KEY AUTOINCREMENT," +
                "$KEY_TITLE TEXT," +
                "$KEY_TIME DATETIME," +
                "$KEY_CONTENT TEXT," +
                "$KEY_STATUS INTEGER )"
        db?.execSQL(createQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}