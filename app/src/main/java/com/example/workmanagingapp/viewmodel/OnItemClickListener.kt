package com.example.workmanagingapp.viewmodel

import com.example.workmanagingapp.model.Constants

interface OnItemClickListener {
    fun onItemDayClick(position: Int)
    fun onItemWorkClick(position: Int, type: Constants.Companion.ViewDetailType)
    fun onItemWorkLongClick(position: Int, type: Constants.Companion.ViewDetailType)
}