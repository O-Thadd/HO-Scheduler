package com.othadd.hoscheduler.ui.recyclerAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.databinding.DialogCreateHoOffdaysSelectionListItemBinding
import com.othadd.hoscheduler.utils.MyRecyclerViewAdapter

class DaysSelectionRecyclerAdapter(val onDayClicked: (Int) -> Unit) : MyRecyclerViewAdapter() {

    override fun updateDataList(list: List<Any>) {
        dataList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysSelectionViewHolder {
        return DaysSelectionViewHolder(
            DialogCreateHoOffdaysSelectionListItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val daySelectionItem = dataList[position] as DaySelectionItem
        (holder as DaysSelectionViewHolder).bind(daySelectionItem)
        holder.itemView.setOnClickListener {
            holder.alter()
            onDayClicked(daySelectionItem.date)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class DaysSelectionViewHolder(val binding: DialogCreateHoOffdaysSelectionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var textColourIsBlack = true

        fun bind(daySelectionItem: DaySelectionItem) {
            binding.dayNumberText.text = daySelectionItem.date.toString()
            binding.dayNumberText.setTextColor(if (daySelectionItem.selected) binding.root.context.getColor(R.color.green) else binding.root.context.getColor(R.color.black))
            textColourIsBlack = !daySelectionItem.selected
        }

        fun alter() {
            textColourIsBlack = !textColourIsBlack
            binding.dayNumberText.setTextColor(if (textColourIsBlack) binding.root.context.getColor(R.color.black) else binding.root.context.getColor(R.color.green))
        }
    }

    class DaySelectionItem(val date: Int, var selected: Boolean = false)
}