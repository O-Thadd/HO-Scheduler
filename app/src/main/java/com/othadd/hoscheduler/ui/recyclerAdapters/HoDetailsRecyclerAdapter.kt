package com.othadd.hoscheduler.ui.recyclerAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.databinding.SingleHoDetailListItemBinding
import com.othadd.hoscheduler.utils.Ho
import com.othadd.hoscheduler.utils.MyRecyclerViewAdapter

class HoDetailsRecyclerAdapter : MyRecyclerViewAdapter() {

    override fun updateDataList(list: List<Any>) {
        dataList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoDetailViewHolder {
        return HoDetailViewHolder(SingleHoDetailListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HoDetailViewHolder).bind(dataList[position] as Ho.HoDetail)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class HoDetailViewHolder(var binding: SingleHoDetailListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(hoDetail: Ho.HoDetail) {
            binding.apply {
                dayAndMonth.text = binding.root.context.getString(
                    R.string.day_slash_month,
                    hoDetail.dayNumber,
                    hoDetail.monthNumber
                )
                dayOfWeek.text = hoDetail.dayName
                ward.text = hoDetail.ward
            }
        }
    }
}