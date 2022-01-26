package com.othadd.hoscheduler.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.FragmentHoListCreationBinding
import com.othadd.hoscheduler.databinding.FragmentSingleDayDetailBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.HoListCreationRecyclerAdapter
import com.othadd.hoscheduler.viewmodel.SchedulerViewModel
import com.othadd.hoscheduler.viewmodel.SchedulerViewModelFactory

class HoListCreationFragment : Fragment() {

    val args: HoListCreationFragmentArgs by navArgs()


    private val sharedViewModel: SchedulerViewModel by activityViewModels {
        SchedulerViewModelFactory(
            (activity?.application as SchedulerApplication).database
                .monthScheduleDao()
        )
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

//        sharedViewModel.newMonthScheduleHoList.observe(viewLifecycleOwner){
//            adapter.updateDataList(it)
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedViewModel.newMonthScheduleHoList.observe(viewLifecycleOwner){
            adapter.updateDataList(it)
        }
    }

    fun addNewHoToList() {

        val action =
            HoListCreationFragmentDirections.actionHoListCreationFragmentToAddHoToListFragment(args.numberOfDaysInMonth)
        findNavController().navigate(action)
    }

    fun finishAddingHos() {
        val action =
            HoListCreationFragmentDirections.actionHoListCreationFragmentToGenerateNewScheduleFragment()
        findNavController().navigate(action)
    }

    fun testAdd30Hos() {
        val HoNames = listOf(
            "Thadd",
            "Ike",
            "Iyke",
            "Genes",
            "Exo",
            "Levi",
            "Num",
            "Duet",
            "Josh",
            "Jud",
            "Rut",
            "Sam",
            "King",
            "Chron",
            "Ezra",
            "Nehe",
            "Esta",
            "Jo",
            "Psalm",
            "Prov",
            "Eccl",
            "Sol",
            "Isa",
            "Jer",
            "Lamen",
            "Eze",
            "Dan",
            "Hos",
            "Amos",
            "Obad",
            "Jon",
            "Mic",
            "Nah"
        )

        val days = 1..28

        for(hoName in HoNames){
            for(offDay in 1..((1..7).random())){
                sharedViewModel.updateOffDay(days.random())
            }

            sharedViewModel.addScheduleGeneratingHo(hoName)
        }

    }
}