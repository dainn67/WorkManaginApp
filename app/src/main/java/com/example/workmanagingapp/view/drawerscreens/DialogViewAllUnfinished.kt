package com.example.workmanagingapp.view.drawerscreens

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workmanagingapp.R
import com.example.workmanagingapp.model.Constants
import com.example.workmanagingapp.view.mainscreen.works.MyWorkListAdapter
import com.example.workmanagingapp.viewmodel.MyViewModel
import com.example.workmanagingapp.viewmodel.OnItemClickListener

class DialogViewAllUnfinished(
    private val listener: OnItemClickListener,
    private val context: Context,
    private val viewModel: MyViewModel,
    private val type: Constants.Companion.ViewDetailType
): DialogFragment() {
    private lateinit var tvTitle: TextView
    private lateinit var recViewAllUnfinished: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = requireActivity().layoutInflater
        val view = layoutInflater.inflate(R.layout.dialog_all_unfinished, null, false)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)

        tvTitle = view.findViewById(R.id.tvAllUnfinishedTitle)
        tvTitle.text = if(type == Constants.Companion.ViewDetailType.ALL) "All works" else "Unfinished works"

        recViewAllUnfinished = view.findViewById(R.id.recViewAllUnfinished)
        recViewAllUnfinished.layoutManager = LinearLayoutManager(context)
        recViewAllUnfinished.adapter = MyWorkListAdapter(listener, context, viewModel, type)

        return builder.create()
    }
}