package com.othadd.hoscheduler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.othadd.hoscheduler.SchedulerApplication
import com.othadd.hoscheduler.databinding.DialogFragmentAddHoToListBinding
import com.othadd.hoscheduler.ui.recyclerAdapters.DaysSelectionRecyclerAdapter
import com.othadd.hoscheduler.viewmodel.SchedulerViewModel
import com.othadd.hoscheduler.viewmodel.SchedulerViewModelFactory

class AddHoToListDialogFragment : DialogFragment() {

    var switchNewHOIsOn = false
    var switchExitingHOIsOn = false

    val args: AddHoToListDialogFragmentArgs by navArgs()


    companion object {
        const val TAG = "FilterDialog"
    }

    private val sharedViewModel: SchedulerViewModel by activityViewModels {
        SchedulerViewModelFactory(
            (activity?.application as SchedulerApplication).database
                .monthScheduleDao()
        )
    }

    lateinit var binding: DialogFragmentAddHoToListBinding

    private val offDaysSelectionRecyclerAdapter: DaysSelectionRecyclerAdapter
        get() {
            val adapter = DaysSelectionRecyclerAdapter {
                sharedViewModel.updateOffDay(it)
            }
            return adapter
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogFragmentAddHoToListBinding.inflate(inflater, container, false)


        val offDaysSelectionAdapter = offDaysSelectionRecyclerAdapter
        offDaysSelectionAdapter.updateDataList(sharedViewModel.daysInMonthForNewSchedule)

        val outsidePostingDaysSelectionAdapter = DaysSelectionRecyclerAdapter{
            sharedViewModel.updateHoOutsidePostingDays(it)
        }
        outsidePostingDaysSelectionAdapter.updateDataList(sharedViewModel.daysInMonthForNewSchedule)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            recyclerOffDaysSelection.adapter = offDaysSelectionAdapter
            recyclerOutsidePostingDaysSelection.adapter = outsidePostingDaysSelectionAdapter
            addHoToListDialogFragment = this@AddHoToListDialogFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setup switch and editText relationship
        val switchNewHO = binding.switchNewHO
        val newHoResumptionDateTextField = binding.textFieldNewHOResumptionDate
        val newHoResumptionDateEditText = binding.editTextNewHOResumptionDate
        switchNewHO.setOnCheckedChangeListener { _, isChecked ->
            switchNewHOIsOn = isChecked
            if(!isChecked) newHoResumptionDateEditText.text?.clear()
            newHoResumptionDateTextField.isEnabled = isChecked
        }

        val switchExitingHO = binding.switchExitingHO
        val exitingHoLastDayTextField = binding.textFieldExitingDate
        val exitingHoLastDayEditText = binding.editTextOldHoExitingDate
        switchExitingHO.setOnCheckedChangeListener { _, isChecked ->
            switchExitingHOIsOn = isChecked
            if(!isChecked) exitingHoLastDayEditText.text?.clear()
            exitingHoLastDayTextField.isEnabled = isChecked
        }
    }

    fun onAddButtonClicked(){
        val name = binding.editTextHoName.text.toString()
        val resumptionDateString = binding.editTextNewHOResumptionDate.text.toString()
        val exitingDateString = binding.editTextOldHoExitingDate.text.toString()
        val thereIsResumptionDate = resumptionDateString.isNotBlank()
        val thereIsExitingDate = exitingDateString.isNotBlank()

        if (name.isBlank()) return

//        if there is a resumption date
        if(thereIsResumptionDate){
            val resumptionDate = resumptionDateString.toInt()
            sharedViewModel.addScheduleGeneratingHo(name, resumptionDate = resumptionDate)
        }

//        if there is an exit date
        else if(thereIsExitingDate){
            val exitingDate = exitingDateString.toInt()
            sharedViewModel.addScheduleGeneratingHo(name, exitDay = exitingDate)
        }

//        if there is both a resumption day and an exit day
        else if(thereIsResumptionDate && thereIsExitingDate){
            val resumptionDate = resumptionDateString.toInt()
            val exitingDate = exitingDateString.toInt()
            sharedViewModel.addScheduleGeneratingHo(name, resumptionDate = resumptionDate, exitDay = exitingDate)
        }

//        if there is neither a resumption day nor an exit day
        else sharedViewModel.addScheduleGeneratingHo(name)

        val action = AddHoToListDialogFragmentDirections.actionAddHoToListFragmentToHoListCreationFragment()
        findNavController().navigate(action)
    }
}