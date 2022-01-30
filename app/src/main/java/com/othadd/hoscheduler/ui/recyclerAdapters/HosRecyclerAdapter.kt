package com.othadd.hoscheduler.ui.recyclerAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.databinding.HosListItemBinding
import com.othadd.hoscheduler.utils.MyRecyclerViewAdapter
import com.othadd.hoscheduler.utils.UIHo

class HosRecyclerAdapter(val onItemClicked: (UIHo) -> Unit) : MyRecyclerViewAdapter() {

    override fun updateDataList(list: List<Any>) {
        dataList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoViewHolder {
        return HoViewHolder(HosListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ho = dataList[position] as UIHo
        (holder as HoViewHolder).bind(ho)
        holder.itemView.setOnClickListener { onItemClicked(ho) }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class HoViewHolder(private var binding: HosListItemBinding) : RecyclerView.ViewHolder(binding.root) {
         fun bind(ho: UIHo){
             binding.apply {
                 hoName.text = ho.name
                 numberOfCalls.text = binding.root.resources.getString(R.string.total_number_of_calls, ho.noOfCalls)
                 numberOfActiveCalls.text = binding.root.resources.getString(R.string.number_of_active_calls, ho.noOfActiveCalls)
                 numberOfSW4Calls.text = binding.root.resources.getString(R.string.number_of_SW4_calls, ho.noOfSW4Calls)
                 numberOfWeekendCalls.text = binding.root.resources.getString(R.string.number_of_weekend_calls, ho.noOfWeekendCalls)
                 numberOfWednesdayCalls.text = binding.root.resources.getString(R.string.number_of_wednesday_calls, ho.noOfWednesdayCalls)
             }
         }
    }
}