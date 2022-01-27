package com.othadd.hoscheduler.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.FragmentMonthScheduleOverviewBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter.DataItem.DayItem
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthScheduleOverviewRecyclerAdapter.DataItem.WardItem
import com.othadd.hoscheduler.viewmodel.OverviewViewModel
import com.othadd.hoscheduler.viewmodel.OverviewViewModelFactory

class MonthScheduleOverviewFragment : Fragment() {

    private val args: MonthScheduleOverviewFragmentArgs by navArgs()

    private val sharedViewModel: OverviewViewModel by activityViewModels {
        OverviewViewModelFactory(
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

        sharedViewModel.loadMonthSchedule(args.monthScheduleId)

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

        val loadingTextView = binding.loadingTextView
        val recyclerView = binding.overViewRecyclerView

        sharedViewModel.stillLoadingSchedule.observe(this.viewLifecycleOwner){
            if (it){
                loadingTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            else {
                loadingTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

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

