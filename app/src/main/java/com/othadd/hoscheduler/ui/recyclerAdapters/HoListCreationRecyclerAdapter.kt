package com.othadd.hoscheduler.ui.recyclerAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.databinding.HoListGenerationListItemBinding
import com.othadd.hoscheduler.utils.MyRecyclerViewAdapter
import com.othadd.hoscheduler.utils.ScheduleGeneratingHo

class HoListCreationRecyclerAdapter : MyRecyclerViewAdapter() {

    override fun updateDataList(list: List<Any>) {
        dataList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoListCreationViewHolder {
        return HoListCreationViewHolder(HoListGenerationListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HoListCreationViewHolder).bind(dataList[position] as ScheduleGeneratingHo)
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    class HoListCreationViewHolder(val binding: HoListGenerationListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(ho: ScheduleGeneratingHo){
            binding.apply {
                hoName.text = ho.name
                numberOfOffDays.text = binding.root.context.getString(R.string.number_of_off_days, ho.outDays.size)
            }
        }
    }
}