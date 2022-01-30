package com.othadd.hoscheduler.ui.recyclerAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.databinding.HoListGenerationListItemBinding
import com.othadd.hoscheduler.utils.MyRecyclerViewAdapter
import com.othadd.hoscheduler.utils.ScheduleGeneratingHo

class HoListCreationRecyclerAdapter(val onItemClick: (Int) -> Unit) : MyRecyclerViewAdapter() {

    override fun updateDataList(list: List<Any>) {
        dataList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoListCreationViewHolder {
        return HoListCreationViewHolder(
            HoListGenerationListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val scheduleGeneratingHo = dataList[position] as ScheduleGeneratingHo
        (holder as HoListCreationViewHolder).bind(scheduleGeneratingHo)
        holder.itemView.setOnClickListener { onItemClick(scheduleGeneratingHo.number) }
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    class HoListCreationViewHolder(val binding: HoListGenerationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ho: ScheduleGeneratingHo) {
            binding.apply {
                hoName.text = ho.name
                numberOfOffDays.text =
                    if (ho.outDays.isEmpty()) "" else binding.root.context.getString(
                        R.string.number_of_off_days,
                        ho.outDays.size
                    )
                resumptionDay.text =
                    if (ho.resumptionDay == -33) "" else binding.root.context.getString(
                        R.string.resumption_day,
                        ho.resumptionDay
                    )
                exitDay.text = if (ho.exitDay == 33) "" else binding.root.context.getString(
                    R.string.exit_day,
                    ho.exitDay
                )
                numberOfOutsidePostingDays.text =
                    if (ho.outSidePostingDays.isEmpty()) "" else binding.root.context.getString(
                        R.string.number_of_outside_posting_days,
                        ho.outSidePostingDays.size
                    )
            }
        }
    }
}