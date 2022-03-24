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

        val adapter = MonthSchedulesRecyclerAdapter({itemClick(it)}, {itemLongClick(it)})

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            monthSchedulesRecyclerView.adapter = adapter
            homeFragment = this@HomeFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.selectionMode.observe(viewLifecycleOwner){
            if (it){
                binding.buttonDelete.visibility = View.VISIBLE
            } else {
                binding.buttonDelete.visibility = View.GONE
            }
        }
    }

    private fun itemClick(it: Int) {
        if (sharedViewModel.selectionMode.value == true){
            sharedViewModel.updateSelectedSchedules(it)
        } else{
            val action = HomeFragmentDirections.actionHomeFragmentToMonthScheduleOverviewFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun itemLongClick(it: Int) {
        sharedViewModel.alterSelectionMode()
        sharedViewModel.updateSelectedSchedules(it)
    }

    fun onCreateMonthScheduleButtonClicked(){
        val action = HomeFragmentDirections.actionHomeFragmentToGenerateNewScheduleFragment()
        findNavController().navigate(action)
    }

    fun onDeleteButtonClicked(){
        sharedViewModel.deleteSelectedSchedules()
    }

}