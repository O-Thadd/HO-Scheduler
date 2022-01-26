package com.othadd.hoscheduler.ui.recyclerAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.othadd.hoscheduler.databinding.SingleDayDetailListItemBinding
import com.othadd.hoscheduler.utils.Day
import com.othadd.hoscheduler.utils.MyRecyclerViewAdapter

class DayDetailsRecyclerAdapter : MyRecyclerViewAdapter() {

    override fun updateDataList(list: List<Any>) {
        dataList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayDetailViewHolder {
        return DayDetailViewHolder(SingleDayDetailListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DayDetailViewHolder).bind(dataList[position] as Day.DayDetail)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class DayDetailViewHolder(val binding: SingleDayDetailListItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(dayDetail: Day.DayDetail){
            binding.apply {
                ward.text = dayDetail.ward
                ho1.text = dayDetail.ho1
                ho2.text = dayDetail.ho2
            }
        }
    }

}