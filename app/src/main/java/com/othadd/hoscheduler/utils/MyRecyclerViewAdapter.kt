package com.othadd.hoscheduler.utils

import androidx.recyclerview.widget.RecyclerView

abstract class MyRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataList: List<Any> = listOf()
    var selectionMode: Boolean = false

    abstract fun updateDataList(list: List<Any>)

    fun startSelection(){
        selectionMode = true
    }

    fun endSelection(){
        selectionMode = false
    }

}