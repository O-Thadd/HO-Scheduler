package com.othadd.hoscheduler.ui.recyclerAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.databinding.SingleWardDetailListItemBinding
import com.othadd.hoscheduler.utils.MyRecyclerViewAdapter
import com.othadd.hoscheduler.utils.Ward

class WardDetailRecyclerAdapter : MyRecyclerViewAdapter() {

    override fun updateDataList(list: List<Any>) {
        dataList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WardDetailViewHolder {
        return WardDetailViewHolder(SingleWardDetailListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as WardDetailViewHolder).bind(dataList[position] as Ward.WardDetail)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class WardDetailViewHolder(val binding: SingleWardDetailListItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(wardDetail: Ward.WardDetail){
            binding.apply {
                dayAndMonth.text = binding.root.context.getString(R.string.day_slash_month, wardDetail.dayNumber, wardDetail.monthNumber)
                day.text = wardDetail.dayName
                ho1.text = wardDetail.ho1
                ho2.text = wardDetail.ho2
            }
        }
    }

}