package com.example.workmanagingapp.model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.workmanagingapp.model.Constants.Companion.TAG
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class Data {
    private var workList = mutableListOf<Work>()
    private var dayList = mutableListOf<Day>()

    init {
        var localDate = LocalDateTime.now()
        var dayOfWeek = localDate.dayOfWeek

        for(i in 2..8){
            dayList.add(Day(dayOfWeek, localDate))
            dayOfWeek += 1
            localDate = localDate.plusDays(1)
        }

        dayList[0].setIsSelected(true)
    }

    fun getWorkList() = workList
    fun getDayList() = dayList
}