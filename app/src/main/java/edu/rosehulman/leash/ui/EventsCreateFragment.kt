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
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import edu.rosehulman.leash.Constants
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentEventsCreateBinding
import edu.rosehulman.leash.models.AlarmViewModel
import edu.rosehulman.leash.models.EventsViewModel
import edu.rosehulman.leash.utils.NotificationUtils
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class EventsCreateFragment : Fragment() {

    private lateinit var model: EventsViewModel
    private lateinit var binding: FragmentEventsCreateBinding
    private lateinit var date: String
    private lateinit var timestamp: Date
    // Create Alarm information
    private lateinit var alarmModel: AlarmViewModel
    private lateinit var calendar: Calendar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = ViewModelProvider(requireActivity()).get(EventsViewModel::class.java)
        binding = FragmentEventsCreateBinding.inflate(inflater, container, false)
        alarmModel = ViewModelProvider(requireActivity())[AlarmViewModel::class.java]
        alarmModel.setCurrentTime()
        NotificationUtils.createChannel(requireContext())

        timestamp = Timestamp.now().toDate()

        // Set up Spinners
        val eventTypeSpinner: Spinner = binding.eventTypeCreateSpinner
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

        val alertSpinner: Spinner = binding.alertCreateSpinner
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

        val recurrenceSpinner: Spinner = binding.recurrenceCreateSpinner
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

        setupButtons()

        return binding.root
    }

    fun setupButtons() {
        // Logic for choosing a time
        binding.timeCreateEditText.setOnClickListener {
            if (binding.eventTypeCreateSpinner.selectedItem.toString() == "Event") {
                binding.recurrenceCreateSpinner.setEnabled(false)
                binding.recurrenceCreateSpinner.setSelection(0)
            }
            else {
                binding.recurrenceCreateSpinner.setEnabled(true)
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
                binding.timeCreateEditText.setText(datePicker.headerText)
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                date = formatter.format(datePicker.selection?.let { it1 -> Date(it1) })
                var year = parseInt("${date.subSequence(6,10)}")-1900
                var month = parseInt("${date.subSequence(3,5)}")
                var day = parseInt("${date.subSequence(0,2)}")

                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setTitleText("Select Event time")
                        .build()
                picker.addOnPositiveButtonClickListener{
                    timestamp = Date(year, month-1, day+1, picker.hour, picker.minute)
                    binding.timeCreateEditText.setText(Timestamp(timestamp).toDate().toString())
                }
                picker.show(parentFragmentManager, "tag")
            }
            datePicker.show(parentFragmentManager, "tag")
        }
        // The following click listeners are meant to disable the reoccurrence spinner
        // if the event type is "Event", as this will not reoccur.
        binding.nameCreateEditText.setOnClickListener {
            if (binding.eventTypeCreateSpinner.selectedItem.toString() == "Event") {
                binding.recurrenceCreateSpinner.setEnabled(false)
                binding.recurrenceCreateSpinner.setSelection(0)
            }
            else {
                binding.recurrenceCreateSpinner.setEnabled(true)
            }
        }

        binding.petCreateEditText.setOnClickListener {
            if (binding.eventTypeCreateSpinner.selectedItem.toString() == "Event") {
                binding.recurrenceCreateSpinner.setEnabled(false)
                binding.recurrenceCreateSpinner.setSelection(0)
            }
            else {
                binding.recurrenceCreateSpinner.setEnabled(true)
            }
        }

        // Logic for saving event // If they select yes for an alert, do that here;
        binding.saveEventCreateButton.setOnClickListener {
            if (binding.eventTypeCreateSpinner.selectedItem.toString() == "Event") {
                binding.recurrenceCreateSpinner.setEnabled(false)
            }
            // TODO: Checking alert item is set to anything except none
            if (binding.alertCreateSpinner.selectedItem.toString() != "None") {
                // launchTimePicker()
                // TODO: Alarm Soon for testing.
                Log.d(Constants.TAG, "INSIDE CONDITIONAL")
                alarmModel.setAlarmSoon()
            }

            model.addEvent(binding.eventTypeCreateSpinner.selectedItem.toString(), binding.nameCreateEditText.text.toString(),
                Timestamp(timestamp), binding.alertCreateSpinner.selectedItem.toString(), binding.recurrenceCreateSpinner.selectedItem.toString(),
                binding.petCreateEditText.text.toString()
            )
            this.findNavController().navigate(R.id.navigation_events)
        }
    }

    // TODO: Will have to check this out::
    private fun launchTimePicker() {
        val futureCalendar = Calendar.getInstance().apply {
            set(Calendar.MINUTE, get(Calendar.MINUTE) + 1)
        }
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(futureCalendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(futureCalendar.get(Calendar.MINUTE))
            .setTitleText("Select Alert time")
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .build()
        picker.addOnPositiveButtonClickListener {
            alarmModel.setAlarmTime(picker.hour, picker.minute)
        }
        picker.addOnNegativeButtonClickListener { }
        picker.show(parentFragmentManager, "tag")
    }
}