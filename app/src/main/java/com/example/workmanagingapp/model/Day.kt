package com.example.workmanagingapp.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class Day(
    private val dayOfWeek: DayOfWeek,
    private val date: LocalDateTime,
    private var hasWork: Boolean = false,
    private var isSelected: Boolean = false
) {
    fun getDayOfWeek(): String = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    fun getDayOfWeekFull(): String = dayOfWeek.toString()

    fun getDate() = date
    fun getDateFormatted() = "${date.dayOfMonth}/${date.month.value}"

    fun getIsSelected() = isSelected
    fun checkHasWork() = hasWork

    fun setHasWork(hasWork: Boolean) {this.hasWork = hasWork}
    fun setIsSelected(isSelected: Boolean){this.isSelected = isSelected}

    override fun toString(): String {
        return "$dayOfWeek: ${date.dayOfMonth}/${date.month} - Haswork: $hasWork - isSelected: $isSelected"
    }
}