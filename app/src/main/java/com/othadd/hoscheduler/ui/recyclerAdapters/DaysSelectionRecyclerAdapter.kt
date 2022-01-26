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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffDaysSelectionViewHolder {
        return OffDaysSelectionViewHolder(
            DialogCreateHoOffdaysSelectionListItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val day = dataList[position] as Int
        (holder as OffDaysSelectionViewHolder).bind(day)
        holder.itemView.setOnClickListener {
            holder.alter()
            onDayClicked(day)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class OffDaysSelectionViewHolder(val binding: DialogCreateHoOffdaysSelectionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var textColourIsBlack = true
        fun bind(date: Int) {
            binding.dayNumberText.text = date.toString()
        }

        fun alter() {
            textColourIsBlack = !textColourIsBlack
            binding.dayNumberText.setTextColor(if (textColourIsBlack) binding.root.context.getColor(R.color.black) else binding.root.context.getColor(R.color.green))
        }
    }
}