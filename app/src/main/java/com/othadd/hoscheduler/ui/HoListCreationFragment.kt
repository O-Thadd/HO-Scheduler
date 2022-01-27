package com.othadd.hoscheduler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.othadd.hoscheduler.databinding.FragmentHoListCreationBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.HoListCreationRecyclerAdapter
import com.othadd.hoscheduler.viewmodel.HoListCreationViewModel
import com.othadd.hoscheduler.viewmodel.HoListCreationViewModelFactory

class HoListCreationFragment : Fragment() {


    private val sharedViewModel: HoListCreationViewModel by activityViewModels {
        HoListCreationViewModelFactory()
    }

    lateinit var binding: FragmentHoListCreationBinding

    lateinit var adapter: HoListCreationRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHoListCreationBinding.inflate(inflater, container, false)

        adapter = HoListCreationRecyclerAdapter()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            recyclerHoListCreation.adapter = adapter
            hoListCreationFragment = this@HoListCreationFragment
        }

        return binding.root
    }


    fun addNewHoToList() {

        val action =
            HoListCreationFragmentDirections.actionHoListCreationFragmentToAddHoToListFragment()
        findNavController().navigate(action)
    }

    fun finishAddingHos() {
        val action =
            HoListCreationFragmentDirections.actionHoListCreationFragmentToGenerateNewScheduleFragment()
        findNavController().navigate(action)
    }

}