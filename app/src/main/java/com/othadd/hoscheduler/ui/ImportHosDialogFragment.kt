package com.othadd.hoscheduler.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.FragmentImportHosDialogBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.MonthSchedulesRecyclerAdapter
import com.othadd.hoscheduler.viewmodel.HoListCreationViewModel
import com.othadd.hoscheduler.viewmodel.HoListCreationViewModelFactory


class ImportHosDialogFragment : DialogFragment() {

    private val sharedViewModel: HoListCreationViewModel by activityViewModels {
        HoListCreationViewModelFactory(
            (activity?.application as SchedulerApplication).database
                .monthScheduleDao()
        )
    }

    lateinit var binding: FragmentImportHosDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentImportHosDialogBinding.inflate(inflater, container, false)

        val adapter = MonthSchedulesRecyclerAdapter({sharedViewModel.addImportedHos(it)
            val action = ImportHosDialogFragmentDirections.actionImportHosDialogFragmentToHoListCreationFragment()
            findNavController().navigate(action)}, {})

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            existingSchedulesRecyclerView.adapter = adapter
        }

        return binding.root
    }

}