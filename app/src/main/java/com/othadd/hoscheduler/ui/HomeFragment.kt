package com.othadd.hoscheduler.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.FragmentHomeBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthSchedulesRecyclerAdapter
import com.othadd.hoscheduler.viewmodel.SchedulerViewModel
import com.othadd.hoscheduler.viewmodel.SchedulerViewModelFactory

class HomeFragment : Fragment() {

    private val sharedViewModel: SchedulerViewModel by activityViewModels {
        SchedulerViewModelFactory(
            (activity?.application as SchedulerApplication).database
                .monthScheduleDao()
        )
    }

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val adapter = MonthSchedulesRecyclerAdapter{
            sharedViewModel.loadSingleMonthSchedule(it)
            val action = HomeFragmentDirections.actionHomeFragmentToMonthScheduleOverviewFragment()
            findNavController().navigate(action)
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            monthSchedulesRecyclerView.adapter = adapter
            homeFragment = this@HomeFragment
        }

        return binding.root
    }

    fun onCreateMonthScheduleButtonClicked(){
        val action = HomeFragmentDirections.actionHomeFragmentToGenerateNewScheduleFragment()
        findNavController().navigate(action)
    }

}