package com.example.workmanagingapp.viewmodel

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanagingapp.model.Constants.Companion.KEY_CONTENT
import com.example.workmanagingapp.model.Constants.Companion.KEY_STATUS
import com.example.workmanagingapp.model.Constants.Companion.KEY_TIME
import com.example.workmanagingapp.model.Constants.Companion.KEY_TITLE
import com.example.workmanagingapp.model.Constants.Companion.TABLE_URI
import com.example.workmanagingapp.model.Constants.Companion.TAG
import com.example.workmanagingapp.model.Data
import com.example.workmanagingapp.model.Day
import com.example.workmanagingapp.model.Work
import com.google.gson.GsonBuilder
import java.text.ParseException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("Range")
class MyViewModel(
    private val context: Context
) : ViewModel() {
    private val data = Data()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    //get the list and use livedata to update
    private var dayList = data.getDayList()
    private var allWorkList = data.getWorkList()
    private var currentWorkList = mutableListOf<Work>()
    private var upcomingWorkList = mutableListOf<Work>()

    //livedata
    private var dayListLiveData = MutableLiveData<MutableList<Day>>()
    private var currentWorkTitleLiveData = MutableLiveData<String>()
    private var addNewDateTVLiveData = MutableLiveData<String>()
    private var addNewTimeTVLiveData = MutableLiveData<String>()

    private var allWorkListLiveData = MutableLiveData<MutableList<Work>>()
    private var currentWorkListLiveData = MutableLiveData<MutableList<Work>>()
    private var upcomingWorkListLiveData = MutableLiveData<MutableList<Work>>()

    private var currentDay = dayList[0]

    //getters
    fun getDayList() = dayList
    fun getAllWorkList() = allWorkList
    fun getCurrentWorkList() = currentWorkList
    fun getUpcomingWorkList() = upcomingWorkList
    fun getUnfinishedList(): MutableList<Work> {
        val tmpList = mutableListOf<Work>()
        allWorkList.forEach {
            if (!it.getStatus())
                tmpList.add(it)
        }
        return tmpList
    }

    fun setCurrentWorkList(list: MutableList<Work>) {
        currentWorkList = list
    }

    fun setUpcomingWorkList(list: MutableList<Work>) {
        upcomingWorkList = list
    }

    fun setDayList(list: MutableList<Day>) {
        dayList = list
    }

    //livedata getters
    fun getAllWorkListLiveData() = allWorkListLiveData
    fun getCurrentWorkListLiveData() = currentWorkListLiveData
    fun getUpcomingWorkListLiveData() = upcomingWorkListLiveData
    fun getDayListLiveData() = dayListLiveData
    fun getCurrentTitleLiveData() = currentWorkTitleLiveData
    fun getAddNewDateTVLiveData() = addNewDateTVLiveData
    fun getAddNewTimeTVLiveData() = addNewTimeTVLiveData

    //livedata needs to be initialized and assigned its value
    init {
        dayListLiveData = MutableLiveData()
        dayListLiveData.value = dayList

        allWorkListLiveData = MutableLiveData()
        allWorkListLiveData.value = allWorkList

        currentWorkListLiveData = MutableLiveData()
        upcomingWorkListLiveData = MutableLiveData()

        currentWorkTitleLiveData = MutableLiveData()
        currentWorkTitleLiveData.value =
            "TODAY - ${LocalDate.now().dayOfMonth}/${LocalDate.now().month.value}"

        addNewDateTVLiveData = MutableLiveData()
        addNewDateTVLiveData.value =
            "Date: ${if (LocalDate.now().dayOfMonth < 10) "0${LocalDate.now().dayOfMonth}" else LocalDate.now().dayOfMonth}/${if (LocalDate.now().month.value < 10) "0${LocalDate.now().month.value}" else LocalDate.now().month.value}/${LocalDate.now().year}"
    }

    companion object {
        fun displayTime(date: LocalDateTime): String {
            val displayHour = if (date.hour < 10) "0${date.hour}" else date.hour
            val displayMinute = if (date.minute < 10) "0${date.minute}" else date.minute

            return "$displayHour:$displayMinute"
        }

        fun displayTime(hour: Int, minute: Int): String {
            val displayHour = if (hour < 10) "0$hour" else hour
            val displayMinute = if (minute < 10) "0$minute" else minute

            return "$displayHour:$displayMinute"
        }

        fun displayDate(date: LocalDateTime): String {
            val displayDay = if (date.dayOfMonth < 10) "0${date.dayOfMonth}" else date.dayOfMonth
            val displayMonth =
                if (date.month.value < 10) "0${date.month.value}" else date.month.value

            return "$displayDay/$displayMonth/${date.year}"
        }
    }

    fun selectDayAndDisplayWork(position: Int) {
        currentDay = dayList[position]

        //set isSelected
        for (day in dayList) day.setIsSelected(false)
        dayList[position].setIsSelected(true)
        dayListLiveData.value = dayList

        //display corresponding title's text
        if (position == 0)
            currentWorkTitleLiveData.value =
                "TODAY - ${currentDay.getDateFormatted()}"
        else
            currentWorkTitleLiveData.value =
                "${currentDay.getDayOfWeekFull()} - ${currentDay.getDateFormatted()}"

        //display the corresponding work list
        currentWorkList.clear()
        allWorkList.forEach { work ->
            if (work.getTime().dayOfMonth == currentDay.getDate().dayOfMonth && work.getTime().month.value == currentDay.getDate().month.value)
                currentWorkList.add(work)
        }
        currentWorkListLiveData.value = currentWorkList
    }

    fun loadWorkList() {
//        addSampleWorkToSQLite()

        allWorkList.clear()
        currentWorkList.clear()
        upcomingWorkList.clear()

        val projection = arrayOf(KEY_TITLE, KEY_TIME, KEY_CONTENT, KEY_STATUS)
        val sortOrder = KEY_TIME

        val tmpList = mutableListOf<Work>()
        val cursor =
            context.contentResolver.query(
                TABLE_URI,
                projection,
                null,
                null,
                sortOrder
            )

        //update the corresponding livedata
        if (cursor?.moveToFirst() == true) {
            do {
                val title = cursor.getString(cursor.getColumnIndex(KEY_TITLE))
                val timeString = cursor.getString(cursor.getColumnIndex(KEY_TIME))
                val formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                var time = LocalDateTime.now()
                try {
                    time = LocalDateTime.parse(timeString, formatter)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val content = cursor.getString(cursor.getColumnIndex(KEY_CONTENT))
                val statusInt = cursor.getInt(cursor.getColumnIndex(KEY_STATUS))
                val status = (statusInt == 1)

                allWorkList.add(Work(title, time, content, status))

            } while (cursor.moveToNext())
        }

        //strftime not working so I only query without whereClause
        allWorkListLiveData.value = tmpList
        filterWorks()
        indicateRedDot()
    }

    private fun filterWorks() {
        allWorkList.forEach { work ->
            val day = work.getTime().dayOfMonth
            val month = work.getTime().month.value
            val year = work.getTime().year

            if (day == currentDay.getDate().dayOfMonth && month == currentDay.getDate().month.value && year == currentDay.getDate().year)
                currentWorkList.add(work)
            if (year > LocalDateTime.now().year
                || year == LocalDateTime.now().year && month > LocalDateTime.now().month.value
                || year == LocalDateTime.now().year && month == LocalDateTime.now().month.value && day > LocalDateTime.now().dayOfMonth
            )
                upcomingWorkList.add(work)
        }

        currentWorkList.sortedWith(compareBy(Work::getTime).thenBy(Work::getTitle))
        upcomingWorkList.sortedWith(compareBy(Work::getTime).thenBy(Work::getTitle))
        currentWorkListLiveData.value = currentWorkList
        upcomingWorkListLiveData.value = upcomingWorkList
    }

    private fun indicateRedDot() {
        dayList.forEach { day ->
            day.setHasWork(false)
            allWorkList.forEach { work ->
                if (day.getDate().dayOfMonth == work.getTime().dayOfMonth && day.getDate().month.value == work.getTime().month.value) {
                    day.setHasWork(true)
                }
            }
        }

        dayListLiveData.value = dayList
    }

    fun addNewToList(work: Work) {
        //add to database
        val values = ContentValues().apply {
            put(KEY_TITLE, work.getTitle())
            put(KEY_TIME, work.getTime().format(formatter))
            put(KEY_CONTENT, work.getContent())
            put(KEY_STATUS, 0)
        }

        context.contentResolver.insert(TABLE_URI, values)
        //NOTE: loadWorkList and setWorkManager is called in onResume of MainActivity, after finish() is called from 2nd screen
    }

    fun removeFromList(work: Work) {
        //delete from database
        val whereClause = "$KEY_TITLE = ? AND $KEY_CONTENT = ?"
        val whereArgs = arrayOf(work.getTitle(), work.getContent())

        context.contentResolver.delete(TABLE_URI, whereClause, whereArgs)
        loadWorkList()
        setWorkManager()
    }

    fun updateWorkInList(newWork: Work, work: Work) {
        val whereClause = "$KEY_TITLE = ? AND $KEY_CONTENT = ?"
        val whereArgs = arrayOf(work.getTitle(), work.getContent())

        val values = ContentValues().apply {
            put(KEY_TITLE, newWork.getTitle())
            put(KEY_CONTENT, newWork.getContent())
            put(KEY_TIME, newWork.getTime().format(formatter))
            put(KEY_STATUS, newWork.getStatus())
        }

        context.contentResolver.update(TABLE_URI, values, whereClause, whereArgs)
        loadWorkList()
        setWorkManager()
    }

    fun setWorkManager() {
        val gson = GsonBuilder()
            .registerTypeAdapter(Work::class.java, WorkJsonAdapter())
            .create()
        val json = gson.toJson(allWorkList)

        val inputData = androidx.work.Data.Builder()
            .putString("serialized_list", json)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<MyWorker>(
            1,
            TimeUnit.DAYS
        )
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }

    private fun addSampleWorkToSQLite() {
        context.contentResolver.delete(TABLE_URI, null, null)

        var values = ContentValues().apply {
            put(KEY_TITLE, "Go to school")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            LocalDateTime.now().format(formatter)
            put(KEY_TIME, LocalDateTime.now().format(formatter))
            put(KEY_CONTENT, "Go to school now")
            put(KEY_STATUS, 0)
        }
        context.contentResolver.insert(TABLE_URI, values)
        values = ContentValues().apply {
            put(KEY_TITLE, "Go to the movie")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = LocalDateTime.now().plusMinutes(30).format(formatter)
            put(KEY_TIME, time)
            put(KEY_CONTENT, "Remember to buy the ticket")
            put(KEY_STATUS, 1)
        }
        context.contentResolver.insert(TABLE_URI, values)
        values = ContentValues().apply {
            put(KEY_TITLE, "Buy food for the movie")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = LocalDateTime.now().plusMinutes(45).format(formatter)
            put(KEY_TIME, time)
            put(KEY_CONTENT, "Remember to buy the popcorn also")
            put(KEY_STATUS, 1)
        }
        context.contentResolver.insert(TABLE_URI, values)
        values = ContentValues().apply {
            put(KEY_TITLE, "Buy drink for the movie")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = LocalDateTime.now().plusMinutes(50).format(formatter)
            put(KEY_TIME, time)
            put(KEY_CONTENT, "And never forget to buy Coke")
            put(KEY_STATUS, 1)
        }
        context.contentResolver.insert(TABLE_URI, values)
        values = ContentValues().apply {
            put(KEY_TITLE, "Skincare")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = LocalDateTime.now().plusMinutes(55).format(formatter)
            put(KEY_TIME, time)
            put(KEY_CONTENT, "Lotion, moisture, ...")
            put(KEY_STATUS, 1)
        }
        context.contentResolver.insert(TABLE_URI, values)
        values = ContentValues().apply {
            put(KEY_TITLE, "Go buy food")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = LocalDateTime.now().plusDays(1).format(formatter)
            put(KEY_TIME, time)
            put(KEY_CONTENT, "Go to Winmart to buy some meat")
            put(KEY_STATUS, 0)
        }
        context.contentResolver.insert(TABLE_URI, values)
        values = ContentValues().apply {
            put(KEY_TITLE, "Go to see the doctor")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = LocalDateTime.now().plusDays(2).format(formatter)
            put(KEY_TIME, time)
            put(KEY_CONTENT, "Go see your therapist")
            put(KEY_STATUS, 1)
        }
        context.contentResolver.insert(TABLE_URI, values)
        values = ContentValues().apply {
            put(KEY_TITLE, "Go to the gym")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val time = LocalDateTime.now().plusDays(1).format(formatter)
            put(KEY_TIME, time)
            put(KEY_CONTENT, "Leg day")
            put(KEY_STATUS, 1)
        }
        context.contentResolver.insert(TABLE_URI, values)
    }
}