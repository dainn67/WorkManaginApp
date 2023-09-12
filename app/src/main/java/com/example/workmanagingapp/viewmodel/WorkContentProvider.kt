package com.example.workmanagingapp.viewmodel

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import com.example.workmanagingapp.model.Constants.Companion.TABLE_NAME

class WorkContentProvider : ContentProvider() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(): Boolean {
        dbHelper = DatabaseHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return dbHelper.getWDB().query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null, null,
            sortOrder
        )
    }

    override fun getType(p0: Uri): String? {
        return ""
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = dbHelper.getWDB().insert(TABLE_NAME, null, values)
        if (id == -1L){
            throw SQLException("Failed to insert")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return ContentUris.withAppendedId(uri, id)
    }

    override fun delete(uri: Uri, whereClause: String?, whereArgs: Array<out String>?): Int {
        dbHelper.getWDB().delete(TABLE_NAME, whereClause, whereArgs)
        return  0
    }

    override fun update(uri: Uri, newValue: ContentValues?, whereClause: String?, whereArgs: Array<out String>?): Int {
        dbHelper.getWDB().update(TABLE_NAME, newValue, whereClause, whereArgs)
        return 0
    }
}