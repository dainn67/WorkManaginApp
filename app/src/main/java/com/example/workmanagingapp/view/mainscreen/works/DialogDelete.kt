package com.example.workmanagingapp.view.mainscreen.works

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.workmanagingapp.R
import com.example.workmanagingapp.model.Work
import com.example.workmanagingapp.viewmodel.MyViewModel
import com.example.workmanagingapp.viewmodel.MyViewModel.Companion.displayDate
import com.example.workmanagingapp.viewmodel.MyViewModel.Companion.displayTime

class DialogDelete(
    private val work: Work,
    private val viewModel: MyViewModel
): DialogFragment() {
    private lateinit var tvTitle: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvContent: TextView

    private lateinit var btnCancel: Button
    private lateinit var btnRemove: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val myInflater = requireActivity().layoutInflater
        val view = myInflater.inflate(R.layout.dialog_delete, null, false)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        //view binding
        tvTitle = view.findViewById(R.id.tvRemoveTitle)
        tvTitle.text = "Remove \"${work.getTitle()}\" ?"

        tvDate = view.findViewById(R.id.tvRemoveDate)
        tvDate.text = displayDate(work.getTime())

        tvTime = view.findViewById(R.id.tvRemoveTime)
        tvTime.text = displayTime(work.getTime())

        tvContent = view.findViewById(R.id.tvRemoveContent)
        tvContent.text = work.getContent()

        //button functions
        btnCancel = view.findViewById(R.id.btnRemoveCancel)
        btnCancel.setOnClickListener{
            this.dismiss()
        }

        btnRemove = view.findViewById(R.id.btnRemoveDone)
        btnRemove.setOnClickListener{
            viewModel.removeFromList(work)
            this.dismiss()
        }

        return builder.create()
    }
}