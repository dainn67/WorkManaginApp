package com.example.workmanagingapp.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.workmanagingapp.model.Constants.Companion.TAG
import com.example.workmanagingapp.model.Work
import com.example.workmanagingapp.viewmodel.alarmscheduler.AlarmScheduler
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class MyWorker(
    context: Context,
    private val params: WorkerParameters
) : Worker(context, params) {
    private val alarmScheduler = AlarmScheduler(context)

    override fun doWork(): Result {
        //receive the worklist
        val json = params.inputData.getString("serialized_list")

        //schedule and send to BR to display the notification
        alarmScheduler.schedule(json!!)

        return Result.success()
    }
}