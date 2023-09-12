package com.example.workmanagingapp.viewmodel.alarmscheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.workmanagingapp.model.Constants.Companion.TAG
import com.example.workmanagingapp.viewmodel.receiver.MyReceiver
import java.time.LocalDateTime
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class AlarmScheduler(
    private val context: Context
) : IAlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    companion object {
        fun convertToMillis(hour: Int, minute: Int): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar.timeInMillis
        }
    }

    override fun schedule(json: String) {
        val intent = Intent(context, MyReceiver::class.java)
        val bundle = Bundle()
        bundle.putString("json", json)
        intent.putExtras(bundle)

        var calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        var desiredTime = calendar
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            desiredTime = calendar
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            desiredTime.timeInMillis,
            PendingIntent.getBroadcast(
                context,
                json.hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        Log.i(TAG, "Schedule with data: $json")
    }

    override fun cancel(json: String) {
        val intent = Intent(context, MyReceiver::class.java)
        intent.putExtra("json", json)

        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                json.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        Log.i(TAG, "Cancel")
    }
}