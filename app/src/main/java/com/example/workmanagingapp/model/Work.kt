package com.example.workmanagingapp.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class Work(
    private val title: String,
    private val time: LocalDateTime,
    private val content: String,
    private var isDone: Boolean = false
):Serializable {
    fun getTitle() = title
    fun getTime() = time
    fun getContent() = content
    fun getStatus() = isDone

    fun setStatus(status: Boolean){
        this.isDone = status
    }
    override fun toString(): String {
        return "$title: ${time.hour}:${time.minute} - $content - $isDone ${formatTime()}"
    }

    private fun formatTime(): String{
        return "${time.year}-${time.month.value}-${time.dayOfMonth} ${time.hour}:${time.minute}"
    }

    fun toJsonString() = "$title/$content/${formatTime()}"

}