package com.othadd.hoscheduler.ui.recyclerAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.databinding.OverviewDayListItemBinding
import com.othadd.hoscheduler.databinding.OverviewHosListItemBinding
import com.othadd.hoscheduler.databinding.OverviewWardListItemBinding

const val WARD = 1
const val DAY = 2
const val HO = 3

class MonthScheduleOverviewRecyclerAdapter(val onItemClick: (DataItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataList = mutableListOf<DataItem>()

    fun postList(
        wardList: List<DataItem.WardItem>,
        dayList: List<DataItem.DayItem>,
        hoList: List<DataItem.HoItem>
    ) {
        val list = mutableListOf<DataItem>()

//        the first 'box' on the table is empty so add dummy DataItem, preferably HoItem since it's the only type that's not clickable
        list.add(DataItem.HoItem(-1, "", mutableListOf("", "")))

        for (ward in wardList) {
            list.add(ward)
        }

        for (day in dayList) {
            list.add(day)
            for (ward in wardList) {
                val ho =
                    hoList.find { it.dayNumber == day.dayNumber && it.wardName == ward.wardName }

                if (ho != null) {
                    list.add(ho)
                }

            }
        }
        dataList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            WARD -> WardViewHolder(OverviewWardListItemBinding.inflate(LayoutInflater.from(parent.context)))
            DAY -> DayViewHolder(OverviewDayListItemBinding.inflate(LayoutInflater.from(parent.context)))
            HO -> HoViewHolder(OverviewHosListItemBinding.inflate(LayoutInflater.from(parent.context)))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = dataList[position]) {
            is DataItem.WardItem -> {
                (holder as WardViewHolder).bind(item)
                holder.itemView.setOnClickListener { onItemClick(item) }
            }
            is DataItem.DayItem -> {
                (holder as DayViewHolder).bind(item)
                holder.itemView.setOnClickListener { onItemClick(item) }
            }
            is DataItem.HoItem -> (holder as HoViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is DataItem.WardItem -> WARD
            is DataItem.DayItem -> DAY
            is DataItem.HoItem -> HO
        }
    }

    class WardViewHolder(var binding: OverviewWardListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wardItem: DataItem.WardItem) {
            binding.wardName.text = wardItem.wardShortName
        }
    }

    class DayViewHolder(var binding: OverviewDayListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dayItem: DataItem.DayItem) {
            binding.dateAndMonth.text = binding.root.context.getString(
                R.string.day_slash_month,
                dayItem.dayNumber,
                dayItem.monthNumber
            )
            binding.shortDayName.text = dayItem.dayShortName
        }
    }

    class HoViewHolder(var binding: OverviewHosListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(hoItem: DataItem.HoItem) {
            binding.hoName1.text = hoItem.hos[0]
            binding.hoName2.text = if (hoItem.hos.size == 1) "" else hoItem.hos[1]
        }
    }

    sealed class DataItem {
        class WardItem(val wardNumber: Int, var wardName: String, var wardShortName: String) : DataItem()
        class DayItem(
            var dayNumber: Int,
            var monthNumber: Int,
            var dayShortName: String
        ) :
            DataItem() {
                val summaryName = "$dayShortName $dayNumber/$monthNumber"
            }

        class HoItem(var dayNumber: Int, var wardName: String, var hos: List<String>) : DataItem()
    }
}