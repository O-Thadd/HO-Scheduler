package com.othadd.hoscheduler.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.FragmentHomeBinding
import com.othadd.hoscheduler.databinding.FragmentMonthScheduleOverviewBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter.DataItem.DayItem
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter.DataItem.WardItem
import com.othadd.hoscheduler.viewmodel.SchedulerViewModel
import com.othadd.hoscheduler.viewmodel.SchedulerViewModelFactory

class MonthScheduleOverviewFragment : Fragment() {

    private val sharedViewModel: SchedulerViewModel by activityViewModels {
        SchedulerViewModelFactory(
            (activity?.application as SchedulerApplication).database
                .monthScheduleDao()
        )
    }

    lateinit var binding: FragmentMonthScheduleOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMonthScheduleOverviewBinding.inflate(inflater, container, false)

        val adapter = MonthScheduleOverviewRecyclerAdapter {
            if (it is WardItem) {
                sharedViewModel.setWard(it.wardNumber)
                val action =
                    MonthScheduleOverviewFragmentDirections.actionMonthScheduleOverviewFragmentToSingleWardDetailFragment(it.wardName)
                findNavController().navigate(action)
            } else if (it is DayItem) {
                sharedViewModel.setDay(it.dayNumber)
                val action =
                    MonthScheduleOverviewFragmentDirections.actionMonthScheduleOverviewFragmentToSingleDayDetailFragment2(it.summaryName)
                findNavController().navigate(action)
            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            overViewRecyclerView.adapter = adapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.schedule_overview_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.hosFragment -> {
                findNavController().navigate(MonthScheduleOverviewFragmentDirections.actionMonthScheduleOverviewFragmentToHosFragment3())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

