package com.othadd.hoscheduler.ui.recyclerAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.database.MonthSchedule
import com.othadd.hoscheduler.databinding.MonthSchedulesListItemBinding
import com.othadd.hoscheduler.utils.MyRecyclerViewAdapter

class MonthSchedulesRecyclerAdapter(
    private val onClickItem: (Int) -> Unit,
    private val onLongClickItem: (Int) -> Unit
) : MyRecyclerViewAdapter() {

    override fun updateDataList(list: List<Any>) {
        dataList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthsScheduleViewHolder {
        return MonthsScheduleViewHolder(
            MonthSchedulesListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val monthSchedule = dataList[position] as MonthSchedule

        holder.itemView.setOnClickListener {
            onClickItem(monthSchedule.id)
            if (selectionMode) (holder as MonthsScheduleViewHolder).alterSelectionState()
        }

        holder.itemView.setOnLongClickListener {
            onLongClickItem(monthSchedule.id)
            startSelection()
            notifyDataSetChanged()
            true
        }

        (holder as MonthsScheduleViewHolder).bind(monthSchedule)
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    class MonthsScheduleViewHolder(private var binding: MonthSchedulesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var selected = false

        fun alterSelectionState() {
            selected = !selected
            binding.selectionTickImageView.visibility =
                if (selected) View.VISIBLE else View.INVISIBLE
        }

        fun bind(monthSchedule: MonthSchedule) {
            binding.apply {
                monthScheduleName.text = monthSchedule.name
                monthScheduleMonthAndYear.text = root.context.getString(
                    R.string.month_schedule_name_and_year,
                    monthSchedule.month,
                    monthSchedule.year.toString()
                )
                monthScheduleNumberOfHos.text = root.context.getString(
                    R.string.number_of_hos,
                    monthSchedule.hos.size
                )
                selectionTickImageView.visibility = if (monthSchedule.selected) View.VISIBLE else View.INVISIBLE
            }
        }
    }
}