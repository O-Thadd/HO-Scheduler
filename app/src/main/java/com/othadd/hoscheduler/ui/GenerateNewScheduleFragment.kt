package com.othadd.hoscheduler.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.othadd.hoscheduler.R
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.FragmentGenerateNewScheduleBinding
import com.othadd.hoscheduler.viewmodel.SchedulerViewModel
import com.othadd.hoscheduler.viewmodel.SchedulerViewModelFactory
import java.util.*

class GenerateNewScheduleFragment : Fragment() {

    private var monthName = ""
    private val monthItems = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    var numberOfDaysInFollowingMonth: Int = 0

    private val sharedViewModel: SchedulerViewModel by activityViewModels {
        SchedulerViewModelFactory(
            (activity?.application as SchedulerApplication).database
                .monthScheduleDao()
        )
    }

    lateinit var binding: FragmentGenerateNewScheduleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGenerateNewScheduleBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            generateNewScheduleFragment = this@GenerateNewScheduleFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), R.layout.month_selection_list_item, monthItems)
        val monthSelectionTextView = binding.monthEditText
        val monthNameTick = binding.monthTick
        monthSelectionTextView.setAdapter(adapter)
        monthSelectionTextView.setOnItemClickListener { _, _, position, _ ->
            sharedViewModel.selectedMonthNumber = position
        }
        monthSelectionTextView.addTextChangedListener {
            sharedViewModel.monthSelectionIsOk = it.toString().isNotBlank()
            if (sharedViewModel.monthSelectionIsOk) {
                monthNameTick.visibility = ImageView.VISIBLE
            } else monthNameTick.visibility = ImageView.INVISIBLE
            sharedViewModel.updateGenerateAndHoListCreateButtonStatus()
            sharedViewModel.selectedMonth = (it.toString())
        }




        val scheduleNameEditText = binding.editTextScheduleName
        val yearEditText = binding.editTextYear
        val yearTick = binding.yearTick
        val hoListTick = binding.hoListTick
        val scheduleNameTick = binding.scheduleNameTick


        scheduleNameEditText.addTextChangedListener {
            sharedViewModel.scheduleNameIsOk = it.toString().isNotBlank()
            if (sharedViewModel.scheduleNameIsOk) {
                scheduleNameTick.visibility = ImageView.VISIBLE
            } else scheduleNameTick.visibility = ImageView.INVISIBLE
            sharedViewModel.updateGenerateAndHoListCreateButtonStatus()
            sharedViewModel.setScheduleName(it.toString())
        }

        yearEditText.addTextChangedListener {
            sharedViewModel.yearIsOk = if(it.toString().isBlank()) false else it.toString().toInt() in 1000..2999
            if (sharedViewModel.yearIsOk) {
                yearTick.visibility = ImageView.VISIBLE
            } else yearTick.visibility = ImageView.INVISIBLE
            sharedViewModel.updateGenerateAndHoListCreateButtonStatus()
            if (it.toString().isNotBlank()){
                sharedViewModel.setScheduleYear(it.toString().toInt())
            }
        }

        sharedViewModel.newMonthScheduleHoList.observe(this.viewLifecycleOwner) {
            if (it.isNotEmpty())
                hoListTick.visibility = ImageView.VISIBLE
            else hoListTick.visibility = ImageView.INVISIBLE
        }

        sharedViewModel.generateScheduleButtonIsActive.observe(this.viewLifecycleOwner) {
                binding.buttonGenerate.isEnabled = it
        }

        sharedViewModel.hoListCreateButtonIsActive.observe(this.viewLifecycleOwner) {
            binding.btnCreateHoList.isEnabled = it
        }
    }

    fun onCreateHoListButtonClicked() {
        sharedViewModel.updateDaysInMonthForNewSchedule()
        val action =
            GenerateNewScheduleFragmentDirections.actionGenerateNewScheduleFragmentToHoListCreationFragment(numberOfDaysInFollowingMonth)
        findNavController().navigate(action)
    }

    fun generateSchedule() {
//        sharedViewModel.generateSchedules(
//            binding.editTextScheduleName.text.toString(),
//            monthName,
//            followingMonthNumber,
//            binding.editTextYear.text.toString().toInt()
//        )

        sharedViewModel.generateSchedulesInStages()

        val action = GenerateNewScheduleFragmentDirections.actionGenerateNewScheduleFragmentToHomeFragment()
        findNavController().navigate(action)
    }
}