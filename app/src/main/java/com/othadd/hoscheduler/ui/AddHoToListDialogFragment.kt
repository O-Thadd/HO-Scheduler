package com.othadd.hoscheduler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.DialogFragmentAddHoToListBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.DaysSelectionRecyclerAdapter
import com.othadd.hoscheduler.ui.recyclerAdapters.DaysSelectionRecyclerAdapter.DaySelectionItem
import com.othadd.hoscheduler.viewmodel.HoListCreationViewModel
import com.othadd.hoscheduler.viewmodel.HoListCreationViewModelFactory

class AddHoToListDialogFragment : DialogFragment() {

    var switchNewHOIsOn = false
    var switchExitingHOIsOn = false


    companion object {
        const val TAG = "FilterDialog"
    }

    private val sharedViewModel: HoListCreationViewModel by activityViewModels {
        HoListCreationViewModelFactory(
            (activity?.application as SchedulerApplication).database
                .monthScheduleDao()
        )
    }

    lateinit var binding: DialogFragmentAddHoToListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogFragmentAddHoToListBinding.inflate(inflater, container, false)

        val offDaysSelectionAdapter = setUpOffDaysSelectionAdapter()

        val outsidePostingDaysSelectionAdapter = setUpOutsidePostingDaysSelectionAdapter()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            recyclerOffDaysSelection.adapter = offDaysSelectionAdapter
            recyclerOutsidePostingDaysSelection.adapter = outsidePostingDaysSelectionAdapter
            addHoToListDialogFragment = this@AddHoToListDialogFragment
        }

        return binding.root
    }

    private fun setUpOutsidePostingDaysSelectionAdapter(): DaysSelectionRecyclerAdapter {
        val outsidePostingDaysSelectionAdapter = DaysSelectionRecyclerAdapter {
            sharedViewModel.updateHoOutsidePostingDays(it)
        }

        val outSidePostingDaySelectionItems = mutableListOf<DaySelectionItem>()
        for (day in sharedViewModel.daysInMonthForNewSchedule) {
            outSidePostingDaySelectionItems.add(DaySelectionItem(day))
        }

        if (sharedViewModel.updatingHo) {
            for (day in sharedViewModel.ho.value?.outSidePostingDays!!) {
                outSidePostingDaySelectionItems.find { it.date == day }?.selected = true
            }
        }

        outsidePostingDaysSelectionAdapter.updateDataList(outSidePostingDaySelectionItems)
        return outsidePostingDaysSelectionAdapter
    }

    private fun setUpOffDaysSelectionAdapter(): DaysSelectionRecyclerAdapter {
        val offDaysSelectionAdapter = DaysSelectionRecyclerAdapter {
            sharedViewModel.updateOffDay(it)
        }

        val offDaySelectionItems = mutableListOf<DaySelectionItem>()
        for (day in sharedViewModel.daysInMonthForNewSchedule) {
            offDaySelectionItems.add(DaySelectionItem(day))
        }

        if (sharedViewModel.updatingHo) {
            for (day in sharedViewModel.ho.value?.outDays!!) {
                offDaySelectionItems.find { it.date == day }?.selected = true
            }
        }

        offDaysSelectionAdapter.updateDataList(offDaySelectionItems)
        return offDaysSelectionAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setup switch and editText relationship and if info available, set text and update switch state.
        val switchNewHO = binding.switchNewHO
        val newHoResumptionDateTextField = binding.textFieldNewHOResumptionDate
        val newHoResumptionDateEditText = binding.editTextNewHOResumptionDate
        switchNewHO.setOnCheckedChangeListener { _, isChecked ->
            switchNewHOIsOn = isChecked
            if (!isChecked) newHoResumptionDateEditText.text?.clear()
            newHoResumptionDateTextField.isEnabled = isChecked
        }
        if (sharedViewModel.updatingHo && sharedViewModel.ho.value?.resumptionDay != -33) {
            switchNewHO.isChecked = true
            newHoResumptionDateEditText.setText(sharedViewModel.ho.value?.resumptionDay.toString())
        }

        val switchExitingHO = binding.switchExitingHO
        val exitingHoLastDayTextField = binding.textFieldExitingDate
        val exitingHoLastDayEditText = binding.editTextOldHoExitingDate
        switchExitingHO.setOnCheckedChangeListener { _, isChecked ->
            switchExitingHOIsOn = isChecked
            if (!isChecked) exitingHoLastDayEditText.text?.clear()
            exitingHoLastDayTextField.isEnabled = isChecked
        }
        if (sharedViewModel.updatingHo && sharedViewModel.ho.value?.exitDay != 33) {
            switchExitingHO.isChecked = true
            exitingHoLastDayEditText.setText(sharedViewModel.ho.value?.exitDay.toString())
        }
    }

    fun onAddButtonClicked() {
        val name = binding.editTextHoName.text.toString()
        val resumptionDateString = binding.editTextNewHOResumptionDate.text.toString()
        val exitingDateString = binding.editTextOldHoExitingDate.text.toString()
        val thereIsResumptionDate = resumptionDateString.isNotBlank()
        val thereIsExitingDate = exitingDateString.isNotBlank()

        if (name.isBlank()) return

        if (sharedViewModel.updatingHo) {
            val hoNumber = (sharedViewModel.ho.value?.number)!!

//            if there is a resumption date
            if (thereIsResumptionDate) {
                val resumptionDate = resumptionDateString.toInt()
                sharedViewModel.updateScheduleGeneratingHoList(
                    hoNumber,
                    name,
                    resumptionDate = resumptionDate
                )
            }

//        if there is an exit date
            else if (thereIsExitingDate) {
                val exitingDate = exitingDateString.toInt()
                sharedViewModel.updateScheduleGeneratingHoList(
                    hoNumber,
                    name,
                    exitDay = exitingDate
                )
            }

//        if there is both a resumption day and an exit day
            else if (thereIsResumptionDate && thereIsExitingDate) {
                val resumptionDate = resumptionDateString.toInt()
                val exitingDate = exitingDateString.toInt()
                sharedViewModel.updateScheduleGeneratingHoList(
                    hoNumber,
                    name,
                    resumptionDate = resumptionDate,
                    exitDay = exitingDate
                )
            }

//        if there is neither a resumption day nor an exit day
            else sharedViewModel.updateScheduleGeneratingHoList(hoNumber, name)
        }

        else {

//            if there is a resumption date
            if (thereIsResumptionDate) {
                val resumptionDate = resumptionDateString.toInt()
                sharedViewModel.updateScheduleGeneratingHoList(
                    name,
                    resumptionDate = resumptionDate
                )
            }

//        if there is an exit date
            else if (thereIsExitingDate) {
                val exitingDate = exitingDateString.toInt()
                sharedViewModel.updateScheduleGeneratingHoList(name, exitDay = exitingDate)
            }

//        if there is both a resumption day and an exit day
            else if (thereIsResumptionDate && thereIsExitingDate) {
                val resumptionDate = resumptionDateString.toInt()
                val exitingDate = exitingDateString.toInt()
                sharedViewModel.updateScheduleGeneratingHoList(
                    name,
                    resumptionDate = resumptionDate,
                    exitDay = exitingDate
                )
            }

//        if there is neither a resumption day nor an exit day
            else sharedViewModel.updateScheduleGeneratingHoList(name)
        }

        sharedViewModel.clearSetHo()

        val action =
            AddHoToListDialogFragmentDirections.actionAddHoToListFragmentToHoListCreationFragment()
        findNavController().navigate(action)
    }
}