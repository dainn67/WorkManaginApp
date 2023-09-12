package com.example.workmanagingapp.view.mainscreen.days

import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.workmanagingapp.R
import com.example.workmanagingapp.model.Day

class MyDayViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView){
    private val tvDayInWeek: TextView
    private val tvDate: TextView
    private val tvDot: TextView

    init {
        tvDayInWeek = itemView.findViewById(R.id.tvDayInWeek)
        tvDate = itemView.findViewById(R.id.tvDate)
        tvDot = itemView.findViewById(R.id.tvDot)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(day: Day){
        tvDayInWeek.text = day.getDayOfWeek().toUpperCase()
        tvDate.text = day.getDateFormatted()
        tvDot.text = if(day.checkHasWork()) "\u2022" else ""
        if(day.getIsSelected()) this.itemView.setBackgroundResource(R.drawable.rounded_border_day_selected)
        else this.itemView.setBackgroundResource(R.drawable.rounded_border_day)
    }
}