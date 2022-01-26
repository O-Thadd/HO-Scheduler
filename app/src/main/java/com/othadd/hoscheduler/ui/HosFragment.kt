package com.othadd.hoscheduler.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.FragmentHosBinding
import com.othadd.hoscheduler.databinding.FragmentSingleDayDetailBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.HosRecyclerAdapter
import com.othadd.hoscheduler.viewmodel.SchedulerViewModel
import com.othadd.hoscheduler.viewmodel.SchedulerViewModelFactory


class HosFragment : Fragment() {
    private val sharedViewModel: SchedulerViewModel by activityViewModels {
        SchedulerViewModelFactory(
            (activity?.application as SchedulerApplication).database
                .monthScheduleDao()
        )
    }

    lateinit var binding: FragmentHosBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHosBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            hosRecyclerView.adapter = HosRecyclerAdapter{
                sharedViewModel.setHo(it.number)
                val action = HosFragmentDirections.actionHosFragmentToSingleHoFragment(it.name)
                findNavController().navigate(action)
            }
        }

        return binding.root
    }
}

