package com.example.workmanagingapp.view.mainscreen.days

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.workmanagingapp.R
import com.example.workmanagingapp.viewmodel.OnItemClickListener
import com.example.workmanagingapp.viewmodel.MyViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MyDayAdapter(
    private val listener: OnItemClickListener,
    private val context: Context,
    private val myViewModel: MyViewModel,
): RecyclerView.Adapter<MyDayViewHolder>() {
    private val list = myViewModel.getDayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDayViewHolder {
        return MyDayViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout_day, parent, false))
    }

    override fun onBindViewHolder(holder: MyDayViewHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener{
            listener.onItemDayClick(position)

            //set selected day
            holder.itemView.setBackgroundResource(R.drawable.rounded_border_day_selected)
            for(item in list) item.setIsSelected(false)
            list[position].setIsSelected(true)

            //update the livedata to refresh the list
            myViewModel.getDayListLiveData().value = list
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}