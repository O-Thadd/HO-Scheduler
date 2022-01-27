package com.othadd.hoscheduler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.FragmentSingleHoBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.HoDetailsRecyclerAdapter
import com.othadd.hoscheduler.viewmodel.OverviewViewModel
import com.othadd.hoscheduler.viewmodel.OverviewViewModelFactory


class SingleHoFragment : Fragment() {

    private val sharedViewModel: OverviewViewModel by activityViewModels {
        OverviewViewModelFactory(
            (activity?.application as SchedulerApplication).database
                .monthScheduleDao()
        )
    }

    lateinit var binding: FragmentSingleHoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingleHoBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            singleHODetailRecyclerView.adapter = HoDetailsRecyclerAdapter()
        }

        return binding.root
    }
}