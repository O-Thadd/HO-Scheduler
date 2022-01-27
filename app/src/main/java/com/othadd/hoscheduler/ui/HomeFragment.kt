package com.othadd.hoscheduler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.FragmentHomeBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthSchedulesRecyclerAdapter
import com.othadd.hoscheduler.viewmodel.HomeViewModel
import com.othadd.hoscheduler.viewmodel.HomeViewModelFactory

class HomeFragment : Fragment() {

    private val sharedViewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(
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
//            sharedViewModel.loadSingleMonthSchedule(it)
            val action = HomeFragmentDirections.actionHomeFragmentToMonthScheduleOverviewFragment(it)
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