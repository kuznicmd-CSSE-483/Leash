package edu.rosehulman.leash.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import edu.rosehulman.leash.Constants
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentEventsEditBinding
import edu.rosehulman.leash.models.AlarmViewModel
import edu.rosehulman.leash.models.EventsViewModel
import edu.rosehulman.leash.utils.NotificationUtils
import java.text.SimpleDateFormat
import java.util.*

class EventsEditFragment : Fragment() {

    private lateinit var binding: FragmentEventsEditBinding
    private lateinit var model: EventsViewModel
    private lateinit var date: String
    private lateinit var timestamp: Date

    // Create Alarm information
    private lateinit var alarmModel: AlarmViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsEditBinding.inflate(inflater, container, false)
        model = ViewModelProvider(requireActivity()).get(EventsViewModel::class.java)
        alarmModel = ViewModelProvider(requireActivity())[AlarmViewModel::class.java]
        alarmModel.setCurrentTime()
        NotificationUtils.createChannel(requireContext())

        timestamp = model.getCurrentEvent().time.toDate()

        // Set up Spinners
        val eventTypeSpinner: Spinner = binding.eventTypeSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.event_type_array,
            R.layout.color_spinner_layout
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
            // Apply the adapter to the spinner
            eventTypeSpinner.adapter = adapter
        }

        val alertSpinner: Spinner = binding.alertSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.alert_array,
            R.layout.color_spinner_layout
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
            // Apply the adapter to the spinner
            alertSpinner.adapter = adapter
        }

        val recurrenceSpinner: Spinner = binding.recurrenceSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.recurrence_array,
            R.layout.color_spinner_layout
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
            // Apply the adapter to the spinner
            recurrenceSpinner.adapter = adapter
        }

        updateView()
        setupButtons()

        return binding.root
    }

    fun updateView() {
        binding.eventTypeSpinner.setSelection(resources.getStringArray(R.array.event_type_array)
            .indexOf(model.getCurrentEvent().reminder))
        binding.nameEditText.setText(model.getCurrentEvent().name)
        binding.timeEditText.setText(model.getCurrentEvent().time.toDate().toString())
        binding.alertSpinner.setSelection(resources.getStringArray(R.array.alert_array)
            .indexOf(model.getCurrentEvent().alert))
        binding.recurrenceSpinner.setSelection(resources.getStringArray(R.array.recurrence_array)
            .indexOf(model.getCurrentEvent().recurrence))
        binding.petEditEditText.setText(model.getCurrentEvent().pet)
        if (model.getCurrentEvent().reminder == "Event") {
            binding.recurrenceSpinner.setEnabled(false)
        }
        else {
            binding.recurrenceSpinner.setEnabled(true)
        }
    }

    fun setupButtons() {
        binding.timeEditText.setOnClickListener {
            if (binding.eventTypeSpinner.selectedItem.toString() == "Event") {
                Log.d(Constants.TAG, "On EVENT")
                binding.recurrenceSpinner.setEnabled(false)
                binding.recurrenceSpinner.setSelection(0)
            }
            else {
                binding.recurrenceSpinner.setEnabled(true)
            }
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                datePicker.dismiss()
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                date = formatter.format(datePicker.selection?.let { it1 -> Date(it1) })
                var year = Integer.parseInt("${date.subSequence(6, 10)}")-1900
                var month = Integer.parseInt("${date.subSequence(3, 5)}")
                var day = Integer.parseInt("${date.subSequence(0, 2)}")

                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setTitleText("Select Event time")
                        .build()
                picker.addOnPositiveButtonClickListener{
                    timestamp = Date(year, month-1, day+1, picker.hour, picker.minute)
                    binding.timeEditText.setText(Timestamp(timestamp).toDate().toString())
                }
                picker.show(parentFragmentManager, "tag")
            }
            datePicker.show(parentFragmentManager, "tag")
        }

        // The following click listeners are meant to disable the reoccurrence spinner
        // if the event type is "Event", as this will not reoccur.
        binding.nameEditText.setOnClickListener {
            if (binding.eventTypeSpinner.selectedItem.toString() == "Event") {
                Log.d(Constants.TAG, "On EVENT")
                binding.recurrenceSpinner.setEnabled(false)
                binding.recurrenceSpinner.setSelection(0)
            }
            else {
                binding.recurrenceSpinner.setEnabled(true)
            }
        }

        binding.petEditEditText.setOnClickListener {
            if (binding.eventTypeSpinner.selectedItem.toString() == "Event") {
                Log.d(Constants.TAG, "On EVENT")
                binding.recurrenceSpinner.setEnabled(false)
                binding.recurrenceSpinner.setSelection(0)
            }
            else {
                binding.recurrenceSpinner.setEnabled(true)
            }
        }

        binding.saveEventButton.setOnClickListener{
            if (binding.eventTypeSpinner.selectedItem.toString() == "Event") {
                binding.recurrenceSpinner.setEnabled(false)
            }
            // TODO: CREATING ALERTS & REOCCURRENCES (EVENT AND/OR REMINDER)
            if (binding.alertSpinner.selectedItem.toString() != "None") {
                if (binding.alertSpinner.selectedItem.toString() == "Time of event") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        0
                    )
                } else if (binding.alertSpinner.selectedItem.toString() == "5 mins before") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        5
                    )
                } else if (binding.alertSpinner.selectedItem.toString() == "15 mins before") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        15
                    )
                } else if (binding.alertSpinner.selectedItem.toString() == "30 mins before") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        30
                    )
                } else if (binding.alertSpinner.selectedItem.toString() == "1 hour before") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        60
                    )
                } else if (binding.alertSpinner.selectedItem.toString() == "2 hours before") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        120
                    )
                }

                if (binding.recurrenceSpinner.selectedItem.toString() == "None") {
                    alarmModel.setAlarmScheduled(binding.nameEditText.text.toString(), model.getCurrentEvent().code
                    )
                } else {
                    if (binding.recurrenceSpinner.selectedItem.toString() == "Daily") {
                        alarmModel.setAlarmRecurring(
                            86400000,
                            binding.nameEditText.text.toString(),
                            model.getCurrentEvent().code
                        )
                    } else if (binding.recurrenceSpinner.selectedItem.toString() == "Weekly") {
                        alarmModel.setAlarmRecurring(
                            604800000,
                            binding.nameEditText.text.toString(),
                            model.getCurrentEvent().code
                        )
                    } else if (binding.recurrenceSpinner.selectedItem.toString() == "Monthly") {
                        alarmModel.setAlarmRecurring(
                            2629746000,
                            binding.nameEditText.text.toString(),
                            model.getCurrentEvent().code
                        )
                    } else if (binding.recurrenceSpinner.selectedItem.toString() == "Annually") {
                        alarmModel.setAlarmRecurring(
                            31556952000,
                            binding.nameEditText.text.toString(),
                            model.getCurrentEvent().code
                        )
                    }
                }
            }
            model.updateCurrentEvent(binding.eventTypeSpinner.selectedItem.toString(), binding.nameEditText.text.toString(),
                Timestamp(timestamp), binding.alertSpinner.selectedItem.toString(), binding.recurrenceSpinner.selectedItem.toString(),
                binding.petEditEditText.text.toString()
            )
            this.findNavController().navigate(R.id.navigation_events)
        }

        binding.deleteEventButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton(android.R.string.ok) {dialog, which ->
                    model.removeCurrentEvent()
                    this.findNavController().navigate(R.id.navigation_events)
                }.setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }
}