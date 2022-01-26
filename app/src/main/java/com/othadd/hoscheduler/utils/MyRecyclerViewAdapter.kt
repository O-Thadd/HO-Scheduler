package com.othadd.hoscheduler.utils

import androidx.recyclerview.widget.RecyclerView

abstract class MyRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataList: List<Any> = listOf()

    abstract fun updateDataList(list: List<Any>)

}