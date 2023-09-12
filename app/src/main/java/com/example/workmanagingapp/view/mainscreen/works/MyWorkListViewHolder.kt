package com.example.workmanagingapp.view.mainscreen.works

import android.os.Build
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.workmanagingapp.R
import com.example.workmanagingapp.model.Work
import com.example.workmanagingapp.viewmodel.MyViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MyWorkListViewHolder(
    itemView: View,
    private val viewModel: MyViewModel
) : RecyclerView.ViewHolder(itemView) {
    private val tvTitle: TextView
    private val tvTime: TextView
    private val cbCheckDone: CheckBox

    init {
        tvTitle = itemView.findViewById(R.id.tvItemName)
        tvTime = itemView.findViewById(R.id.tvItemTime)
        cbCheckDone = itemView.findViewById(R.id.cbCheckDone)
    }


    fun bindCurrentLayout(work: Work) {
        tvTitle.text = work.getTitle()
        tvTime.text = MyViewModel.displayTime(work.getTime())
        cbCheckDone.isChecked = work.getStatus()
        listenToCheckBox(work)
    }

    fun bindGeneralLayout(work: Work) {
        val displayTime = MyViewModel.displayTime(work.getTime())
        val day = work.getTime().dayOfMonth
        val month = work.getTime().month.value
        val year = work.getTime().year

        tvTitle.text = work.getTitle()
        tvTime.text = "$day/$month/$year - $displayTime"
        cbCheckDone.isChecked = work.getStatus()
        listenToCheckBox(work)
    }

    private fun listenToCheckBox(work: Work){
        cbCheckDone.setOnClickListener {
            for (item in viewModel.getAllWorkList()) {
                if (item.getTitle() == work.getTitle() && item.getContent() == work.getContent()) {
                    val newWork = Work(item.getTitle(), item.getTime(), item.getContent(), cbCheckDone.isChecked)
                    viewModel.updateWorkInList(newWork, item)
                    break
                }
            }
        }
    }
}