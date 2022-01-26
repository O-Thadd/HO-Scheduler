package com.othadd.hoscheduler.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter.DataItem.*

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, list: List<Any>?){
    if (list != null) {
        (recyclerView.adapter as MyRecyclerViewAdapter).updateDataList(list)
    }
}

@BindingAdapter("wardList", "dayList", "hoList")
fun bindOverviewRecyclerView(recyclerView: RecyclerView, wardList: List<WardItem>?, dayList: List<DayItem>?, hoList: List<HoItem>?){
    if (wardList != null && dayList != null && hoList != null) {
        (recyclerView.adapter as MonthScheduleOverviewRecyclerAdapter).postList(wardList, dayList, hoList)
    }
}