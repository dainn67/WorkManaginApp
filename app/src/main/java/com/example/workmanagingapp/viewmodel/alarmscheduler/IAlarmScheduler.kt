package com.example.workmanagingapp.viewmodel.alarmscheduler

interface IAlarmScheduler {
    fun schedule(json: String)
    fun cancel(json: String)
}